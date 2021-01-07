/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General  License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General  License for more details.
 *
 * You should have received a copy of the GNU General  License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block1.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.test.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckSigEncryptionParamsTest extends Block1VerificationAbstractTest {

	@BeforeEach
	void setup() {
		verification = new CheckSigEncryptionParams();
	}

	@Test
	void executeTestOK() throws Exception {
		// TODO Generate a new test encryptionParameters.json.p7 file
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/CheckSigEncryptionParamsTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKCertKo() {
		// TODO Check if test is relevant, because executeTestNOKJsonKo got the same error
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigEncryptionParamsTest/NOK/CERT-NOT-OK").toURI()))
		);
		assertEquals("The signature verification of the file encryptionParameters.json failed", ex.getMessage());
	}

	@Test
	void executeTestNOKJsonKo() {
		// TODO Check if test is relevant, because executeTestNOKCertKo got the same error
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigEncryptionParamsTest/NOK/JSON-NOT-OK").toURI()))
		);
		assertEquals("The signature verification of the file encryptionParameters.json failed", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundRootCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigEncryptionParamsTest/NOK/NOK-NOFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.INTEGRATION_CA);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundEncryptionParameters() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigEncryptionParamsTest/NOK/NOK-NOFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ENCRYPTION_PARAMETERS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundEncryptionParametersP7() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigEncryptionParamsTest/NOK/NOK-NOFILE3").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ENCRYPTION_PARAMETERS);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.P7.toFileExtension()).matches(ex.getMessage()));
	}
}
