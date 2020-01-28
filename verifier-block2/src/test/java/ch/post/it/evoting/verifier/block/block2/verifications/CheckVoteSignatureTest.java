package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class CheckVoteSignatureTest extends Block2VerificationAbstractTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckVoteSignature();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKInvalidSignature() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the vote failed");
        verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(CheckVoteSignature.DOWNLOADED_BALLOT_BOX_CSV);
        verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKElectionFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(CheckVoteSignature.ELECTION_INFORMATION_CONTENTS_JSON);
        verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKInterCertNotFound() throws Exception {
        exceptionRule.expect(JsonMissingNodeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckVoteSignature.CREDENTIALS_CA));
        verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOCERT").toURI()));
    }

    @Test
    public void executeTestNOKRootNotFound() throws Exception {
        exceptionRule.expect(JsonMissingNodeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckVoteSignature.ELECTION_ROOT_CA));
        verification.verify(Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOCERT2").toURI()));
    }

}
