/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.math.ZqGroup;
import ch.post.it.evoting.cryptoprimitives.mixnet.ShuffleArgument;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.DecryptionProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.VerifiablePlaintextDecryption;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;

/**
 * Regroups the input values needed by the VerifyTallyControlComponentBallotBox algorithm.
 *
 * <ul>
 *     <li>c<sub>dec,4</sub>, the last online control component’s partially decrypted votes. Non-null.</li>
 *     <li>Tally Component's shuffle:</li>
 *     <ul>
 *         <li>c<sub>mix,5</sub>, the tally component’s shuffled votes. Non-null.</li>
 *         <li>pi<sub>mix,5</sub>, the tally component’s shuffle proofs. Non-null.</li>
 *     </ul>
 *     <li>Verifiable plaintext decryption:</li>
 *     <ul>
 *         <li>m, the decrypted votes. Non-null.</li>
 *         <li>pi<sub>dec,5</sub>, the decryption proofs. Non-null.</li>
 *     </ul>
 *     <li>L<sub>votes</sub>, the list of all selected encoded voting options. Non-null.</li>
 *     <li>L<sub>decodedVotes</sub>, the list of all selected decoded voting options. Non-null.</li>
 *     <li>L<sub>writeIns</sub>, the list of all selected decoded write-in votes. Non-null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentBallotBoxInput {

	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes;
	private final VerifiableShuffle tallyComponentsShuffle;
	private final VerifiablePlaintextDecryption verifiablePlaintextDecryption;
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;
	private final ImmutableList<ImmutableList<String>> selectedDecodedVotingOptions;
	private final ImmutableList<ImmutableList<String>> selectedDecodedWriteInVotes;

	public VerifyTallyControlComponentBallotBoxInput(final ControlComponentShufflePayload lastControlComponentShufflePayload, final
	TallyComponentShufflePayload tallyComponentShufflePayload, final TallyComponentVotesPayload tallyComponentVotesPaylaod) {
		checkNotNull(lastControlComponentShufflePayload);
		checkNotNull(tallyComponentShufflePayload);
		checkNotNull(tallyComponentVotesPaylaod);

		// id consistency checks.
		checkState(
				allEqual(Stream.of(lastControlComponentShufflePayload.getEncryptionGroup(),
								tallyComponentShufflePayload.getEncryptionGroup(),
								tallyComponentVotesPaylaod.getEncryptionGroup()),
						Function.identity()),
				"The last control component shuffle, the tally component shuffle and tally component votes payloads must have the same group.");
		checkState(
				allEqual(Stream.of(lastControlComponentShufflePayload.getElectionEventId(),
								tallyComponentShufflePayload.getElectionEventId(),
								tallyComponentVotesPaylaod.getElectionEventId()),
						Function.identity()),
				"The last control component shuffle, the tally component shuffle and tally component votes payloads must have the same election event id.");
		checkState(
				allEqual(Stream.of(lastControlComponentShufflePayload.getBallotBoxId(),
								tallyComponentShufflePayload.getBallotBoxId(),
								tallyComponentVotesPaylaod.getBallotBoxId()),
						Function.identity()),
				"The last control component shuffle, the tally component shuffle and tally component votes payloads must have the same ballot box id.");

		this.partiallyDecryptedVotes = lastControlComponentShufflePayload.getVerifiableDecryptions().getCiphertexts();
		this.tallyComponentsShuffle = tallyComponentShufflePayload.getVerifiableShuffle();
		this.verifiablePlaintextDecryption = tallyComponentShufflePayload.getVerifiablePlaintextDecryption();
		this.selectedEncodedVotingOptions = tallyComponentVotesPaylaod.getVotes();
		this.selectedDecodedVotingOptions = tallyComponentVotesPaylaod.getActualSelectedVotingOptions();
		this.selectedDecodedWriteInVotes = tallyComponentVotesPaylaod.getDecodedWriteInVotes();

		// Cross-checks.
		checkArgument(allEqual(Stream.of(partiallyDecryptedVotes, tallyComponentsShuffle.shuffledCiphertexts(),
						verifiablePlaintextDecryption.getDecryptedVotes()), GroupVector::getGroup),
				"All input must have the same group.");
		if (!selectedEncodedVotingOptions.isEmpty()) {
			checkArgument(verifiablePlaintextDecryption.getGroup().equals(selectedEncodedVotingOptions.getGroup()),
					"The selected encoded voting options and verifiable plaintext decryption must have the same group.");
		}

		checkArgument(selectedEncodedVotingOptions.size() == selectedDecodedVotingOptions.size(),
				"There must be as many encoded as decoded voting options.");
		checkArgument(selectedDecodedWriteInVotes.size() == selectedEncodedVotingOptions.size(),
				"There must be as many decoded write-in votes as encoded voting options.");

		checkArgument(allEqual(selectedDecodedVotingOptions.stream(), ImmutableList::size),
				"All selected decoded voting options must have the same size.");
		checkArgument(selectedDecodedVotingOptions.isEmpty()
						|| selectedEncodedVotingOptions.getElementSize() == selectedDecodedVotingOptions.get(0).size(),
				"All selected encoded and decoded voting options must have the same size.");

		checkArgument(allEqual(Stream.of(partiallyDecryptedVotes, tallyComponentsShuffle.shuffledCiphertexts(),
						verifiablePlaintextDecryption.getDecryptedVotes()),
				GroupVector::size), "All input must have the same size.");

		checkArgument(allEqual(Stream.of(partiallyDecryptedVotes, tallyComponentsShuffle.shuffledCiphertexts(),
						verifiablePlaintextDecryption.getDecryptedVotes()),
				GroupVector::getElementSize), "All input must have the same number of elements.");
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getPreviousPartiallyDecryptedVotes() {
		return partiallyDecryptedVotes;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getShuffledVotes() {
		return tallyComponentsShuffle.shuffledCiphertexts();
	}

	public ShuffleArgument getShuffleProofs() {
		return tallyComponentsShuffle.shuffleArgument();
	}

	public VerifiablePlaintextDecryption getVerifiablePlaintextDecryption() {
		return verifiablePlaintextDecryption;
	}

	public VerifiableDecryptions getVerifiableDecryptions() {
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> decryptedVotes = verifiablePlaintextDecryption.getDecryptedVotes();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> ciphertexts = IntStream.range(0, decryptedVotes.size())
				.mapToObj(i -> ElGamalMultiRecipientCiphertext.create(tallyComponentsShuffle.shuffledCiphertexts().get(i).getGamma(),
						decryptedVotes.get(i).getElements()))
				.collect(GroupVector.toGroupVector());
		final GroupVector<DecryptionProof, ZqGroup> decryptionProofs = verifiablePlaintextDecryption.getDecryptionProofs();

		return new VerifiableDecryptions(ciphertexts, decryptionProofs);
	}

	public GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> getSelectedEncodedVotingOptions() {
		return selectedEncodedVotingOptions;
	}

	public ImmutableList<ImmutableList<String>> getSelectedDecodedVotingOptions() {
		return selectedDecodedVotingOptions;
	}

	public ImmutableList<ImmutableList<String>> getSelectedDecodedWriteInVotes() {
		return selectedDecodedWriteInVotes;
	}
}
