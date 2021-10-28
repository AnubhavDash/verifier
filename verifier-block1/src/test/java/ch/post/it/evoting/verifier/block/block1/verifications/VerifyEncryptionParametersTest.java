/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.block.block1.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;

class VerifyEncryptionParametersTest extends Block1VerificationAbstractTest {

	public VerifyEncryptionParametersTest() {
		super(VerifyEncryptionParameters.class);
	}

	@Test
	void executeTestOK() throws Exception {
		final VerificationResult verificationResult = verification
				.verify(Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/OK").toURI()));

		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOK_P() throws Exception {
		final Path inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/NOK_P").toURI());
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> verification.verify(inputDirectory));

		assertEquals("p must be equal to 2 * q + 1.", exception.getMessage());
	}

	@Test
	void executeTestNOK_Q() throws Exception {
		final Path inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/NOK_Q").toURI());
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> verification.verify(inputDirectory));

		assertEquals("p must be equal to 2 * q + 1.", exception.getMessage());
	}

	@Test
	void executeTestNOK_G() throws Exception {
		final Path inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/NOK_G").toURI());
		final VerificationFailureException exception = assertThrows(VerificationFailureException.class, () -> verification.verify(inputDirectory));

		assertEquals("The provided encryption parameters do not match the computed ones.", exception.getMessage());
	}

	@Test
	void executeTestNOK() throws Exception {
		final Path inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/NOK").toURI());
		final VerificationFailureException exception = assertThrows(VerificationFailureException.class, () -> verification.verify(inputDirectory));

		assertEquals("The provided encryption parameters do not match the computed ones.", exception.getMessage());
	}
}