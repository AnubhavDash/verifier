/*
 * (c) Copyright 2025 Swiss Post Ltd.
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
import java.util.stream.Stream;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.mixnet.ShuffleArgument;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
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
 *     <li>L<sub>votes</sub>, the list of decrypted votes. Non-null.</li>
 *     <li>L<sub>decodedVotes</sub>, the list of decoded votes. Non-null.</li>
 *     <li>L<sub>writeIns</sub>, the list of decoded write-ins. Non-null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentBallotBoxInput {

	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> partiallyDecryptedVotes;
	private final VerifiableShuffle tallyComponentsShuffle;
	private final VerifiablePlaintextDecryption verifiablePlaintextDecryption;
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> decryptedVotes;
	private final ImmutableList<ImmutableList<String>> decodedVotes;
	private final ImmutableList<ImmutableList<String>> decodedWriteIns;

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
		this.decryptedVotes = tallyComponentVotesPaylaod.getDecryptedVotes();
		this.decodedVotes = tallyComponentVotesPaylaod.getDecodedVotes();
		this.decodedWriteIns = tallyComponentVotesPaylaod.getDecodedWriteIns();

		// Cross-checks.
		checkArgument(allEqual(Stream.of(partiallyDecryptedVotes, tallyComponentsShuffle.shuffledCiphertexts(),
						verifiablePlaintextDecryption.getDecryptedVotes()), GroupVector::getGroup),
				"All input must have the same group.");
		if (!decryptedVotes.isEmpty()) {
			checkArgument(verifiablePlaintextDecryption.getGroup().equals(decryptedVotes.getGroup()),
					"The decrypted votes and verifiable plaintext decryption must have the same group.");
		}

		checkArgument(decryptedVotes.size() == decodedVotes.size(),
				"There must be as many decrypted votes as decoded votes.");
		checkArgument(decodedWriteIns.size() == decryptedVotes.size(),
				"There must be as many decoded write-in votes as decrypted votes.");

		checkArgument(allEqual(decodedVotes.stream(), ImmutableList::size),
				"All decoded votes must have the same size.");
		checkArgument(decodedVotes.isEmpty() || decryptedVotes.getElementSize() == decodedVotes.get(0).size(),
				"All decrypted votes and decoded votes must have the same size.");

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

	public GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> getDecryptedVotes() {
		return decryptedVotes;
	}

	public ImmutableList<ImmutableList<String>> getDecodedVotes() {
		return decodedVotes;
	}

	public ImmutableList<ImmutableList<String>> getDecodedWriteIns() {
		return decodedWriteIns;
	}
}
