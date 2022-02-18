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
package ch.post.it.evoting.verifier.block.block3.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetInitialPayload;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.MixnetShufflePayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.verifications.CheckConsistencyOnlineProofs.CheckConsistencyOnlineProofsInput;
import ch.post.it.evoting.verifier.core.internal.exceptions.MissingFileException;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.plugin.contract.event.PreDecryptionEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

class CheckConsistencyOnlineProofsTest extends Block3VerificationTest {

	private static final String BAD_SHUFFLE_PAYLOAD_LIST_SIZE_MESSAGE = "There must be exactly 3 shuffle payloads in the ballot box";

	private static ElectionDataExtractionService extractionService;

	@BeforeAll
	static void setupAll() {
		extractionService = new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance());
		verification = new CheckConsistencyOnlineProofs(pathService, extractionService, applicationEventPublisherMock);
	}

	@Test
	void testVerifyOk() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckConsistencyOnlineProofsTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new PreDecryptionEvent(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	private static Stream<Arguments> getBadInputTests() {
		return Stream.of(
				Arguments.of("/CheckConsistencyOnlineProofsTest/NOK_encryptionGroup1", "verification41.nok.encryption.group.message"),
				Arguments.of("/CheckConsistencyOnlineProofsTest/NOK_encryptionGroup2", "verification41.nok.encryption.group.message"),
				Arguments.of("/CheckConsistencyOnlineProofsTest/NOK_nodeElectionPublicKey", "verification41.nok.public.keys.message"),
				Arguments.of("/CheckConsistencyOnlineProofsTest/NOK_previousRemainingElectionPublicKey",
						"verification41.nok.public.keys.message"),
				Arguments.of("/CheckConsistencyOnlineProofsTest/NOK_remainingElectionPublicKey", "verification41.nok.public.keys.message")
		);
	}

	@ParameterizedTest
	@MethodSource("getBadInputTests")
	void testVerifyNok(final String inputDirectory, final String messageIdentifier) throws Exception {
		final var event = new PreDecryptionEvent(this, Paths.get(getClass().getResource(inputDirectory).toURI()).toString());
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, messageIdentifier));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void testMissingMixnetShufflePayloads() throws URISyntaxException {
		final var inputDirectory = Paths.get(
				Objects.requireNonNull(getClass().getResource("/CheckConsistencyOnlineProofsTest/NOK_missingPayloads")).toURI()).toString();
		final var event = new PreDecryptionEvent(this, inputDirectory);
		final MissingFileException exception = assertThrows(MissingFileException.class, () -> verification.verify(event));
		assertEquals("Missing shufflePayload file(s)", exception.getMessage());
	}

	private static Stream<Arguments> getBadInputs() throws URISyntaxException {
		final Path ballotBoxInputPath = Paths.get(Objects.requireNonNull(
						CheckConsistencyOnlineProofsTest.class.getResource(
								"/CheckConsistencyOnlineProofsTest/OK/ballot_boxes/e4482a8f38774338b543d9a57a9dec80"))
				.toURI());
		final var initialPayload = extractionService.getMixnetInitialPayload(ballotBoxInputPath);
		List<MixnetShufflePayload> shufflePayloads = extractionService.getMixnetShufflePayloads(ballotBoxInputPath);
		final var firstShufflePayload = shufflePayloads.get(0);
		final var encryptionGroup = firstShufflePayload.getEncryptionGroup();
		final var verifiableShuffle = firstShufflePayload.getVerifiableShuffle();
		final var verifiableDecryptions = firstShufflePayload.getVerifiableDecryptions();
		final var previousRemainingElectionPublicKey = firstShufflePayload.getPreviousRemainingElectionPublicKey();
		final var remainingElectionPublicKey = firstShufflePayload.getRemainingElectionPublicKey();
		final var nodeElectionPublicKey = firstShufflePayload.getNodeElectionPublicKey();
		final int nodeId = firstShufflePayload.getNodeId();

		final var tooLongPreviousKey = addElementToPublicKey(previousRemainingElectionPublicKey,
				previousRemainingElectionPublicKey.get(0));
		final var tooLongKey = addElementToPublicKey(remainingElectionPublicKey, remainingElectionPublicKey.get(0));
		final var tooLongNodeKey = addElementToPublicKey(nodeElectionPublicKey, nodeElectionPublicKey.get(0));

		final var shufflePayloadTooLongPreviousKey = new MixnetShufflePayload(encryptionGroup, verifiableDecryptions,
				verifiableShuffle, remainingElectionPublicKey, tooLongPreviousKey, nodeElectionPublicKey, nodeId);
		final ImmutableList<MixnetShufflePayload> payloadListFirstPayloadTooLongPreviousKey = ImmutableList.of(shufflePayloadTooLongPreviousKey,
				shufflePayloads.get(1),
				shufflePayloads.get(2));
		final var inputFirstShufflePayloadTooLongPreviousKey = new CheckConsistencyOnlineProofsInput(encryptionGroup, initialPayload,
				payloadListFirstPayloadTooLongPreviousKey);

		final var shufflePayloadTooLongKey = new MixnetShufflePayload(encryptionGroup, verifiableDecryptions,
				verifiableShuffle, tooLongKey, previousRemainingElectionPublicKey, nodeElectionPublicKey, nodeId);
		final ImmutableList<MixnetShufflePayload> payloadListFirstPayloadTooLongKey = ImmutableList.of(shufflePayloadTooLongKey,
				shufflePayloads.get(1),
				shufflePayloads.get(2));
		final var inputFirstShufflePayloadTooLongKey = new CheckConsistencyOnlineProofsInput(encryptionGroup, initialPayload,
				payloadListFirstPayloadTooLongKey);

		final var shufflePayloadTooLongNodeKey = new MixnetShufflePayload(encryptionGroup, verifiableDecryptions,
				verifiableShuffle, remainingElectionPublicKey, previousRemainingElectionPublicKey, tooLongNodeKey, nodeId);
		final ImmutableList<MixnetShufflePayload> payloadListFirstPayloadTooLongNodeKey = ImmutableList.of(shufflePayloadTooLongNodeKey,
				shufflePayloads.get(1),
				shufflePayloads.get(2));
		final var inputFirstShufflePayloadTooLongNodeKey = new CheckConsistencyOnlineProofsInput(encryptionGroup, initialPayload,
				payloadListFirstPayloadTooLongNodeKey);

		final ImmutableList<MixnetShufflePayload> payloadListTooLongKeys = ImmutableList.of(shufflePayloadTooLongKey, shufflePayloadTooLongKey,
				shufflePayloadTooLongKey);
		final var inputShufflePayloadsTooLongKeys = new CheckConsistencyOnlineProofsInput(encryptionGroup, initialPayload,
				payloadListTooLongKeys);

		final var shufflePayloadsTooShortNodeKey = new MixnetShufflePayload(encryptionGroup, verifiableDecryptions,
				verifiableShuffle, tooLongKey, tooLongPreviousKey, remainingElectionPublicKey, nodeId);
		final ImmutableList<MixnetShufflePayload> payloadListTooShortNodeKeys = ImmutableList.of(shufflePayloadsTooShortNodeKey,
				shufflePayloadsTooShortNodeKey,
				shufflePayloadsTooShortNodeKey);
		final var inputShufflePayloadsTooShortNodeKeys = new CheckConsistencyOnlineProofsInput(encryptionGroup, initialPayload,
				payloadListTooShortNodeKeys);

		return Stream.of(
				Arguments.of(inputFirstShufflePayloadTooLongPreviousKey, "All previous remaining election public keys must have the same size"),
				Arguments.of(inputFirstShufflePayloadTooLongKey, "All remaining election public keys must have the same size"),
				Arguments.of(inputFirstShufflePayloadTooLongNodeKey, "All node election public keys must have the same size"),
				Arguments.of(inputShufflePayloadsTooLongKeys,
						"The previous remaining election public key and the remaining public key must have the same size"),
				Arguments.of(inputShufflePayloadsTooShortNodeKeys,
						"The remaining election public key must not have more elements than the node election public key")
		);
	}

	private static ElGamalMultiRecipientPublicKey addElementToPublicKey(final ElGamalMultiRecipientPublicKey publicKey,
			final GqElement newKeyElement) {
		final List<GqElement> keyElements = publicKey.getKeyElements();
		keyElements.add(newKeyElement);
		return new ElGamalMultiRecipientPublicKey(keyElements);
	}

	@Nested
	class CheckEncryptionGroupTest {

		private CheckConsistencyOnlineProofsInput input;

		@BeforeEach
		void setup() throws URISyntaxException {
			final Path ballotBoxDirectoryPath = Paths.get(Objects.requireNonNull(
					getClass().getResource("/CheckConsistencyOnlineProofsTest/OK/ballot_boxes/e4482a8f38774338b543d9a57a9dec80")).toURI());
			final var encryptionGroup = extractionService.getEncryptionParameters(Paths.get(Objects.requireNonNull(
					getClass().getResource("/CheckConsistencyOnlineProofsTest/OK").toURI())));
			final var initialPayload = extractionService.getMixnetInitialPayload(ballotBoxDirectoryPath);
			List<MixnetShufflePayload> shufflePayloads = extractionService.getMixnetShufflePayloads(ballotBoxDirectoryPath);
			input = new CheckConsistencyOnlineProofsInput(encryptionGroup, initialPayload, ImmutableList.copyOf(shufflePayloads));
		}

		@Test
		void testWithNullArgument() {
			assertThrows(NullPointerException.class, () -> ((CheckConsistencyOnlineProofs) verification).checkEncryptionGroup(null));
		}

		@Test
		void testWithBadEncryptionGroup() {
			final var p = input.encryptionGroup.getP();
			final var q = input.encryptionGroup.getQ();
			final var badEncryptionGroup = new GqGroup(p, q, BigInteger.valueOf(4));
			final var badInput = new CheckConsistencyOnlineProofsInput(badEncryptionGroup, input.mixnetInitialPayload,
					input.mixnetShufflePayloads);
			assertFalse(((CheckConsistencyOnlineProofs) verification).checkEncryptionGroup(badInput));
		}

		@Test
		void testWithPayloadListBadSize() {
			final var tooFewShufflePayloadsInput = new CheckConsistencyOnlineProofsInput(input.encryptionGroup, input.mixnetInitialPayload,
					ImmutableList.copyOf(input.mixnetShufflePayloads.subList(0, 2)));
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
					() -> ((CheckConsistencyOnlineProofs) verification).checkEncryptionGroup(tooFewShufflePayloadsInput));
			assertEquals(BAD_SHUFFLE_PAYLOAD_LIST_SIZE_MESSAGE, exception.getMessage());

			final ImmutableList<MixnetShufflePayload> tooManyShufflePayloads = ImmutableList.of(input.mixnetShufflePayloads.get(0),
					input.mixnetShufflePayloads.get(1),
					input.mixnetShufflePayloads.get(2),
					input.mixnetShufflePayloads.get(0));
			final var tooManyShufflePayloadsInput = new CheckConsistencyOnlineProofsInput(input.encryptionGroup, input.mixnetInitialPayload,
					tooManyShufflePayloads);
			exception = assertThrows(IllegalArgumentException.class,
					() -> ((CheckConsistencyOnlineProofs) verification).checkEncryptionGroup(tooManyShufflePayloadsInput));
			assertEquals(BAD_SHUFFLE_PAYLOAD_LIST_SIZE_MESSAGE, exception.getMessage());
		}

		@Test
		void testWithGoodInput() {
			assertTrue(((CheckConsistencyOnlineProofs) verification).checkEncryptionGroup(input));
		}

		@Test
		void testWithBadInput() {
			final MixnetShufflePayload lastShufflePayload = input.mixnetShufflePayloads.get(2);
			final GqGroup encryptionGroup = lastShufflePayload.getEncryptionGroup();
			MixnetShufflePayload badGroupShufflePayload = spy(lastShufflePayload);
			when(badGroupShufflePayload.getEncryptionGroup()).thenReturn(new GqGroup(encryptionGroup.getP(), encryptionGroup.getQ(),
					BigInteger.valueOf(4)));
			final ImmutableList<MixnetShufflePayload> badShufflePayloads = ImmutableList.of(input.mixnetShufflePayloads.get(0),
					input.mixnetShufflePayloads.get(1),
					badGroupShufflePayload);
			final var badInput = new CheckConsistencyOnlineProofsInput(input.encryptionGroup, input.mixnetInitialPayload, badShufflePayloads);
			assertFalse(((CheckConsistencyOnlineProofs) verification).checkEncryptionGroup(badInput));
		}
	}

	@Nested
	class CheckRemainingElectionPublicKeysTest {

		private CheckConsistencyOnlineProofsInput input;

		@BeforeEach
		void setup() throws URISyntaxException {
			final Path ballotBoxDirectoryPath = Paths.get(Objects.requireNonNull(
					getClass().getResource("/CheckConsistencyOnlineProofsTest/OK/ballot_boxes/e4482a8f38774338b543d9a57a9dec80")).toURI());
			final var encryptionGroup = extractionService.getEncryptionParameters(Paths.get(Objects.requireNonNull(
					getClass().getResource("/CheckConsistencyOnlineProofsTest/OK").toURI())));
			final MixnetInitialPayload initialPayload = extractionService.getMixnetInitialPayload(ballotBoxDirectoryPath);
			List<MixnetShufflePayload> shufflePayloads = extractionService.getMixnetShufflePayloads(ballotBoxDirectoryPath);
			input = new CheckConsistencyOnlineProofsInput(encryptionGroup, initialPayload, ImmutableList.copyOf(shufflePayloads));
		}

		@Test
		void testWithNullArgument() {
			assertThrows(NullPointerException.class,
					() -> ((CheckConsistencyOnlineProofs) verification).checkRemainingElectionPublicKeys(null));
		}

		@Test
		void testWithPayloadListBadSize() {
			final var tooFewShufflePayloadsInput = new CheckConsistencyOnlineProofsInput(input.encryptionGroup, input.mixnetInitialPayload,
					ImmutableList.copyOf(input.mixnetShufflePayloads.subList(0, 2)));
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
					() -> ((CheckConsistencyOnlineProofs) verification).checkRemainingElectionPublicKeys(tooFewShufflePayloadsInput));
			assertEquals(BAD_SHUFFLE_PAYLOAD_LIST_SIZE_MESSAGE, exception.getMessage());

			final ImmutableList<MixnetShufflePayload> tooManyShufflePayloads = ImmutableList.of(input.mixnetShufflePayloads.get(0),
					input.mixnetShufflePayloads.get(1),
					input.mixnetShufflePayloads.get(2),
					input.mixnetShufflePayloads.get(0));
			final var tooManyShufflePayloadsInput = new CheckConsistencyOnlineProofsInput(input.encryptionGroup, input.mixnetInitialPayload,
					tooManyShufflePayloads);
			exception = assertThrows(IllegalArgumentException.class,
					() -> ((CheckConsistencyOnlineProofs) verification).checkRemainingElectionPublicKeys(tooManyShufflePayloadsInput));
			assertEquals(BAD_SHUFFLE_PAYLOAD_LIST_SIZE_MESSAGE, exception.getMessage());
		}

		@ParameterizedTest
		@MethodSource("ch.post.it.evoting.verifier.block.block3.verifications.CheckConsistencyOnlineProofsTest#getBadInputs")
		void testWithBadPublicKeySizes(final CheckConsistencyOnlineProofsInput input, final String expectedErrorMessage) {
			final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
					() -> ((CheckConsistencyOnlineProofs) verification).checkRemainingElectionPublicKeys(input));
			assertEquals(expectedErrorMessage, Throwables.getRootCause(exception).getMessage());
		}
	}
}
