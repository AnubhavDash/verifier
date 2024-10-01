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
import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap.toImmutableMap;
import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentNode;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;

/**
 * Regroups the input values needed by the VerifyOnlineControlComponents algorithm.
 *
 * <ul>
 *     <li>(vc<sub>1</sub>, E1<sub>1</sub>, E1<sub>1</sub>_tilde, E2<sub>1</sub>, pi<sub>exp,1</sub>, pi<sub>EqEnc,1</sub>), the first Control
 *     		Component Ballot Boxes for all bb<sub>i</sub>. Non-null.</li>
 *     <li>{c<sub>mix,j</sub>, pi<sub>mix,j</sub>, c<sub>dec,j</sub>, pi<sub>dec,j</sub>}<sup>4</sup><sub>j=1</sub>, the Control Component Shuffles
 *     		for all bb<sub>i</sub>. Non-null.</li>
 *     <li>(vc, K), the Setup Component Tally data for all vcs<sub>i</sub>. Non-null.</li>
 * </ul>
 */
public class VerifyOnlineControlComponentsInput {

	private final ImmutableMap<String, ControlComponentBallotBoxPayload> firstControlComponentBallotBoxesPerBallotBoxId;
	private final ImmutableMap<String, ImmutableList<ControlComponentShufflePayload>> controlComponentShufflesPerBallotBoxId;
	private final ImmutableMap<String, SetupComponentTallyDataPayload> setupComponentTallyDataPerVerificationCardSetId;

	public VerifyOnlineControlComponentsInput(final Stream<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads,
			final Stream<ControlComponentShufflePayload> controlComponentShufflePayloads,
			final Stream<SetupComponentTallyDataPayload> setupComponentTallyDataPayloads) {
		this.firstControlComponentBallotBoxesPerBallotBoxId = checkNotNull(controlComponentBallotBoxPayloads)
				.filter(controlComponentBallotBoxPayload ->
						controlComponentBallotBoxPayload.getNodeId() == ControlComponentNode.ids().stream().sorted().collect(toImmutableList())
								.get(0))
				.collect(toImmutableMap(ControlComponentBallotBoxPayload::getBallotBoxId, Function.identity()));
		this.controlComponentShufflesPerBallotBoxId = ImmutableMap.from(checkNotNull(controlComponentShufflePayloads)
				.parallel()
				.collect(Collectors.groupingBy(ControlComponentShufflePayload::getBallotBoxId, toImmutableList())));
		this.setupComponentTallyDataPerVerificationCardSetId = checkNotNull(setupComponentTallyDataPayloads)
				.collect(toImmutableMap(SetupComponentTallyDataPayload::getVerificationCardSetId, Function.identity()));

		checkArgument(firstControlComponentBallotBoxesPerBallotBoxId.keySet().equals(controlComponentShufflesPerBallotBoxId.keySet()),
				"The first control component ballot boxes and control component shuffles must correspond to the same ballot box ids.");
		checkArgument(!firstControlComponentBallotBoxesPerBallotBoxId.isEmpty(),
				"There must be at least one control component ballot boxes and control component shuffles.");
		checkArgument(allEqual(
						Stream.of(
										firstControlComponentBallotBoxesPerBallotBoxId.values().stream().map(ControlComponentBallotBoxPayload::getElectionEventId),
										controlComponentShufflesPerBallotBoxId.values().stream().flatMap(ImmutableList::stream)
												.map(ControlComponentShufflePayload::getElectionEventId),
										setupComponentTallyDataPerVerificationCardSetId.values().stream().map(SetupComponentTallyDataPayload::getElectionEventId))
								.flatMap(Function.identity()),
						Function.identity()),
				"The first control component ballot boxes, the control component shuffles and the setup component tally data must correspond to the same election event id.");
	}

	public ImmutableMap<String, ControlComponentBallotBoxPayload> getFirstControlComponentBallotBoxesPerBallotBoxId() {
		return firstControlComponentBallotBoxesPerBallotBoxId;
	}

	public ImmutableMap<String, ImmutableList<ControlComponentShufflePayload>> getControlComponentShufflesPerBallotBoxId() {
		return controlComponentShufflesPerBallotBoxId;
	}

	public ImmutableMap<String, SetupComponentTallyDataPayload> getSetupComponentTallyDataPerVerificationCardSetId() {
		return setupComponentTallyDataPerVerificationCardSetId;
	}

	public GqGroup getEncryptionGroup() {
		return firstControlComponentBallotBoxesPerBallotBoxId.values().stream()
				.findFirst()
				// There is at least one ControlComponentBallotBoxPayload.
				.orElseThrow(IllegalStateException::new)
				.getEncryptionGroup();
	}

	public String getElectionEventId() {
		return firstControlComponentBallotBoxesPerBallotBoxId.values().stream()
				.findFirst()
				// There is at least one controlComponentBallotBoxPayload.
				.orElseThrow(IllegalStateException::new)
				.getElectionEventId();
	}
}
