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
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegularLogVerifierTest {

	private static RegularLogVerifier regularLogVerifier;
	private static Random random;
	private RegularLogVerifier.VerifyLogHmacInput.VerifyLogHmacInputBuilder regularBuilder;

	@BeforeAll
	static void setUpAll(){
		regularLogVerifier = new RegularLogVerifier();
		random = new Random();
	}

	@BeforeEach
	void setUp() {
		this.regularBuilder = RegularLogVerifier.VerifyLogHmacInput.builder()
				.withCurrentSessionKey(TestData.REGULAR_HMAC_KEY)
				.withPreviousHmac(TestData.REGULAR_PHMAC)
				.withTimestamp(TestData.REGULAR_TS)
				.withLogMessage(TestData.REGULAR_MESSAGE)
				.withLoggedHmac(TestData.REGULAR_HMAC);
	}

	@Test
	@DisplayName("Verify regular log hmac")
	void verifyLogHmacOfRegularLog() {
		var input = regularBuilder.build();
		assertTrue(regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void shortSessionKeyThrows() {
		byte[] shortInput = new byte[31];
		random.nextBytes(shortInput);
		var input = regularBuilder.withCurrentSessionKey(shortInput).build();

		assertThrows(IllegalArgumentException.class, () -> regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void shortPhmacThrows() {
		byte[] shortInput = new byte[31];
		random.nextBytes(shortInput);
		var input = regularBuilder.withPreviousHmac(shortInput).build();

		assertThrows(IllegalArgumentException.class, () -> regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void shortHmacThrows() {
		byte[] shortInput = new byte[31];
		random.nextBytes(shortInput);
		var input = regularBuilder.withLoggedHmac(shortInput).build();

		assertThrows(IllegalArgumentException.class, () -> regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void invalidSessionKeyDoesntValidate() {
		byte[] invalidInput = new byte[32];
		random.nextBytes(invalidInput);
		var input = regularBuilder.withCurrentSessionKey(invalidInput).build();

		assertFalse(regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void invalidPhmacDoesntValidate() {
		byte[] invalidInput = new byte[32];
		random.nextBytes(invalidInput);
		var input = regularBuilder.withPreviousHmac(invalidInput).build();

		assertFalse(regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void invalidTimeStampDoesntValidate() {
		var input = regularBuilder.withTimestamp(TestData.REGULAR_TS.add(BigInteger.ONE)).build();

		assertFalse(regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void messageWithCRThrows() {
		String invalidMessage = TestData.CHECKPOINT_MESSAGE + "\n";
		var input = regularBuilder.withLogMessage(invalidMessage).build();

		assertThrows(IllegalArgumentException.class, () -> regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void messageWithLFThrows() {
		String invalidMessage = TestData.CHECKPOINT_MESSAGE + "\r";
		var input = regularBuilder.withLogMessage(invalidMessage).build();

		assertThrows(IllegalArgumentException.class, () -> regularLogVerifier.verifyLogHmac(input));
	}

	@Test
	void invalidHmacDoesntValidate() {
		byte[] invalidInput = new byte[32];
		random.nextBytes(invalidInput);
		var input = regularBuilder.withLoggedHmac(invalidInput).build();

		assertFalse(regularLogVerifier.verifyLogHmac(input));
	}


}
