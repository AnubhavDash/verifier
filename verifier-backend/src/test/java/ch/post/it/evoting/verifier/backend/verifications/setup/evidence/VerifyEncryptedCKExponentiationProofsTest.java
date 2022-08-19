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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

@DisplayName("VerifyEncryptedCKExponentiationProofs with")
class VerifyEncryptedCKExponentiationProofsTest extends SetupVerificationTest {

	private static VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm verifyCKProofsVerificationCardSetAlgorithm;

	@BeforeAll
	static void setUpAll() {
		verifyCKProofsVerificationCardSetAlgorithm = spy(new VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm(
				ZeroKnowledgeProofFactory.createZeroKnowledgeProof()));

		final VerifyEncryptedCKExponentiationProofsAlgorithm verifyEncryptedCKExponentiationProofsAlgorithm = new VerifyEncryptedCKExponentiationProofsAlgorithm(
				verifyCKProofsVerificationCardSetAlgorithm);
		final ExponentiationProofsVerificationExtractionService exponentiationProofsVerificationExtractionService = new ExponentiationProofsVerificationExtractionService(
				new ElectionDataExtractionService(pathService, objectMapper), pathService);
		verification = new VerifyEncryptedCKExponentiationProofs(applicationEventPublisherMock, exponentiationProofsVerificationExtractionService,
				verifyEncryptedCKExponentiationProofsAlgorithm);
	}

	@AfterEach
	void setUp() {
		reset(verifyCKProofsVerificationCardSetAlgorithm);
	}

	@Test
	@DisplayName("valid input files is successful")
	void executeTestOK() {
		final VerificationResult result = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, result);
	}

	@Test
	@DisplayName("algorithm returning false is failed")
	void algorithmReturningFalse() {
		doReturn(false).when(verifyCKProofsVerificationCardSetAlgorithm).verifyEncryptedCKExponentiationProofsVerificationCardSet(any(), any());

		final VerificationResult result = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME,
						"setup.verification.exponentiation.proofs.nok.message"));
		assertEquals(expectedResult, result);
	}
}