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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@DisplayName("VerifyTallyNodeIdsConsistency with")
class VerifyTallyNodeIdsConsistencyTest extends TallyVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyTallyNodeIdsConsistency(applicationEventPublisherMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void testVerifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@ParameterizedTest
	@MethodSource()
	@DisplayName("inconsistent node id in control component ballot box payloads failed")
	void inconsistentNodeId(int... nodeIds) {

		final List<ControlComponentBallotBoxPayload> ballotBoxPayloads = generateBallotBoxesMock(nodeIds);
		final List<ControlComponentShufflePayload> shufflePayloads = generateShufflesMock(nodeIds);

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(ballotBoxPayloads).when(extractionServiceSpy).getAllControlComponentBallotBoxPayloadsOrderedByNodeId(datasetPath);
		doReturn(shufflePayloads).when(extractionServiceSpy).getAllControlComponentShufflePayloadsOrderedByNodeId(datasetPath);

		final VerifyTallyNodeIdsConsistency verifyElectionEventIdConsistency = new VerifyTallyNodeIdsConsistency(
				applicationEventPublisherMock, extractionServiceSpy);

		final VerificationResult result = verifyElectionEventIdConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification306.nok.message"));
		assertEquals(expectedResult, result);
	}

	static Stream<Arguments> inconsistentNodeId() {
		return Stream.of(
				Arguments.of(new int[] { 1, 2, 3, 4, 5 }),
				Arguments.of(new int[] { 1, 2, 4 }),
				Arguments.of(new int[] { 0, 1, 2, 3 }),
				Arguments.of(new int[] { 2, 3, 4, 5 })
		);
	}

	private List<ControlComponentBallotBoxPayload> generateBallotBoxesMock(int... nodeIds) {
		return Arrays.stream(nodeIds).boxed()
				.map(nodeId -> {
					final var mock = mock(ControlComponentBallotBoxPayload.class);
					when(mock.getNodeId()).thenReturn(nodeId);
					when(mock.getBallotBoxId()).thenReturn("7b170560b5ae4b6b87ab00119ddc6782");
					return mock;
				})
				.toList();
	}

	private List<ControlComponentShufflePayload> generateShufflesMock(int... nodeIds) {
		return Arrays.stream(nodeIds).boxed()
				.map(nodeId -> {
					final var mock = mock(ControlComponentShufflePayload.class);
					when(mock.getNodeId()).thenReturn(nodeId);
					when(mock.getBallotBoxId()).thenReturn("7b170560b5ae4b6b87ab00119ddc6782");
					return mock;
				})
				.toList();
	}
}
