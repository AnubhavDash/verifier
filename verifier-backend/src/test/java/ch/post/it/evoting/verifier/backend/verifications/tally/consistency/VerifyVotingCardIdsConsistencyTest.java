package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

@DisplayName("VerifyVotingCardSetIdsConsistency with")
class VerifyVotingCardIdsConsistencyTest extends TallyVerificationTest {

	private static ElectionDataExtractionService electionDataExtractionService;

	@BeforeAll
	static void setupAll() {
		electionDataExtractionService = new ElectionDataExtractionService(pathService, objectMapper);
		verification = new VerifyVotingCardIdsConsistency(applicationEventPublisherMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}
}