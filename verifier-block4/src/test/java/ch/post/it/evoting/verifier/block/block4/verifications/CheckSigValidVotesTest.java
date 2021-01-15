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
package ch.post.it.evoting.verifier.block.block4.verifications;

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
import ch.post.it.evoting.verifier.common.block.test.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckSigValidVotesTest extends Block4VerificationAbstractTest {

	public CheckSigValidVotesTest() {
		super(CheckSigValidVotes.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest" +
				"/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKCsvKo() {
		// TODO Check if test is relevant, because executeTestNOKCertKo got the same error
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK/CSV-NOT-OK").toURI()))
		);
		assertEquals("The signature verification of the svv_[EE_alias].csv report failed", ex.getMessage());
	}

	@Test
	void executeTestNOKCertKo() {
		// TODO Check if test is relevant, because executeTestNOKCsvKo got the same error
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK/CERT-NOT-OK").toURI()))
		);
		assertEquals("The signature verification of the svv_[EE_alias].csv report failed", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK-NOTFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ADMIN_BOARD_CERT);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
	}

	@Test
	void executeTestNOKFileNotFoundRootCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK-NOTFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
	}

	@Test
	void executeTestNOKFileNotFoundValidVotesResult() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK-NOTFILE3").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.VALID_VOTES_RESULT);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier()).matches(ex.getMessage()));
	}

	@Test
	void executeTestNOKFileNotFoundValidVotesResultMetadata() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigValidVotesTest/NOK-NOTFILE4").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.VALID_VOTES_RESULT);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.METADATA.toFileExtension()).matches(ex.getMessage()));
	}
}
