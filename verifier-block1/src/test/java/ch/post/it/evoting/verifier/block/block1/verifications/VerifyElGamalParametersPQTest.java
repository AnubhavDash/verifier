package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.test.annotation.Slow;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class VerifyElGamalParametersPQTest extends Block1VerificationAbstractTest {

    @BeforeEach
    void setup() {
        verification = new VerifyElGamalParametersPQ();
    }

    public void executeTestOK() throws Exception {
        final VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/OK").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }
    @Slow
    void executeTestOKIntensive() throws Exception {
        final VerificationResult verificationResult =
                verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/OKI").toURI()));
        assertNotNull(verificationResult);
        assertEquals(Status.OK, verificationResult.getStatus());
    }

    @Test
    void executeTestNOK_L() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_L").toURI()))
        );
        assertEquals("p bit length is not equal to " + VerifyElGamalParametersPQ.EXPECTED_P_BIT_LEN + " bits.", ex.getMessage());
    }

    @Test
    void executeTestNOK_N() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_N").toURI()))
        );
        assertEquals("q bit length is not equal to " + VerifyElGamalParametersPQ.EXPECTED_Q_BIT_LEN + " bits.", ex.getMessage());
    }

    @Test
    void executeTestNOK_CounterValue() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_CounterValue").toURI()))
        );
        assertEquals("The q counter is too large.", ex.getMessage());
    }

    @Test
    void executeTestNOK_SeedLength() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_SeedLength").toURI()))
        );
        assertEquals("The seed has not the required size (" + VerifyElGamalParametersPQ.MIN_SEED_BIT_LEN + " bits).", ex.getMessage());
    }

    @Test
    void executeTestNOK_qCounter() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_qCounter").toURI()))
        );
        assertEquals("A smaller counter value giving another pair of valid primes was found.", ex.getMessage());
    }

    @Test
    void executeTestNOK_computedP() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_computedP").toURI()))
        );
        assertEquals("The calculated values of (p,q) do not correspond to the provided values of (p,q).", ex.getMessage());
    }

    @Test
    void executeTestNOK_computedQ() {
        final VerificationFailureException ex = assertThrows(
                VerificationFailureException.class,
                () -> verification.verify(Paths.get(getClass().getResource("/VerifyElGamalParameterPQTest/NOK_computedQ").toURI()))
        );
        assertEquals("The calculated values of (p,q) do not correspond to the provided values of (p,q).", ex.getMessage());
    }
}
