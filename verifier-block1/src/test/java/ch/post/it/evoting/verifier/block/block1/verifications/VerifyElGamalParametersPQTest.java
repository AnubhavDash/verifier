package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.SlowTestCategory;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.nio.file.Paths;

public class VerifyElGamalParametersPQTest extends Block1VerificationAbstractTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        verification = new VerifyElGamalParametersPQ();
    }

    @Test
    public void executeTestOK() throws Exception {
        final VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/OK").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    @Category(SlowTestCategory.class)
    public void executeTestOKIntensive() throws Exception {
        final VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/OKI").toURI()));
        Assert.assertNotNull(verificationResult);
        Assert.assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    public void executeTestNOK_L() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("p bit length is not equal to " + VerifyElGamalParametersPQ.EXPECTED_P_BIT_LEN + " bits.");
        verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_L").toURI()));
    }

    @Test
    public void executeTestNOK_N() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("q bit length is not equal to " + VerifyElGamalParametersPQ.EXPECTED_Q_BIT_LEN + " bits.");
        verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_N").toURI()));
    }

    @Test
    public void executeTestNOK_CounterValue() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The q counter is too large.");
        verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_CounterValue").toURI()));
    }

    @Test
    public void executeTestNOK_SeedLength() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The seed has not the required size (" + VerifyElGamalParametersPQ.MIN_SEED_BIT_LEN + " bits).");
        verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_SeedLength").toURI()));
    }

    @Test
    public void executeTestNOK_qCounter() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("A smaller counter value giving another pair of valid primes was found.");
        verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_qCounter").toURI()));
    }

    @Test
    public void executeTestNOK_computedP() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The calculated values of (p,q) do not correspond to the provided values of (p,q).");
        verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_computedP").toURI()));
    }

    @Test
    public void executeTestNOK_computedQ() throws Exception {
        exceptionRule.expect(VerificationFailureException.class);
        exceptionRule.expectMessage("The calculated values of (p,q) do not correspond to the provided values of (p,q).");
        verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_computedQ").toURI()));
    }
}
