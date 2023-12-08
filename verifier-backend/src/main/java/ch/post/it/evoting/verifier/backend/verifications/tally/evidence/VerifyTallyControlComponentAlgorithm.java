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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingdecrypt.Results;

@Service
public class VerifyTallyControlComponentAlgorithm {

	private final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm;
	private final VerifyTallyFilesAlgorithm verifyTallyFilesAlgorithm;

	public VerifyTallyControlComponentAlgorithm(final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm,
			final VerifyTallyFilesAlgorithm verifyTallyFilesAlgorithm) {
		this.verifyTallyControlComponentBallotBoxAlgorithm = verifyTallyControlComponentBallotBoxAlgorithm;
		this.verifyTallyFilesAlgorithm = verifyTallyFilesAlgorithm;
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
		final List<String> bb = context.getBallotBoxIds();
		final ElectionEventContext electionEventContext = context.getElectionEventContext();
		final SetupComponentPublicKeys setupComponentPublicKeys = context.getSetupComponentPublicKeys();
		final int N_bb = bb.size();

		// Input.
		final Map<String, ControlComponentShufflePayload> lastOnlineControlComponentShuffles = input.getLastOnlineControlComponentShufflesPerBallotBoxId();
		final Map<String, TallyComponentShufflePayload> tallyControlComponentShuffles = input.getTallyControlComponentShufflesPerBallotBoxId();
		final Map<String, TallyComponentVotesPayload> tallyControlComponentVotes = input.getTallyControlComponentVotesPerBallotBoxId();
		final Map<String, TallyComponentVotesPayload> L_decodedVotesbb = input.getTallyControlComponentVotesPerAuthorizationAlias();
		final Configuration electionEventConfiguration = input.getElectionEventConfiguration();
		final Results tallyControlComponentDecryptions = input.getTallyControlComponentDecryptions();
		final Delivery tallyControlComponentResults = input.getTallyControlComponentResults();
		final ch.ech.xmlns.ech_0222._1.Delivery tallyControlComponentDetailedResults = input.getTallyControlComponentDetailedResults();

		// Cross-checks.
		checkArgument(lastOnlineControlComponentShuffles.keySet().equals(new HashSet<>(bb)),
				"The last control component shuffles, the tally component shuffles and the tally component votes must correspond to the correct ballot box ids.");
		checkArgument(input.getElectionEventId().equals(ee), "The input must have the correct election event id.");

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

		final VerifyTallyFilesInput verifyTallyFilesInput = new VerifyTallyFilesInput.Builder()
				.cantonConfig(electionEventConfiguration)
				.setTallyComponentDecrypt(tallyControlComponentDecryptions)
				.setTallyComponentEch0110(tallyControlComponentResults)
				.setTallyComponentEch0222(tallyControlComponentDetailedResults)
				.setTallyComponentVotesPayloads(L_decodedVotesbb)
				.build();
		final boolean tallyFilesVerif = verifyTallyFilesAlgorithm.verifyTallyFiles(ee, verifyTallyFilesInput);

		return tallyVerif && tallyFilesVerif;
	}
}
