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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet.toImmutableSet;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

/**
 * This verification ensures that the verification card set ids in the dataset are consistent to the verification card sets in the election event
 * context object.
 */
@Component
public class VerifyFileNameVerificationCardSetIdsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyFileNameVerificationCardSetIdsConsistency(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService electionDataExtractionService) {
		super(resultPublisherService);
		this.electionDataExtractionService = electionDataExtractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification306.description"));
		definition.setId("03.06");
		definition.setName("VerifyFileNameVerificationCardSetIdsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyFileNameVerificationCardSetIdsConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification306.nok.message"));
		}
	}

	private boolean verifyFileNameVerificationCardSetIdsConsistency(final Path inputDirectoryPath) {
		// Input.
		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(inputDirectoryPath);
		final ImmutableList<Path> verificationCardSetIdPaths = electionDataExtractionService.getContextVerificationCardSetIdPaths(inputDirectoryPath);

		// Operation.
		final ImmutableSet<String> contextVerificationCardSetIds = verificationCardSetIdPaths.stream()
				.map(Path::getFileName)
				.map(Path::toString)
				.collect(toImmutableSet());
		final ImmutableSet<String> electionEventContextVerificationCardSetIds = electionEventContext.verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::getVerificationCardSetId)
				.collect(toImmutableSet());

		// Verifying set equality is sufficient since the ElectionEventContext constructor ensures that there are no duplicate verification card set IDs.
		return contextVerificationCardSetIds.equals(electionEventContextVerificationCardSetIds);
	}

}

