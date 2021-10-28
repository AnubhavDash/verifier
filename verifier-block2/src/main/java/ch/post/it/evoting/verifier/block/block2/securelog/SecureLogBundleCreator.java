/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block2.securelog;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

public final class SecureLogBundleCreator {
	private static final Logger LOGGER = Logger.getLogger(SecureLogBundleCreator.class);

	private SecureLogBundleCreator() {
		//private ctor. Use static method
	}

	public static Flux<SecureLogBundle> from(Flux<SecureLogEntry> source) {
		return from(source, null);
	}

	public static Flux<SecureLogBundle> from(Flux<SecureLogEntry> source, final Map<String, SecureLogBundleCertificates> hostPEMMapping) {
		AtomicReference<SecureLogEntry> last = new AtomicReference<>();
		return source
				.doOnNext(last::set)
				.concatMap(e -> {
					//duplicate all checkpoints
					if (e instanceof CheckPointLogEntry) {
						return Flux.just(e, e);
					} else {
						return Flux.just(e);
					}
				}).scan(new MyStruct(), (s, e) -> {
					MyStruct result = new MyStruct();
					if (e instanceof CheckPointLogEntry) {
						if (e.equals(s.getLastAnalysedCheckPoint())) {
							//this checkpoint is a duplicate one -> create new Bundle
							result.setBundle(new SecureLogBundle());
							result.getBundle().setBeginCheckPoint((CheckPointLogEntry) e);
							if (hostPEMMapping != null) {
								result.getBundle().setCertificates(hostPEMMapping.get(e.getHost()));
							}
						} else if (s.getLastAnalysedCheckPoint() != null) {
							//this checkpoint is not the first one of the Flux
							result.setBundle(s.getBundle());
							result.getBundle().setEndCheckPoint((CheckPointLogEntry) e);
							result.setTerminal(true);
						}
						result.setLastAnalysedCheckPoint((CheckPointLogEntry) e);
					} else if (e instanceof RegularLogEntry) {
						if (s.getBundle() == null) {
							LOGGER.fatal(
									String.format("Regular log found without prior CheckPoint in file : %s. Raw : %s", e.getSource(), e.getRaw()));
							throw new IllegalArgumentException("Regular log found without prior CheckPoint in file : " + e.getSource());
						} else {
							result.setLastAnalysedCheckPoint(s.getLastAnalysedCheckPoint());
							result.setBundle(s.getBundle());
							result.getBundle().addRegularLogEntry((RegularLogEntry) e);
						}
					} else if (e instanceof LastRowEntry) {
						//lastRows are just ignored
					} else {
						throw new IllegalArgumentException("Unsupported SecureLogEntry implementation : " + e.getClass());
					}
					return result;
				}).filter(MyStruct::isTerminal).map(MyStruct::getBundle).doOnComplete(() -> {
					SecureLogEntry lastEntry = last.get();
					if (lastEntry != null && !(lastEntry instanceof CheckPointLogEntry)) {
						throw new RuntimeException("SecureLog on host {" + lastEntry.getHost() + "}, source {" + lastEntry.getSource()
								+ "} does not terminate with a checkpoint");
					}
				});
	}

	@Getter
	@Setter
	static class MyStruct {
		SecureLogBundle bundle;
		CheckPointLogEntry lastAnalysedCheckPoint;
		boolean terminal = false;
	}
}
