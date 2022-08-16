package ch.post.it.evoting.verifier.backend.verifications.setup.consistency.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

import lombok.SneakyThrows;

class VerifyChunkConsistencyTest extends SetupVerificationTest {

	private static ElectionDataExtractionService extractionService;

	@BeforeAll
	static void setupAll() {
		extractionService = new ElectionDataExtractionService(pathService, objectMapper);
		verification = new VerifyChunkConsistency(pathService, applicationEventPublisherMock, extractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@ParameterizedTest
	@MethodSource("invalidChunkIdConsistencyProvider")
	@DisplayName("invalid chunk ID in control component code shares are detected")
	@SneakyThrows
	void invalidChunkIdConsistency(String path) {
		final Path inputDirectory = Paths.get(getClass().getResource(path).toURI());
		final VerificationResult result = verification.verify(inputDirectory);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification40.nok.message"));
		assertEquals(expectedResult, result);
	}

	static Stream<Arguments> invalidChunkIdConsistencyProvider() {
		return Stream.of(
				Arguments.of("/setup/VerifyChunkConsistencyTest/chunking-control-component-code-shares-invalid-ids"),
				Arguments.of("/setup/VerifyChunkConsistencyTest/chunking-setup-component-verification-data-invalid-ids"),
				Arguments.of("/setup/VerifyChunkConsistencyTest/chunking-invalid-monotony")
		);
	}

	@Test
	@DisplayName("validate the monotony check algorithm pass with happy path")
	void monotonyCheckWorkWithHappyPath() {
		// given
		final var verifyChunkConsistency = new VerifyChunkConsistency(pathService, applicationEventPublisherMock, extractionService);
		final List<List<Path>> payloadsPerCardSet = List.of(
				List.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.0.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.2.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.3.json")
				),
				List.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.2.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.0.json")
				)
		);

		// when
		final boolean result = verifyChunkConsistency.isSequenceMonotonic(payloadsPerCardSet);

		// then
		assertTrue(result);
	}

	@Test
	@DisplayName("validate the monotony check algorithm detect missing index")
	void monotonyCheckDetectMissingIndex() {
		// given
		final var verifyChunkConsistency = new VerifyChunkConsistency(pathService, applicationEventPublisherMock, extractionService);
		final List<List<Path>> payloadsPerCardSet = List.of(
				List.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.0.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.3.json")
				)
		);

		// when
		final boolean result = verifyChunkConsistency.isSequenceMonotonic(payloadsPerCardSet);

		// then
		assertFalse(result);
	}

	@Test
	@DisplayName("validate the monotony check algorithm detect duplicated index")
	void monotonyCheckDetectDuplicatedIndex() {
		// given
		final var verifyChunkConsistency = new VerifyChunkConsistency(pathService, applicationEventPublisherMock, extractionService);
		final List<List<Path>> payloadsPerCardSet = List.of(
				List.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.0.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.3.json")
				)
		);

		// when
		final boolean result = verifyChunkConsistency.isSequenceMonotonic(payloadsPerCardSet);

		// then
		assertFalse(result);
	}

	@Test
	@DisplayName("validate the monotony check algorithm detect illegal start of index")
	void monotonyCheckDetectIllegalStartOfIndex() {
		// given
		final var verifyChunkConsistency = new VerifyChunkConsistency(pathService, applicationEventPublisherMock, extractionService);
		final List<List<Path>> payloadsPerCardSet = List.of(
				List.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.2.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.3.json")
				)
		);

		// when
		final boolean result = verifyChunkConsistency.isSequenceMonotonic(payloadsPerCardSet);

		// then
		assertFalse(result);
	}

	@Test
	@DisplayName("validate the monotony check algorithm fails if any sequence is wrong")
	void monotonyCheckPriorityToFailing() {
		// given
		final var verifyChunkConsistency = new VerifyChunkConsistency(pathService, applicationEventPublisherMock, extractionService);
		final List<List<Path>> payloadsPerCardSet = List.of(
				List.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.0.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.2.json")
				),
				List.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.2.json")
				)
		);

		// when
		final boolean result = verifyChunkConsistency.isSequenceMonotonic(payloadsPerCardSet);

		// then
		assertFalse(result);
	}

	@Test
	@DisplayName("validate the monotony check algorithm pass if nothing to check")
	void monotonyCheckEmptyListIsValid() {
		// given
		final var verifyChunkConsistency = new VerifyChunkConsistency(pathService, applicationEventPublisherMock, extractionService);
		final List<List<Path>> payloadsPerCardSet = Collections.emptyList();

		// when
		final boolean result = verifyChunkConsistency.isSequenceMonotonic(payloadsPerCardSet);

		// then
		assertTrue(result);
	}
}