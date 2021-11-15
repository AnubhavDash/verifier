package ch.post.it.evoting.verifier.block.block1.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import ch.post.it.evoting.cryptoprimitives.SecurityLevel;
import ch.post.it.evoting.cryptoprimitives.SecurityLevelConfig;
import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofService;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class VerifyEncryptedCKExponentiationProofsTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		final ElectionDataExtractionService dataExtractionService = new ElectionDataExtractionService(pathService,
				DomainObjectMapper.getNewInstance());

		verification = new VerifyEncryptedCKExponentiationProofs(new ExponentiationProofsVerificationService<>(dataExtractionService, pathService),
				new ZeroKnowledgeProofService(), applicationEventPublisherMock);
	}

	@Test
	void verifyOneVerificationCard() throws Exception {
		try (MockedStatic<SecurityLevelConfig> securityLevelConfigMockedStatic = Mockito.mockStatic(SecurityLevelConfig.class)) {
			securityLevelConfigMockedStatic.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevel.DEFAULT);
			final var inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptedCKExponentiationProofs/ok/").toURI()).toString();
			final var event = new Block1Event(this, inputDirectory);

			final VerificationResultEvent resultEvent = verification.verify(event);

			assertEquals(Status.OK, resultEvent.getStatus());
		}
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"/VerifyEncryptedCKExponentiationProofs/wrong-proof/",
			"/VerifyEncryptedCKExponentiationProofs/wrong-public-key/",
			"/VerifyEncryptedCKExponentiationProofs/wrong-cipher-text/"
	})
	void invalidScenarioTests(String url) throws Exception {
		try (MockedStatic<SecurityLevelConfig> securityLevelConfigMockedStatic = Mockito.mockStatic(SecurityLevelConfig.class)) {
			securityLevelConfigMockedStatic.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevel.DEFAULT);
			final var inputDirectory = Paths.get(getClass().getResource(url).toURI()).toString();
			final var event = new Block1Event(this, inputDirectory);

			final VerificationResultEvent resultEvent = verification.verify(event);

			assertEquals(Status.NOK, resultEvent.getStatus());
		}
	}

	@Test
	void noVerificationCard() throws Exception {
		try (MockedStatic<SecurityLevelConfig> securityLevelConfigMockedStatic = Mockito.mockStatic(SecurityLevelConfig.class)) {
			securityLevelConfigMockedStatic.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevel.DEFAULT);
			final var inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptedCKExponentiationProofs/empty/").toURI()).toString();
			final var event = new Block1Event(this, inputDirectory);

			assertThrows(VerificationPreconditionException.class, () -> verification.verify(event));
		}
	}

	@RepeatedTest(4)
	void verify10VVerificationCardsEachFourVerificationCardSets() throws Exception {
		try (MockedStatic<SecurityLevelConfig> securityLevelConfigMockedStatic = Mockito.mockStatic(SecurityLevelConfig.class)) {
			securityLevelConfigMockedStatic.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevel.DEFAULT);
			final var inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptedCKExponentiationProofs/full/").toURI()).toString();
			final var event = new Block1Event(this, inputDirectory);

			final VerificationResultEvent resultEvent = verification.verify(event);

			assertEquals(Status.OK, resultEvent.getStatus());
		}
	}
}