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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Streams;

import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.EncryptionGroupParametersExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;
import ch.post.it.evoting.verifier.backend.domain.EncryptionGroupParameters;

@DisplayName("VerifySetupEncryptionGroupConsistency with")
class VerifySetupEncryptionGroupConsistencyTest extends SetupVerificationTest {

	private static EncryptionGroupParameters differentEncryptionGroupParameters;
	private static EncryptionGroupParametersExtractionService extractionService;

	@BeforeAll
	static void setupAll() {
		differentEncryptionGroupParameters = new EncryptionGroupParameters("p", "q", "g");
		extractionService = new EncryptionGroupParametersExtractionService(pathService, objectMapper);
		verification = new VerifySetupEncryptionGroupConsistency(applicationEventPublisherMock, extractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("ElectionEventContext having different encryption group parameters fails")
	void invalidElectionEventContextPayload() {
		final EncryptionGroupParametersExtractionService groupParametersExtractorMock = spy(extractionService);
		doReturn(differentEncryptionGroupParameters).when(groupParametersExtractorMock).getFromElectionEventContext(datasetPath);

		assertInvalidVerification(groupParametersExtractorMock);
	}

	@Test
	@DisplayName("EncryptionParameters having different encryption group parameters fails")
	void invalidEncryptionParametersPayload() {
		final EncryptionGroupParametersExtractionService groupParametersExtractorMock = spy(extractionService);
		doReturn(differentEncryptionGroupParameters).when(groupParametersExtractorMock).getFromEncryptionParameters(datasetPath);

		assertInvalidVerification(groupParametersExtractorMock);
	}

	@Test
	@DisplayName("ControlComponentPublicKeys containing different encryption group parameters fails")
	void invalidControlComponentPublicKeysPayload() {
		final EncryptionGroupParametersExtractionService groupParametersExtractorMock = spy(extractionService);
		final Stream<EncryptionGroupParameters> controlComponentPublicKeysParameters = extractionService.getFromControlComponentPublicKeys(
				datasetPath);
		doReturn(Streams.concat(Stream.of(differentEncryptionGroupParameters), controlComponentPublicKeysParameters)).when(
				groupParametersExtractorMock).getFromControlComponentPublicKeys(datasetPath);

		assertInvalidVerification(groupParametersExtractorMock);
	}

	@Test
	@DisplayName("SetupComponentVerificationData containing different encryption group parameters fails")
	void invalidSetupComponentVerificationDataPayload() {
		final EncryptionGroupParametersExtractionService groupParametersExtractorMock = spy(extractionService);
		final Stream<EncryptionGroupParameters> controlComponentPublicKeysParameters = extractionService.getFromSetupComponentVerificationDataPayloads(
				datasetPath);
		doReturn(Streams.concat(Stream.of(differentEncryptionGroupParameters), controlComponentPublicKeysParameters)).when(
				groupParametersExtractorMock).getFromSetupComponentVerificationDataPayloads(datasetPath);

		assertInvalidVerification(groupParametersExtractorMock);
	}

	@Test
	@DisplayName("NodeContributions containing different encryption group parameters fails")
	void invalidNodeContributions() {
		final EncryptionGroupParametersExtractionService groupParametersExtractorMock = spy(extractionService);
		final Stream<EncryptionGroupParameters> controlComponentPublicKeysParameters = extractionService.getFromControlComponentCodeShares(
				datasetPath);
		doReturn(Streams.concat(Stream.of(differentEncryptionGroupParameters), controlComponentPublicKeysParameters)).when(
				groupParametersExtractorMock).getFromControlComponentCodeShares(datasetPath);

		assertInvalidVerification(groupParametersExtractorMock);
	}

	@Test
	@DisplayName("SetupComponentTallyData containing different encryption group parameters fails")
	void invalidSetupComponentTallyDataPayload() {
		final EncryptionGroupParametersExtractionService groupParametersExtractorMock = spy(extractionService);
		final Stream<EncryptionGroupParameters> controlComponentPublicKeysParameters = extractionService.getFromSetupComponentTallyDataPayloads(
				datasetPath);
		doReturn(Streams.concat(Stream.of(differentEncryptionGroupParameters), controlComponentPublicKeysParameters)).when(
				groupParametersExtractorMock).getFromSetupComponentTallyDataPayloads(datasetPath);

		assertInvalidVerification(groupParametersExtractorMock);
	}

	private void assertInvalidVerification(final EncryptionGroupParametersExtractionService groupParametersExtractorMock) {
		final VerifySetupEncryptionGroupConsistency invalidVerification = new VerifySetupEncryptionGroupConsistency(applicationEventPublisherMock,
				groupParametersExtractorMock);
		final VerificationResult verificationResult = invalidVerification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(invalidVerification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification30.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}