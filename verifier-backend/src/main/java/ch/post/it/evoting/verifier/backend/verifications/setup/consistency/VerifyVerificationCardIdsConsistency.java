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

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
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

@Component("verifySetupVerificationCardIdsConsistency")
public class VerifyVerificationCardIdsConsistency extends AbstractVerification {

	private final ElectionDataExtractionService electionDataExtractionService;

	protected VerifyVerificationCardIdsConsistency(
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
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification307.description"));
		definition.setId("03.07");
		definition.setName("VerifyVerificationCardIdsConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {

		if (verifyVerificationCardIdsConsistency(inputDirectoryPath)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification307.nok.message"));
		}
	}

	private boolean verifyVerificationCardIdsConsistency(final Path inputDirectoryPath) {
		// Input.
		final ImmutableList<VerificationCardSetContext> verificationCardSetContexts = electionDataExtractionService.getElectionEventContext(
				inputDirectoryPath).verificationCardSetContexts();
		final ImmutableList<Path> verificationCardSetIdPaths = electionDataExtractionService.getContextVerificationCardSetIdPaths(inputDirectoryPath);

		// Operation.
		return verificationCardSetIdPaths.stream()
				.parallel()
				.map(verificationCardSetIdPath -> {
					final String verificationCardSetId = verificationCardSetIdPath.getFileName().toString();

					final ImmutableList<String> setupComponentTallyDataVerificationCardIds = electionDataExtractionService.getSetupComponentTallyDataPayload(
							inputDirectoryPath, verificationCardSetId).getVerificationCardIds();

					final int numberOfEligibleVoters = verificationCardSetContexts.stream()
							.parallel()
							.filter(verificationCardSetContext -> verificationCardSetContext.getVerificationCardSetId().equals(verificationCardSetId))
							.collect(MoreCollectors.onlyElement())
							.getNumberOfEligibleVoters();

					// The SetupComponentTallyData payload ensures no verification card id are duplicates.
					return setupComponentTallyDataVerificationCardIds.size() == numberOfEligibleVoters;
				})
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

}
