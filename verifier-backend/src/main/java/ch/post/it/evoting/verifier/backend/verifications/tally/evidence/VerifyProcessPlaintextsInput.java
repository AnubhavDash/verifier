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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Preconditions;

import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTable;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Regroups the input values needed by the VerifyProcessPlaintexts algorithm.
 *
 * <ul>
 * <li>pTable, the primes mapping table. Non-null.</li>
 * <li>m, the list of plaintext votes. Non-null.</li>
 * <li>p<sub>w</sub>_tilde, the write-in voting options. Non-null.</li>
 * <li>psi, the number of selectable voting options. Between 1 and 120 inclusive.</li>
 * <li>delta_hat, the number of write-ins + 1. Strictly positive.</li>
 * <li>L<sub>votes</sub>, the list of all selected encoded voting options. Non-null.</li>
 * <li>L<sub>decodedVotes</sub>, the list of all selected decoded voting options. Non-null.</li>
 * <li>L<sub>writeIns</sub>, the list of all selected decoded write-in votes. Non-null.</li>
 * </ul>
 */
public class VerifyProcessPlaintextsInput {

	private final PrimesMappingTable primesMappingTable;
	private final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes;
	private final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions;
	private final int numberOfSelectableVotingOptions;
	private final int numberOfAllowedWriteInsPlusOne;
	private final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;
	private final List<List<String>> selectedDecodedVotingOptions;
	private final List<List<String>> selectedDecodedWriteInVotes;

	private VerifyProcessPlaintextsInput(final PrimesMappingTable primesMappingTable,
			final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes,
			final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions,
			final int numberOfSelectableVotingOptions,
			final int numberOfAllowedWriteInsPlusOne,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions,
			final List<List<String>> selectedDecodedVotingOptions,
			final List<List<String>> selectedDecodedWriteInVotes) {
		this.primesMappingTable = primesMappingTable;
		this.plaintextVotes = plaintextVotes;
		this.writeInVotingOptions = writeInVotingOptions;
		this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
		this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;
		this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
		this.selectedDecodedVotingOptions = selectedDecodedVotingOptions;
		this.selectedDecodedWriteInVotes = selectedDecodedWriteInVotes;
	}

	public PrimesMappingTable getPrimesMappingTable() {
		return primesMappingTable;
	}

	public GroupVector<ElGamalMultiRecipientMessage, GqGroup> getPlaintextVotes() {
		return plaintextVotes;
	}

	public GroupVector<PrimeGqElement, GqGroup> getWriteInVotingOptions() {
		return writeInVotingOptions;
	}

	public int getNumberOfSelectableVotingOptions() {
		return numberOfSelectableVotingOptions;
	}

	public int getNumberOfAllowedWriteInsPlusOne() {
		return numberOfAllowedWriteInsPlusOne;
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

		private PrimesMappingTable primesMappingTable;
		private GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes;
		private GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions;
		private int numberOfSelectableVotingOptions;
		private int numberOfAllowedWriteInsPlusOne;
		private GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions;
		private List<List<String>> selectedDecodedVotingOptions;
		private List<List<String>> selectedDecodedWriteInVotes;

		public Builder setPrimesMappingTable(final PrimesMappingTable primesMappingTable) {
			this.primesMappingTable = primesMappingTable;
			return this;
		}

		public Builder setPlaintextVotes(
				final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes) {
			this.plaintextVotes = plaintextVotes;
			return this;
		}

		public Builder setWriteInVotingOptions(
				final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions) {
			this.writeInVotingOptions = writeInVotingOptions;
			return this;
		}

		public Builder setNumberOfSelectableVotingOptions(final int numberOfSelectableVotingOptions) {
			this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
			return this;
		}

		public Builder setNumberOfAllowedWriteInsPlusOne(final int numberOfAllowedWriteInsPlusOne) {
			this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;
			return this;
		}

		public Builder setSelectedEncodedVotingOptions(
				final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions) {
			this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
			return this;
		}

		public Builder setSelectedDecodedVotingOptions(final List<List<String>> selectedDecodedVotingOptions) {
			final List<List<String>> selectedDecodedVotingOptionsCopy = List.copyOf(checkNotNull(selectedDecodedVotingOptions));
			selectedDecodedVotingOptionsCopy.stream().parallel().forEach(Preconditions::checkNotNull);
			selectedDecodedVotingOptionsCopy.stream().parallel().forEach(options -> options.forEach(Preconditions::checkNotNull));
			this.selectedDecodedVotingOptions = selectedDecodedVotingOptionsCopy.stream()
					.parallel()
					.map(List::copyOf)
					.toList();
			return this;
		}

		public Builder setSelectedDecodedWriteInVotes(final List<List<String>> selectedDecodedWriteInVotes) {
			final List<List<String>> selectedDecodedWriteInVotesCopy = List.copyOf(checkNotNull(selectedDecodedWriteInVotes));
			selectedDecodedWriteInVotesCopy.forEach(Preconditions::checkNotNull);
			selectedDecodedWriteInVotesCopy.forEach(options -> options.forEach(Preconditions::checkNotNull));
			this.selectedDecodedWriteInVotes = selectedDecodedWriteInVotesCopy.stream()
					.map(List::copyOf)
					.toList();
			return this;
		}

		/**
		 * @throws NullPointerException     if any parameter is null.
		 * @throws IllegalArgumentException if
		 *                                  <ul>
		 *                                      <li>the plaintext votes, encoded voting options, selected encoded voting options and write-in voting options
		 *                                      	do not have the same group.</li>
		 *                                      <li>the number of elements of each plaintext vote does not correspond to the number of allowed write ins plus one.</li>
		 *                                      <li>the number of selectable voting options is strictly smaller than 1.</li>
		 *                                      <li>the number of selectable voting options is strictly greater than 120.</li>
		 *                                      <li>the number of allowed write-ins + 1 is smaller or equal to 0.</li>
		 *                                      <li>not all selected encoded voting options are of size selected number of selectable voting options.</li>
		 *                                      <li>there is a different number of selected encoded and decoded voting options.</li>
		 *                                      <li>there is a different number of selected write-in votes and selected encoded voting options.</li>
		 *                                      <li>the size of the write-in voting options is not equal to the number of allowed write-ins.</li>
		 *                                  </ul>
		 */
		public VerifyProcessPlaintextsInput build() {
			checkNotNull(primesMappingTable);
			checkNotNull(plaintextVotes);
			checkNotNull(writeInVotingOptions);
			checkNotNull(selectedEncodedVotingOptions);

			checkArgument(numberOfSelectableVotingOptions >= 1, "The number of selectable voting options must be greater or equal to 1. [psi: %s]",
					numberOfSelectableVotingOptions);
			checkArgument(numberOfSelectableVotingOptions <= 120,
					"The number of selectable voting options must be smaller or equal to 120. [psi: %s]",
					numberOfSelectableVotingOptions);
			checkArgument(numberOfAllowedWriteInsPlusOne > 0, "The number of allowed write-ins + 1 must be strictly greater than 0. [delta_hat: %s]",
					numberOfAllowedWriteInsPlusOne);

			// Cross-group checks.
			checkArgument(plaintextVotes.getGroup().equals(primesMappingTable.getPTable().getGroup()),
					"The plaintext votes and the primes mapping table must have the same group.");
			checkArgument(selectedEncodedVotingOptions.isEmpty() || selectedEncodedVotingOptions.getGroup().equals(plaintextVotes.getGroup()),
					"The selected encoded voting options and plaintexts votes must have the same group.");
			checkArgument(writeInVotingOptions.isEmpty() || writeInVotingOptions.getGroup().equals(plaintextVotes.getGroup()),
					"The write-in voting options and plaintexts votes must have the same group.");

			// Cross-size checks.
			checkArgument(plaintextVotes.getElementSize() == numberOfAllowedWriteInsPlusOne,
					"The number of elements of each plaintext vote must correspond to the number of allowed write ins plus one. [votes: %s, allowed write-ins plus one: %s]",
					plaintextVotes.getElementSize(), numberOfAllowedWriteInsPlusOne);
			checkArgument(selectedEncodedVotingOptions.isEmpty() || selectedEncodedVotingOptions.getElementSize() == numberOfSelectableVotingOptions,
					"Each selected encoded voting options must be of size number of selectable voting options. [expected: %s, actual: %s]",
					numberOfSelectableVotingOptions, selectedEncodedVotingOptions.getElementSize());
			checkArgument(selectedDecodedVotingOptions.stream().allMatch(options -> options.size() == numberOfSelectableVotingOptions),
					"Each selected decoded voting options must be of size number of selectable voting options.");
			checkArgument(selectedEncodedVotingOptions.size() == selectedDecodedVotingOptions.size(),
					"There must be as many encoded as decoded voting options.");
			checkArgument(selectedDecodedWriteInVotes.size() == selectedEncodedVotingOptions.size(),
					"There must be as many decoded write-in votes as encoded voting options.");
			checkArgument(writeInVotingOptions.size() + 1 == numberOfAllowedWriteInsPlusOne,
					"The size of the write-in voting options plus one must be equal to the number of allowed write-ins plus one.");

			return new VerifyProcessPlaintextsInput(primesMappingTable, plaintextVotes, writeInVotingOptions, numberOfSelectableVotingOptions,
					numberOfAllowedWriteInsPlusOne, selectedEncodedVotingOptions, selectedDecodedVotingOptions, selectedDecodedWriteInVotes);
		}
	}
}
