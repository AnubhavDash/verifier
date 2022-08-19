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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

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

import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

class VerifyNumberConfirmedEncryptedVotesConsistencyTest extends TallyVerificationTest {

	private static ElectionDataExtractionService electionDataExtractionService;

	@BeforeAll
	static void setUpAll() {
		electionDataExtractionService = new ElectionDataExtractionService(pathService, objectMapper);

		verification = new VerifyNumberConfirmedEncryptedVotesConsistency(applicationEventPublisherMock, pathService, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent number of confirmed encrypted votes failed")
	void inconsistentNumberConfirmedEncryptedVotes() {
		final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload = mock(ControlComponentBallotBoxPayload.class);
		when(controlComponentBallotBoxPayload.getConfirmedEncryptedVotes()).thenReturn(Collections.emptyList());

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(Collections.singletonList(controlComponentBallotBoxPayload)).when(electionDataExtractionServiceSpy)
				.getControlComponentBallotBoxPayloads(any());

		final VerifyNumberConfirmedEncryptedVotesConsistency verifyNumberConfirmedEncryptedVotesConsistency =
				new VerifyNumberConfirmedEncryptedVotesConsistency(applicationEventPublisherMock, pathService, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyNumberConfirmedEncryptedVotesConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification304.nok.message"));
		assertEquals(expectedResult, result);
	}

}
