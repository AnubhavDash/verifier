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

import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Streams;

import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;
import ch.post.it.evoting.verifier.protocol.domain.EncryptedVerifiableVote;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

class VerifyConfirmedEncryptedVotesConsistencyTest extends TallyVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyConfirmedEncryptedVotesConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	void testVerifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void testVerifyNok() {
		final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
				datasetPath);
		final ControlComponentBallotBoxPayload payloadWithVotes = controlComponentBallotBoxPayloads.stream()
				.filter(payload -> payload.getConfirmedEncryptedVotes().size() > 1 && payload.getNodeId() == 1)
				.findFirst().orElseThrow(() -> new IllegalStateException("Could not find a ballot box payload with enough votes"));
		final List<EncryptedVerifiableVote> confirmedEncryptedVotes = payloadWithVotes.getConfirmedEncryptedVotes();
		final List<EncryptedVerifiableVote> confirmedEncryptedVotesWithMissingVote = confirmedEncryptedVotes.stream().skip(1).toList();
		final ControlComponentBallotBoxPayload newPayload = new ControlComponentBallotBoxPayload(
				payloadWithVotes.getEncryptionGroup(), payloadWithVotes.getElectionEventId(), payloadWithVotes.getBallotBoxId(),
				payloadWithVotes.getNodeId(),
				confirmedEncryptedVotesWithMissingVote);
		final List<ControlComponentBallotBoxPayload> newControlComponentBallotBoxPayloads = Streams.concat(Stream.of(newPayload),
				electionDataExtractionService.getControlComponentBallotBoxPayloadsOrderedByNodeId(datasetPath, newPayload.getBallotBoxId()).stream()
						.filter(payload -> payload.getNodeId() != 1)).toList();

		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		doReturn(newControlComponentBallotBoxPayloads).when(extractionServiceMock)
				.getControlComponentBallotBoxPayloadsOrderedByNodeId(datasetPath, newPayload.getBallotBoxId());
		final VerifyConfirmedEncryptedVotesConsistency verificationWithMock = new VerifyConfirmedEncryptedVotesConsistency(
				resultPublisherServiceMock, extractionServiceMock);

		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(), getFromResourceBundle(
				TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification300.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
