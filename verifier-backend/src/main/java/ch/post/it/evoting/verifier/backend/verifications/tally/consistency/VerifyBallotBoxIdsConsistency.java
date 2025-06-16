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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet.toImmutableSet;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component
public class VerifyBallotBoxIdsConsistency extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService extractionService;

	protected VerifyBallotBoxIdsConsistency(final ResultPublisherService resultPublisherService, final PathService pathService,
			final ElectionDataExtractionService extractionService) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification805.description"));
		definition.setId("08.05");
		definition.setName("VerifyBallotBoxIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyBallotBoxIdsConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification805.nok.message"));
		}
	}

	private boolean verifyBallotBoxIdsConsistency(final Path inputDirectoryPath) {
		// Input.
		final ImmutableList<Path> ballotBoxIdPaths = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath)
				.getRegexPaths();

		// Operation.
		return ballotBoxIdPaths.stream()
				.parallel()
				.map(ballotBoxIdPath -> {
					final String ballotBoxId = ballotBoxIdPath.getFileName().toString();

					final ImmutableSet<String> controlComponentBallotBoxIds = extractionService.getControlComponentBallotBoxPayloadsOrderedByNodeId(
									inputDirectoryPath, ballotBoxId)
							.map(ControlComponentBallotBoxPayload::getBallotBoxId)
							.collect(toImmutableSet());

					final ImmutableSet<String> onlineControlComponentShuffleIds = extractionService.getControlComponentShufflePayloadsOrderedByNodeId(
									inputDirectoryPath, ballotBoxId)
							.map(ControlComponentShufflePayload::getBallotBoxId)
							.collect(toImmutableSet());

					final ImmutableSet<String> tallyControlComponentShuffleBallotBoxIds = ImmutableSet.of(
							extractionService.getTallyComponentShufflePayload(inputDirectoryPath, ballotBoxId).getBallotBoxId());

					final ImmutableSet<String> tallyControlComponentVotesBallotBoxIds = ImmutableSet.of(
							extractionService.getTallyComponentVotesPayload(ballotBoxIdPath).getBallotBoxId());

					final boolean isConsistentAcrossFiles = controlComponentBallotBoxIds.equals(onlineControlComponentShuffleIds) &&
							controlComponentBallotBoxIds.equals(tallyControlComponentShuffleBallotBoxIds) &&
							controlComponentBallotBoxIds.equals(tallyControlComponentVotesBallotBoxIds);

					final boolean matchesPathName = ImmutableSet.of(ballotBoxId).equals(controlComponentBallotBoxIds);

					return isConsistentAcrossFiles && matchesPathName;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}
}
