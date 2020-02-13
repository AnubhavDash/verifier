package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckSigSuccessfulVotesTest extends Block2VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigSuccessfulVotes();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKInvalidSignature() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.SUCCESSFUL_VOTES);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKBallotFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.BALLOT_BOX);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKElectionFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        final StructureNode structureNode = verification.getPathService().getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
        exceptionRule.expectMessage(structureNode.getQualifier());
        verification.verify(Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/NOK-NOFILE3").toURI()));
    }

    @Test
    public void executeTestNOKSignCertNotFound() throws Exception {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigSuccessfulVotes.BALLOT_BOX_CERT));
        verification.verify(Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/NOK-NOCERT").toURI()));
    }

    @Test
    public void executeTestNOKInterCertNotFound() throws Exception {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigSuccessfulVotes.SERVICES_CA));
        verification.verify(Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/NOK-NOCERT2").toURI()));
    }

    @Test
    public void executeTestNOKRootNotFound() throws Exception {
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigSuccessfulVotes.ELECTION_ROOT_CA));
        verification.verify(Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/NOK-NOCERT3").toURI()));
    }
}
