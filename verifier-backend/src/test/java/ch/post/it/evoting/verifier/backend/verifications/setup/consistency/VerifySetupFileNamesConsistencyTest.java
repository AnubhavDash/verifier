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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentPublicKeysPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

class VerifySetupFileNamesConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifySetupFileNamesConsistency(resultPublisherServiceMock, pathService,
				setupComponentVerificationDataPayloadDataExtractor,
				controlComponentPublicKeysPayloadDataExtractor, controlComponentCodeSharesPayloadDataExtractor);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNok() {
		final ControlComponentPublicKeysPayloadDataExtractor controlComponentPublicKeysPayloadDataExtractorMock = mock(
				ControlComponentPublicKeysPayloadDataExtractor.class);

		final ControlComponentPublicKeysPayload firstPublicKeysPayload =
				electionDataExtractionService.getControlComponentPublicKeysPayloads(datasetPath).get(0);

		final String electionEventId = firstPublicKeysPayload.getElectionEventId();
		final int nodeId = firstPublicKeysPayload.getControlComponentPublicKeys().nodeId();

		when(controlComponentPublicKeysPayloadDataExtractorMock.load(any()))
				.thenReturn(new ControlComponentPublicKeysPayloadDataExtractor.DataExtraction(nodeId, electionEventId));

		final VerifySetupFileNamesConsistency failingVerification = new VerifySetupFileNamesConsistency(resultPublisherServiceMock, pathService,
				setupComponentVerificationDataPayloadDataExtractor, controlComponentPublicKeysPayloadDataExtractorMock,
				controlComponentCodeSharesPayloadDataExtractor);
		final VerificationResult verificationResult = failingVerification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification302.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}