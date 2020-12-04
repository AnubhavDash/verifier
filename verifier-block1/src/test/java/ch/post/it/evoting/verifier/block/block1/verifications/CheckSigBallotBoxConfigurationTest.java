package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CheckSigBallotBoxConfigurationTest extends Block1VerificationAbstractTest {


    @BeforeEach
    void setup() {
        verification = new CheckSigBallotBoxConfiguration();
    }

    @Test
    void executeTestSignValid() throws Exception {
        final VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckSigBallotBoxConfigurationTest/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    void executeTestSignInvalid() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK").toURI()))
        );
        assertEquals("The signature verification of the file failed", ex.getMessage());
    }

    @Test
    void executeTestNOKFileNotFoundCertificate() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ADMIN_BOARD_CERT);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }

    @Test
    void executeTestNOKFileNotFoundIntermediateCertificate() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE2").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }

    @Test
    void executeTestNOKFileNotFoundRootCertificate() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE3").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.PLATFORM_ROOT_CA);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }

    @Test
    void executeTestNOKFileNotFoundBallotBox() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE4").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOX);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }

    @Test
    void executeTestNOKFileNotFoundBallotBoxSign() {
        final NoSuchFileException ex = assertThrows(
                NoSuchFileException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE5").toURI()))
        );
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOX);
        assertTrue(ex.getMessage().contains(structureNode.getQualifier()));
    }

    @Test
    void executeTestNOKMissingJWT() {
        final JsonMissingNodeException ex = assertThrows(
                JsonMissingNodeException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOJWT").toURI()))
        );
        assertEquals("The signature is missing from the file!", ex.getMessage());
    }

    @Test
    void executeTestNOKModifiedJWT() {
        final SignatureException ex = assertThrows(
                SignatureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-JWT").toURI()))
        );
        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted" +
                ".", ex.getMessage());
    }

}
