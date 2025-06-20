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

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.UUIDGenerator;
import ch.post.it.evoting.evotinglibraries.domain.common.ContextIds;
import ch.post.it.evoting.evotinglibraries.domain.common.EncryptedVerifiableVote;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

@DisplayName("VerifyVerificationCardIdsConsistencyTest with")
class VerifyVerificationCardIdsConsistencyTest extends TallyVerificationTest {

	private static final UUIDGenerator uuidGenerator = UUIDGenerator.getInstance();

	@BeforeAll
	static void setupAll() {
		verification = new VerifyVerificationCardIdsConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("inconsistentRelationBetweenVerificationCardSetAndBallotBoxProvider")
	@DisplayName("inconsistent relation between verification card set and ballot box is failed when")
	void inconsistentRelationBetweenVerificationCardSetAndBallotBox(final String testName,
			final ImmutableList<ControlComponentBallotBoxPayload> inconsistentControlComponentBallotBoxPayloads) {

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(inconsistentControlComponentBallotBoxPayloads.stream()).when(electionDataExtractionServiceSpy)
				.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(any());

		final VerifyVerificationCardIdsConsistency verifyVerificationCardIdsConsistency = new VerifyVerificationCardIdsConsistency(
				resultPublisherServiceMock, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyVerificationCardIdsConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification807.nok.message"));
		assertEquals(expectedResult, result);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("inconsistentVerificationCardIdsInExpectedSetProvider")
	@DisplayName("inconsistent verification card ids in expected set failed")
	void inconsistentVerificationCardIdsInExpectedSet(final String testName,
			final ImmutableList<SetupComponentTallyDataPayload> setupComponentTallyDataPayloads) {

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(setupComponentTallyDataPayloads.stream()).when(electionDataExtractionServiceSpy).getSetupComponentTallyDataPayloads(any());

		final VerifyVerificationCardIdsConsistency verifyVerificationCardIdsConsistency = new VerifyVerificationCardIdsConsistency(
				resultPublisherServiceMock, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyVerificationCardIdsConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification807.nok.message"));
		assertEquals(expectedResult, result);
	}

	private static Stream<Arguments> inconsistentRelationBetweenVerificationCardSetAndBallotBoxProvider() {

		final ImmutableList<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloads = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						datasetPath)
				.collect(toImmutableList());

		return Stream.of(
				Arguments.of("verification card set ids is swapped between 2 ballot box.",
						ImmutableList.of(
								new ControlComponentBallotBoxPayload(controlComponentBallotBoxPayloads.getFirst().getEncryptionGroup(),
										controlComponentBallotBoxPayloads.getFirst().getElectionEventId(),
										controlComponentBallotBoxPayloads.getFirst().getBallotBoxId(),
										controlComponentBallotBoxPayloads.getFirst().getNodeId(),
										controlComponentBallotBoxPayloads.get(2).getConfirmedEncryptedVotes()),
								new ControlComponentBallotBoxPayload(controlComponentBallotBoxPayloads.get(1).getEncryptionGroup(),
										controlComponentBallotBoxPayloads.get(1).getElectionEventId(),
										controlComponentBallotBoxPayloads.get(1).getBallotBoxId(),
										controlComponentBallotBoxPayloads.get(1).getNodeId(),
										controlComponentBallotBoxPayloads.getFirst().getConfirmedEncryptedVotes()))),
				Arguments.of("verification card set does not exist in election event context.",
						controlComponentBallotBoxPayloads.stream()
								.map(controlComponentBallotBoxPayload -> {
									final String verificationCardSetId = uuidGenerator.generate();
									return new ControlComponentBallotBoxPayload(controlComponentBallotBoxPayload.getEncryptionGroup(),
											controlComponentBallotBoxPayload.getElectionEventId(),
											controlComponentBallotBoxPayload.getBallotBoxId(),
											controlComponentBallotBoxPayload.getNodeId(),
											controlComponentBallotBoxPayload.getConfirmedEncryptedVotes().stream()
													.map(encryptedVerifiableVote -> new EncryptedVerifiableVote(
															new ContextIds(
																	encryptedVerifiableVote.contextIds().electionEventId(),
																	verificationCardSetId,
																	encryptedVerifiableVote.contextIds().verificationCardId()),
															encryptedVerifiableVote.encryptedVote(),
															encryptedVerifiableVote.exponentiatedEncryptedVote(),
															encryptedVerifiableVote.encryptedPartialChoiceReturnCodes(),
															encryptedVerifiableVote.exponentiationProof(),
															encryptedVerifiableVote.plaintextEqualityProof()
													)).collect(toImmutableList()));
								}).collect(toImmutableList())),
				Arguments.of("ballot box which does not exist in election event context.",
						controlComponentBallotBoxPayloads.stream()
								.map(controlComponentBallotBoxPayload ->
										new ControlComponentBallotBoxPayload(controlComponentBallotBoxPayload.getEncryptionGroup(),
												controlComponentBallotBoxPayload.getElectionEventId(),
												uuidGenerator.generate(),
												controlComponentBallotBoxPayload.getNodeId(),
												controlComponentBallotBoxPayload.getConfirmedEncryptedVotes()))
								.collect(toImmutableList()))
		);
	}

	private static Stream<Arguments> inconsistentVerificationCardIdsInExpectedSetProvider() {

		final ImmutableList<SetupComponentTallyDataPayload> setupComponentTallyDataPayloads = electionDataExtractionService.getSetupComponentTallyDataPayloads(
						datasetPath)
				.collect(toImmutableList());

		return Stream.of(
				Arguments.of("missing verification card ID in the related set.",
						ImmutableList.of(
								new SetupComponentTallyDataPayload(
										setupComponentTallyDataPayloads.getFirst().getEncryptionGroup(),
										setupComponentTallyDataPayloads.getFirst().getElectionEventId(),
										setupComponentTallyDataPayloads.getFirst().getVerificationCardSetId(),
										setupComponentTallyDataPayloads.getFirst().getVerificationCardIds().subList(0, 1),
										setupComponentTallyDataPayloads.getFirst().getBallotBoxDefaultTitle(),
										setupComponentTallyDataPayloads.getFirst().getVerificationCardPublicKeys().subVector(0, 1)
								)
						)),
				Arguments.of("unexpected verification card set",
						ImmutableList.of(
								new SetupComponentTallyDataPayload(
										setupComponentTallyDataPayloads.getFirst().getEncryptionGroup(),
										setupComponentTallyDataPayloads.getFirst().getElectionEventId(),
										uuidGenerator.generate(),
										setupComponentTallyDataPayloads.getFirst().getVerificationCardIds(),
										setupComponentTallyDataPayloads.getFirst().getBallotBoxDefaultTitle(),
										setupComponentTallyDataPayloads.getFirst().getVerificationCardPublicKeys()
								)
						)));
	}
}
