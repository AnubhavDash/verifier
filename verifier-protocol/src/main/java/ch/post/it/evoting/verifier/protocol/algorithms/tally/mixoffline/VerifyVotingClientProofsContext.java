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

import static ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants.MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTable;
import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.cryptoprimitives.domain.validations.FailedValidationException;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;

/**
 * Regroups the context values needed by the VerifyVotingClientProofs algorithm.
 *
 * <ul>
 * <li>ee, the election event id. Not null and a valid UUID.</li>
 * <li>psi, the number of selectable voting options. In range [1, 120].</li>
 * <li>delta_hat, the number of allowed write-ins + 1. Strictly positive.</li>
 * <li>pTable, the primes mapping table of size n. Must be non-null.</li>
 * </ul>
 */
public class VerifyVotingClientProofsContext {

	private final GqGroup encryptionGroup;
	private final String electionEventId;
	private final int numberOfSelectableVotingOptions;
	private final int numberOfAllowedWriteInsPlusOne;
	private final GroupVector<PrimesMappingTableEntry, GqGroup> primesMappingTable;

	private VerifyVotingClientProofsContext(final GqGroup encryptionGroup, final String electionEventId, final int numberOfSelectableVotingOptions,
			final int numberOfAllowedWriteInsPlusOne, final GroupVector<PrimesMappingTableEntry, GqGroup> primesMappingTable) {
		this.encryptionGroup = encryptionGroup;
		this.electionEventId = electionEventId;
		this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
		this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;
		this.primesMappingTable = primesMappingTable;
	}

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public int getNumberOfSelectableVotingOptions() {
		return numberOfSelectableVotingOptions;
	}

	public int getNumberOfAllowedWriteInsPlusOne() {
		return numberOfAllowedWriteInsPlusOne;
	}

	public GroupVector<PrimesMappingTableEntry, GqGroup> getPrimesMappingTable() {
		return primesMappingTable;
	}

	public static class Builder {

		private GqGroup encryptionGroup;
		private String electionEventId;
		private int numberOfSelectableVotingOptions;
		private int numberOfAllowedWriteInsPlusOne;
		private PrimesMappingTable primesMappingTable;

		public VerifyVotingClientProofsContext.Builder setEncryptionGroup(final GqGroup encryptionGroup) {
			this.encryptionGroup = encryptionGroup;
			return this;
		}

		public VerifyVotingClientProofsContext.Builder setElectionEventId(final String electionEventId) {
			this.electionEventId = electionEventId;
			return this;
		}

		public VerifyVotingClientProofsContext.Builder setNumberOfSelectableVotingOptions(final int numberOfSelectableVotingOptions) {
			this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
			return this;
		}

		public VerifyVotingClientProofsContext.Builder setNumberOfAllowedWriteInsPlusOne(final int numberOfAllowedWriteInsPlusOne) {
			this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;
			return this;
		}

		public VerifyVotingClientProofsContext.Builder setPrimesMappingTable(final PrimesMappingTable primesMappingTable) {
			this.primesMappingTable = primesMappingTable;
			return this;
		}

		/**
		 * Creates a VerifyVotingClientProofsContext object.
		 *
		 * @throws NullPointerException      if the election event id is null.
		 * @throws FailedValidationException if the election event id is not a valid UUID.
		 * @throws IllegalArgumentException  if
		 *                                   <ul>
		 *                                       <li>the number of selectable voting options is not in the range [1, 120].</li>
		 *                                       <li>the number of allowed write-ins + 1 is smaller or equal to 0.</li>
		 *                                   </ul>
		 */
		public VerifyVotingClientProofsContext build() {
			checkNotNull(encryptionGroup);
			validateUUID(electionEventId);
			checkNotNull(primesMappingTable);

			final int psi = numberOfSelectableVotingOptions;
			final int delta_hat = numberOfAllowedWriteInsPlusOne;

			checkArgument(psi >= 1, "The number of selectable voting options should be greater or equal to one. [psi: %s]", psi);
			checkArgument(psi <= MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS,
					"The number of selectable voting options should be smaller or equal to %s. [psi: %s]",
					MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS, psi);
			checkArgument(0 < delta_hat, "The number of allowed write ins plus one must be a strictly positive number. [delta_hat: %s]", delta_hat);
			checkArgument(encryptionGroup.equals(primesMappingTable.getPTable().getGroup()),
					"The primes mapping table's entries must belong to the encryption group. [encryptionGroup: %s]", encryptionGroup);

			return new VerifyVotingClientProofsContext(encryptionGroup, electionEventId, numberOfSelectableVotingOptions,
					numberOfAllowedWriteInsPlusOne, primesMappingTable.getPTable());
		}
	}

}
