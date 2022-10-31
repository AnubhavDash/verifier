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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;

import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;
import ch.post.it.evoting.verifier.protocol.domain.EncryptedVerifiableVote;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.protocol.domain.tally.ControlComponentBallotBoxPayload;

@DisplayName("VerifyVerificationCardIdsConsistencyTest with")
class VerifyVerificationCardIdsConsistencyTest extends TallyVerificationTest {

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
	@DisplayName("inconsistent relation between verification card set and ballot box failed when")
	void inconsistentRelationBetweenVerificationCardSetAndBallotBox(final String testName,
			final ElectionEventContextPayload electionEventContextPayloadMock,
			final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloadsMock) {
		// given
		final var verifyElectionEventIdConsistency = new VerifyVerificationCardIdsConsistency(resultPublisherServiceMock,
				electionDataExtractionService);

		// when
		final var result = verifyElectionEventIdConsistency.verifyVerificationCardSetRelationToBallotBox(controlComponentBallotBoxPayloadsMock,
				electionEventContextPayloadMock);

		// then
		assertFalse(result);
	}

	static Stream<Arguments> inconsistentRelationBetweenVerificationCardSetAndBallotBoxProvider() {

		final ElectionEventContextPayload electionEventContextPayloadMock = new ElectionEventContextPayloadMockBuilder()
				.add("verificationCardSetId_1", "ballotBoxId_1")
				.add("verificationCardSetId_2", "ballotBoxId_2")
				.add("verificationCardSetId_3", "ballotBoxId_3")
				.build();

		return Stream.of(
				Arguments.of("verification card set ids is swapped between 2 ballot box.",
						electionEventContextPayloadMock,
						List.of(
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_1")
										.add("verificationCardSetId_1")
										.build(),
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_2")
										.add("verificationCardSetId_3")
										.build(),
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_3")
										.add("verificationCardSetId_2")
										.build()
						)),
				Arguments.of("verification card set does not exist in election event context.",
						electionEventContextPayloadMock,
						List.of(
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_1")
										.add("verificationCardSetId_1")
										.build(),
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_2")
										.add("verificationCardSetId_2")
										.build(),
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_3")
										.add("verificationCardSetId_4")
										.build()
						)),
				Arguments.of("ballot box which does not exist in election event context.",
						electionEventContextPayloadMock,
						List.of(
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_1")
										.add("verificationCardSetId_1")
										.build(),
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_2")
										.add("verificationCardSetId_2")
										.build(),
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_3")
										.add("verificationCardSetId_3")
										.build(),
								new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_4")
										.add("verificationCardSetId_3")
										.build()
						)));
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("inconsistentVerificationCardIdsInExpectedSetProvider")
	@DisplayName("inconsistent verification card ids in expected set failed")
	void inconsistentVerificationCardIdsInExpectedSet(final String testName,
			final List<ControlComponentBallotBoxPayload> controlComponentBallotBoxPayloadsMock,
			final List<SetupComponentTallyDataPayload> setupComponentTallyDataPayloads) {
		// given

		// when
		final var verifyElectionEventIdConsistency = new VerifyVerificationCardIdsConsistency(resultPublisherServiceMock,
				electionDataExtractionService);
		final var result = verifyElectionEventIdConsistency.verifyVerificationCardIdsInExpectedSet(controlComponentBallotBoxPayloadsMock,
				setupComponentTallyDataPayloads);

		// then
		assertFalse(result);
	}

	static Stream<Arguments> inconsistentVerificationCardIdsInExpectedSetProvider() {
		return Stream.of(
				Arguments.of("missing verification card ID in the related set.",
						List.of(new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_1")
								.add("verificationCardSetId_1", "verificationCardId_1")
								.add("verificationCardSetId_1", "verificationCardId_2")
								.add("verificationCardSetId_2", "verificationCardId_3")
								.add("verificationCardSetId_2", "verificationCardId_4")
								.add("verificationCardSetId_2", "verificationCardId_5")
								.add("verificationCardSetId_3", "verificationCardId_6")
								.add("verificationCardSetId_3", "verificationCardId_7")
								.build()
						), List.of(
								new SetupComponentTallyDataPayloadsMockBuilder("verificationCardSetId_1")
										.add("verificationCardId_1")
										.add("verificationCardId_2")
										.build(),
								new SetupComponentTallyDataPayloadsMockBuilder("verificationCardSetId_2")
										.add("verificationCardId_3")
										.add("verificationCardId_4")
										.add("verificationCardId_5")
										.build(),
								new SetupComponentTallyDataPayloadsMockBuilder("verificationCardSetId_3")
										.add("verificationCardId_6")
										.build()
						),
						Arguments.of("unexpected verification card set",
								List.of(new ControlComponentBallotBoxPayloadMockBuilder("ballotBoxId_1")
										.add("verificationCardSetId_1", "verificationCardId_1")
										.add("verificationCardSetId_1", "verificationCardId_2")
										.add("verificationCardSetId_2", "verificationCardId_3")
										.add("verificationCardSetId_2", "verificationCardId_4")
										.add("verificationCardSetId_2", "verificationCardId_5")
										.add("verificationCardSetId_3", "verificationCardId_6")
										.build()
								), List.of(
										new SetupComponentTallyDataPayloadsMockBuilder("verificationCardSetId_1")
												.add("verificationCardId_1")
												.add("verificationCardId_2")
												.build(),
										new SetupComponentTallyDataPayloadsMockBuilder("verificationCardSetId_2")
												.add("verificationCardId_3")
												.add("verificationCardId_4")
												.build(),
										new SetupComponentTallyDataPayloadsMockBuilder("verificationCardSetId_3")
												.add("verificationCardId_5")
												.add("verificationCardId_6")
												.build()
								))));
	}

	private static class ElectionEventContextPayloadMockBuilder {

		private final Set<Pair<String, String>> verificationCardSetIdAndBallotBoxId = new HashSet<>();

		public ElectionEventContextPayloadMockBuilder add(final String verificationCardSetId, final String ballotBoxId) {
			verificationCardSetIdAndBallotBoxId.add(Pair.of(verificationCardSetId, ballotBoxId));
			return this;
		}

		public ElectionEventContextPayload build() {
			final List<VerificationCardSetContext> verificationCardSetContexts = verificationCardSetIdAndBallotBoxId.stream()
					.map(pair -> {
						final VerificationCardSetContext mock = mock(VerificationCardSetContext.class);
						when(mock.verificationCardSetId()).thenReturn(pair.getLeft());
						when(mock.ballotBoxId()).thenReturn(pair.getRight());
						return mock;
					})
					.toList();

			final var electionEventContextPayloadMock = mock(ElectionEventContextPayload.class, Answers.RETURNS_DEEP_STUBS);
			when(electionEventContextPayloadMock.getElectionEventContext().verificationCardSetContexts()).thenReturn(verificationCardSetContexts);
			return electionEventContextPayloadMock;
		}
	}

	private static class ControlComponentBallotBoxPayloadMockBuilder {

		private final String ballotBoxId;
		private final Set<Pair<String, String>> verificationCardSetIdsAndVerificationCardIds = new HashSet<>();

		private ControlComponentBallotBoxPayloadMockBuilder(final String ballotBoxId) {
			this.ballotBoxId = ballotBoxId;
		}

		public ControlComponentBallotBoxPayloadMockBuilder add(final String verificationCardSetId) {
			return add(verificationCardSetId, "");
		}

		public ControlComponentBallotBoxPayloadMockBuilder add(final String verificationCardSetId, final String verificationCardId) {
			verificationCardSetIdsAndVerificationCardIds.add(Pair.of(verificationCardSetId, verificationCardId));
			return this;
		}

		public ControlComponentBallotBoxPayload build() {
			final List<EncryptedVerifiableVote> encryptedVerifiableVotes = verificationCardSetIdsAndVerificationCardIds.stream()
					.map(pair -> {
						final EncryptedVerifiableVote mock = mock(EncryptedVerifiableVote.class, Answers.RETURNS_DEEP_STUBS);
						when(mock.contextIds().verificationCardSetId()).thenReturn(pair.getLeft());
						when(mock.contextIds().verificationCardId()).thenReturn(pair.getRight());
						return mock;
					})
					.toList();
			final var controlComponentBallotBoxPayload = mock(ControlComponentBallotBoxPayload.class);
			when(controlComponentBallotBoxPayload.getBallotBoxId()).thenReturn(ballotBoxId);
			when(controlComponentBallotBoxPayload.getConfirmedEncryptedVotes()).thenReturn(encryptedVerifiableVotes);
			return controlComponentBallotBoxPayload;
		}
	}

	private static class SetupComponentTallyDataPayloadsMockBuilder {

		private final String verificationCardSetId;
		private final Set<String> verificationCardIds = new HashSet<>();

		public SetupComponentTallyDataPayloadsMockBuilder(final String verificationCardSetId) {
			this.verificationCardSetId = verificationCardSetId;
		}

		public SetupComponentTallyDataPayloadsMockBuilder add(final String verificationCardId) {
			verificationCardIds.add(verificationCardId);
			return this;
		}

		public SetupComponentTallyDataPayload build() {
			final SetupComponentTallyDataPayload mock = mock(SetupComponentTallyDataPayload.class, Answers.RETURNS_DEEP_STUBS);
			when(mock.getVerificationCardIds()).thenReturn(verificationCardIds.stream().toList());
			when(mock.getVerificationCardSetId()).thenReturn(verificationCardSetId);
			return mock;
		}
	}
}