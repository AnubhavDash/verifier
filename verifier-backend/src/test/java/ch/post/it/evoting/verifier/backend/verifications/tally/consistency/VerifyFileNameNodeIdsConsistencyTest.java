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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifyFileNameNodeIdsConsistencyTest extends TallyVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyFileNameNodeIdsConsistency(resultPublisherServiceMock, pathService, electionDataExtractionService);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokBallotBoxPayloadNodeId() {
		final ElectionDataExtractionService electionDataExtractionServiceMock = spy(electionDataExtractionService);
		final ControlComponentBallotBoxPayload firstBallotBoxPayload = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
				datasetPath).collect(toImmutableList()).get(0);
		doReturn(firstBallotBoxPayload).when(electionDataExtractionServiceMock).getControlComponentBallotBoxPayload(any());

		final VerifyFileNameNodeIdsConsistency failingVerification = new VerifyFileNameNodeIdsConsistency(resultPublisherServiceMock,
				pathService, electionDataExtractionServiceMock);
		final VerificationResult verificationResult = failingVerification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification803.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokShufflePayloadNodeId() {
		final ElectionDataExtractionService electionDataExtractionServiceMock = spy(electionDataExtractionService);
		final ControlComponentShufflePayload firstShufflePayload = electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
				datasetPath).findFirst().orElseThrow();
		doReturn(firstShufflePayload).when(electionDataExtractionServiceMock).getControlComponentShufflePayload(any());

		final VerifyFileNameNodeIdsConsistency failingVerification = new VerifyFileNameNodeIdsConsistency(resultPublisherServiceMock,
				pathService, electionDataExtractionServiceMock);
		final VerificationResult verificationResult = failingVerification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification803.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
