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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static ch.post.it.evoting.evotinglibraries.domain.ControlComponentConstants.NODE_IDS;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.PlaintextEqualityProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.evotinglibraries.domain.common.EncryptedVerifiableVote;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;

/**
 * Regroups the input values needed by the VerifyOnlineControlComponentsBallotBox algorithm.
 *
 * <ul>
 *     <li>KMap, the key-value map of the verification card public keys. Non-null.</li>
 *     <li>the first Control Component Ballot Box. Non-null. Contains:</li>
 *     <ul>
 *         <li>vc<sub>1</sub>, the control component’s list of confirmed verification card IDs.</li>
 *         <li>E1<sub>1</sub>, the control component’s list of encrypted, confirmed votes.</li>
 *         <li>E1<sub>1</sub>_tilde, the control component’s list of exponentiated, encrypted, confirmed votes.</li>
 *         <li>E2<sub>1</sub>, the control component’s list of encrypted, partial Choice Return Codes.</li>
 *         <li>pi<sub>exp,1</sub>, the control component’s list of exponentiation proofs.</li>
 *         <li>pi<sub>EqEnc,1</sub>, the control component’s list of plaintext equality proofs.</li>
 *     </ul>
 *     <li>the Control Component Shuffles. Non-null. Contains:</li>
 *     <ul>
 *         <li>{c<sub>mix,j</sub>}<sup>4</sup><sub>j=1</sub>, the preceding shuffled votes.</li>
 *         <li>{pi<sub>mix,j</sub>}<sup>4</sup><sub>j=1</sub>, the preceding shuffle proofs.</li>
 *         <li>{c<sub>dec,j</sub>}<sup>4</sup><sub>j=1</sub>, the preceding partially decrypted votes.</li>
 *         <li>{pi<sub>dec,j</sub>}<sup>4</sup><sub>j=1</sub>, the preceding decryption proofs.</li>
 *     </ul>
 * </ul>
 */
public record VerifyOnlineControlComponentsBallotBoxInput(Map<String, ElGamalMultiRecipientPublicKey> verificationCardPublicKeyMap,
														  ControlComponentBallotBoxPayload firstControlComponentBallotBox,
														  List<ControlComponentShufflePayload> controlComponentShuffles) {

	public VerifyOnlineControlComponentsBallotBoxInput {
		verificationCardPublicKeyMap = Collections.unmodifiableMap(new LinkedHashMap<>(checkNotNull(verificationCardPublicKeyMap)));
		checkNotNull(firstControlComponentBallotBox);
		controlComponentShuffles = List.copyOf(checkNotNull(controlComponentShuffles));

		// First Control Component Ballot Box consistency checks.
		final List<EncryptedVerifiableVote> encryptedVerifiableVotes = firstControlComponentBallotBox.getConfirmedEncryptedVotes();
		checkArgument(
				allEqual(encryptedVerifiableVotes.stream().map(EncryptedVerifiableVote::encryptedVote), ElGamalMultiRecipientCiphertext::size),
				"All encrypted, confirmed votes must have the same size.");
		checkArgument(
				allEqual(Stream.concat(encryptedVerifiableVotes.stream()
								.map(EncryptedVerifiableVote::exponentiatedEncryptedVote)
								.map(ElGamalMultiRecipientCiphertext::size), Stream.of(1)),
						Function.identity()),
				"All exponentiated, encrypted, confirmed votes must have the same size and it must be equal to 1.");
		checkArgument(
				allEqual(encryptedVerifiableVotes.stream().map(EncryptedVerifiableVote::encryptedPartialChoiceReturnCodes),
						ElGamalMultiRecipientCiphertext::size),
				"All encrypted, partial Choice Return Codes must have the same size.");
		// The class ExponentiationProof ensures all exponentiation proofs have a size of 1.
		checkArgument(
				allEqual(Stream.concat(encryptedVerifiableVotes.stream()
								.map(EncryptedVerifiableVote::plaintextEqualityProof)
								.map(PlaintextEqualityProof::size), Stream.of(2)),
						Function.identity()),
				"All plaintext equality proofs must the same size and it must be equal to 2.");

		// Control Component Shuffles consistency checks.
		final List<VerifiableShuffle> verifiableShuffles = controlComponentShuffles.stream().parallel()
				.map(ControlComponentShufflePayload::getVerifiableShuffle)
				.toList();
		final List<VerifiableDecryptions> verifiableDecryptions = controlComponentShuffles.stream().parallel()
				.map(ControlComponentShufflePayload::getVerifiableDecryptions)
				.toList();
		checkArgument(verifiableShuffles.size() == NODE_IDS.size(), "There must be as many shuffle proofs as there are nodes. [nodeIdsSize: %s]",
				NODE_IDS.size());
		checkArgument(verifiableDecryptions.size() == NODE_IDS.size(),
				"There must be as many decryption proofs as there are nodes. [nodeIdsSize: %s]",
				NODE_IDS.size());
		checkArgument(
				allEqual(Stream.concat(verifiableShuffles.stream().map(VerifiableShuffle::shuffledCiphertexts),
								verifiableDecryptions.stream().map(VerifiableDecryptions::getCiphertexts)),
						GroupVector::size),
				"All preceding shuffled votes and preceding partially decrypted votes must have the same size.");
		checkArgument(
				allEqual(Stream.concat(verifiableShuffles.stream().map(VerifiableShuffle::shuffledCiphertexts),
								verifiableDecryptions.stream().map(VerifiableDecryptions::getCiphertexts)),
						GroupVector::getElementSize),
				"All preceding shuffled votes and preceding partially decrypted votes must have the same element size.");

		// Cross-checks.
		checkArgument(verificationCardPublicKeyMap.size() >= encryptedVerifiableVotes.size(),
				"The size of KMap must be greater or equal to the number of confirmed votes. [N_E: %s, N_C: %s]", verificationCardPublicKeyMap.size(),
				encryptedVerifiableVotes.size());
		checkArgument(encryptedVerifiableVotes.isEmpty() || encryptedVerifiableVotes.get(0).encryptedVote().size() == verifiableShuffles.get(0)
						.shuffledCiphertexts().getElementSize(),
				"The size of the encrypted, confirmed vote must be equal to the element size of the shuffled and the partially decrypted votes.");
	}
}
