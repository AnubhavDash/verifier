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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyVerificationCardSetIdsConsistencyTest extends SetupVerificationTest {

	private static ElectionDataExtractionService electionDataExtractionService;

	@BeforeAll
	static void setUpAll() {
		electionDataExtractionService = new ElectionDataExtractionService(pathService, objectMapper);

		verification = new VerifyVerificationCardSetIdsConsistency(applicationEventPublisherMock, pathService, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent verification card set ids is failed")
	void inconsistentVerificationCardIds() {
		SetupComponentVerificationDataPayload setupComponentVerificationDataPayloadMock = mock(SetupComponentVerificationDataPayload.class);
		when(setupComponentVerificationDataPayloadMock.getVerificationCardSetId()).thenReturn("4c6f28483a324d84b5363261aa2062f6");

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(Collections.singletonList(setupComponentVerificationDataPayloadMock)).when(electionDataExtractionServiceSpy)
				.deserializeSetupComponentVerificationDataPayload(any());

		final VerifyVerificationCardSetIdsConsistency verifyVerificationCardSetIdsConsistency = new VerifyVerificationCardSetIdsConsistency(
				applicationEventPublisherMock, pathService, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyVerificationCardSetIdsConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification307.nok.message"));
		assertEquals(expectedResult, result);
	}
}
