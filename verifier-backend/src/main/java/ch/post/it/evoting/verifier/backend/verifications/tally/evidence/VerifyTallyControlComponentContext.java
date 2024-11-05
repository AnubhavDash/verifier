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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.SetupComponentPublicKeysPayload;

/**
 * Regroups the context values needed by the VerifyTallyControlComponent algorithm.
 *
 * <ul>
 *     <li>ee, the election event id. Non-null and a valid UUID.</li>
 *     <li>bb, the vector of ballot box id. Non-null and contains only valid UUIDs.</li>
 *     <li>the Election Event Context. Non-null.</li>
 *     <li>the Setup Component Public Keys. Non-null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentContext {

	private final String electionEventId;
	private final ImmutableList<String> ballotBoxIds;
	private final ElectionEventContext electionEventContext;
	private final SetupComponentPublicKeys setupComponentPublicKeys;

	public VerifyTallyControlComponentContext(final ElectionEventContextPayload electionEventContextPayload,
			final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload) {
		this.electionEventContext = checkNotNull(electionEventContextPayload).getElectionEventContext();
		this.setupComponentPublicKeys = checkNotNull(setupComponentPublicKeysPayload).getSetupComponentPublicKeys();
		this.electionEventId = electionEventContext.electionEventId();
		// The ElectionEventContext constructor ensures the ballot box ids are valid UUIDs and unique.
		this.ballotBoxIds = electionEventContext.verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::getBallotBoxId)
				.collect(toImmutableList());

		// By definition ballotBoxIds have the same size.
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public ImmutableList<String> getBallotBoxIds() {
		return ballotBoxIds;
	}

	public ElectionEventContext getElectionEventContext() {
		return electionEventContext;
	}

	public SetupComponentPublicKeys getSetupComponentPublicKeys() {
		return setupComponentPublicKeys;
	}

	public GqGroup getEncryptionGroup() {
		// The constructor of the ElectionEventContext ensures there is at least one verification card set context.
		final PrimesMappingTable primesMappingTable = electionEventContext.verificationCardSetContexts().get(0).getPrimesMappingTable();
		return primesMappingTable.getEncryptionGroup();
	}
}
