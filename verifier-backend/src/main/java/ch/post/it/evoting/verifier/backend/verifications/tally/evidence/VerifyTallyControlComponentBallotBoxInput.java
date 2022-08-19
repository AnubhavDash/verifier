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

import java.util.stream.Stream;

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

public class VerifyTallyControlComponentBallotBoxInput {

	private final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes;
	private final VerifiableShuffle verifiableShuffle;
	private final VerifiablePlaintextDecryption verifiablePlaintextDecryption;
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;

	private VerifyTallyControlComponentBallotBoxInput(final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes,
			final VerifiableShuffle verifiableShuffle, final VerifiablePlaintextDecryption verifiablePlaintextDecryption,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions) {
		this.previousPartiallyDecryptedVotes = previousPartiallyDecryptedVotes;
		this.verifiableShuffle = verifiableShuffle;
		this.verifiablePlaintextDecryption = verifiablePlaintextDecryption;
		this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
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
		final GqGroup group = verifiablePlaintextDecryption.getGroup();
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> decryptedVotes = verifiablePlaintextDecryption.getDecryptedVotes();
		final GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> ciphertexts = decryptedVotes.stream()
				.map(vote -> ElGamalMultiRecipientCiphertext.create(group.getIdentity(), vote.getElements()))
				.collect(GroupVector.toGroupVector());
		final GroupVector<DecryptionProof, ZqGroup> decryptionProofs = verifiablePlaintextDecryption.getDecryptionProofs();

		return new VerifiableDecryptions(ciphertexts, decryptionProofs);
	}

	public GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> getSelectedEncodedVotingOptions() {
		return selectedEncodedVotingOptions;
	}

	public static class Builder {
		private GroupVector<ElGamalMultiRecipientCiphertext, GqGroup> previousPartiallyDecryptedVotes;
		private VerifiableShuffle verifiableShuffle;
		private VerifiablePlaintextDecryption verifiablePlaintextDecryption;
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

		public Builder setSelectedEncodedVotingOptions(
				final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions) {
			this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
			return this;
		}

		public VerifyTallyControlComponentBallotBoxInput build() {
			checkNotNull(previousPartiallyDecryptedVotes);
			checkNotNull(verifiableShuffle);
			checkNotNull(verifiablePlaintextDecryption);
			checkNotNull(selectedEncodedVotingOptions);

			checkArgument(allEqual(Stream.of(previousPartiallyDecryptedVotes, verifiableShuffle.shuffledCiphertexts()), GroupVector::getGroup),
					"All input must have the same group.");
			checkArgument(verifiablePlaintextDecryption.getGroup().equals(previousPartiallyDecryptedVotes.getGroup()));

			if (!selectedEncodedVotingOptions.isEmpty()) {
				checkArgument(verifiablePlaintextDecryption.getGroup().equals(selectedEncodedVotingOptions.getGroup()));
			}

			checkArgument(allEqual(Stream.of(previousPartiallyDecryptedVotes, verifiableShuffle.shuffledCiphertexts(),
							verifiablePlaintextDecryption.getDecryptedVotes(), verifiablePlaintextDecryption.getDecryptionProofs()), GroupVector::size),
					"All input must have the same size.");

			checkArgument(allEqual(Stream.of(previousPartiallyDecryptedVotes, verifiableShuffle.shuffledCiphertexts(),
							verifiablePlaintextDecryption.getDecryptedVotes(), verifiablePlaintextDecryption.getDecryptionProofs()),
					GroupVector::getElementSize), "All input must have the same number of elements.");

			return new VerifyTallyControlComponentBallotBoxInput(previousPartiallyDecryptedVotes, verifiableShuffle, verifiablePlaintextDecryption,
					selectedEncodedVotingOptions);
		}
	}
}
