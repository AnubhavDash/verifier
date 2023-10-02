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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.SetupComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component
public class VerifyOnlineControlComponents extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;
	private final VerifyOnlineControlComponentsAlgorithm verifyOnlineControlComponentsAlgorithm;

	public VerifyOnlineControlComponents(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService,
			final VerifyOnlineControlComponentsAlgorithm verifyOnlineControlComponentsAlgorithm) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
		this.verifyOnlineControlComponentsAlgorithm = verifyOnlineControlComponentsAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME,
				"tally.verification500.description"));
		definition.setId("10.01");
		definition.setName("VerifyOnlineControlComponents");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(
				inputDirectoryPath);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();

		final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload = electionDataExtractionService.getSetupComponentPublicKeysPayload(
				inputDirectoryPath);
		final SetupComponentPublicKeys setupComponentPublicKeys = setupComponentPublicKeysPayload.getSetupComponentPublicKeys();

		final String electionEventId = electionEventContext.electionEventId();
		final List<String> ballotBoxIds = electionEventContext.verificationCardSetContexts().stream()
				.parallel()
				.map(VerificationCardSetContext::ballotBoxId)
				.toList();

		final Map<String, List<ControlComponentBallotBoxPayload>> controlComponentBallotBoxesByBallotBoxId = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						inputDirectoryPath)
				.parallel()
				.collect(Collectors.groupingByConcurrent(ControlComponentBallotBoxPayload::getBallotBoxId));
		final Map<String, List<ControlComponentShufflePayload>> controlComponentShufflesByBallotBoxId = electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
						inputDirectoryPath)
				.parallel()
				.collect(Collectors.groupingByConcurrent(ControlComponentShufflePayload::getBallotBoxId));

		final Map<String, SetupComponentTallyDataPayload> setupComponentTallyDataByBallotBoxId = ballotBoxIds.stream()
				.parallel()
				.collect(Collectors.toMap(Function.identity(), bb -> {
					final String verificationCardSetId = electionEventContext.verificationCardSetContexts().stream()
							.filter(verificationCardSetContext -> verificationCardSetContext.ballotBoxId().equals(bb))
							.collect(MoreCollectors.onlyElement())
							.verificationCardSetId();

					return electionDataExtractionService.getSetupComponentTallyDataPayload(
							inputDirectoryPath, verificationCardSetId);
				}));

		final ConcurrentMap<String, Integer> numberOfSelectionsByBallotBoxId = ballotBoxIds.stream()
				.parallel()
				.collect(Collectors.toConcurrentMap(Function.identity(), bb -> electionEventContext.verificationCardSetContexts().stream()
						.filter(verificationCardSetContext -> verificationCardSetContext.ballotBoxId().equals(bb))
						.collect(MoreCollectors.onlyElement())
						.getNumberOfSelections()));

		final VerificationResult verificationResult;
		if (verifyOnlineControlComponentsAlgorithm.verifyOnlineControlComponents(electionEventId, ballotBoxIds, numberOfSelectionsByBallotBoxId,
				controlComponentBallotBoxesByBallotBoxId, controlComponentShufflesByBallotBoxId, setupComponentTallyDataByBallotBoxId,
				electionEventContext, setupComponentPublicKeys
		)) {
			verificationResult = VerificationResult.success(getVerificationDefinition());
		} else {
			verificationResult = VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification500.nok.message"));
		}

		return verificationResult;
	}
}
