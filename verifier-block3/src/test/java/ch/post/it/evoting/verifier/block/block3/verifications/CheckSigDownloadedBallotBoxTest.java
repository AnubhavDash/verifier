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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckSigDownloadedBallotBoxTest extends Block3VerificationAbstractTest {

	public CheckSigDownloadedBallotBoxTest() {
		super(CheckSigDownloadedBallotBox.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKInvalidSignature() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK").toURI()))
		);
		assertEquals("The signature verification of the file failed", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFound() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.DOWNLOADED_BALLOT_BOX);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKBallotFileNotFound() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOX);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKElectionFileNotFound() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOFILE3").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKSignCertNotFound() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOCERT").toURI()))
		);
		assertEquals(String.format("%s certificate is missing!", CheckSigDownloadedBallotBox.BALLOT_BOX_CERT_NODE), ex.getMessage());
	}

	@Test
	void executeTestNOKInterCertNotFound() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOCERT2").toURI()))
		);
		assertEquals(String.format("%s certificate is missing!", CheckSigDownloadedBallotBox.SERVICES_CA_NODE), ex.getMessage());
	}

	@Test
	void executeTestNOKRootNotFound() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigDownloadedBallotBoxTest/NOK-NOCERT3").toURI()))
		);
		assertEquals(String.format("%s certificate is missing!", CheckSigDownloadedBallotBox.ELECTION_ROOT_CA_NODE), ex.getMessage());
	}
}
