/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckConfirmationAttemptsTest extends Block2VerificationAbstractTest {

	public CheckConfirmationAttemptsTest() {
		super(CheckConfirmationAttempts.class);
	}

	@Test
	@Disabled("Enable when we got secureLog files with correct pattern.")
	void executeTest() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/CheckConfirmationAttemptsTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	@Disabled("Enable when we got secureLog files with correct pattern.")
	void executeTestNOK() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckConfirmationAttemptsTest/NOK").toURI()))
		);
		assertEquals("TODO", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundVoterInformation() {
		final FileNotFoundException ex = assertThrows(
				FileNotFoundException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckConfirmationAttemptsTest/NOK-NOTFILE").toURI()))
		);
		assertEquals("voterInformation.*\\.csv", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundMappingCcHosts() {
		final FileNotFoundException ex = assertThrows(
				FileNotFoundException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckConfirmationAttemptsTest/NOK-NOTFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.MAPPING_CC_HOSTS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

}
