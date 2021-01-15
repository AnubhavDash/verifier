package ch.post.it.evoting.verifier.block.block2.verifications;

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

class CheckVoteSignatureTest extends Block2VerificationAbstractTest {

	public CheckVoteSignatureTest() {
		super(CheckVoteSignature.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKInvalidSignature() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK").toURI()))
		);
		assertEquals("The signature verification of the vote failed", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundDownloadedBallot() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.DOWNLOADED_BALLOT_BOX);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundElectionInfo() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKInterCertNotFound() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOCERT").toURI()))
		);
		assertEquals(String.format("%s certificate is missing!", CheckVoteSignature.CREDENTIALS_CA), ex.getMessage());
	}

	@Test
	void executeTestNOKRootNotFound() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOCERT2").toURI()))
		);
		assertEquals(String.format("%s certificate is missing!", CheckVoteSignature.ELECTION_ROOT_CA), ex.getMessage());
	}

}
