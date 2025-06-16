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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Streams;

import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyNodeIdsConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyNodeIdsConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokControlComponentPublicKeysCompleteness() {
		final Stream<ControlComponentPublicKeysPayload> stream = electionDataExtractionService.getControlComponentPublicKeysPayloads(
				datasetPath).skip(1);

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(extractionServiceSpy).getControlComponentPublicKeysPayloads(datasetPath);

		final VerifyNodeIdsConsistency verificationWithMock = new VerifyNodeIdsConsistency(resultPublisherServiceMock, extractionServiceSpy);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification302.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokControlComponentPublicKeysUniqueness() {
		final Stream<ControlComponentPublicKeysPayload> stream = electionDataExtractionService.getControlComponentPublicKeysPayloads(
				datasetPath)
				.limit(2)
				.flatMap(payload -> Streams.concat(Stream.of(payload), Stream.of(payload)));

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(extractionServiceSpy).getControlComponentPublicKeysPayloads(datasetPath);

		final VerifyNodeIdsConsistency verificationWithMock = new VerifyNodeIdsConsistency(resultPublisherServiceMock, extractionServiceSpy);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification302.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
