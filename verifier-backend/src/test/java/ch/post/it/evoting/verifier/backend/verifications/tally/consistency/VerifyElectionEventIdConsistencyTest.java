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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifyElectionEventIdConsistencyTest extends TallyVerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyElectionEventIdConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent election event id failed")
	void inconsistentNumberConfirmedEncryptedVotes() {
		final ElectionEventContext electionEventContextMock = mock(ElectionEventContext.class);
		when(electionEventContextMock.electionEventId()).thenReturn("anotherId");

		final ElectionEventContextPayload electionEventContextPayloadMock = mock(ElectionEventContextPayload.class);
		when(electionEventContextPayloadMock.getElectionEventContext()).thenReturn(electionEventContextMock);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(electionEventContextPayloadMock).when(electionDataExtractionServiceSpy).getElectionEventContextPayload(any());

		final VerifyElectionEventIdConsistency verifyElectionEventIdConsistency$ =
				new VerifyElectionEventIdConsistency(resultPublisherServiceMock, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyElectionEventIdConsistency$.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification307.nok.message"));
		assertEquals(expectedResult, result);
	}

}
