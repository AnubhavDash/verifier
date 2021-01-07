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
package ch.post.it.evoting.verifier.block.block1.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckGeneratorGTest extends Block1VerificationAbstractTest {

	@BeforeEach
	void setup() {
		verification = new CheckGeneratorG();
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckGeneratorGTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKPrimality() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckGeneratorGTest/NOK/NOK").toURI()))
		);
		assertEquals("The generator g is not prime", ex.getMessage());
	}

	@Test
	void executeTestNOKEulerCriterion() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckGeneratorGTest/NOK/NOK-EULER").toURI()))
		);
		assertEquals("g is not part of the subgroup q", ex.getMessage());
	}

	@Test
	void executeTestNOKSubgroupSmallest() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckGeneratorGTest/NOK/NOK-SMALLEST").toURI()))
		);
		assertEquals("g must be the smallest prime number in the subgroup (p, q)", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFound() {
		final IOException ex = assertThrows(
				IOException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckGeneratorGTest/NOK/NOK-NOFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ENCRYPTION_PARAMETERS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}
}
