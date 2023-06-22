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

import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.validations.FailedValidationException;

/**
 * Regroups the context values needed by the VerifyTallyControlComponentBallotBox algorithm.
 *
 * <ul>
 * <li>encryptionGroup, the encryption group. Non-null.</li>
 * <li>ee, the election event id. Non-null and a valid UUID.</li>
 * <li>bb, the ballot box id. Non-null and a valid UUID.</li>
 * <li>EB<sub>pk</sub>, the electoral board public key. Non-null.</li>
 * <li>pTable, the primes mapping table. Non-null.</li>
 * <li>p<sub>w</sub>_tilde, the write-in voting options. Non-null.</li>
 * <li>psi, the number of selectable voting options. Between 1 and 120 inclusive.</li>
 * <li>delta_hat, the number of write-ins + 1. Strictly positive.</li>
 * </ul>
 */
public class VerifyTallyControlComponentBallotBoxContext {

	private final GqGroup encryptionGroup;
	private final String electionEventId;
	private final String ballotBoxId;
	private final ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
	private final PrimesMappingTable primesMappingTable;
	private final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions;
	private final int numberOfSelectableVotingOptions;
	private final int numberOfAllowedWriteInsPlusOne;

	private VerifyTallyControlComponentBallotBoxContext(final GqGroup encryptionGroup, final String electionEventId, final String ballotBoxId,
			final ElGamalMultiRecipientPublicKey electoralBoardPublicKey, final PrimesMappingTable primesMappingTable,
			final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions, final int numberOfSelectableVotingOptions,
			final int numberOfAllowedWriteInsPlusOne) {
		this.encryptionGroup = encryptionGroup;
		this.electionEventId = electionEventId;
		this.ballotBoxId = ballotBoxId;
		this.electoralBoardPublicKey = electoralBoardPublicKey;
		this.primesMappingTable = primesMappingTable;
		this.writeInVotingOptions = writeInVotingOptions;
		this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
		this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;
	}

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public String getBallotBoxId() {
		return ballotBoxId;
	}

	public ElGamalMultiRecipientPublicKey getElectoralBoardPublicKey() {
		return electoralBoardPublicKey;
	}

	public PrimesMappingTable getPrimesMappingTable() {
		return primesMappingTable;
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

	public static class Builder {

		private GqGroup encryptionGroup;
		private String electionEventId;
		private String ballotBoxId;
		private ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
		private PrimesMappingTable primesMappingTable;
		private GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions;
		private int numberOfSelectableVotingOptions;
		private int numberOfAllowedWriteInsPlusOne;

		public Builder setEncryptionGroup(final GqGroup encryptionGroup) {
			this.encryptionGroup = encryptionGroup;
			return this;
		}

		public Builder setElectionEventId(final String electionEventId) {
			this.electionEventId = electionEventId;
			return this;
		}

		public Builder setBallotBoxId(final String ballotBoxId) {
			this.ballotBoxId = ballotBoxId;
			return this;
		}

		public Builder setElectoralBoardPublicKey(final ElGamalMultiRecipientPublicKey electoralBoardPublicKey) {
			this.electoralBoardPublicKey = electoralBoardPublicKey;
			return this;
		}

		public Builder setPrimesMappingTable(final PrimesMappingTable primesMappingTable) {
			this.primesMappingTable = primesMappingTable;
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

		/**
		 * @throws NullPointerException      if any parameter is null.
		 * @throws FailedValidationException if the election event id or ballot box id are not valid UUIDs.
		 * @throws IllegalArgumentException  if
		 *                                   <ul>
		 *                                       <li>not all context have the same group.</li>
		 *                                      <li>the size of the write-in voting options is not equal to the number of allowed write-ins.</li>
		 *                                      <li>the number of selectable voting options is strictly smaller than 1.</li>
		 *                                      <li>the number of selectable voting options is strictly greater than 120.</li>
		 *                                      <li>the number of allowed write-ins + 1 is smaller or equal to 0.</li>
		 *                                   </ul>
		 */
		public VerifyTallyControlComponentBallotBoxContext build() {
			checkNotNull(encryptionGroup);
			validateUUID(electionEventId);
			validateUUID(ballotBoxId);
			checkNotNull(electoralBoardPublicKey);
			checkNotNull(primesMappingTable);
			checkNotNull(writeInVotingOptions);

			checkArgument(electoralBoardPublicKey.getGroup().equals(encryptionGroup));
			checkArgument(primesMappingTable.getPTable().getGroup().equals(encryptionGroup));
			checkArgument(writeInVotingOptions.isEmpty() || writeInVotingOptions.getGroup().equals(encryptionGroup));

			checkArgument(writeInVotingOptions.size() + 1 == numberOfAllowedWriteInsPlusOne,
					"The size of the write-in voting options plus one must be equal to the number of allowed write-ins plus one.");
			checkArgument(1 <= numberOfSelectableVotingOptions && numberOfSelectableVotingOptions <= 120,
					"The number of selectable voting options must be within the allowed bounds.");
			checkArgument(numberOfAllowedWriteInsPlusOne > 0, "The number of allowed write-ins plus one must be at least 1.");

			return new VerifyTallyControlComponentBallotBoxContext(encryptionGroup, electionEventId, ballotBoxId, electoralBoardPublicKey,
					primesMappingTable, writeInVotingOptions, numberOfSelectableVotingOptions, numberOfAllowedWriteInsPlusOne);
		}
	}
}
