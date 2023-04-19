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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

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
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

/**
 * This verification ensures that the verification card set IDs in the audit archive (data set) are consistent to the verification card sets in the
 * election event context object.
 */
@Component
public class VerifyFileNameVerificationCardSetIdsConsistency extends AbstractVerification {

	private final PathService pathService;
	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyFileNameVerificationCardSetIdsConsistency(
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
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification310.description"));
		definition.setId("03.11");
		definition.setName("VerifyFileNameVerificationCardSetIdsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final PathNode verificationCardSets = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		final Set<String> verificationCardSetIds = verificationCardSets.getRegexPaths().stream()
				.parallel()
				.map(Path::getFileName)
				.map(Path::toString)
				.collect(Collectors.toUnmodifiableSet());

		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContextPayload(inputDirectoryPath)
				.getElectionEventContext();
		final Set<String> payloadVerificationCardSetIds = electionEventContext.verificationCardSetContexts().stream()
				.parallel()
				.map(VerificationCardSetContext::verificationCardSetId)
				.collect(Collectors.toUnmodifiableSet());

		// Verifying set equality is sufficient since the payload ensures that there are no duplicate verification card set IDs.
		final boolean sameVerificationCardSetIds = verificationCardSetIds.equals(payloadVerificationCardSetIds);

		final VerificationResult verificationResult;
		if (sameVerificationCardSetIds) {
			verificationResult = VerificationResult.success(getVerificationDefinition());
		} else {
			verificationResult = VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification310.nok.message"));
		}

		return verificationResult;
	}

}

