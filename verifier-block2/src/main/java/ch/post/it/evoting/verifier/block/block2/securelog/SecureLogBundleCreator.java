/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.securelog;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class SecureLogBundleCreator {
    private static final Logger LOGGER = Logger.getLogger(SecureLogBundleCreator.class);

    private SecureLogBundleCreator() {
        //private ctor. Use static method
    }

    @Getter
    @Setter
    static class MyStruct {
        SecureLogBundle bundle;
        CheckPointLogEntry lastAnalysedCheckPoint;
        boolean terminal = false;
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
                            LOGGER.fatal(String.format("Regular log found without prior CheckPoint in file : %s. Raw : %s", e.getSource(), e.getRaw()));
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
                        throw new RuntimeException("SecureLog on host {" + lastEntry.getHost() + "}, source {" + lastEntry.getSource() + "} does not terminate with a checkpoint");
                    }
                });
    }
}
