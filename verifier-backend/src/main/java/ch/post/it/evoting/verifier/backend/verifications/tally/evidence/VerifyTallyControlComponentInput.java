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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants;
import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.verifier.protocol.domain.tally.TallyComponentVotesPayload;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

/**
 * Regroups the input values needed by the VerifyTallyControlComponent algorithm.
 *
 * <ul>
 * <li>ee, the election event context. Not null and a valid UUID.</li>
 * <li>bb, the vector of ballot box ids, sorted. Not null and valid UUIDs.</li>
 * <li>Last Online Control Component Shuffles, sorted by ballot box id. Not null.</li>
 * <li>Tally Control Component Shuffles, sorted by ballot box id. Not null.</li>
 * <li>Tally Control Component Votes, sorted by ballot box id. Not null.</li>
 * <li>Election Event Context. Not null.</li>
 * <li>Election Event Configuration, the configuration-anonymized as {@link Configuration}. Not null.</li>
 * <li>Tally Control Component Decryptions, the evoting-decrypt as {@link Results}. Not null.</li>
 * <li>Tally Control Component Results, the eCH-0110 as {@link Delivery}. Not null.</li>
 * <li>Tally Control Component eCH-0222 as {@link ch.ech.xmlns.ech_0222._1.Delivery}. Not null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentInput {

	private final List<String> ballotBoxIds;
	private final List<ControlComponentShufflePayload> lastOnlineControlComponentShuffles;
	private final List<TallyComponentShufflePayload> tallyControlComponentShuffles;
	private final List<TallyComponentVotesPayload> tallyControlComponentVotes;
	private final Map<String, List<List<String>>> allSelectedDecodedVotingOptions;
	private final ElectionEventContextPayload electionEventContextPayload;
	private final Configuration electionEventConfiguration;
	private final Results tallyControlComponentDecryptions;
	private final Delivery tallyControlComponentResults;
	private final ch.ech.xmlns.ech_0222._1.Delivery tallyComponentEch0222;

	public VerifyTallyControlComponentInput(final ElectionEventContextPayload electionEventContextPayload,
			final List<ControlComponentShufflePayload> controlComponentShufflePayloads,
			final List<TallyComponentShufflePayload> tallyComponentShufflePayloads,
			final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloads,
			final Configuration electionEventConfiguration,
			final Results tallyControlComponentDecryptions,
			final Delivery tallyControlComponentResults, final ch.ech.xmlns.ech_0222._1.Delivery tallyComponentEch0222) {
		this.lastOnlineControlComponentShuffles = List.copyOf(checkNotNull(controlComponentShufflePayloads)).stream()
				.filter(controlComponentShufflePayload -> controlComponentShufflePayload.getNodeId() == ControlComponentConstants.NODE_IDS.last())
				.sorted(Comparator.comparing(ControlComponentShufflePayload::getBallotBoxId))
				.toList();
		this.tallyControlComponentShuffles = List.copyOf(checkNotNull(tallyComponentShufflePayloads)).stream()
				.sorted(Comparator.comparing(TallyComponentShufflePayload::getBallotBoxId))
				.toList();
		this.electionEventContextPayload = checkNotNull(electionEventContextPayload);
		this.electionEventConfiguration = checkNotNull(electionEventConfiguration);
		this.tallyControlComponentDecryptions = checkNotNull(tallyControlComponentDecryptions);
		this.tallyControlComponentResults = checkNotNull(tallyControlComponentResults);
		this.ballotBoxIds = electionEventContextPayload.getElectionEventContext().verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::ballotBoxId)
				.sorted(String::compareTo)
				.toList();
		this.tallyComponentEch0222 = checkNotNull(tallyComponentEch0222);

		final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloadMap = Map.copyOf(checkNotNull(tallyComponentVotesPayloads));
		this.tallyControlComponentVotes = tallyComponentVotesPayloadMap.values().stream()
				.sorted(Comparator.comparing(TallyComponentVotesPayload::getBallotBoxId))
				.toList();
		final Map<String, List<List<String>>> allSelectedDecodedVotingOptionsMap = new HashMap<>();
		tallyComponentVotesPayloadMap.forEach((key, value) -> allSelectedDecodedVotingOptionsMap.put(key, value.getActualSelectedVotingOptions()));
		this.allSelectedDecodedVotingOptions = Map.copyOf(allSelectedDecodedVotingOptionsMap);
	}

	public String getElectionEventId() {
		return electionEventContextPayload.getElectionEventContext().electionEventId();
	}

	public List<String> getBallotBoxIds() {
		return ballotBoxIds;
	}

	public List<ControlComponentShufflePayload> getLastOnlineControlComponentShuffles() {
		return lastOnlineControlComponentShuffles;
	}

	public List<TallyComponentShufflePayload> getTallyControlComponentShuffles() {
		return tallyControlComponentShuffles;
	}

	public List<TallyComponentVotesPayload> getTallyControlComponentVotes() {
		return tallyControlComponentVotes;
	}

	public ElectionEventContext getElectionEventContext() {
		return electionEventContextPayload.getElectionEventContext();
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

	public ch.ech.xmlns.ech_0222._1.Delivery getTallyComponentEch0222() {
		return tallyComponentEch0222;
	}

	public Map<String, List<List<String>>> getAllSelectedDecodedVotingOptions() {
		return allSelectedDecodedVotingOptions;
	}
}
