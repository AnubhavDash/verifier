package ch.post.it.evoting.verifier.backend.verifications.tally.consistency.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifyCiphertextsConsistencyTest extends TallyVerificationTest {

	private static ElectionDataExtractionService extractionService;

	@BeforeAll
	static void setupAll() {
		extractionService = new ElectionDataExtractionService(pathService, objectMapper);
		verification = new VerifyCiphertextsConsistency(applicationEventPublisherMock, extractionService);
	}

	@Test
	void testVerifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void testVerifyNokBallotBoxCiphertexts() {
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(datasetPath);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();
		final List<VerificationCardSetContext> vcsContexts = electionEventContext.verificationCardSetContexts();
		final VerificationCardSetContext firstContext = vcsContexts.get(0);
		final int numberOfWriteInsPlusOne = firstContext.numberOfWriteInFields() + 1;
		final VerificationCardSetContext modifiedFirstContext = new VerificationCardSetContext(firstContext.verificationCardSetId(),
				firstContext.ballotBoxId(), firstContext.testBallotBox(), numberOfWriteInsPlusOne, firstContext.numberOfVotingCards(),
				firstContext.gracePeriod());
		final List<VerificationCardSetContext> modifiedVcsContexts = Streams.concat(Stream.of(modifiedFirstContext), vcsContexts.stream().skip(1))
				.toList();
		final ElectionEventContext modifiedElectionEventContext = spy(electionEventContext);
		doReturn(modifiedVcsContexts).when(modifiedElectionEventContext).verificationCardSetContexts();
		final ElectionEventContextPayload modifiedElectionEventContextPayload = new ElectionEventContextPayload(
				electionEventContextPayload.getEncryptionGroup(), modifiedElectionEventContext);
		final ElectionDataExtractionService extractionServiceMock = spy(extractionService);
		doReturn(modifiedElectionEventContextPayload).when(extractionServiceMock).getElectionEventContextPayload(datasetPath);

		final VerifyCiphertextsConsistency verificationWithMock = new VerifyCiphertextsConsistency(applicationEventPublisherMock,
				extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification42.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}