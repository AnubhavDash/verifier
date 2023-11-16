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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentConstants;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.SetupComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.Results;

/**
 * Regroups the input values needed by the VerifyTallyControlComponent algorithm.
 *
 * <ul>
 *     <li>ee, the election event context. Not null and a valid UUID.</li>
 *     <li>bb, the vector of ballot box ids, sorted. Not null and valid UUIDs.</li>
 *     <li>Last Online Control Component Shuffles, sorted by ballot box id. Not null.</li>
 *     <li>Tally Control Component Shuffles, sorted by ballot box id. Not null.</li>
 *     <li>Tally Control Component Votes, sorted by ballot box id. Not null.</li>
 *     <li>Election Event Context. Not null.</li>
 *     <li>Setup Component Public Keys. Not null.</li>
 *     <li>Election Event Configuration, the configuration-anonymized as {@link Configuration}. Not null.</li>
 *     <li>Tally Control Component Decryptions, the evoting-decrypt as {@link Results}. Not null.</li>
 *     <li>Tally Control Component Detailed Results, the eCH-0222 as {@link ch.ech.xmlns.ech_0222._1.Delivery}. Not null.</li>
 *     <li>Tally Control Component Results, the eCH-0110 as {@link Delivery}. Not null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentInput {

	private final List<String> ballotBoxIds;
	private final List<ControlComponentShufflePayload> lastOnlineControlComponentShuffles;
	private final List<TallyComponentShufflePayload> tallyControlComponentShuffles;
	private final List<TallyComponentVotesPayload> tallyControlComponentVotes;
	private final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads;
	private final ElectionEventContextPayload electionEventContextPayload;
	private final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload;
	private final Configuration electionEventConfiguration;
	private final Results tallyControlComponentDecryptions;
	private final ch.ech.xmlns.ech_0222._1.Delivery tallyControlComponentDetailedResults;
	private final Delivery tallyControlComponentResults;

	public VerifyTallyControlComponentInput(final List<ControlComponentShufflePayload> controlComponentShufflePayloads,
			final List<TallyComponentShufflePayload> tallyComponentShufflePayloads,
			final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads,
			final ElectionEventContextPayload electionEventContextPayload,
			final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload,
			final Configuration electionEventConfiguration,
			final Results tallyControlComponentDecryptions,
			final ch.ech.xmlns.ech_0222._1.Delivery tallyControlComponentDetailedResults,
			final Delivery tallyControlComponentResults) {
		this.lastOnlineControlComponentShuffles = List.copyOf(checkNotNull(controlComponentShufflePayloads)).stream()
				.filter(controlComponentShufflePayload -> controlComponentShufflePayload.getNodeId() == ControlComponentConstants.NODE_IDS.last())
				.sorted(Comparator.comparing(ControlComponentShufflePayload::getBallotBoxId))
				.toList();
		this.tallyControlComponentShuffles = List.copyOf(checkNotNull(tallyComponentShufflePayloads)).stream()
				.sorted(Comparator.comparing(TallyComponentShufflePayload::getBallotBoxId))
				.toList();
		this.electionEventContextPayload = checkNotNull(electionEventContextPayload);
		this.setupComponentPublicKeysPayload = checkNotNull(setupComponentPublicKeysPayload);
		this.electionEventConfiguration = checkNotNull(electionEventConfiguration);
		this.tallyControlComponentDecryptions = checkNotNull(tallyControlComponentDecryptions);
		this.tallyControlComponentResults = checkNotNull(tallyControlComponentResults);
		// The ElectionEventContext constructor ensures all ballot box ids are distinct.
		this.ballotBoxIds = electionEventContextPayload.getElectionEventContext().verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::ballotBoxId)
				.sorted(String::compareTo)
				.toList();
		this.tallyControlComponentDetailedResults = checkNotNull(tallyControlComponentDetailedResults);

		final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloadMap = Map.copyOf(checkNotNull(tallyComponentVotesPayloads));
		this.tallyControlComponentVotes = tallyComponentVotesPayloadMap.values().stream()
				.sorted(Comparator.comparing(TallyComponentVotesPayload::getBallotBoxId))
				.toList();
		this.tallyComponentVotesPayloads = tallyComponentVotesPayloadMap;
	}

	public String getElectionEventId() {
		return electionEventContextPayload.getElectionEventContext().electionEventId();
	}

	public List<String> getBallotBoxIds() {
		return List.copyOf(ballotBoxIds);
	}

	public List<ControlComponentShufflePayload> getLastOnlineControlComponentShuffles() {
		return List.copyOf(lastOnlineControlComponentShuffles);
	}

	public List<TallyComponentShufflePayload> getTallyControlComponentShuffles() {
		return List.copyOf(tallyControlComponentShuffles);
	}

	public List<TallyComponentVotesPayload> getTallyControlComponentVotes() {
		return List.copyOf(tallyControlComponentVotes);
	}

	public ElectionEventContext getElectionEventContext() {
		return electionEventContextPayload.getElectionEventContext();
	}

	public SetupComponentPublicKeys getSetupComponentPublicKeys() {
		return setupComponentPublicKeysPayload.getSetupComponentPublicKeys();
	}

	public GqGroup getEncryptionGroup() {
		return electionEventContextPayload.getEncryptionGroup();
	}

	public Configuration getElectionEventConfiguration() {
		return electionEventConfiguration;
	}

	public Results getTallyControlComponentDecryptions() {
		return tallyControlComponentDecryptions;
	}

	public Delivery getTallyControlComponentResults() {
		return tallyControlComponentResults;
	}

	public ch.ech.xmlns.ech_0222._1.Delivery getTallyControlComponentDetailedResults() {
		return tallyControlComponentDetailedResults;
	}

	public Map<String, TallyComponentVotesPayload> getTallyComponentVotesPayloads() {
		return tallyComponentVotesPayloads;
	}
}
