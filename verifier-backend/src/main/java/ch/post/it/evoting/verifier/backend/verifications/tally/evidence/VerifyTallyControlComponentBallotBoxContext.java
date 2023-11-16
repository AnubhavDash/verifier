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
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.validations.FailedValidationException;

/**
 * Regroups the context values needed by the VerifyTallyControlComponentBallotBox algorithm.
 *
 * <ul>
 *     <li>(p, q, g), the encryption group. Non-null.</li>
 *     <li>ee, the election event id. Non-null and a valid UUID.</li>
 *     <li>bb, the ballot box id. Non-null and a valid UUID.</li>
 *     <li>EB<sub>pk</sub>, the electoral board public key. Non-null.</li>
 *     <li>pTable, the primes mapping table. Non-null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentBallotBoxContext {

	private final GqGroup encryptionGroup;
	private final String electionEventId;
	private final String ballotBoxId;
	private final ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
	private final PrimesMappingTable primesMappingTable;

	private VerifyTallyControlComponentBallotBoxContext(final GqGroup encryptionGroup, final String electionEventId, final String ballotBoxId,
			final ElGamalMultiRecipientPublicKey electoralBoardPublicKey, final PrimesMappingTable primesMappingTable) {
		this.encryptionGroup = encryptionGroup;
		this.electionEventId = electionEventId;
		this.ballotBoxId = ballotBoxId;
		this.electoralBoardPublicKey = electoralBoardPublicKey;
		this.primesMappingTable = primesMappingTable;
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

	public static class Builder {

		private GqGroup encryptionGroup;
		private String electionEventId;
		private String ballotBoxId;
		private ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
		private PrimesMappingTable primesMappingTable;

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

		/**
		 * @throws NullPointerException      if any parameter is null.
		 * @throws FailedValidationException if the election event id or ballot box id are not valid UUIDs.
		 * @throws IllegalArgumentException  if the group of the electoral board public key or primes mapping table is not equal to the encryption
		 *                                   group.
		 */
		public VerifyTallyControlComponentBallotBoxContext build() {
			checkNotNull(encryptionGroup);
			validateUUID(electionEventId);
			validateUUID(ballotBoxId);
			checkNotNull(electoralBoardPublicKey);
			checkNotNull(primesMappingTable);

			checkArgument(electoralBoardPublicKey.getGroup().equals(encryptionGroup));
			checkArgument(primesMappingTable.getEncryptionGroup().equals(encryptionGroup));

			return new VerifyTallyControlComponentBallotBoxContext(encryptionGroup, electionEventId, ballotBoxId, electoralBoardPublicKey,
					primesMappingTable);
		}
	}
}
