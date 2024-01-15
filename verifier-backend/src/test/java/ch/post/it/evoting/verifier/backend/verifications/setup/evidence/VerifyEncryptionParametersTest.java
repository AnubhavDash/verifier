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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.internal.securitylevel.SecurityLevelConfig;
import ch.post.it.evoting.cryptoprimitives.internal.securitylevel.SecurityLevelInternal;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyEncryptionParametersTest extends SetupVerificationTest {

	private static VerifyEncryptionParametersAlgorithm verifyEncryptionParametersAlgorithm;

	@BeforeAll
	static void setUpAll() {
		final ElGamal elGamal = ElGamalFactory.createElGamal();
		verifyEncryptionParametersAlgorithm = spy(new VerifyEncryptionParametersAlgorithm(elGamal));

		verification = new VerifyEncryptionParameters(electionDataExtractionService, resultPublisherServiceMock, verifyEncryptionParametersAlgorithm);
	}

	@BeforeEach
	void setUp() {
		reset(verifyEncryptionParametersAlgorithm);
	}

	@Test
	void executeTestOK() {
		final VerificationResult result;
		try (final MockedStatic<SecurityLevelConfig> mocked = mockStatic(SecurityLevelConfig.class)) {
			mocked.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevelInternal.STANDARD);
			result = verification.verify(datasetPath);
		}

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, result);
	}

	@Test
	void executeTestNOK() {
		doReturn(false).when(verifyEncryptionParametersAlgorithm).verifyEncryptionParameters(any(), any(), any(), any());

		final VerificationResult result = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification500.nok.message"));
		assertEquals(expectedResult, result);
	}
}
