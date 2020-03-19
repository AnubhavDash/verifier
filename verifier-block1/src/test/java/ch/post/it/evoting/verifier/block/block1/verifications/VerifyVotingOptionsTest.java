package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Paths;

public class VerifyVotingOptionsTest extends Block1VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new VerifyVotingOptions();
    }

    @Test
    public void executeTestOK() throws Exception {
        VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/VerifyVotingOptionsTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOKG() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The primes list contains the generator g.");
        verification.verify(Paths.get(getClass().getResource("/VerifyVotingOptionsTest/NOK-G").toURI()));
    }

    @Test
    public void executeTestNOKDuplicates() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The primes list contains duplicates.");
        verification.verify(Paths.get(getClass().getResource("/VerifyVotingOptionsTest/NOK-DUPLICATES").toURI()));
    }

    @Test
    public void executeTestNOKSmallest() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("There is a prime number of the subgroup not present in the primes list.");
        verification.verify(Paths.get(getClass().getResource("/VerifyVotingOptionsTest/NOK-SMALLEST").toURI()));
    }

}
