package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.base.Throwables;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.HeaderType;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyTotalVotersConsistencyTest extends SetupVerificationTest {

	private static ElectionDataExtractionService extractionService;

	@BeforeAll
	static void setupAll() {
		extractionService = new ElectionDataExtractionService(pathService, objectMapper);
		verification = new VerifyTotalVotersConsistency(applicationEventPublisherMock, extractionService);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokBadVoterTotal() {
		final Configuration configuration = extractionService.getConfiguration(datasetPath);
		final Configuration configurationMock = spy(configuration);
		final HeaderType headerType = new HeaderType();
		headerType.setVoterTotal(BigInteger.valueOf(39));
		when(configurationMock.getHeader()).thenReturn(headerType);
		final ElectionDataExtractionService extractionServiceMock = spy(extractionService);
		doReturn(configurationMock).when(extractionServiceMock).getConfiguration(datasetPath);

		final VerifyTotalVotersConsistency verificationBadVoterTotal = new VerifyTotalVotersConsistency(applicationEventPublisherMock,
				extractionServiceMock);

		final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> verificationBadVoterTotal.verify(datasetPath));
		assertEquals("The voter total in the header must be the same as the size of the voter list. [voterTotal: 39, voterCount: 40]",
				Throwables.getRootCause(exception).getMessage());
	}

	@Test
	void verifyNok() {
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(datasetPath);
		final ElectionEventContextPayload electionEventContextPayloadMock = spy(electionEventContextPayload);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();
		final ElectionEventContext electionEventContextMock = spy(electionEventContext);
		doReturn(List.of()).when(electionEventContextMock).verificationCardSetContexts();
		doReturn(electionEventContextMock).when(electionEventContextPayloadMock).getElectionEventContext();

		final ElectionDataExtractionService extractionServiceMock = spy(extractionService);
		doReturn(electionEventContextPayloadMock).when(extractionServiceMock).getElectionEventContextPayload(datasetPath);

		final VerifyTotalVotersConsistency verificationNok = new VerifyTotalVotersConsistency(applicationEventPublisherMock,
				extractionServiceMock);
		final VerificationResult verificationResult = verificationNok.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationResult.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification312.nok.message"));

		assertEquals(expectedResult, verificationResult);
	}
}