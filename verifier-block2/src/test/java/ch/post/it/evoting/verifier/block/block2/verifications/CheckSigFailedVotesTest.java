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

public class CheckSigFailedVotesTest extends Block2VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new CheckSigFailedVotes();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKInvalidSignature() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK").toURI()));
    }

    @Test
    public void executeTestNOKFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(CheckSigFailedVotes.FAILED_VOTES_CSV);
        verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE").toURI()));
    }

    @Test
    public void executeTestNOKBallotFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(CheckSigFailedVotes.BALLOT_BOX_JSON);
        verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE2").toURI()));
    }

    @Test
    public void executeTestNOKElectionFileNotFound() throws Exception {
        exceptionRule.expect(NoSuchFileException.class);
        exceptionRule.expectMessage(CheckSigFailedVotes.ELECTION_INFORMATION_CONTENTS_JSON);
        verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE3").toURI()));
    }

    @Test
    public void executeTestNOKSignCertNotFound() throws Exception {
        exceptionRule.expect(JsonMissingNodeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigFailedVotes.BALLOT_BOX_CERT));
        verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT").toURI()));
    }

    @Test
    public void executeTestNOKInterCertNotFound() throws Exception {
        exceptionRule.expect(JsonMissingNodeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigFailedVotes.SERVICES_CA));
        verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT2").toURI()));
    }

    @Test
    public void executeTestNOKRootNotFound() throws Exception {
        exceptionRule.expect(JsonMissingNodeException.class);
        exceptionRule.expectMessage(String.format("%s certificate is missing!", CheckSigFailedVotes.ELECTION_ROOT_CA));
        verification.verify(Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT3").toURI()));
    }
}
