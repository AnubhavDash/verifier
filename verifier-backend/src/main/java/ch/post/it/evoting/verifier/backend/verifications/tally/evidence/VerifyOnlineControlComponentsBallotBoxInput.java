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
import static com.google.common.base.Preconditions.checkState;

import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.PlaintextEqualityProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.evotinglibraries.domain.common.EncryptedVerifiableVote;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;

/**
 * Regroups the input values needed by the VerifyOnlineControlComponentsBallotBox algorithm.
 *
 * <ul>
 *     <li>Control Component's lists:</li>
 *     <ul>
 *         <li>vc<sub>1</sub>, the control component’s list of confirmed verification card IDs. Not null.</li>
 *         <li>E1<sub>1</sub>, the control component’s list of encrypted, confirmed votes. Not null.</li>
 *         <li>E1<sub>1</sub>_tilde, the control component’s list of exponentiated, encrypted, confirmed votes. Not null.</li>
 *         <li>E2<sub>1</sub>, the control component’s list of encrypted, partial Choice Return Codes. Not null.</li>
 *         <li>pi<sub>exp,1</sub>, the control component’s list of exponentiation proofs. Not null.</li>
 *         <li>pi<sub>EqEnc,1</sub>, the control component’s list of plaintext equality proofs. Not null.</li>
 *     </ul>
 *     <li>Preceding shuffle:</li>
 *     <ul>
 *         <li>{c<sub>mix,j</sub>}<sup>4</sup><sub>j=1</sub>, the preceding shuffled votes. Not null.</li>
 *         <li>{pi<sub>mix,j</sub>}<sup>4</sup><sub>j=1</sub>, the preceding shuffle proofs. Not null.</li>
 *     </ul>
 *     <li>Preceding partial decryptions:</li>
 *     <ul>
 *         <li>{c<sub>dec,j</sub>}<sup>4</sup><sub>j=1</sub>, the preceding partially decrypted votes. Not null.</li>
 *         <li>{pi<sub>dec,j</sub>}<sup>4</sup><sub>j=1</sub>, the preceding decryption proofs. Not null.</li>
 *     </ul>
 *     <li>vc_vector, the vector of verification card ids. Not null and must contain only valid UUIDs.</li>
 *     <li>K_vector, the verification card public keys. Not null.</li>
 * </ul>
 */
public class VerifyOnlineControlComponentsBallotBoxInput {

	private final List<EncryptedVerifiableVote> controlComponentsLists;
	private final List<VerifiableShuffle> precedingShuffle;
	private final List<VerifiableDecryptions> precedingPartialDecryptions;
	private final List<String> verificationCardIds;
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> verificationCardPublicKeys;

	public VerifyOnlineControlComponentsBallotBoxInput(final ControlComponentBallotBoxPayload firstControlComponentBallotBox,
			final List<ControlComponentShufflePayload> controlComponentShuffles,
			final SetupComponentTallyDataPayload setupComponentTallyDataPayload) {
		checkNotNull(setupComponentTallyDataPayload);
		verifyConsistency(checkNotNull(firstControlComponentBallotBox));
		final List<ControlComponentShufflePayload> controlComponentShufflesCopy = checkNotNull(controlComponentShuffles).stream()
				.map(Preconditions::checkNotNull)
				.toList();
		checkArgument(controlComponentShufflesCopy.size() == NODE_IDS.size());
		verifyConsistency(controlComponentShufflesCopy);

		// id consistency checks.
		checkState(
				allEqual(Stream.of(firstControlComponentBallotBox.getEncryptionGroup(),
								controlComponentShufflesCopy.get(0).getEncryptionGroup(),
								setupComponentTallyDataPayload.getEncryptionGroup()),
						Function.identity()),
				"The first control component ballot box payload, the control component shuffle payloads and the setup component tally data payload must have the same group.");
		checkState(
				allEqual(Stream.of(firstControlComponentBallotBox.getElectionEventId(),
								controlComponentShufflesCopy.get(0).getElectionEventId(),
								setupComponentTallyDataPayload.getElectionEventId()),
						Function.identity()),
				"The first control component ballot box payload, the control component shuffle payloads and the setup component tally data payload must have the same election event id.");
		final String ballotBoxId = firstControlComponentBallotBox.getBallotBoxId();
		checkState(ballotBoxId.equals(controlComponentShufflesCopy.get(0).getBallotBoxId()),
				"The The first control component ballot box payload and the control component shuffle payloads must have the same ballot box id.");

		this.controlComponentsLists = firstControlComponentBallotBox.getConfirmedEncryptedVotes();
		this.precedingShuffle = controlComponentShufflesCopy.stream().parallel()
				.map(ControlComponentShufflePayload::getVerifiableShuffle)
				.toList();
		this.precedingPartialDecryptions = controlComponentShufflesCopy.stream().parallel()
				.map(ControlComponentShufflePayload::getVerifiableDecryptions)
				.toList();
		this.verificationCardIds = setupComponentTallyDataPayload.getVerificationCardIds();
		this.verificationCardPublicKeys = setupComponentTallyDataPayload.getVerificationCardPublicKeys();

		// Cross-checks.
		checkArgument(controlComponentsLists.isEmpty() || controlComponentsLists.get(0).encryptedVote().size() == precedingShuffle.get(0)
						.shuffledCiphertexts().getElementSize(),
				"The size of the encrypted, confirmed vote must be equal to the element size of the shuffled and the partially decrypted votes.");
		checkArgument(setupComponentTallyDataPayload.getVerificationCardPublicKeys().size() >= controlComponentsLists.size(),
				"The number of verification card public keys must be greater or equal to the number of confirmed votes. [N_E: %s, N_C: %s]",
				setupComponentTallyDataPayload.getVerificationCardPublicKeys().size(), controlComponentsLists.size());
	}

	public List<EncryptedVerifiableVote> getControlComponentsLists() {
		return controlComponentsLists;
	}

	public List<VerifiableShuffle> getPrecedingShuffle() {
		return precedingShuffle;
	}

	public List<VerifiableDecryptions> getPrecedingPartialDecryptions() {
		return precedingPartialDecryptions;
	}

	public List<String> getVerificationCardIds() {
		return verificationCardIds;
	}

	public GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> getVerificationCardPublicKeys() {
		return verificationCardPublicKeys;
	}

	public GqGroup getEncryptionGroup() {
		return verificationCardPublicKeys.getGroup();
	}

	private void verifyConsistency(final ControlComponentBallotBoxPayload firstControlComponentBallotBox) {
		checkArgument(firstControlComponentBallotBox.getNodeId() == 1, "Wrong control component ballot box payload.");
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
	}

	private void verifyConsistency(final List<ControlComponentShufflePayload> controlComponentShuffles) {
		checkState(allEqual(controlComponentShuffles.stream(), ControlComponentShufflePayload::getEncryptionGroup),
				"All control component shuffle payloads must have the same group.");
		checkState(allEqual(controlComponentShuffles.stream(), ControlComponentShufflePayload::getElectionEventId),
				"All control component shuffle payloads must have the same election event id.");
		checkState(allEqual(controlComponentShuffles.stream(), ControlComponentShufflePayload::getBallotBoxId),
				"All control component shuffle payloads must have the same ballot box id.");
		checkState(NODE_IDS.size() == controlComponentShuffles.size(), "Wrong number of control component shuffle payloads.");
		final List<Integer> shufflePayloadsNodeIds = controlComponentShuffles.stream()
				.map(ControlComponentShufflePayload::getNodeId)
				.toList();
		checkState(NODE_IDS.equals(new HashSet<>(shufflePayloadsNodeIds)), "The control component shuffle payloads contain invalid node ids.");
		final List<VerifiableShuffle> verifiableShuffles = controlComponentShuffles.stream()
				.map(ControlComponentShufflePayload::getVerifiableShuffle)
				.toList();
		final List<VerifiableDecryptions> verifiableDecryptions = controlComponentShuffles.stream()
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
	}
}
