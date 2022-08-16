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
package ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline;

import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;

/**
 * Regroups the input needed by the VerifyMixDecOnline<sub>j</sub> and VerifyMixDecOffline algorithms.
 */
public record VerifyMixDecInput(GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> initialCiphertexts,
								List<VerifiableShuffle> precedingVerifiableShuffledVotes,
								List<VerifiableDecryptions> precedingVerifiableDecryptedVotes,
								ElGamalMultiRecipientPublicKey electionPublicKey,
								GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> ccmElectionPublicKeys,
								ElGamalMultiRecipientPublicKey electoralBoardPublicKey) {

	/**
	 * Constructs the input needed by the algorithms.
	 *
	 * @throws NullPointerException     if any of the input arguments is null.
	 * @throws IllegalArgumentException if any of the conditions on the input arguments is not satisfied.
	 */
	public VerifyMixDecInput {

		checkNotNull(initialCiphertexts);
		checkNotNull(precedingVerifiableShuffledVotes);
		checkNotNull(precedingVerifiableDecryptedVotes);
		checkNotNull(electionPublicKey);
		checkNotNull(ccmElectionPublicKeys);
		checkNotNull(electoralBoardPublicKey);

		checkArgument(!initialCiphertexts.isEmpty(), "The vector of initial ciphertexts must not be empty.");
		checkArgument(!precedingVerifiableShuffledVotes.isEmpty(), "The list of verifiable shuffles must not be empty.");
		checkArgument(!precedingVerifiableDecryptedVotes.isEmpty(), "The list of verifiable decryptions must not be empty.");
		checkArgument(precedingVerifiableShuffledVotes.stream().allMatch(Objects::nonNull), "The verifiable shuffles must not contain any null.");
		checkArgument(precedingVerifiableDecryptedVotes.stream().allMatch(Objects::nonNull),
				"The verifiable decryptions must not contain any null.");

		// Shuffles cross-checks.
		checkArgument(allEqual(precedingVerifiableShuffledVotes.stream(), s -> s.shuffledCiphertexts().getGroup()),
				"The verifiable shuffles must all have the same group.");
		checkArgument(allEqual(precedingVerifiableShuffledVotes.stream(), s -> s.shuffledCiphertexts().getElementSize()),
				"The verifiable shuffles ciphertexts must all have the same element size.");
		checkArgument(allEqual(precedingVerifiableShuffledVotes.stream(), s -> s.shuffledCiphertexts().size()),
				"The verifiable shuffles ciphertexts vectors must all have the same size.");

		// Decryptions cross-checks.
		checkArgument(allEqual(precedingVerifiableDecryptedVotes.stream(), VerifiableDecryptions::getGroup),
				"The verifiable decryptions must all have the same group.");
		checkArgument(allEqual(precedingVerifiableDecryptedVotes.stream(), d -> d.getDecryptionProofs().getElementSize()),
				"The verifiable decryptions must all have the same element size.");
		checkArgument(allEqual(precedingVerifiableDecryptedVotes.stream(), d -> d.getDecryptionProofs().size()),
				"The verifiable decryptions proofs must all have the same size.");

		// Cross size checks.
		checkArgument(precedingVerifiableShuffledVotes.size() == precedingVerifiableDecryptedVotes.size(),
				"The verifiable shuffles and verifiable decryptions lists must have the same size.");
		checkArgument(initialCiphertexts.size() == precedingVerifiableShuffledVotes.get(0).shuffledCiphertexts().size(),
				"The initial ciphertexts vector and verifiable shuffles ciphertexts vector must have the same size.");
		checkArgument(initialCiphertexts.getElementSize() == precedingVerifiableShuffledVotes.get(0).shuffledCiphertexts().getElementSize(),
				"The initial ciphertexts and verifiable shuffles ciphertexts must have the same element size.");
		checkArgument(initialCiphertexts.size() == precedingVerifiableDecryptedVotes.get(0).getCiphertexts().size(),
				"The initial ciphertexts vector and verifiable decryptions ciphertexts vector must have the same size.");
		checkArgument(initialCiphertexts.getElementSize() == precedingVerifiableDecryptedVotes.get(0).getCiphertexts().getElementSize(),
				"The initial ciphertexts and verifiable decryptions ciphertexts must have the same element size.");
		checkArgument(initialCiphertexts.getElementSize() <= electionPublicKey.size(),
				"The initial ciphertexts must not have more elements than the election public key.");
		checkArgument(electionPublicKey.size() == electoralBoardPublicKey.size(),
				"The election public key and the electoral board public key must have the same size.");
		checkArgument(electionPublicKey.size() <= ccmElectionPublicKeys.getElementSize(),
				"The election public key must not have more elements than the CCM election public keys.");
		checkArgument(ccmElectionPublicKeys.size() == 4, "There must be exactly 4 CCM election public keys.");

		// Cross group checks.
		checkArgument(initialCiphertexts.getGroup().equals(precedingVerifiableShuffledVotes.get(0).shuffledCiphertexts().getGroup()),
				"The initial ciphertexts and verifiable shuffles must have the same group.");
		checkArgument(initialCiphertexts.getGroup().equals(precedingVerifiableDecryptedVotes.get(0).getGroup()),
				"The initial ciphertexts and verifiable decryptions must have the same group.");
		checkArgument(initialCiphertexts.getGroup().equals(electionPublicKey.getGroup()),
				"The initial ciphertexts and election public key must have the same group.");
		checkArgument(initialCiphertexts.getGroup().equals(ccmElectionPublicKeys.getGroup()),
				"The initial ciphertexts and CCM election public keys must have the same group.");
		checkArgument(initialCiphertexts.getGroup().equals(electoralBoardPublicKey.getGroup()),
				"The initial ciphertexts and electoral board public key must have the same group.");

		// Check election public key
		final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> publicKeys = Streams.concat(
				ccmElectionPublicKeys.stream()
						.map(ccmElectionPublicKey ->
								new ElGamalMultiRecipientPublicKey(
										ccmElectionPublicKey.getKeyElements().subList(0, electoralBoardPublicKey.size()))),
				Stream.of(electoralBoardPublicKey)).collect(GroupVector.toGroupVector());

		checkArgument(electionPublicKey.equals(ElGamalFactory.createElGamal().combinePublicKeys(publicKeys)),
				"Multiplication of the ccmElectionPublicKeys times the electoralBoardPublicKey must equal the electionPublicKey.");

	}

}
