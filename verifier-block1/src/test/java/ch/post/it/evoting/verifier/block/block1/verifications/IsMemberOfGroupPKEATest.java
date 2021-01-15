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

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class IsMemberOfGroupPKEATest extends Block1VerificationAbstractTest {

	public IsMemberOfGroupPKEATest() {
		super(IsMemberOfGroupPKEA.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/IsMemberOfGroupPKEATest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOK() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK").toURI()))
		);
		assertEquals("No such Elements was found in the publicKey", ex.getMessage());
	}

	@Test
	void executeTestNOK2() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK2").toURI()))
		);
		assertEquals("Euler criterion does not equal to 1", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFound() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/IsMemberOfGroupPKEATest/NOK/NOK-NOFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ELECTORAL_PUBLIC_KEY);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

}
