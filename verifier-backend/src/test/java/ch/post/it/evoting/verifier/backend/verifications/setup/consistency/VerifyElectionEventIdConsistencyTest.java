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

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;
import ch.post.it.evoting.verifier.protocol.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;

@DisplayName("VerifyElectionEventIdConsistency with")
class VerifyElectionEventIdConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyElectionEventIdConsistency(resultPublisherServiceMock,
				electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent control component code shares payloads failed")
	void inconsistentControlComponentCodeSharesPayload() {

		final ControlComponentCodeSharesPayload controlComponentCodeSharesPayloadMock = mock(ControlComponentCodeSharesPayload.class);
		when(controlComponentCodeSharesPayloadMock.getElectionEventId()).thenReturn("wrong election event ID");

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(singletonList(controlComponentCodeSharesPayloadMock)).when(extractionServiceSpy)
				.getControlComponentCodeSharesPayloadsOrderedByNodeId(datasetPath);

		final VerifyElectionEventIdConsistency verifyElectionEventIdConsistency = new VerifyElectionEventIdConsistency(
				resultPublisherServiceMock, extractionServiceSpy);

		final VerificationResult result = verifyElectionEventIdConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.nok.message"));
		assertEquals(expectedResult, result);
	}

	@Test
	@DisplayName("inconsistent setup component verification data payload failed")
	void inconsistentSetupComponentVerificationDataPayload() {

		final SetupComponentVerificationDataPayload setupComponentVerificationDataPayloadMock = mock(SetupComponentVerificationDataPayload.class);
		when(setupComponentVerificationDataPayloadMock.getElectionEventId()).thenReturn("wrong election event ID");

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(singletonList(setupComponentVerificationDataPayloadMock)).when(extractionServiceSpy)
				.getSetupComponentVerificationDataPayloadsOrderByChunkId(datasetPath);

		final VerifyElectionEventIdConsistency verifyElectionEventIdConsistency = new VerifyElectionEventIdConsistency(
				resultPublisherServiceMock, extractionServiceSpy);

		final VerificationResult result = verifyElectionEventIdConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.nok.message"));
		assertEquals(expectedResult, result);
	}

	@Test
	@DisplayName("inconsistent setup component tally data payload failed")
	void inconsistentSetupComponentTallyDataPayload() {

		final SetupComponentTallyDataPayload setupComponentTallyDataPayloadMock = mock(SetupComponentTallyDataPayload.class);
		when(setupComponentTallyDataPayloadMock.getElectionEventId()).thenReturn("wrong election event ID");

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(singletonList(setupComponentTallyDataPayloadMock)).when(extractionServiceSpy).getSetupComponentTallyDataPayloads(datasetPath);

		final VerifyElectionEventIdConsistency verifyElectionEventIdConsistency = new VerifyElectionEventIdConsistency(
				resultPublisherServiceMock, extractionServiceSpy);

		final VerificationResult result = verifyElectionEventIdConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.nok.message"));
		assertEquals(expectedResult, result);
	}

	@Test
	@DisplayName("inconsistent control component public keys payload failed")
	void inconsistentControlComponentPublicKeysPayload() {

		final ControlComponentPublicKeysPayload controlComponentPublicKeysPayloadMock = mock(ControlComponentPublicKeysPayload.class);
		when(controlComponentPublicKeysPayloadMock.getElectionEventId()).thenReturn("wrong election event ID");

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(singletonList(controlComponentPublicKeysPayloadMock)).when(extractionServiceSpy).getControlComponentPublicKeysPayloads(datasetPath);

		final VerifyElectionEventIdConsistency verifyElectionEventIdConsistency = new VerifyElectionEventIdConsistency(
				resultPublisherServiceMock, extractionServiceSpy);

		final VerificationResult result = verifyElectionEventIdConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.nok.message"));
		assertEquals(expectedResult, result);
	}
}
