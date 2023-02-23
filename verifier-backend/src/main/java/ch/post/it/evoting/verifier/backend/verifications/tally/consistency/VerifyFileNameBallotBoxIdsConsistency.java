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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
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

/**
 * This verification ensures that the ballot box IDs in the audit archive (data set) are consistent to the ballot boxes in the election event context
 * object.
 */
@Component
public class VerifyFileNameBallotBoxIdsConsistency extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyFileNameBallotBoxIdsConsistency(
			final PathService pathService,
			final ResultPublisherService resultPublisherService,
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
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification305.description"));
		definition.setId(305);
		definition.setName("VerifyFileNameBallotBoxIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		final Set<String> ballotBoxIds = ballotBoxes.getRegexPaths().stream()
				.parallel()
				.map(Path::getFileName)
				.map(Path::toString)
				.collect(Collectors.toUnmodifiableSet());

		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContextPayload(inputDirectoryPath)
				.getElectionEventContext();
		final Set<String> payloadBallotBoxIds = electionEventContext.verificationCardSetContexts().stream()
				.parallel()
				.map(VerificationCardSetContext::ballotBoxId)
				.collect(Collectors.toUnmodifiableSet());

		// Verifying set equality is sufficient since the payload ensures that there are no duplicate ballot box IDs.
		final boolean sameBallotBoxIds = ballotBoxIds.equals(payloadBallotBoxIds);

		final VerificationResult verificationResult;
		if (sameBallotBoxIds) {
			verificationResult = VerificationResult.success(getVerificationDefinition());
		} else {
			verificationResult = VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification305.nok.message"));
		}

		return verificationResult;
	}
}
