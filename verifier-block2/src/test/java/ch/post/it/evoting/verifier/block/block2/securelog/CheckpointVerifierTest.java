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
package ch.post.it.evoting.verifier.block.block2.securelog;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.core.internal.exceptions.VerificationException;

class CheckpointVerifierTest {

	private static PublicKey signingPublicKey;
	private static CheckpointVerifier checkpointVerifier;
	private static Random random;

	@BeforeAll
	static void setUp() throws NoSuchAlgorithmException, InvalidKeySpecException {
		RSAPublicKeySpec spec = new RSAPublicKeySpec(TestData.MODULUS, TestData.PUBLIC_EXPONENT);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		signingPublicKey = factory.generatePublic(spec);
		checkpointVerifier = new CheckpointVerifier();
		random = new Random();
	}

	@Nested
	@DisplayName("VerifyLogHmacCheckpoint")
	class VerifyLogHmacCheckpoint {

		private CheckpointVerifier.VerifyLogHmacCheckpointInput.VerifyLogHmacCheckpointInputBuilder initialCheckpointBuilder;
		private CheckpointVerifier.VerifyLogHmacCheckpointInput.VerifyLogHmacCheckpointInputBuilder checkpointBuilder;

		@BeforeEach
		void setUp() {
			this.initialCheckpointBuilder = CheckpointVerifier.VerifyLogHmacCheckpointInput.builder()
					.withCurrentSessionKey(TestData.INITIAL_CHECKPOINT_HMAC_KEY)
					.withHmac(TestData.INITIAL_CHECKPOINT_HMAC)
					.withEncryptedSessionKey(TestData.INITIAL_CHECKPOINT_ESK)
					.withLiberateSessionKey(TestData.INITIAL_CHECKPOINT_LSK)
					.withLogMessage(TestData.INITIAL_CHECKPOINT_MESSAGE)
					.withPreviousHmac(TestData.INITIAL_CHECKPOINT_PHMAC)
					.withMaxLineNumber(TestData.INITIAL_CHECKPOINT_MAX_LINES)
					.withTimestamp(TestData.INITIAL_CHECKPOINT_TS);

			this.checkpointBuilder = CheckpointVerifier.VerifyLogHmacCheckpointInput.builder()
					.withCurrentSessionKey(TestData.CHECKPOINT_HMAC_KEY)
					.withHmac(TestData.CHECKPOINT_HMAC)
					.withEncryptedSessionKey(TestData.CHECKPOINT_ESK)
					.withLiberateSessionKey(TestData.CHECKPOINT_LSK)
					.withLogMessage(TestData.CHECKPOINT_MESSAGE)
					.withPreviousHmac(TestData.CHECKPOINT_PHMAC)
					.withMaxLineNumber(TestData.CHECKPOINT_MAX_LINES)
					.withTimestamp(TestData.CHECKPOINT_TS);
		}

		@Test
		@DisplayName("Verify first line checkpoint log hmac")
		void verifyLogHmacInitialCheckpoint() {
			var input = initialCheckpointBuilder.build();

			assertTrue(checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacInitialCheckpointWithPHMACAndNoLSKThrows() {
			byte[] previousHmac = new byte[32];
			random.nextBytes(previousHmac);
			var input = initialCheckpointBuilder.withPreviousHmac(previousHmac).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacInitialCheckpointWithNoPHMACAndLSKThrows() {
			byte[] lsk = new byte[32];
			random.nextBytes(lsk);
			var input = initialCheckpointBuilder.withLiberateSessionKey(lsk).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithShortHmacThrows() {
			byte[] shortInput = new byte[31];
			random.nextBytes(shortInput);
			var input = checkpointBuilder.withHmac(shortInput).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithShortSessionKeyThrows() {
			byte[] shortInput = new byte[31];
			random.nextBytes(shortInput);
			var input = checkpointBuilder.withCurrentSessionKey(shortInput).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithShortLSKThrows() {
			byte[] shortInput = new byte[31];
			random.nextBytes(shortInput);
			var input = checkpointBuilder.withLiberateSessionKey(shortInput).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithShortPHMACThrows() {
			byte[] shortInput = new byte[31];
			random.nextBytes(shortInput);
			var input = checkpointBuilder.withPreviousHmac(shortInput).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithInvalidHmacDoesntValidate() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withHmac(invalidInput).build();

			assertFalse(checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithInvalidSessionKeyDoesntValidate() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withCurrentSessionKey(invalidInput).build();

			assertFalse(checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithInvalidLSKDoesntValidate() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withLiberateSessionKey(invalidInput).build();

			assertFalse(checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithInvalidPHMACDoesntValidate() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withPreviousHmac(invalidInput).build();

			assertFalse(checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithInvalidTimestampDoesntValidate() {
			var input = checkpointBuilder.withTimestamp(TestData.CHECKPOINT_TS.add(BigInteger.ONE)).build();
			assertFalse(checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithInvalidMaxLinesValidates() {
			var input = checkpointBuilder.withMaxLineNumber(TestData.CHECKPOINT_MAX_LINES.add(BigInteger.ONE)).build();

			assertFalse(checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithCRInMessageDoesntValidate() {
			String invalidMessage = TestData.CHECKPOINT_MESSAGE + "\n";
			var input = checkpointBuilder.withLogMessage(invalidMessage).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		void verifyLogHmacCheckpointWithLFInMessageDoesntValidate() {
			String invalidMessage = TestData.CHECKPOINT_MESSAGE + "\r";
			var input = checkpointBuilder.withLogMessage(invalidMessage).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyLogHmacCheckpoint(input));
		}

		@Test
		@DisplayName("Verify regular checkpoint log hmac")
		void verifyLogHmacRegularCheckpoint() {
			var input = checkpointBuilder.build();
			assertTrue(checkpointVerifier.verifyLogHmacCheckpoint(input));
		}
	}


	@Nested
	class VerifyCheckpointSignature {

		private CheckpointVerifier.VerifyCheckpointSignatureInput.VerifyCheckpointSignatureInputBuilder initalCheckpointBuilder;
		private CheckpointVerifier.VerifyCheckpointSignatureInput.VerifyCheckpointSignatureInputBuilder checkpointBuilder;

		@BeforeEach
		void setUp() {
			this.initalCheckpointBuilder = CheckpointVerifier.VerifyCheckpointSignatureInput.builder()
					.withSigningPublicKey(signingPublicKey)
					.withHmac(TestData.INITIAL_CHECKPOINT_HMAC)
					.withEncryptedSessionKey(TestData.INITIAL_CHECKPOINT_ESK)
					.withLiberateSessionKey(TestData.INITIAL_CHECKPOINT_LSK)
					.withLogMessage(TestData.INITIAL_CHECKPOINT_MESSAGE)
					.withPreviousHmac(TestData.INITIAL_CHECKPOINT_PHMAC)
					.withMaxLineNumber(TestData.INITIAL_CHECKPOINT_MAX_LINES)
					.withTimestamp(TestData.INITIAL_CHECKPOINT_TS)
					.withSignature(TestData.INITIAL_CHECKPOINT_SIGNATURE);

			this.checkpointBuilder = CheckpointVerifier.VerifyCheckpointSignatureInput.builder()
					.withSigningPublicKey(signingPublicKey)
					.withHmac(TestData.CHECKPOINT_HMAC)
					.withEncryptedSessionKey(TestData.CHECKPOINT_ESK)
					.withLiberateSessionKey(TestData.CHECKPOINT_LSK)
					.withLogMessage(TestData.CHECKPOINT_MESSAGE)
					.withPreviousHmac(TestData.CHECKPOINT_PHMAC)
					.withMaxLineNumber(TestData.CHECKPOINT_MAX_LINES)
					.withTimestamp(TestData.CHECKPOINT_TS)
					.withSignature(TestData.CHECKPOINT_SIGNATURE);
		}

		@Test
		@DisplayName("Verify initial checkpoint log signature")
		void verifyCheckpointSignatureFirstLineCheckpoint() {
			var input = initalCheckpointBuilder.build();

			assertTrue(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		@DisplayName("Verify regular checkpoint log signature")
		void verifyCheckpointSignatureRegularCheckpoint() {
			var input = checkpointBuilder.build();

			assertTrue(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void malformedSignatureThrows() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withSignature(invalidInput).build();

			assertThrows(VerificationException.class, () -> checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void invalidSignatureDoesntValidate() {
			var input = checkpointBuilder.withSignature(TestData.INITIAL_CHECKPOINT_SIGNATURE).build();

			assertFalse(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void shortLSKThrows() {
			byte[] shortInput = new byte[31];
			random.nextBytes(shortInput);
			var input = checkpointBuilder.withLiberateSessionKey(shortInput).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void shortPHMACThrows() {
			byte[] shortInput = new byte[31];
			random.nextBytes(shortInput);
			var input = checkpointBuilder.withPreviousHmac(shortInput).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void shortHMACThrows() {
			byte[] shortInput = new byte[31];
			random.nextBytes(shortInput);
			var input = checkpointBuilder.withHmac(shortInput).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void invalidSigningKeyDoesntValidate() throws NoSuchAlgorithmException, InvalidKeySpecException {
			RSAPublicKeySpec spec = new RSAPublicKeySpec(TestData.MODULUS, TestData.PUBLIC_EXPONENT.add(BigInteger.ONE));
			KeyFactory factory = KeyFactory.getInstance("RSA");
			var invalidSigningPublicKey = factory.generatePublic(spec);
			var input = checkpointBuilder.withSigningPublicKey(invalidSigningPublicKey).build();

			assertFalse(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void invalidLSKDoesntValidate() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withLiberateSessionKey(invalidInput).build();

			assertFalse(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void invalidESKDoesntValidate() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withEncryptedSessionKey(invalidInput).build();

			assertFalse(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void invalidPHMACDoesntValidate() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withPreviousHmac(invalidInput).build();

			assertFalse(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void invalidMaxLinesDoesntValidate() {
			var input = checkpointBuilder.withMaxLineNumber(TestData.CHECKPOINT_MAX_LINES.add(BigInteger.ONE)).build();

			assertFalse(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void invalidTimestampDoesntValidate() {
			var input = checkpointBuilder.withTimestamp(TestData.CHECKPOINT_TS.add(BigInteger.ONE)).build();

			assertFalse(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void invalidHMACDoesntValidate() {
			byte[] invalidInput = new byte[32];
			random.nextBytes(invalidInput);
			var input = checkpointBuilder.withHmac(invalidInput).build();

			assertFalse(checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void emptyLSKButNotPHMACThrows() {
			var input = checkpointBuilder.withLiberateSessionKey(new byte[]{}).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyCheckpointSignature(input));
		}

		@Test
		void emptyPHMACButNotLSKThrow() {
			var input = checkpointBuilder.withPreviousHmac(new byte[]{}).build();

			assertThrows(IllegalArgumentException.class, () -> checkpointVerifier.verifyCheckpointSignature(input));
		}
	}
}
