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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

@DisplayName("VerifyEncryptionGroupConsistencyTest with")
class VerifyEncryptionGroupConsistencyTest extends TallyVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyEncryptionGroupConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("ControlComponentBallotBox containing different encryption group parameters fails")
	void invalidControlComponentBallotBoxPayload() {
		final GqGroup otherGqGroup = new GqGroup(BigInteger.valueOf(47), BigInteger.valueOf(23), BigInteger.valueOf(2));

		final ControlComponentBallotBoxPayload controlComponentBallotBoxPayloadMock = mock(ControlComponentBallotBoxPayload.class);
		when(controlComponentBallotBoxPayloadMock.getEncryptionGroup()).thenReturn(otherGqGroup);

		final Stream<ControlComponentBallotBoxPayload> stream = Stream.concat(
				Stream.of(controlComponentBallotBoxPayloadMock),
				electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(datasetPath).skip(1)
		);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(electionDataExtractionServiceSpy).getAllControlComponentBallotBoxPayloadsOrderedByNodeId(datasetPath);

		assertInvalidVerification(electionDataExtractionServiceSpy);
	}

	@Test
	@DisplayName("ControlComponentShuffle containing different encryption group parameters fails")
	void invalidControlComponentShufflePayload() {
		final GqGroup otherGqGroup = new GqGroup(BigInteger.valueOf(47), BigInteger.valueOf(23), BigInteger.valueOf(2));

		final ControlComponentShufflePayload controlComponentShufflePayloadMock = mock(ControlComponentShufflePayload.class);
		when(controlComponentShufflePayloadMock.getEncryptionGroup()).thenReturn(otherGqGroup);

		final Stream<ControlComponentShufflePayload> stream = Stream.concat(
				Stream.of(controlComponentShufflePayloadMock),
				electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(datasetPath).skip(1)
		);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(electionDataExtractionServiceSpy).getAllControlComponentShufflePayloadsOrderedByNodeId(datasetPath);

		assertInvalidVerification(electionDataExtractionServiceSpy);
	}

	@Test
	@DisplayName("TallyComponentShuffle containing different encryption group parameters fails")
	void invalidTallyComponentShufflePayload() {
		final GqGroup otherGqGroup = new GqGroup(BigInteger.valueOf(47), BigInteger.valueOf(23), BigInteger.valueOf(2));

		final TallyComponentShufflePayload tallyComponentShufflePayloadMock = mock(TallyComponentShufflePayload.class);
		when(tallyComponentShufflePayloadMock.getEncryptionGroup()).thenReturn(otherGqGroup);

		final Stream<TallyComponentShufflePayload> stream = Stream.concat(
				Stream.of(tallyComponentShufflePayloadMock),
				electionDataExtractionService.getTallyComponentShufflePayloads(datasetPath).skip(1)
		);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(electionDataExtractionServiceSpy).getTallyComponentShufflePayloads(datasetPath);

		assertInvalidVerification(electionDataExtractionServiceSpy);
	}

	@Test
	@DisplayName("TallyComponentVotes containing different encryption group parameters fails")
	void invalidTallyComponentVotesPayload() {
		final GqGroup otherGqGroup = new GqGroup(BigInteger.valueOf(47), BigInteger.valueOf(23), BigInteger.valueOf(2));

		final TallyComponentVotesPayload tallyComponentVotesPayloadMock = mock(TallyComponentVotesPayload.class);
		when(tallyComponentVotesPayloadMock.getEncryptionGroup()).thenReturn(otherGqGroup);

		final Stream<TallyComponentVotesPayload> stream = Stream.concat(
				Stream.of(tallyComponentVotesPayloadMock),
				electionDataExtractionService.getTallyComponentVotesPayloads(datasetPath).skip(1)
		);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(electionDataExtractionServiceSpy).getTallyComponentVotesPayloads(datasetPath);

		assertInvalidVerification(electionDataExtractionServiceSpy);
	}

	private void assertInvalidVerification(final ElectionDataExtractionService electionDataExtractionServiceSpy) {
		final VerifyEncryptionGroupConsistency invalidVerification = new VerifyEncryptionGroupConsistency(resultPublisherServiceMock,
				electionDataExtractionServiceSpy);
		final VerificationResult verificationResult = invalidVerification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(invalidVerification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification801.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
