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

public class CheckSigCredentialDataTest {
    private CheckSigCredentialData checkSigCredentialData;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        checkSigCredentialData = new CheckSigCredentialData();
    }

    @Test
    public void executeTestAllSignValid() throws Exception {
        final VerificationResult verificationResult = checkSigCredentialData.verify(Paths.get(getClass().getResource(
                "/CheckSigCredentialDataTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestOneSignInvalid() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The signature verification of the file failed");
        checkSigCredentialData.verify(Paths.get(getClass().getResource("/CheckSigCredentialDataTest/NOK").toURI()));
    }
}
