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
package ch.post.it.evoting.verifier.block.block3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import com.scytl.products.ov.mixnet.BGVerifier;

import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineCommitmentParametersLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineEncryptedBallotsLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineEncryptionParametersLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflinePublicKeyLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineReEncryptedBallotsLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineShuffleProofLoader;
import ch.post.it.evoting.verifier.block.block3.scytl.TestType;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OfflineDataLoader;
import ch.post.it.evoting.verifier.common.Status;

public class BGOfflineVerificationProcessor {

	private static final BGOfflineVerificationProcessor instance = new BGOfflineVerificationProcessor();
	private final Map<TestType, AbstractMap.SimpleEntry<Status, String>> statuses = new HashMap<>();
	private final List<Object> attachedClients = new LinkedList<>();
	private Path path;
	private boolean processed = false;

	private BGOfflineVerificationProcessor() {
		//singleton, use static getInstanceAndRegister method
	}

	public static BGOfflineVerificationProcessor getInstanceAndRegister(Object o) {
		instance.register(o);
		return instance;
	}

	public synchronized void register(Object o) {
		if (!attachedClients.contains(o)) {
			attachedClients.add(o);
		}
	}

	public synchronized void unregister(Object o) {
		attachedClients.remove(o);
		if (attachedClients.isEmpty()) {
			this.reset();
		}
	}

	private void reset() {
		this.processed = false;
		this.statuses.clear();
		this.path = null;
	}

	public synchronized void executeProcess(Path path) {
		if (this.path == null) {
			this.path = path;
		} else if (!this.path.equals(path)) {
			throw new RuntimeException("Not unique Path defined");
		}

		BiFunction<Path, Path, OfflineDataLoader> offlineDataLoaderBiFunction = (p, outputParentPath) -> {
			try {
				OfflineDataLoader result = new OfflineDataLoader();

				result.setEncryptionParametersLoader(new OfflineEncryptionParametersLoader(outputParentPath.getParent()));
				result.setPublicKeyLoader(new OfflinePublicKeyLoader(p));
				result.setEncryptedBallotsLoader(new OfflineEncryptedBallotsLoader(p, outputParentPath.getParent()));
				result.setReEncryptedBallotsLoader(new OfflineReEncryptedBallotsLoader(p, outputParentPath.getParent()));
				result.setShuffleProofLoader(new OfflineShuffleProofLoader(p));
				result.setCommitmentParametersLoader(new OfflineCommitmentParametersLoader(p));

				return result;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};

		if (!this.processed) {
			BGVerifier.verify(this.path, (TestType t, ch.post.it.evoting.verifier.block.block3.scytl.Status s, String m) -> {
				if (!statuses.containsKey(t) || !statuses.get(t).getKey().equals(Status.NOK)) {
					Status status = StatusConverter.map(s);
					statuses.put(t, new AbstractMap.SimpleEntry<>(status, status == Status.NOK ? m : null));
				}
			}, offlineDataLoaderBiFunction);
			this.processed = true;
		}
	}

	public AbstractMap.SimpleEntry<Status, String> getStatus(TestType type) {
		if (!processed) {
			throw new RuntimeException("you must call executeProcess before getting result");
		}
		return statuses.containsKey(type) ? statuses.get(type) : new AbstractMap.SimpleEntry<>(Status.NA, null);
	}

}
