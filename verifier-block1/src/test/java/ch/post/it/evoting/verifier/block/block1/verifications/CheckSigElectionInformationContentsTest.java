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
import ch.post.it.evoting.verifier.common.block.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.test.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;

import io.jsonwebtoken.SignatureException;

class CheckSigElectionInformationContentsTest extends Block1VerificationAbstractTest {

	public CheckSigElectionInformationContentsTest() {
		super(CheckSigElectionInformationContents.class);
	}

	@Test
	void executeTestSignValid() throws Exception {
		final VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
				"/CheckSigElectionInformationContentsTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestSignInvalid() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK").toURI()))
		);
		assertEquals("The signature verification of the file failed", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ADMIN_BOARD_CERT);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundIntermediateCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE2").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundRootCertificate() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE3").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.PLATFORM_ROOT_CA);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundElectionInfo() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE4").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundElectionInfoSign() {
		final NoSuchFileException ex = assertThrows(
				NoSuchFileException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE5").toURI()))
		);
		final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.SIGN.toFileExtension()).matches(ex.getMessage()));
	}

	@Test
	void executeTestNOKMissingJWT() {
		final JsonMissingNodeException ex = assertThrows(
				JsonMissingNodeException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOJWT").toURI()))
		);
		assertEquals("The signature is missing from the file!", ex.getMessage());
	}

	@Test
	void executeTestNOKModifiedJWT() {
		final SignatureException ex = assertThrows(
				SignatureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-JWT").toURI()))
		);
		assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted" +
				".", ex.getMessage());
	}

}
