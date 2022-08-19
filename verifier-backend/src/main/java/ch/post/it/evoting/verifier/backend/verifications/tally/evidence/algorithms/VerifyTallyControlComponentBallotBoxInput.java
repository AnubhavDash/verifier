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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence.algorithms;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.VerifiablePlaintextDecryption;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientCiphertext;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;

public class VerifyTallyControlComponentBallotBoxInput {

	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes;
	private final VerifiableShuffle verifiableShuffle;
	private final VerifiablePlaintextDecryption verifiablePlaintextDecryption;
	private final ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
	private final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions;
	private final int numberOfSelectableVotingOptions;
	private final int numberOfAllowedWriteIns;
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;

	private VerifyTallyControlComponentBallotBoxInput(final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes,
			final VerifiableShuffle verifiableShuffle, final VerifiablePlaintextDecryption verifiablePlaintextDecryption,
			final ElGamalMultiRecipientPublicKey electoralBoardPublicKey, final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions,
			final int numberOfSelectableVotingOptions, final int numberOfAllowedWriteIns,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions) {
		this.previousPartiallyDecryptedVotes = previousPartiallyDecryptedVotes;
		this.verifiableShuffle = verifiableShuffle;
		this.verifiablePlaintextDecryption = verifiablePlaintextDecryption;
		this.electoralBoardPublicKey = electoralBoardPublicKey;
		this.encodedVotingOptions = encodedVotingOptions;
		this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
		this.numberOfAllowedWriteIns = numberOfAllowedWriteIns;
		this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
	}

	public GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> getPreviousPartiallyDecryptedVotes() {
		return previousPartiallyDecryptedVotes;
	}

	public VerifiableShuffle getVerifiableShuffle() {
		return verifiableShuffle;
	}

	public VerifiablePlaintextDecryption getVerifiablePlaintextDecryption() {
		return verifiablePlaintextDecryption;
	}

	public ElGamalMultiRecipientPublicKey getElectoralBoardPublicKey() {
		return electoralBoardPublicKey;
	}

	public GroupVector<PrimeGqElement, GqGroup> getEncodedVotingOptions() {
		return encodedVotingOptions;
	}

	public int getNumberOfSelectableVotingOptions() {
		return numberOfSelectableVotingOptions;
	}

	public int getNumberOfAllowedWriteIns() {
		return numberOfAllowedWriteIns;
	}

	public GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> getSelectedEncodedVotingOptions() {
		return selectedEncodedVotingOptions;
	}

	public static class Builder {
		private GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes;
		private VerifiableShuffle verifiableShuffle;
		private VerifiablePlaintextDecryption verifiablePlaintextDecryption;
		private ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
		private GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions;
		private int numberOfSelectableVotingOptions;
		private int numberOfAllowedWriteIns;
		private GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;

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

		public Builder setElectoralBoardPublicKey(final ElGamalMultiRecipientPublicKey electoralBoardPublicKey) {
			this.electoralBoardPublicKey = electoralBoardPublicKey;
			return this;
		}

		public Builder setEncodedVotingOptions(final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions) {
			this.encodedVotingOptions = encodedVotingOptions;
			return this;
		}

		public Builder setNumberOfSelectableVotingOptions(final int numberOfSelectableVotingOptions) {
			this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
			return this;
		}

		public Builder setNumberOfAllowedWriteIns(final int numberOfAllowedWriteIns) {
			this.numberOfAllowedWriteIns = numberOfAllowedWriteIns;
			return this;
		}

		public Builder setSelectedEncodedVotingOptions(
				final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions) {
			this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
			return this;
		}

		public VerifyTallyControlComponentBallotBoxInput build() {
			checkNotNull(previousPartiallyDecryptedVotes);
			checkNotNull(verifiableShuffle);
			checkNotNull(verifiablePlaintextDecryption);
			checkNotNull(electoralBoardPublicKey);
			checkNotNull(encodedVotingOptions);
			checkNotNull(selectedEncodedVotingOptions);

			checkArgument(verifiableShuffle.shuffledCiphertexts().size() == verifiablePlaintextDecryption.getDecryptedVotes().size());
			checkArgument(verifiableShuffle.shuffledCiphertexts().getGroup().equals(verifiablePlaintextDecryption.getGroup()));
			checkArgument(verifiablePlaintextDecryption.getGroup().equals(electoralBoardPublicKey.getGroup()));

			checkArgument(1 <= numberOfSelectableVotingOptions && numberOfSelectableVotingOptions <= 120,
					"The number of selectable voting options must be within the allowed bounds.");
			checkArgument(numberOfAllowedWriteIns >= 0, "The number of allowed write-ins cannot be negative");
			checkArgument(selectedEncodedVotingOptions.stream().allMatch(sev -> encodedVotingOptions.containsAll(sev)),
					"All selected voting options must be contained in the list of voting options");

			return new VerifyTallyControlComponentBallotBoxInput(previousPartiallyDecryptedVotes, verifiableShuffle, verifiablePlaintextDecryption,
					electoralBoardPublicKey, encodedVotingOptions, numberOfSelectableVotingOptions, numberOfAllowedWriteIns,
					selectedEncodedVotingOptions);
		}
	}
}
