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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.VerifiablePlaintextDecryption;
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

/**
 * Regroups the input values needed by the VerifyTallyControlComponentBallotBox algorithm.
 *
 * <ul>
 * <li>c<sub>dec, 4</sub>, the last online control component’s partially decrypted votes. Non-null.</li>
 * <li>c<sub>mix, 5</sub>, the tally component’s shuffled votes. Non-null.</li>
 * <li>pi<sub>mix, 5</sub>, the tally component’s shuffle proofs. Non-null.</li>
 * <li>m, the decrypted votes. Non-null.</li>
 * <li>pi<sub>dec, 5</sub>, the decryption proofs. Non-null.</li>
 * <li>L<sub>votes</sub>, the list of all selected encoded voting options. Non-null.</li>
 * <li>L<sub>decodedVotes</sub>, the list of all selected decoded voting options. Non-null.</li>
 * <li>L<sub>writeIns</sub>, the list of all selected decoded write-in votes. Non-null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentBallotBoxInput {

	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes;
	private final VerifiableShuffle verifiableShuffle;
	private final VerifiablePlaintextDecryption verifiablePlaintextDecryption;
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;
	private final List<List<String>> selectedDecodedVotingOptions;
	private final List<List<String>> selectedDecodedWriteInVotes;

	private VerifyTallyControlComponentBallotBoxInput(final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes,
			final VerifiableShuffle verifiableShuffle, final VerifiablePlaintextDecryption verifiablePlaintextDecryption,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions,
			final List<List<String>> selectedDecodedVotingOptions, final List<List<String>> selectedDecodedWriteInVotes) {
		this.previousPartiallyDecryptedVotes = previousPartiallyDecryptedVotes;
		this.verifiableShuffle = verifiableShuffle;
		this.verifiablePlaintextDecryption = verifiablePlaintextDecryption;
		this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
		this.selectedDecodedVotingOptions = selectedDecodedVotingOptions;
		this.selectedDecodedWriteInVotes = selectedDecodedWriteInVotes;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getPreviousPartiallyDecryptedVotes() {
		return previousPartiallyDecryptedVotes;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getShuffledVotes() {
		return verifiableShuffle.shuffledCiphertexts();
	}

	public ShuffleArgument getShuffleProofs() {
		return verifiableShuffle.shuffleArgument();
	}

	public VerifiablePlaintextDecryption getVerifiablePlaintextDecryption() {
		return verifiablePlaintextDecryption;
	}

	public VerifiableDecryptions getVerifiableDecryptions() {
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> decryptedVotes = verifiablePlaintextDecryption.getDecryptedVotes();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> ciphertexts = IntStream.range(0, decryptedVotes.size())
				.mapToObj(i -> ElGamalMultiRecipientCiphertext.create(verifiableShuffle.shuffledCiphertexts().get(i).getGamma(),
						decryptedVotes.get(i).getElements()))
				.collect(GroupVector.toGroupVector());
		final GroupVector<DecryptionProof, ZqGroup> decryptionProofs = verifiablePlaintextDecryption.getDecryptionProofs();

		return new VerifiableDecryptions(ciphertexts, decryptionProofs);
	}

	public GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> getSelectedEncodedVotingOptions() {
		return selectedEncodedVotingOptions;
	}

	public List<List<String>> getSelectedDecodedVotingOptions() {
		return selectedDecodedVotingOptions.stream()
				.map(List::copyOf)
				.toList();
	}

	public List<List<String>> getSelectedDecodedWriteInVotes() {
		return selectedDecodedWriteInVotes.stream()
				.map(List::copyOf)
				.toList();
	}

	public static class Builder {
		private GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes;
		private VerifiableShuffle verifiableShuffle;
		private VerifiablePlaintextDecryption verifiablePlaintextDecryption;
		private GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;
		private List<List<String>> selectedDecodedVotingOptions;
		private List<List<String>> selectedDecodedWriteInVotes;

		public Builder setPreviousPartiallyDecryptedVotes(
				final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes) {
			this.previousPartiallyDecryptedVotes = previousPartiallyDecryptedVotes;
			return this;
		}

		public Builder setVerifiableShuffle(final VerifiableShuffle verifiableShuffle) {
			this.verifiableShuffle = verifiableShuffle;
			return this;
		}

		public Builder setVerifiablePlaintextDecryption(final VerifiablePlaintextDecryption verifiablePlaintextDecryption) {
			this.verifiablePlaintextDecryption = verifiablePlaintextDecryption;
			return this;
		}

		public Builder setSelectedEncodedVotingOptions(
				final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions) {
			this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
			return this;
		}

		public Builder setSelectedDecodedVotingOptions(final List<List<String>> selectedDecodedVotingOptions) {
			checkNotNull(selectedDecodedVotingOptions);
			final List<List<String>> selectedDecodedVotingOptionsCopy = List.copyOf(selectedDecodedVotingOptions);
			selectedDecodedVotingOptionsCopy.forEach(Preconditions::checkNotNull);

			this.selectedDecodedVotingOptions = selectedDecodedVotingOptionsCopy.stream()
					.map(List::copyOf)
					.toList();
			return this;
		}

		public Builder setSelectedDecodedWriteInVotes(final List<List<String>> selectedDecodedWriteInVotes) {
			checkNotNull(selectedDecodedWriteInVotes);
			final List<List<String>> selectedDecodedWriteInVotesCopy = List.copyOf(selectedDecodedWriteInVotes);
			selectedDecodedWriteInVotesCopy.forEach(Preconditions::checkNotNull);

			this.selectedDecodedWriteInVotes = selectedDecodedWriteInVotesCopy.stream()
					.map(List::copyOf)
					.toList();
			return this;
		}

		/**
		 * @throws NullPointerException     if any parameter is null.
		 * @throws IllegalArgumentException if
		 *                                  <ul>
		 *                                      <li>not all input have the same group.</li>
		 *                                      <li>there is a different number of selected encoded and decoded voting options.</li>
		 *                                      <li>there is a different number of selected write-in votes and selected encoded voting options.</li>
		 *                                      <li>not all input have the same size.</li>
		 *                                      <li>not all input have the same number of elements.</li>
		 *                                  </ul>
		 */
		public VerifyTallyControlComponentBallotBoxInput build() {
			checkNotNull(previousPartiallyDecryptedVotes);
			checkNotNull(verifiableShuffle);
			checkNotNull(verifiablePlaintextDecryption);
			checkNotNull(selectedEncodedVotingOptions);
			checkNotNull(selectedDecodedVotingOptions);
			checkNotNull(selectedDecodedWriteInVotes);

			checkArgument(allEqual(Stream.of(previousPartiallyDecryptedVotes, verifiableShuffle.shuffledCiphertexts(),
							verifiablePlaintextDecryption.getDecryptedVotes()), GroupVector::getGroup),
					"All input must have the same group.");
			if (!selectedEncodedVotingOptions.isEmpty()) {
				checkArgument(verifiablePlaintextDecryption.getGroup().equals(selectedEncodedVotingOptions.getGroup()));
			}

			checkArgument(selectedEncodedVotingOptions.size() == selectedDecodedVotingOptions.size(),
					"There must be as many encoded as decoded voting options.");
			checkArgument(selectedDecodedWriteInVotes.size() == selectedEncodedVotingOptions.size(),
					"There must be as many decoded write-in votes as encoded voting options.");

			checkArgument(allEqual(Stream.of(previousPartiallyDecryptedVotes, verifiableShuffle.shuffledCiphertexts(),
							verifiablePlaintextDecryption.getDecryptedVotes(), verifiablePlaintextDecryption.getDecryptionProofs()), GroupVector::size),
					"All input must have the same size.");

			checkArgument(allEqual(Stream.of(previousPartiallyDecryptedVotes, verifiableShuffle.shuffledCiphertexts(),
							verifiablePlaintextDecryption.getDecryptedVotes(), verifiablePlaintextDecryption.getDecryptionProofs()),
					GroupVector::getElementSize), "All input must have the same number of elements.");

			return new VerifyTallyControlComponentBallotBoxInput(previousPartiallyDecryptedVotes, verifiableShuffle, verifiablePlaintextDecryption,
					selectedEncodedVotingOptions, selectedDecodedVotingOptions, selectedDecodedWriteInVotes);
		}
	}
}
