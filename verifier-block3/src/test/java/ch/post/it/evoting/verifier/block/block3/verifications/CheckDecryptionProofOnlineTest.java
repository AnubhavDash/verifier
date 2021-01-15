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
package ch.post.it.evoting.verifier.block.block3.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckDecryptionProofOnlineTest extends Block3VerificationAbstractTest {

	public CheckDecryptionProofOnlineTest() {
		super(CheckDecryptionProofOnline.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult result = verification.verify(Paths.get(getClass().getResource("/CheckDecryptionProofOnlineTest/OK").toURI()));
		assertEquals(Status.OK, result.getStatus());
	}

	@Test
	void executeTestNOK() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckDecryptionProofOnlineTest/NOK").toURI()))
		);
		assertEquals("the number of control components expected is 3 but actual is 1", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFound() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckDecryptionProofOnlineTest/NOK-NOTFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.CC_MIXING_KEYS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

}
