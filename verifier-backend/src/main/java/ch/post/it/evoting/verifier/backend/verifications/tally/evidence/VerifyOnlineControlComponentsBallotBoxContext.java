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
import static java.util.function.Predicate.not;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.evotinglibraries.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;

public class VerifyOnlineControlComponentsBallotBoxContext {

	private final GqGroup encryptionGroup;
	private final String electionEventId;
	private final String ballotBoxId;
	private final int numberOfSelectableVotingOptions;
	private final ElectionEventContext electionEventContext;
	private final SetupComponentPublicKeys setupComponentPublicKeys;

	public VerifyOnlineControlComponentsBallotBoxContext(final GqGroup encryptionGroup, final String electionEventId, final String ballotBoxId,
			final int numberOfSelectableVotingOptions, final ElectionEventContext electionEventContext,
			final SetupComponentPublicKeys setupComponentPublicKeys) {
		this.encryptionGroup = checkNotNull(encryptionGroup);
		this.electionEventId = validateUUID(electionEventId);
		this.ballotBoxId = validateUUID(ballotBoxId);
		checkArgument(numberOfSelectableVotingOptions > 0);
		this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
		this.electionEventContext = checkNotNull(electionEventContext);
		this.setupComponentPublicKeys = setupComponentPublicKeys;

		checkArgument(this.electionEventId.equals(this.electionEventContext.electionEventId()));
		checkArgument(electionEventContext.verificationCardSetContexts().stream()
				.anyMatch(verificationCardSetContext -> verificationCardSetContext.ballotBoxId().equals(ballotBoxId)));
		checkArgument(setupComponentPublicKeys.electionPublicKey().getGroup().equals(encryptionGroup));
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

	public int getNumberOfSelectableVotingOptions() {
		return numberOfSelectableVotingOptions;
	}

	public ElGamalMultiRecipientPublicKey getElectionPublicKey() {
		return setupComponentPublicKeys.electionPublicKey();
	}

	public GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> getCcmElectionPublicKeys() {
		return setupComponentPublicKeys.combinedControlComponentPublicKeys().stream()
				.map(ControlComponentPublicKeys::ccmjElectionPublicKey)
				.collect(GroupVector.toGroupVector());
	}

	public ElGamalMultiRecipientPublicKey getElectoralBoardPublicKey() {
		return setupComponentPublicKeys.electoralBoardPublicKey();
	}

	public ElGamalMultiRecipientPublicKey getChoiceReturnCodesEncryptionPublicKey() {
		return setupComponentPublicKeys.choiceReturnCodesEncryptionPublicKey();
	}

	public int getNumberOfAllowedWriteInsPlusOne() {
		return electionEventContext.verificationCardSetContexts().stream()
				.filter(verificationCardSetContext -> verificationCardSetContext.ballotBoxId().equals(ballotBoxId))
				.collect(MoreCollectors.onlyElement())
				.getNumberOfWriteIns() + 1;
	}

	public int getNumberOfEligibleVoters() {
		return electionEventContext.verificationCardSetContexts().stream()
				.filter(not(VerificationCardSetContext::testBallotBox))
				.map(VerificationCardSetContext::numberOfVotingCards)
				.reduce(0, Integer::sum);
	}

	public PrimesMappingTable getPrimesMappingTable() {
		return electionEventContext.verificationCardSetContexts().stream()
				.filter(verificationCardSetContext -> verificationCardSetContext.ballotBoxId().equals(ballotBoxId))
				.map(VerificationCardSetContext::primesMappingTable)
				// The constructor already verifies the verification card set contexts for the wanted ballotBoxId exists.
				.collect(MoreCollectors.onlyElement());
	}
}
