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

import java.math.BigInteger;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
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
@SuppressWarnings("java:S117")
public class VerifyEncryptionParameters extends AbstractVerification {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyEncryptionParameters.class);

	private final ElectionDataExtractionService extractionService;
	private final VerifyEncryptionParametersAlgorithm verifyEncryptionParametersAlgorithm;

	protected VerifyEncryptionParameters(
			final ElectionDataExtractionService extractionService,
			final ResultPublisherService resultPublisherService,
			final VerifyEncryptionParametersAlgorithm verifyEncryptionParametersAlgorithm) {
		super(resultPublisherService);
		this.extractionService = extractionService;
		this.verifyEncryptionParametersAlgorithm = verifyEncryptionParametersAlgorithm;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final VerificationDefinition definition = new VerificationDefinition();
		definition.setBlock(SetupVerificationSuite.BLOCK_NAME);
		definition.setCategory(Category.EVIDENCE);
		definition.setDescription(
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification500.description"));
		definition.setId("05.01");
		definition.setName("VerifyEncryptionParameters");
		definition.addVerifierEvent(SetupEvent.TYPE);
		return definition;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) {
		// Deserialize file.
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(inputDirectoryPath);
		final GqGroup encryptionGroup = electionEventContextPayload.getEncryptionGroup();

		// Extract parameters.
		final BigInteger p_hat = encryptionGroup.getP();
		final BigInteger q_hat = encryptionGroup.getQ();
		final GqElement g_hat = encryptionGroup.getGenerator();
		final String seed = electionEventContextPayload.getSeed();

		final VerificationResult verificationResult;
		if (!verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p_hat, q_hat, g_hat, seed)) {
			verificationResult = VerificationResult.failure(getVerificationDefinition(),
					TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification500.nok.message"));
		} else {
			verificationResult = VerificationResult.success(getVerificationDefinition());
			LOGGER.info("Successfully verified the encryption parameters p, q, g. [p: {}, q: {}, g: {}]", p_hat, q_hat, g_hat);
		}

		return verificationResult;
	}

}
