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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.SetupComponentPublicKeysPayload;
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
public class VerifyKeyGenerationSchnorrProofs extends AbstractVerification {

	private final ElectionDataExtractionService extractionService;
	private final VerifyKeyGenerationSchnorrProofsAlgorithm verifyKeyGenerationSchnorrProofsAlgorithm;

	public VerifyKeyGenerationSchnorrProofs(
			final ResultPublisherService resultPublisherService,
			final ElectionDataExtractionService extractionService,
			final VerifyKeyGenerationSchnorrProofsAlgorithm verifyEncryptedCKExponentiationProofsAlgorithm) {
		super(resultPublisherService);
		this.extractionService = extractionService;
		this.verifyKeyGenerationSchnorrProofsAlgorithm = verifyEncryptedCKExponentiationProofsAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
				"setup.verification503.description"));
		definition.setId(503);
		definition.setName("VerifyKeyGenerationSchnorrProofs");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(inputDirectoryPath);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();

		final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload = extractionService.getSetupComponentPublicKeysPayload(
				inputDirectoryPath);
		final SetupComponentPublicKeys setupComponentPublicKeys = setupComponentPublicKeysPayload.getSetupComponentPublicKeys();

		final VerifyKeyGenerationSchnorrProofsContext context = new VerifyKeyGenerationSchnorrProofsContext(
				electionEventContextPayload.getEncryptionGroup(), electionEventContext.electionEventId(),
				electionEventContext.getMaxNumberOfWriteInFields() + 1);
		final VerifyKeyGenerationSchnorrProofsInput input = new VerifyKeyGenerationSchnorrProofsInput(setupComponentPublicKeys);

		final boolean result = verifyKeyGenerationSchnorrProofsAlgorithm.verifyKeyGenerationSchnorrProofs(context, input);

		if (result) {
			return VerificationResult.success(getVerificationDefinition());
		} else {
			return VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification503.nok.message"));
		}
	}
}