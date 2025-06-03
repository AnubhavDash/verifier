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

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;

import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
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
 * This verification ensures that the ballot box IDs in the dataset are consistent to the ballot boxes in the election event context object.
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
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification806.description"));
		definition.setId("08.06");
		definition.setName("VerifyFileNameBallotBoxIdsConsistency");
		definition.addVerifierEvent(TallyEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final PathNode ballotBoxes = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

		final ImmutableSet<String> ballotBoxIds = ballotBoxes.getRegexPaths().stream()
				.map(Path::getFileName)
				.map(Path::toString)
				.collect(toImmutableSet());

		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(inputDirectoryPath);
		final ImmutableSet<String> payloadBallotBoxIds = electionEventContext.verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::getBallotBoxId)
				.collect(toImmutableSet());

		// Verifying set equality is sufficient since the payload ensures that there are no duplicate ballot box IDs.
		final boolean sameBallotBoxIds = ballotBoxIds.equals(payloadBallotBoxIds);

		final VerificationResult verificationResult;
		if (sameBallotBoxIds) {
			verificationResult = VerificationResult.success(getVerificationDefinition());
		} else {
			verificationResult = VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification806.nok.message"));
		}

		return verificationResult;
	}
}
