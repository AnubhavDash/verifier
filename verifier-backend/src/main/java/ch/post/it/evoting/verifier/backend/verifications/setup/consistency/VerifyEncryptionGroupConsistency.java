/*
 * (c) Copyright 2024 Swiss Post Ltd.
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
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dataextractors.EncryptionGroupParametersDataExtractor;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.processor.ResultPublisherService;
import ch.post.it.evoting.verifier.backend.tools.EncryptionGroupParametersExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifyEncryptionGroupConsistency extends AbstractVerification {

	private final EncryptionGroupParametersExtractionService extractionService;

	public VerifyEncryptionGroupConsistency(final ResultPublisherService resultPublisherService,
			final EncryptionGroupParametersExtractionService extractionService) {
		super(resultPublisherService);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification301.description"));
		definition.setId("03.01");
		definition.setName("VerifyEncryptionGroupConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final EncryptionGroupParametersDataExtractor.DataExtraction encryptionGroupParametersDataExtraction = extractionService.getFromElectionEventContext(
				inputDirectoryPath);

		final ImmutableList<BiFunction<Path, EncryptionGroupParametersDataExtractor.DataExtraction, Boolean>> validations = ImmutableList.of(
				this::validateControlComponentPublicKeys,
				this::validateSetupComponentVerificationDataPayloads,
				this::validateControlComponentCodeSharesPayload,
				this::validateSetupComponentTallyDataPayloads);


		final boolean sameGroupParameters = validations.stream()
				.parallel()
				.map(f -> f.apply(inputDirectoryPath, encryptionGroupParametersDataExtraction))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);

		if (sameGroupParameters) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification301.nok.message"));
		}
	}

	private boolean validateControlComponentPublicKeys(final Path inputDirectoryPath,
			final EncryptionGroupParametersDataExtractor.DataExtraction encryptionGroupParametersDataExtraction) {
		return extractionService.getFromControlComponentPublicKeys(inputDirectoryPath)
				.distinct()
				.allMatch(encryptionGroupParametersDataExtraction::equals);
	}

	private boolean validateSetupComponentVerificationDataPayloads(final Path inputDirectoryPath,
			final EncryptionGroupParametersDataExtractor.DataExtraction encryptionGroupParametersDataExtraction) {
		return extractionService.getFromSetupComponentVerificationDataPayloads(inputDirectoryPath)
				.distinct()
				.allMatch(encryptionGroupParametersDataExtraction::equals);
	}

	private boolean validateControlComponentCodeSharesPayload(final Path inputDirectoryPath,
			final EncryptionGroupParametersDataExtractor.DataExtraction encryptionGroupParametersDataExtraction) {
		return extractionService.getFromControlComponentCodeShares(inputDirectoryPath)
				.distinct()
				.allMatch(encryptionGroupParametersDataExtraction::equals);
	}

	private boolean validateSetupComponentTallyDataPayloads(final Path inputDirectoryPath,
			final EncryptionGroupParametersDataExtractor.DataExtraction encryptionGroupParametersDataExtraction) {
		return extractionService.getFromSetupComponentTallyDataPayloads(inputDirectoryPath)
				.distinct()
				.allMatch(encryptionGroupParametersDataExtraction::equals);
	}
}
