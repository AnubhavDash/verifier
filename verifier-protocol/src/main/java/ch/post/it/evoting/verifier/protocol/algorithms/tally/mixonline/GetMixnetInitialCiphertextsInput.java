/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.protocol.algorithms.tally.mixonline;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.post.it.evoting.cryptoprimitives.domain.validations.Validations;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;

/**
 * Regroups the input values needed by the GetMixnetInitialCiphertexts<sub>j</sub> algorithm.
 *
 * <ul>
 * <li>delta_hat, the number of allowed write-ins + 1 for this specific ballot box. Strictly positive.</li>
 * <li>vcMap<sub>j</sub>, the key-value map of verification card ids to encrypted, confirmed votes. Not null.</li>
 * <li>EL<sub>pk</sub>, the election public key.</li>
 * </ul>
 */
public record GetMixnetInitialCiphertextsInput(int numberOfAllowedWriteInsPlusOne,
											   Map<String, ElGamalMultiRecipientCiphertext> encryptedConfirmedVotes,
											   ElGamalMultiRecipientPublicKey electionPublicKey) {

	public GetMixnetInitialCiphertextsInput(final int numberOfAllowedWriteInsPlusOne,
			final Map<String, ElGamalMultiRecipientCiphertext> encryptedConfirmedVotes, final ElGamalMultiRecipientPublicKey electionPublicKey) {
		checkArgument(numberOfAllowedWriteInsPlusOne > 0, "The number of allowed write-ins + 1 must be strictly positive.");
		checkNotNull(encryptedConfirmedVotes);
		checkNotNull(electionPublicKey);

		final Map<String, ElGamalMultiRecipientCiphertext> encryptedConfirmedVotesCopy = Collections.unmodifiableMap(
				new LinkedHashMap<>(encryptedConfirmedVotes));
		encryptedConfirmedVotesCopy.keySet().forEach(Validations::validateUUID);
		final List<ElGamalMultiRecipientCiphertext> encryptedConfirmedVotesList = encryptedConfirmedVotesCopy.values().stream().toList();

		// The GroupVector constructor checks all ciphertexts have the same group and the same size.
		GroupVector.from(encryptedConfirmedVotesList);

		checkArgument(encryptedConfirmedVotesList.isEmpty() || encryptedConfirmedVotesList.get(0).getGroup().equals(electionPublicKey.getGroup()),
				"The ciphertexts must have the same group as the election public key.");

		checkArgument(numberOfAllowedWriteInsPlusOne <= electionPublicKey.size(),
				"The election public key must have at least as many elements as the number of allowed write-ins + 1. [delta_hat: %s, delta: %s]",
				numberOfAllowedWriteInsPlusOne, electionPublicKey.size());
		checkArgument(encryptedConfirmedVotesList.isEmpty() || encryptedConfirmedVotesList.get(0).size() == numberOfAllowedWriteInsPlusOne,
				"The ciphertexts must be of size number of allowed write-ins + 1.");

		this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;
		this.encryptedConfirmedVotes = encryptedConfirmedVotesCopy;
		this.electionPublicKey = electionPublicKey;
	}
}