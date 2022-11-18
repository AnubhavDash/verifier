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

import static ch.post.it.evoting.cryptoprimitives.domain.ControlComponentConstants.NODE_IDS;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static ch.post.it.evoting.cryptoprimitives.utils.Validations.allEqual;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.validations.Validations;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

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
	 * @param electionEventId ee, the election event to be verified
	 * @param ballotBoxIds bb, the ballot boxes to be verified
	 * @param numberOfSelectableVotingOptions ψ, the number of selectable voting options per ballot box
	 * @param controlComponentBallotBoxPayloads the list of ControlComponentBallotBox payloads per ballot box
	 * @param controlComponentShufflePayloads the list of OnlineControlComponentShuffle payloads per ballot box
	 * @param setupComponentTallyDataPayloads the SetupComponentTallyData payload per ballot box
	 * @param electionEventContext the election event context containing the public keys
	 * @return {@code true} if all proofs verify for all ballot boxes, {@code false} otherwise
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyOnlineControlComponents(final String electionEventId, final List<String> ballotBoxIds,
			final Map<String, Integer> numberOfSelectableVotingOptions,
			final Map<String, List<ControlComponentBallotBoxPayload>> controlComponentBallotBoxPayloads,
			final Map<String, List<ControlComponentShufflePayload>> controlComponentShufflePayloads,
			final Map<String, SetupComponentTallyDataPayload> setupComponentTallyDataPayloads, final ElectionEventContext electionEventContext,
			final SetupComponentPublicKeys setupComponentPublicKeys) {
		validateUUID(electionEventId);
		checkNotNull(ballotBoxIds);
		checkArgument(!ballotBoxIds.isEmpty());

		final List<String> ballotBoxIdsCopy = List.copyOf(ballotBoxIds);
		ballotBoxIdsCopy.forEach(Validations::validateUUID);

		final String ee = electionEventId;

		// Operation.
		return ballotBoxIdsCopy.stream()
				.parallel()
				.map(bb -> {
					final SetupComponentTallyDataPayload tallyDataPayload = setupComponentTallyDataPayloads.get(bb);
					final List<ControlComponentBallotBoxPayload> ballotBoxPayloads = controlComponentBallotBoxPayloads.get(bb);
					final List<ControlComponentShufflePayload> shufflePayloads = controlComponentShufflePayloads.get(bb);

					verifyConsistency(ballotBoxPayloads, shufflePayloads);

					final List<String> verificationCardIds = tallyDataPayload.getVerificationCardIds();
					final GroupVector<ElGamalMultiRecipientPublicKey, GqGroup> verificationCardPublicKeys = tallyDataPayload.getVerificationCardPublicKeys();
					final Map<String, ElGamalMultiRecipientPublicKey> KMap = IntStream.range(0, verificationCardIds.size())
							.boxed()
							.collect(Collectors.toMap(verificationCardIds::get, verificationCardPublicKeys::get));

					final ControlComponentBallotBoxPayload firstControlComponentBallotBoxPayload = ballotBoxPayloads.get(0);

					final VerifyOnlineControlComponentsBallotBoxContext context = new VerifyOnlineControlComponentsBallotBoxContext(
							ee, bb, numberOfSelectableVotingOptions.get(bb), electionEventContext, setupComponentPublicKeys);
					final VerifyOnlineControlComponentBallotBoxInput input = new VerifyOnlineControlComponentBallotBoxInput(
							KMap, firstControlComponentBallotBoxPayload, shufflePayloads);

					final boolean bbOnlineCCVerif_i = verifyOnlineControlComponentsBallotBoxAlgorithm.verifyOnlineControlComponentsBallotBox(context, input);

					if (!bbOnlineCCVerif_i) {
						LOGGER.error("The online control component ballot box is invalid. [ballotBoxId: {}]", bb);
					}

					return bbOnlineCCVerif_i;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private void verifyConsistency(final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads,
			final List<ControlComponentShufflePayload> controlComponentShufflePayloads) {

		checkState(allEqual(controlComponentBallotBoxPayloads.stream(), ControlComponentBallotBoxPayload::getEncryptionGroup),
				"All control component ballot box payloads must have the same group.");
		checkState(allEqual(controlComponentBallotBoxPayloads.stream(), ControlComponentBallotBoxPayload::getElectionEventId),
				"All control component ballot box payloads must have the same election event id.");
		checkState(allEqual(controlComponentBallotBoxPayloads.stream(), ControlComponentBallotBoxPayload::getBallotBoxId),
				"All control component ballot box payloads must have the same ballot box id.");
		checkState(allEqual(controlComponentBallotBoxPayloads.stream(), ControlComponentBallotBoxPayload::getConfirmedEncryptedVotes),
				"All control component ballot box payloads must have the same confirmed encrypted votes.");

		final List<Integer> ballotBoxPayloadsNodeIds = controlComponentBallotBoxPayloads.stream()
				.parallel()
				.map(ControlComponentBallotBoxPayload::getNodeId)
				.toList();
		checkState(NODE_IDS.size() == ballotBoxPayloadsNodeIds.size() && NODE_IDS.equals(new HashSet<>(ballotBoxPayloadsNodeIds)),
				"Wrong number of control component ballot box payloads.");

		checkState(allEqual(controlComponentShufflePayloads.stream(), ControlComponentShufflePayload::getEncryptionGroup),
				"All control component shuffle payloads must have the same group.");
		checkState(allEqual(controlComponentShufflePayloads.stream(), ControlComponentShufflePayload::getElectionEventId),
				"All control component shuffle payloads must have the same election event id.");
		checkState(allEqual(controlComponentShufflePayloads.stream(), ControlComponentShufflePayload::getBallotBoxId),
				"All control component shuffle payloads must have the same ballot box id.");

		final List<Integer> shufflePayloadsNodeIds = controlComponentShufflePayloads.stream()
				.parallel()
				.map(ControlComponentShufflePayload::getNodeId)
				.toList();
		checkState(NODE_IDS.size() == shufflePayloadsNodeIds.size() && NODE_IDS.equals(new HashSet<>(shufflePayloadsNodeIds)),
				"Wrong number of control component shuffle payloads.");

		// Cross-checks.
		checkState(controlComponentBallotBoxPayloads.get(0).getEncryptionGroup().equals(controlComponentShufflePayloads.get(0).getEncryptionGroup()),
				"The control component ballot box and shuffle payloads must have the same group.");
		checkState(controlComponentBallotBoxPayloads.get(0).getElectionEventId().equals(controlComponentShufflePayloads.get(0).getElectionEventId()),
				"The control component ballot box and shuffle payloads must have the same election event id.");
		checkState(controlComponentBallotBoxPayloads.get(0).getBallotBoxId().equals(controlComponentShufflePayloads.get(0).getBallotBoxId()),
				"The control component ballot box and shuffle payloads must have the same ballot box id.");
	}

}
