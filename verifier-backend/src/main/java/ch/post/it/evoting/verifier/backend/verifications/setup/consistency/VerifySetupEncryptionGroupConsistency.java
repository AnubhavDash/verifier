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
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.google.common.collect.Streams;

import ch.post.it.evoting.verifier.backend.AbstractVerification;
import ch.post.it.evoting.verifier.backend.Category;
import ch.post.it.evoting.verifier.backend.VerificationDefinition;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.domain.EncryptionGroupParameters;
import ch.post.it.evoting.verifier.backend.event.SetupEvent;
import ch.post.it.evoting.verifier.backend.tools.EncryptionGroupParametersExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;

@Component
public class VerifySetupEncryptionGroupConsistency extends AbstractVerification {

	private final EncryptionGroupParametersExtractionService extractionService;

	public VerifySetupEncryptionGroupConsistency(final ApplicationEventPublisher applicationEventPublisher,
			final EncryptionGroupParametersExtractionService extractionService) {
		super(applicationEventPublisher);
		this.extractionService = extractionService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.CONSISTENCY);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification300.description"));
		definition.setId(300);
		definition.setName("VerifyEncryptionGroupConsistency");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) {
		final EncryptionGroupParameters encryptionGroupParameters = extractionService.getFromElectionEventContext(inputDirectoryPath);

		final Stream<EncryptionGroupParameters> allGroupParameters = Streams.concat(
				Stream.of(extractionService.getFromEncryptionParameters(inputDirectoryPath)),
				extractionService.getFromControlComponentPublicKeys(inputDirectoryPath),
				extractionService.getFromSetupComponentVerificationDataPayloads(inputDirectoryPath),
				extractionService.getFromControlComponentCodeShares(inputDirectoryPath),
				extractionService.getFromSetupComponentTallyDataPayloads(inputDirectoryPath)
		);

		if (allGroupParameters.allMatch(group -> group.equals(encryptionGroupParameters))) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification300.nok.message"));
		}
	}
}
