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

import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifyPrimesMappingTableConsistency extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final VerifyPrimesMappingTableConsistencyAlgorithm consistencyAlgorithm;

	protected VerifyPrimesMappingTableConsistency(final ElectionDataExtractionService extractionService,
			final VerifyPrimesMappingTableConsistencyAlgorithm consistencyAlgorithm,
			final ResultPublisherService resultPublisherService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
		this.consistencyAlgorithm = consistencyAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.description"));
		definition.setId("03.13");
		definition.setName("VerifyPrimesMappingTableConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final ElectionEventContext electionEventContext = extractionService.getElectionEventContext(inputDirectoryPath);
		final Configuration configuration = extractionService.getCantonConfig(inputDirectoryPath);

		if (consistencyAlgorithm.verifyPrimesMappingTableConsistency(electionEventContext, configuration)) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.nok.message"));
		}
	}
}
