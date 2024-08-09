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

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Regroups the input values needed by the VerifyProcessPlaintexts algorithm.
 *
 * <ul>
 *     <li>m, the list of plaintext votes. Non-null.</li>
 *     <li>L<sub>votes</sub>, the list of all selected encoded voting options. Non-null.</li>
 *     <li>L<sub>decodedVotes</sub>, the list of all selected decoded voting options. Non-null.</li>
 *     <li>L<sub>writeIns</sub>, the list of all selected decoded write-in votes. Non-null.</li>
 * </ul>
 */
public class VerifyProcessPlaintextsInput {

	private final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes;
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;
	private final ImmutableList<ImmutableList<String>> selectedDecodedVotingOptions;
	private final ImmutableList<ImmutableList<String>> selectedDecodedWriteInVotes;

	private VerifyProcessPlaintextsInput(final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions,
			final ImmutableList<ImmutableList<String>> selectedDecodedVotingOptions,
			final ImmutableList<ImmutableList<String>> selectedDecodedWriteInVotes) {
		this.plaintextVotes = plaintextVotes;
		this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
		this.selectedDecodedVotingOptions = selectedDecodedVotingOptions;
		this.selectedDecodedWriteInVotes = selectedDecodedWriteInVotes;
	}

	public GroupVector<ElGamalMultiRecipientMessage, GqGroup> getPlaintextVotes() {
		return plaintextVotes;
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

	public static class Builder {

		private GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes;
		private GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;
		private ImmutableList<ImmutableList<String>> selectedDecodedVotingOptions;
		private ImmutableList<ImmutableList<String>> selectedDecodedWriteInVotes;

		public Builder setPlaintextVotes(
				final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes) {
			this.plaintextVotes = plaintextVotes;
			return this;
		}

		public Builder setSelectedEncodedVotingOptions(
				final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions) {
			this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
			return this;
		}

		public Builder setSelectedDecodedVotingOptions(final ImmutableList<ImmutableList<String>> selectedDecodedVotingOptions) {
			this.selectedDecodedVotingOptions = checkNotNull(selectedDecodedVotingOptions);
			return this;
		}

		public Builder setSelectedDecodedWriteInVotes(final ImmutableList<ImmutableList<String>> selectedDecodedWriteInVotes) {
			this.selectedDecodedWriteInVotes = checkNotNull(selectedDecodedWriteInVotes);
			return this;
		}

		/**
		 * @throws NullPointerException     if any parameter is null.
		 * @throws IllegalArgumentException if
		 *                                  <ul>
		 *                                      <li>the plaintext votes and the selected encoded voting options do not have the same group.</li>
		 *                                      <li>there is a different number of selected encoded and decoded voting options.</li>
		 *                                      <li>there is a different number of selected write-in votes and selected encoded voting options.</li>
		 *                                  </ul>
		 */
		public VerifyProcessPlaintextsInput build() {
			checkNotNull(plaintextVotes);
			checkNotNull(selectedEncodedVotingOptions);

			// Cross-group checks.
			checkArgument(selectedEncodedVotingOptions.isEmpty() || selectedEncodedVotingOptions.getGroup().equals(plaintextVotes.getGroup()),
					"The selected encoded voting options and plaintexts votes must have the same group.");

			// Cross-size checks.
			checkArgument(selectedEncodedVotingOptions.size() == selectedDecodedVotingOptions.size(),
					"There must be as many encoded as decoded voting options.");
			checkArgument(selectedDecodedWriteInVotes.size() == selectedEncodedVotingOptions.size(),
					"There must be as many decoded write-in votes as encoded voting options.");
			checkArgument(allEqual(selectedDecodedVotingOptions.stream(), ImmutableList::size),
					"All selected decoded voting options must have the same size.");
			checkArgument(selectedEncodedVotingOptions.isEmpty()
							|| selectedEncodedVotingOptions.getElementSize() == selectedDecodedVotingOptions.get(0).size(),
					"All selected encoded and decoded voting options must have the same size.");

			return new VerifyProcessPlaintextsInput(plaintextVotes, selectedEncodedVotingOptions, selectedDecodedVotingOptions,
					selectedDecodedWriteInVotes);
		}
	}
}
