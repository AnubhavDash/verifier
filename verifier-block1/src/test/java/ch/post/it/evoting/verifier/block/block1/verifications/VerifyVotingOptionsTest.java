package ch.post.it.evoting.verifier.block.block1.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;

class VerifyVotingOptionsTest extends Block1VerificationAbstractTest {

	public VerifyVotingOptionsTest() {
		super(VerifyVotingOptions.class);
	}

	@Test
	void executeTestOK() throws Exception {
		VerificationResult verificationResult =
				verification.verify(Paths.get(getClass().getResource("/VerifyVotingOptionsTest/OK").toURI()));
		assertNotNull(verificationResult);
		assertEquals(Status.OK, verificationResult.getStatus());
	}

	@Test
	void executeTestNOKG() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/VerifyVotingOptionsTest/NOK-G").toURI()))
		);
		assertEquals("The primes list contains the generator g.", ex.getMessage());
	}

	@Test
	void executeTestNOKDuplicates() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/VerifyVotingOptionsTest/NOK-DUPLICATES").toURI()))
		);
		assertEquals("The primes list contains duplicates.", ex.getMessage());
	}

	@Test
	void executeTestNOKSmallest() {
		final VerificationFailureException ex = assertThrows(
				VerificationFailureException.class,
				() -> verification.verify(Paths.get(getClass().getResource("/VerifyVotingOptionsTest/NOK-SMALLEST").toURI()))
		);
		assertEquals("There is a prime number of the subgroup not present in the primes list.", ex.getMessage());
	}

}
