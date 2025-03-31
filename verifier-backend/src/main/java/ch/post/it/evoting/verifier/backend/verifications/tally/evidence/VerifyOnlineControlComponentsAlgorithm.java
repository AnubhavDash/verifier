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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;

/**
 * Implements the VerifyOnlineControlComponents algorithm.
 */
@Service
public class VerifyOnlineControlComponentsAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyOnlineControlComponentsAlgorithm.class);

	private final VerifyOnlineControlComponentsBallotBoxAlgorithm verifyOnlineControlComponentsBallotBoxAlgorithm;

	public VerifyOnlineControlComponentsAlgorithm(
			final VerifyOnlineControlComponentsBallotBoxAlgorithm verifyOnlineControlComponentsBallotBoxAlgorithm) {
		this.verifyOnlineControlComponentsBallotBoxAlgorithm = verifyOnlineControlComponentsBallotBoxAlgorithm;
	}

	/**
	 * Verifies the proofs of all OnlineControlComponents.
	 *
	 * @param context the context as a {@link VerifyOnlineControlComponentsContext}. Must be non-null.
	 * @param input   the input as a {@link VerifyOnlineControlComponentsInput}. Must be non-null.
	 * @return {@code true} if all proofs verify for all ballot boxes, {@code false} otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyOnlineControlComponents(final VerifyOnlineControlComponentsContext context, final VerifyOnlineControlComponentsInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Cross-group check.
		checkArgument(context.getEncryptionGroup().equals(input.getEncryptionGroup()), "The context and input must have the same encryption group.");

		// Context.
		final String ee = context.getElectionEventId();
		final ImmutableList<String> vcs = context.getVerificationCardSetIds();
		final ImmutableList<String> bb = context.getBallotBoxIds();
		final ElectionEventContext electionEventContext = context.getElectionEventContext();
		final SetupComponentPublicKeys setupComponentPublicKeys = context.getSetupComponentPublicKeys();
		final int N_bb = bb.size();

		// Input.
		final ImmutableMap<String, ControlComponentBallotBoxPayload> firstControlComponentBallotBoxes = input.getFirstControlComponentBallotBoxesPerBallotBoxId();
		final ImmutableMap<String, ImmutableList<ControlComponentShufflePayload>> onlineControlComponentShuffles = input.getControlComponentShufflesPerBallotBoxId();
		final ImmutableMap<String, SetupComponentTallyDataPayload> setupComponentTallyData = input.getSetupComponentTallyDataPerVerificationCardSetId();

		// Cross-checks.
		checkArgument(ee.equals(input.getElectionEventId()), "The context and input must have the same election event id.");
		checkArgument(setupComponentTallyData.keySet().equals(vcs.toImmutableSet()),
				"The Setup Component Tally Data must correspond to the correct verification card set id.");
		checkArgument(firstControlComponentBallotBoxes.keySet().equals(bb.toImmutableSet()),
				"The first control component ballot boxes and the control component shuffles must correspond to the correct ballot box ids.");

		// Operation.
		return IntStream.range(0, N_bb)
				.parallel()
				.mapToObj(i -> {
					final String vcs_i = vcs.get(i);
					final String bb_i = bb.get(i);
					final VerifyOnlineControlComponentsBallotBoxInput Input_bb_i = new VerifyOnlineControlComponentsBallotBoxInput(
							firstControlComponentBallotBoxes.get(bb_i), onlineControlComponentShuffles.get(bb_i), setupComponentTallyData.get(vcs_i));

					final VerifyOnlineControlComponentsBallotBoxContext Context_bb_i = new VerifyOnlineControlComponentsBallotBoxContext.Builder()
							.setVerificationCardSetId(vcs_i)
							.setBallotBoxId(bb_i)
							.setElectionEventContext(electionEventContext)
							.setSetupComponentPublicKeys(setupComponentPublicKeys)
							.build();
					final boolean bbOnlineCCVerif_i = verifyOnlineControlComponentsBallotBoxAlgorithm.verifyOnlineControlComponentsBallotBox(
							Context_bb_i, Input_bb_i);

					if (!bbOnlineCCVerif_i) {
						LOGGER.error("The online control component ballot box is invalid. [ballotBoxId: {}]", bb_i);
					}

					return bbOnlineCCVerif_i;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

}
