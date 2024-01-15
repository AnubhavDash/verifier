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
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.evotinglibraries.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;

/**
 * Regroups the context values needed by the VerifyOnlineControlComponentsBallotBox algorithm.
 *
 * <ul>
 *     <li>(p, q, g), the encryption group. Non-null.</li>
 *     <li>ee, the election event id. Non-null and a valid UUID.</li>
 *     <li>vcs, the verification card set id. Non-null and a valid UUID.</li>
 *     <li>bb, the ballot box id. Non-null and a valid UUID.</li>
 *     <li>N_E, the number of eligible voters. Non-null.</li>
 *     <li>pTable, the primes mapping table. Non-null.</li>
 *     <li>EL<sub>pk</sub>, the election public key. Non-null.</li>
 *     <li>(EL<sub>pk,1</sub>, EL<sub>pk,2</sub>, EL<sub>pk,3</sub>, EL<sub>pk,4</sub>), the CCM election public keys. Non-null.</li>
 *     <li>EB<sub>pk</sub>, the electoral board public key. Non-null.</li>
 *     <li>pk<sub>CCR</sub>, the Choice Return Codes encryption public key. Non-null.</li>
 * </ul>
 */
public class VerifyOnlineControlComponentsBallotBoxContext {

	private final GqGroup encryptionGroup;
	private final String electionEventId;
	private final String verificationCardSetId;
	private final String ballotBoxId;
	private final int numberOfEligibleVoters;
	private final PrimesMappingTable primesMappingTable;
	private final ElGamalMultiRecipientPublicKey electionPublicKey;
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> ccmElectionPublicKeys;
	private final ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
	private final ElGamalMultiRecipientPublicKey choiceReturnCodesEncryptionPublicKey;

	private VerifyOnlineControlComponentsBallotBoxContext(final String electionEventId, final String verificationCardSetId, final String ballotBoxId,
			final ElectionEventContext electionEventContext, final SetupComponentPublicKeys setupComponentPublicKeys) {
		checkNotNull(electionEventContext);
		checkNotNull(setupComponentPublicKeys);

		this.electionEventId = validateUUID(electionEventId);
		this.verificationCardSetId = validateUUID(verificationCardSetId);
		this.ballotBoxId = validateUUID(ballotBoxId);
		final VerificationCardSetContext verificationCardSetContextForBallotBoxId = electionEventContext.verificationCardSetContexts().stream()
				.filter(verificationCardSetContext -> verificationCardSetContext.getBallotBoxId().equals(ballotBoxId))
				.collect(MoreCollectors.onlyElement());
		this.numberOfEligibleVoters = verificationCardSetContextForBallotBoxId.getNumberOfVotingCards();
		this.primesMappingTable = verificationCardSetContextForBallotBoxId.getPrimesMappingTable();
		this.encryptionGroup = primesMappingTable.getEncryptionGroup();
		this.electionPublicKey = setupComponentPublicKeys.electionPublicKey();
		this.ccmElectionPublicKeys = setupComponentPublicKeys.combinedControlComponentPublicKeys().stream()
				.map(ControlComponentPublicKeys::ccmjElectionPublicKey)
				.collect(GroupVector.toGroupVector());
		this.electoralBoardPublicKey = setupComponentPublicKeys.electoralBoardPublicKey();
		this.choiceReturnCodesEncryptionPublicKey = setupComponentPublicKeys.choiceReturnCodesEncryptionPublicKey();

		checkArgument(electionEventId.equals(electionEventContext.electionEventId()));
		checkArgument(verificationCardSetId.equals(verificationCardSetContextForBallotBoxId.getVerificationCardSetId()));
		checkArgument(this.encryptionGroup.equals(electionPublicKey.getGroup()));
	}

	public GqGroup getEncryptionGroup() {
		return encryptionGroup;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public String getVerificationCardSetId() {
		return verificationCardSetId;
	}

	public String getBallotBoxId() {
		return ballotBoxId;
	}

	public int getNumberOfEligibleVoters() {
		return numberOfEligibleVoters;
	}

	public PrimesMappingTable getPrimesMappingTable() {
		return primesMappingTable;
	}

	public ElGamalMultiRecipientPublicKey getElectionPublicKey() {
		return electionPublicKey;
	}

	public GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> getCcmElectionPublicKeys() {
		return ccmElectionPublicKeys;
	}

	public ElGamalMultiRecipientPublicKey getElectoralBoardPublicKey() {
		return electoralBoardPublicKey;
	}

	public ElGamalMultiRecipientPublicKey getChoiceReturnCodesEncryptionPublicKey() {
		return choiceReturnCodesEncryptionPublicKey;
	}

	public static class Builder {

		private String electionEventId;
		private String ballotBoxId;
		private String verificationCardSetId;
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

		public Builder setVerificationCardSetId(final String verificationCardSetId) {
			this.verificationCardSetId = verificationCardSetId;
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

		public VerifyOnlineControlComponentsBallotBoxContext build() {
			return new VerifyOnlineControlComponentsBallotBoxContext(electionEventId, verificationCardSetId, ballotBoxId, electionEventContext,
					setupComponentPublicKeys);
		}
	}
}
