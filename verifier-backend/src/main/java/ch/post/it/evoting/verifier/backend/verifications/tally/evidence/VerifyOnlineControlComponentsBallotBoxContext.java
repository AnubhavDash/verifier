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
 *     <li>bb, the ballot box id. Non-null and a valid UUID.</li>
 *     <li>the Setup Component Public Keys. Non-null. Contains:</li>
 *     <ul>
 *         <li>EL<sub>pk</sub>, the election public key.</li>
 *         <li>(EL<sub>pk,1</sub>, EL<sub>pk,2</sub>, EL<sub>pk,3</sub>, EL<sub>pk,4</sub>), the CCM election public keys.</li>
 *         <li>EB<sub>pk</sub>, the electoral board public key.</li>
 *         <li>pk<sub>CCR</sub>, the Choice Return Codes encryption public key.</li>
 *     </ul>
 *     <li>the Election Event Context. Non-null. Contains:</li>
 *     <ul>
 *         <li>vcs, the verification card set id.</li>
 *         <li>N_E, the number of eligible voters.</li>
 *         <li>pTable, the primes mapping table.</li>
 *     </ul>
 * </ul>
 */
public class VerifyOnlineControlComponentsBallotBoxContext {

	private final GqGroup encryptionGroup;
	private final String electionEventId;
	private final String ballotBoxId;
	private final ElGamalMultiRecipientPublicKey electionPublicKey;
	private final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> ccmElectionPublicKeys;
	private final ElGamalMultiRecipientPublicKey electoralBoardPublicKey;
	private final ElGamalMultiRecipientPublicKey choiceReturnCodesEncryptionPublicKey;
	private final String verificationCardSetId;
	private final int numberOfEligibleVoters;
	private final PrimesMappingTable primesMappingTable;

	public VerifyOnlineControlComponentsBallotBoxContext(final GqGroup encryptionGroup, final String electionEventId,
			final String ballotBoxId, final SetupComponentPublicKeys setupComponentPublicKeys, final ElectionEventContext electionEventContext) {
		checkNotNull(encryptionGroup);
		validateUUID(electionEventId);
		validateUUID(ballotBoxId);
		checkNotNull(setupComponentPublicKeys);
		checkNotNull(electionEventContext);

		checkArgument(electionEventId.equals(electionEventContext.electionEventId()));
		checkArgument(setupComponentPublicKeys.electionPublicKey().getGroup().equals(encryptionGroup));

		final VerificationCardSetContext verificationCardSetContextForBallotBoxId = electionEventContext.verificationCardSetContexts().stream()
				.filter(verificationCardSetContext -> verificationCardSetContext.ballotBoxId().equals(ballotBoxId))
				.collect(MoreCollectors.onlyElement());

		this.encryptionGroup = checkNotNull(encryptionGroup);
		this.electionEventId = validateUUID(electionEventId);
		this.ballotBoxId = validateUUID(ballotBoxId);
		this.electionPublicKey = setupComponentPublicKeys.electionPublicKey();
		this.ccmElectionPublicKeys = setupComponentPublicKeys.combinedControlComponentPublicKeys().stream()
				.map(ControlComponentPublicKeys::ccmjElectionPublicKey)
				.collect(GroupVector.toGroupVector());
		this.electoralBoardPublicKey = setupComponentPublicKeys.electoralBoardPublicKey();
		this.choiceReturnCodesEncryptionPublicKey = setupComponentPublicKeys.choiceReturnCodesEncryptionPublicKey();
		this.verificationCardSetId = verificationCardSetContextForBallotBoxId.verificationCardSetId();
		this.numberOfEligibleVoters = verificationCardSetContextForBallotBoxId.numberOfVotingCards();
		this.primesMappingTable = verificationCardSetContextForBallotBoxId.primesMappingTable();
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

	public String getVerificationCardSetId() {
		return verificationCardSetId;
	}

	public int getNumberOfEligibleVoters() {
		return numberOfEligibleVoters;
	}

	public PrimesMappingTable getPrimesMappingTable() {
		return primesMappingTable;
	}
}
