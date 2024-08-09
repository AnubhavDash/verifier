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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyChunkConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyChunkConsistency(pathService, resultPublisherServiceMock, controlComponentCodeSharesPayloadDataExtractor,
				setupComponentVerificationDataPayloadDataExtractor);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("validate the monotony check algorithm pass with happy path")
	void monotonyCheckWorkWithHappyPath() {
		// given
		final VerifyChunkConsistency verifyChunkConsistency = new VerifyChunkConsistency(pathService, resultPublisherServiceMock,
				controlComponentCodeSharesPayloadDataExtractor, setupComponentVerificationDataPayloadDataExtractor);
		final ImmutableList<ImmutableList<Path>> payloadsPerCardSet = ImmutableList.of(
				ImmutableList.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.0.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.2.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.3.json")
				),
				ImmutableList.of(
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
		final VerifyChunkConsistency verifyChunkConsistency = new VerifyChunkConsistency(pathService, resultPublisherServiceMock,
				controlComponentCodeSharesPayloadDataExtractor, setupComponentVerificationDataPayloadDataExtractor);
		final ImmutableList<ImmutableList<Path>> payloadsPerCardSet = ImmutableList.of(
				ImmutableList.of(
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
		final VerifyChunkConsistency verifyChunkConsistency = new VerifyChunkConsistency(pathService, resultPublisherServiceMock,
				controlComponentCodeSharesPayloadDataExtractor, setupComponentVerificationDataPayloadDataExtractor);
		final ImmutableList<ImmutableList<Path>> payloadsPerCardSet = ImmutableList.of(
				ImmutableList.of(
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
		final VerifyChunkConsistency verifyChunkConsistency = new VerifyChunkConsistency(pathService, resultPublisherServiceMock,
				controlComponentCodeSharesPayloadDataExtractor, setupComponentVerificationDataPayloadDataExtractor);
		final ImmutableList<ImmutableList<Path>> payloadsPerCardSet = ImmutableList.of(
				ImmutableList.of(
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
		final VerifyChunkConsistency verifyChunkConsistency = new VerifyChunkConsistency(pathService, resultPublisherServiceMock,
				controlComponentCodeSharesPayloadDataExtractor, setupComponentVerificationDataPayloadDataExtractor);
		final ImmutableList<ImmutableList<Path>> payloadsPerCardSet = ImmutableList.of(
				ImmutableList.of(
						Path.of("/root/dir/controlComponentPublicKeysPayload.0.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.1.json"),
						Path.of("/root/dir/controlComponentPublicKeysPayload.2.json")
				),
				ImmutableList.of(
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
		final VerifyChunkConsistency verifyChunkConsistency = new VerifyChunkConsistency(pathService, resultPublisherServiceMock,
				controlComponentCodeSharesPayloadDataExtractor, setupComponentVerificationDataPayloadDataExtractor);
		final ImmutableList<ImmutableList<Path>> payloadsPerCardSet = ImmutableList.emptyList();

		// when
		final boolean result = verifyChunkConsistency.isSequenceMonotonic(payloadsPerCardSet);

		// then
		assertTrue(result);
	}
}
