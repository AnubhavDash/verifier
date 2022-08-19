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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence.algorithms;

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.domain.election.CombinedCorrectnessInformation;
import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.cryptoprimitives.domain.validations.Validations;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@Service
public class VerifyOnlineControlComponentsAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyOnlineControlComponentsAlgorithm.class);

	private final ElectionDataExtractionService electionDataExtractionService;
	private final VerifyOnlineControlComponentsBallotBoxAlgorithm verifyOnlineControlComponentsBallotBoxAlgorithm;

	public VerifyOnlineControlComponentsAlgorithm(
			final ElectionDataExtractionService electionDataExtractionService,
			final VerifyOnlineControlComponentsBallotBoxAlgorithm verifyOnlineControlComponentsBallotBoxAlgorithm) {
		this.electionDataExtractionService = electionDataExtractionService;
		this.verifyOnlineControlComponentsBallotBoxAlgorithm = verifyOnlineControlComponentsBallotBoxAlgorithm;
	}

	public boolean verifyOnlineControlComponents(final Path inputDirectoryPath, final String electionEventId, final List<String> ballotBoxIds) {
		checkNotNull(inputDirectoryPath);
		validateUUID(electionEventId);
		checkNotNull(ballotBoxIds);

		final List<String> ballotBoxIdsCopy = List.copyOf(ballotBoxIds);
		ballotBoxIdsCopy.forEach(Validations::validateUUID);

		// Operation.
		return ballotBoxIdsCopy.stream()
				.parallel()
				.map(bb -> {
					final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads = electionDataExtractionService.getControlComponentBallotBoxPayloads(
							inputDirectoryPath, bb);
					final List<ControlComponentShufflePayload> controlComponentShufflePayloads = electionDataExtractionService.getControlComponentShufflePayloads(
							inputDirectoryPath, bb);

					final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(inputDirectoryPath,
							electionEventId);

					final String verificationCardSetId = electionEventContext.verificationCardSetContexts().stream()
							.filter(verificationCardSetContext -> verificationCardSetContext.ballotBoxId().equals(bb))
							.collect(MoreCollectors.onlyElement())
							.verificationCardSetId();

					final SetupComponentTallyDataPayload setupComponentTallyDataPayload = electionDataExtractionService.getSetupComponentTallyDataPayload(
							inputDirectoryPath, verificationCardSetId);

					final CombinedCorrectnessInformation combinedCorrectnessInformation = electionDataExtractionService.getCombinedCorrectnessInformation(
							inputDirectoryPath, verificationCardSetId);
					final int numberOfSelectableVotingOptions = combinedCorrectnessInformation.getTotalNumberOfSelections();

					final boolean bbOnlineCCVerif_i = verifyOnlineControlComponentsBallotBoxAlgorithm.verifyOnlineControlComponentsBallotBox(
							electionEventContext, bb, numberOfSelectableVotingOptions, controlComponentBallotBoxPayloads,
							controlComponentShufflePayloads, setupComponentTallyDataPayload);

					if (!bbOnlineCCVerif_i) {
						LOGGER.error("The online control component ballot box is invalid. [ballotBoxId: {}]", bb);
					}

					return bbOnlineCCVerif_i;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

}
