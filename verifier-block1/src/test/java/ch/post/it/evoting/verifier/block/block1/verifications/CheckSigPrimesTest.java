package ch.post.it.evoting.verifier.block.block1.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;

class CheckSigPrimesTest extends Block1VerificationAbstractTest {

	@BeforeEach
	void setup() {
		verification = new CheckSigPrimes();
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult = verification.verify(Paths.get(getClass().getResource("/CheckSigPrimesTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKCertKo() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/CheckSigPrimesTest/NOK").toURI()))
		);
		assertEquals("The signature verification of the file failed.", ex.getMessage());
	}
}
