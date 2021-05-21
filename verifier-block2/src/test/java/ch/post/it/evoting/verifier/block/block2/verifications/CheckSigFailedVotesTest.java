package ch.post.it.evoting.verifier.block.block2.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

class CheckSigFailedVotesTest extends Block2VerificationAbstractTest {

	public CheckSigFailedVotesTest() {
		super(CheckSigFailedVotes.class);
	}

	@Test
	@Disabled("Certificate in dataset has expired, temporary deactivation until a new dataset is provided")
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKInvalidSignature() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK").toURI()))
		);
		assertEquals("The signature verification of the file failed", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFound() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.FAILED_VOTES);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKBallotFileNotFound() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOX);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKElectionFileNotFound() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE3").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKSignCertNotFound() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT").toURI()))
		);
		assertEquals(String.format("%s certificate is missing!", CheckSigFailedVotes.BALLOT_BOX_CERT), ex.getMessage());
	}

	@Test
	void executeTestNOKInterCertNotFound() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT2").toURI()))
		);
		assertEquals(String.format("%s certificate is missing!", CheckSigFailedVotes.SERVICES_CA), ex.getMessage());
	}

	@Test
	void executeTestNOKRootNotFound() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT3").toURI()))
		);
		assertEquals(String.format("%s certificate is missing!", CheckSigFailedVotes.ELECTION_ROOT_CA), ex.getMessage());
	}
}
