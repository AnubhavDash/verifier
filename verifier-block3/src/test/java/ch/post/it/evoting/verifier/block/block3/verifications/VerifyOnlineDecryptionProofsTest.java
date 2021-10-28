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
package ch.post.it.evoting.verifier.block.block3.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.MissingFileException;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

class VerifyOnlineDecryptionProofsTest extends Block3VerificationAbstractTest {

	public VerifyOnlineDecryptionProofsTest() {
		super(VerifyOnlineDecryptionProofs.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(Objects.requireNonNull(getClass().getResource("/VerifyOnlineDecryptionProofsTest/OK")).toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(Objects.requireNonNull(getClass().getResource("/VerifyOnlineDecryptionProofsTest/NOK")).toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.NOK, verificationResult.getStatus());
		assertEquals(TranslationHelper.getFromResourceBundle("block3/resources", "verification02.failure"), verificationResult.getMessage());
	}

	@Test
	void executeTestNOKFileNotFound() throws URISyntaxException {
		final Path path = Paths
				.get(Objects.requireNonNull(getClass().getResource("/VerifyOnlineDecryptionProofsTest/NOK_missingFiles")).toURI());
		MissingFileException exception = assertThrows(MissingFileException.class, () -> verification.verify(path));
		assertEquals("Missing shufflePayload file(s)", exception.getMessage());
	}

	@Test
	void executeTestNOKCorruptedFile() throws URISyntaxException {
		final Path path = Paths
				.get(Objects.requireNonNull(getClass().getResource("/VerifyOnlineDecryptionProofsTest/NOK_corruptedFile")).toURI());
		assertThrows(UncheckedIOException.class, () -> verification.verify(path));
	}
}