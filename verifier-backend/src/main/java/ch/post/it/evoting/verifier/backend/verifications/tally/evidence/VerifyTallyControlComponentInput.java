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

import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentConstants;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.Results;

/**
 * Regroups the input values needed by the VerifyTallyControlComponent algorithm.
 *
 * <ul>
 *     <li>(c<sub>mix,4</sub>, pi<sub>mix,4</sub>, c<sub>dec,4</sub>, pi<sub>dec,4</sub>), the Last Online Control Component Shuffles for all bb<sub>i</sub>. Not null.</li>
 *     <li>(c<sub>mix,5</sub>, pi<sub>mix,5</sub>, m, pi<sub>dec,5</sub>), the Tally Control Component Shuffles for all bb<sub>i</sub>. Not null.</li>
 *     <li>(L<sub>votes</sub>, L<sub>decodedVotes</sub>, L<sub>writeIns</sub>), the Tally Control Component Votes for all bb<sub>i</sub>. Not null.</li>
 *     <li>L<sub>decodedVotesbb</sub>, the list of all selected decoded voting options for all bb<sub>i</sub>. Not null.</li>
 *     <li>Election Event Configuration, the configuration-anonymized as {@link Configuration}. Not null.</li>
 *     <li>Tally Control Component Decryptions, the evoting-decrypt as {@link Results}. Not null.</li>
 *     <li>Tally Control Component Results, the eCH-0110 as {@link Delivery}. Not null.</li>
 *     <li>Tally Control Component Detailed Results, the eCH-0222 as {@link ch.ech.xmlns.ech_0222._1.Delivery}. Not null.</li>
 * </ul>
 */
public class VerifyTallyControlComponentInput {

	private final Map<String, ControlComponentShufflePayload> lastOnlineControlComponentShufflesPerBallotBoxId;
	private final Map<String, TallyComponentShufflePayload> tallyControlComponentShufflesPerBallotBoxId;
	private final Map<String, TallyComponentVotesPayload> tallyControlComponentVotesPerBallotBoxId;
	private final Map<String, TallyComponentVotesPayload> tallyControlComponentVotesPerAuthorizationAlias;
	private final Configuration electionEventConfiguration;
	private final Results tallyControlComponentDecryptions;
	private final Delivery tallyControlComponentResults;
	private final ch.ech.xmlns.ech_0222._1.Delivery tallyControlComponentDetailedResults;

	public VerifyTallyControlComponentInput(final Stream<ControlComponentShufflePayload> controlComponentShufflePayloads,
			final Stream<TallyComponentShufflePayload> tallyComponentShufflePayloads,
			final Map<String, TallyComponentVotesPayload> tallyControlComponentVotesPerAuthorizationAlias,
			final Configuration electionEventConfiguration,
			final Results tallyControlComponentDecryptions,
			final Delivery tallyControlComponentResults,
			final ch.ech.xmlns.ech_0222._1.Delivery tallyControlComponentDetailedResults) {
		this.lastOnlineControlComponentShufflesPerBallotBoxId = checkNotNull(controlComponentShufflePayloads)
				.filter(controlComponentShufflePayload -> controlComponentShufflePayload.getNodeId() == ControlComponentConstants.NODE_IDS.last())
				.collect(Collectors.toConcurrentMap(ControlComponentShufflePayload::getBallotBoxId, Function.identity()));
		this.tallyControlComponentShufflesPerBallotBoxId = checkNotNull(tallyComponentShufflePayloads)
				.collect(Collectors.toConcurrentMap(TallyComponentShufflePayload::getBallotBoxId, Function.identity()));
		final Map<String, TallyComponentVotesPayload> tallyComponentVotesPayloadsCopy = Map.copyOf(
				checkNotNull(tallyControlComponentVotesPerAuthorizationAlias));
		this.tallyControlComponentVotesPerBallotBoxId = tallyComponentVotesPayloadsCopy.values().stream()
				.collect(Collectors.toConcurrentMap(TallyComponentVotesPayload::getBallotBoxId, Function.identity()));
		this.tallyControlComponentVotesPerAuthorizationAlias = tallyComponentVotesPayloadsCopy;
		this.electionEventConfiguration = checkNotNull(electionEventConfiguration);
		this.tallyControlComponentDecryptions = checkNotNull(tallyControlComponentDecryptions);
		this.tallyControlComponentResults = checkNotNull(tallyControlComponentResults);
		this.tallyControlComponentDetailedResults = checkNotNull(tallyControlComponentDetailedResults);

		checkArgument(allEqual(Stream.of(
								lastOnlineControlComponentShufflesPerBallotBoxId.keySet(),
								tallyControlComponentShufflesPerBallotBoxId.keySet(),
								tallyControlComponentVotesPerBallotBoxId.keySet()),
						Function.identity()),
				"The last control component shuffles, the tally component shuffles and the tally component votes must correspond to the same ballot box ids.");
		checkArgument(lastOnlineControlComponentShufflesPerBallotBoxId.size() != 0,
				"There must be at least one control component shuffle payload, tally component shuffle payload and tally component votes payload.");
		checkArgument(allEqual(
						Stream.of(
										lastOnlineControlComponentShufflesPerBallotBoxId.values().stream().map(ControlComponentShufflePayload::getElectionEventId),
										tallyControlComponentShufflesPerBallotBoxId.values().stream().map(TallyComponentShufflePayload::getElectionEventId),
										tallyControlComponentVotesPerBallotBoxId.values().stream().map(TallyComponentVotesPayload::getElectionEventId))
								.flatMap(Function.identity())
						,
						Function.identity()),
				"The last control component shuffles, tally component shuffles and tally component votes must correspond to the same election event id.");

	}

	public Map<String, ControlComponentShufflePayload> getLastOnlineControlComponentShufflesPerBallotBoxId() {
		return lastOnlineControlComponentShufflesPerBallotBoxId;
	}

	public Map<String, TallyComponentShufflePayload> getTallyControlComponentShufflesPerBallotBoxId() {
		return tallyControlComponentShufflesPerBallotBoxId;
	}

	public Map<String, TallyComponentVotesPayload> getTallyControlComponentVotesPerBallotBoxId() {
		return tallyControlComponentVotesPerBallotBoxId;
	}

	public Map<String, TallyComponentVotesPayload> getTallyControlComponentVotesPerAuthorizationAlias() {
		return tallyControlComponentVotesPerAuthorizationAlias;
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

	public GqGroup getEncryptionGroup() {
		return tallyControlComponentShufflesPerBallotBoxId.values().stream()
				.findFirst()
				// There is at least one TallyControlComponentShufflesPayload.
				.orElseThrow(IllegalStateException::new)
				.getEncryptionGroup();
	}

	public String getElectionEventId() {
		return lastOnlineControlComponentShufflesPerBallotBoxId.values().stream()
				.findFirst()
				// There is at least one TallyControlComponentShufflesPayload.
				.orElseThrow(IllegalStateException::new)
				.getElectionEventId();
	}
}
