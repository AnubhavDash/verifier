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

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.TallyEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

@Component("verifyTallyFileNameNodeIdsConsistency")
public class VerifyFileNameNodeIdsConsistency extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyFileNameNodeIdsConsistency(
			final ResultPublisherService resultPublisherService,
			final PathService pathService,
			final ElectionDataExtractionService electionDataExtractionService) {
		super(resultPublisherService);
		this.pathService = pathService;
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(TallyVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification803.description"));
		definition.setId("08.03");
		definition.setName("VerifyFileNameNodeIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyFileNameNodeIdsConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification803.nok.message"));
		}
	}

	private boolean verifyFileNameNodeIdsConsistency(final Path inputDirectoryPath) {
		// Input.
		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		// Operation.
		return ballotBoxes.getRegexPaths().stream()
				.parallel()
				.map(ballotBoxPath -> validateControlComponentBallotBox(ballotBoxPath) && validateControlComponentShuffle(ballotBoxPath))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean validateControlComponentBallotBox(final Path ballotBoxPath) {
		final PathNode controlComponentBallotBox = pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_BALLOT_BOX,
				ballotBoxPath);

		return controlComponentBallotBox.getRegexPaths().stream()
				.parallel()
				.map(path -> {
					final String fileName = path.getFileName().toString();
					final String nodeIdGroup = pathService.getRegexGroup(StructureKey.CONTROL_COMPONENT_BALLOT_BOX, fileName, 1);
					final int fileNodeId = Integer.parseInt(nodeIdGroup);

					final int payloadNodeId = electionDataExtractionService.getControlComponentBallotBoxPayload(path).getNodeId();

					return fileNodeId == payloadNodeId;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	private boolean validateControlComponentShuffle(final Path ballotBoxPath) {
		final PathNode onlineControlComponentShuffle = pathService.buildFromDynamicAncestorPath(StructureKey.CONTROL_COMPONENT_SHUFFLE,
				ballotBoxPath);

		return onlineControlComponentShuffle.getRegexPaths().stream()
				.parallel()
				.map(path -> {
					final String fileName = path.getFileName().toString();
					final String nodeIdGroup = pathService.getRegexGroup(StructureKey.CONTROL_COMPONENT_SHUFFLE, fileName, 1);
					final int fileNodeId = Integer.parseInt(nodeIdGroup);

					final int payloadNodeId = electionDataExtractionService.getControlComponentShufflePayload(path).getNodeId();

					return fileNodeId == payloadNodeId;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}
}
