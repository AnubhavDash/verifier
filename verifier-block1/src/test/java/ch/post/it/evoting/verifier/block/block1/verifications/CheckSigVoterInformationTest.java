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
import ch.post.it.evoting.verifier.common.block.test.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckSigVoterInformationTest extends Block1VerificationAbstractTest {

	public CheckSigVoterInformationTest() {
		super(CheckSigVoterInformation.class);
	}

	@Test
	void executeTestAllSignValid() throws Exception {
		final VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
				"/CheckSigVoterInformationTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestOneSignInvalid() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK").toURI()))
		);
		assertEquals("The signature verification of the file failed", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ADMIN_BOARD_CERT);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundIntermediateCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundRootCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE3").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.PLATFORM_ROOT_CA);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundVotingCardSetsIdDir() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE4").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.VOTING_CARD_SETS_ID_DIR);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundCredential() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE5").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.VOTER_INFORMATION);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundCredentialSign() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigVoterInformationTest/NOK-NOFILE6").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.VOTER_INFORMATION);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.SIGN.toFileExtension()).matches(ex.getMessage()));
	}
}
