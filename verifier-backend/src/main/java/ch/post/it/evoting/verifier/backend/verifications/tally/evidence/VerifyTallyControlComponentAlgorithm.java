/*
 * (c) Copyright 2025 Swiss Post Ltd.
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

import org.springframework.stereotype.Service;

import ch.ech.xmlns.ech_0222._3.Delivery;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;

@Service
public class VerifyTallyControlComponentAlgorithm {

	private final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm;
	private final VerifyECH0222Algorithm verifyECH0222Algorithm;

	public VerifyTallyControlComponentAlgorithm(final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm,
			final VerifyECH0222Algorithm verifyECH0222Algorithm) {
		this.verifyTallyControlComponentBallotBoxAlgorithm = verifyTallyControlComponentBallotBoxAlgorithm;
		this.verifyECH0222Algorithm = verifyECH0222Algorithm;
	}

	/**
	 * Verifies the Tally control component’s operations.
	 *
	 * @param input the input for the VerifyTallyControlComponent algorithm as a {@link VerifyTallyControlComponentInput}. Not null.
	 * @return true if the operations are valid for all ballot boxes, false otherwise.
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyTallyControlComponent(final VerifyTallyControlComponentContext context, final VerifyTallyControlComponentInput input) {
		checkNotNull(context);
		checkNotNull(input);

		// Cross-group check.
		checkArgument(context.getEncryptionGroup().equals(input.getEncryptionGroup()), "The context and input must have the same encryption group.");

		// Context.
		final String ee = context.getElectionEventId();
		final ImmutableList<String> bb = context.getBallotBoxIds();
		final ElectionEventContext electionEventContext = context.getElectionEventContext();
		final SetupComponentPublicKeys setupComponentPublicKeys = context.getSetupComponentPublicKeys();
		final int N_bb = bb.size();

		// Input.
		final ImmutableMap<String, ControlComponentShufflePayload> lastOnlineControlComponentShuffles = input.getLastOnlineControlComponentShufflesPerBallotBoxId();
		final ImmutableMap<String, TallyComponentShufflePayload> tallyControlComponentShuffles = input.getTallyControlComponentShufflesPerBallotBoxId();
		final ImmutableMap<String, TallyComponentVotesPayload> tallyControlComponentVotes = input.getTallyControlComponentVotesPerBallotBoxId();
		final Configuration configurationXML = input.getElectionEventConfiguration();
		final Delivery eCH0222XML = input.getTallyControlComponentDetailedResults();
		final ImmutableMap<AuthorizationType, TallyComponentVotesPayload> Map_decodedVotes_Map_writeIns = input.getTallyControlComponentVotesPerAuthorizationName();

		// Cross-checks.
		checkArgument(ee.equals(input.getElectionEventId()), "The context and input must have the same election event id.");
		checkArgument(lastOnlineControlComponentShuffles.keySet().equals(bb.toImmutableSet()),
				"The last control component shuffles, the tally component shuffles and the tally component votes must correspond to the correct ballot box ids.");

		// Operation.
		final boolean tallyVerif = IntStream.range(0, N_bb)
				.parallel()
				.mapToObj(i -> {
					final String bb_i = bb.get(i);

					final VerifyTallyControlComponentBallotBoxInput Input_bb_i = new VerifyTallyControlComponentBallotBoxInput(
							lastOnlineControlComponentShuffles.get(bb_i), tallyControlComponentShuffles.get(bb_i),
							tallyControlComponentVotes.get(bb_i));

					final VerifyTallyControlComponentBallotBoxContext Context_bb_i = new VerifyTallyControlComponentBallotBoxContext.Builder()
							.setElectionEventId(ee)
							.setBallotBoxId(bb_i)
							.setElectionEventContext(electionEventContext)
							.setSetupComponentPublicKeys(setupComponentPublicKeys)
							.build();
					return verifyTallyControlComponentBallotBoxAlgorithm.verifyTallyControlComponentBallotBox(Context_bb_i, Input_bb_i);
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		final VerifyECH0222Input Input_eCH0222 = new VerifyECH0222Input.Builder()
				.setCantonConfig(configurationXML)
				.setTallyComponentEch0222(eCH0222XML)
				.setTallyControlComponentVotesPerAuthorizationName(Map_decodedVotes_Map_writeIns)
				.build();

		final boolean eCH0222Verif = verifyECH0222Algorithm.verifyECH0222(Input_eCH0222);

		return tallyVerif && eCH0222Verif;
	}
}
