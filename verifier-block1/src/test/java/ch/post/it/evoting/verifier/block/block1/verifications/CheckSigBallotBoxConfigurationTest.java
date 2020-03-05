package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import io.jsonwebtoken.SignatureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckSigBallotBoxConfigurationTest extends Block1VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigBallotBoxConfiguration();
    }

    @Test
    public void executeTestSignValid() throws Exception {
        final VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource(
                "/CheckSigBallotBoxConfigurationTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestSignInvalid() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ADMIN_BOARD_CERT);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundIntermediateCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.TENANT_100);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundRootCertificate() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.PLATFORM_ROOT_CA);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE3").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundBallotBox() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOX);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE4").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFoundBallotBoxSign() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOX);
        exceptionRule.expectMessage(structureNode.getQualifier() + RelationType.SIGN.toFileExtension());
        verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE5").toURI()));
    }

    @Test
    public void executeTestNOKMissingJWT() throws Exception {
        exceptionRule.expect(JsonMissingNodeException.class);
        exceptionRule.expectMessage("The signature is missing from the file!");
        verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOJWT").toURI()));
    }

    @Test
    public void executeTestNOKModifiedJWT() throws Exception {
        exceptionRule.expect(SignatureException.class);
        exceptionRule.expectMessage(
                "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
        verification.verify(Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-JWT").toURI()));
    }

}
