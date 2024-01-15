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

import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;

/**
 * Regroups the context values needed by the VerifyTallyControlComponentBallotBox algorithm.
 *
 * <ul>
 *     <li>(p, q, g), the encryption group. Non-null.</li>
 *     <li>ee, the election event id. Non-null and a valid UUID.</li>
 *     <li>bb, the ballot box id. Non-null and a valid UUID.</li>
 *     <li>pTable, the primes mapping table. Non-null.</li>
 *     <li>EB<sub>pk</sub>, the electoral board public key. Non-null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentBallotBoxContext {

	private final GqGroup encryptionGroup;
	private final String electionEventId;
	private final String ballotBoxId;
	private final PrimesMappingTable primesMappingTable;
	private final ElGamalMultiRecipientPublicKey electoralBoardPublicKey;

	private VerifyTallyControlComponentBallotBoxContext(final String electionEventId, final String ballotBoxId,
			final ElectionEventContext electionEventContext, final SetupComponentPublicKeys setupComponentPublicKeys) {
		checkNotNull(electionEventContext);
		checkNotNull(setupComponentPublicKeys);

		this.electionEventId = validateUUID(electionEventId);
		this.ballotBoxId = validateUUID(ballotBoxId);
		final VerificationCardSetContext verificationCardSetContext = electionEventContext.verificationCardSetContexts().stream()
				.filter(vcsContext -> vcsContext.getBallotBoxId().equals(ballotBoxId))
				.collect(MoreCollectors.onlyElement());
		this.primesMappingTable = verificationCardSetContext.getPrimesMappingTable();
		this.encryptionGroup = primesMappingTable.getEncryptionGroup();
		this.electoralBoardPublicKey = setupComponentPublicKeys.electoralBoardPublicKey();

		checkArgument(encryptionGroup.equals(electoralBoardPublicKey.getGroup()));
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

		private String electionEventId;
		private String ballotBoxId;
		private ElectionEventContext electionEventContext;
		private SetupComponentPublicKeys setupComponentPublicKeys;

		public Builder setElectionEventId(final String electionEventId) {
			this.electionEventId = electionEventId;
			return this;
		}

		public Builder setBallotBoxId(final String ballotBoxId) {
			this.ballotBoxId = ballotBoxId;
			return this;
		}

		public Builder setElectionEventContext(final ElectionEventContext electionEventContext) {
			this.electionEventContext = electionEventContext;
			return this;
		}

		public Builder setSetupComponentPublicKeys(final SetupComponentPublicKeys setupComponentPublicKeys) {
			this.setupComponentPublicKeys = setupComponentPublicKeys;
			return this;
		}

		public VerifyTallyControlComponentBallotBoxContext build() {
			return new VerifyTallyControlComponentBallotBoxContext(electionEventId, ballotBoxId, electionEventContext, setupComponentPublicKeys);
		}
	}
}
