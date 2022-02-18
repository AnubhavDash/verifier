/*
 * Copyright 2022 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import ch.post.it.evoting.verifier.plugin.contract.Status;
import ch.post.it.evoting.verifier.core.internal.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.plugin.contract.event.ConfigurationEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

class VerifyEncryptedCKExponentiationProofsTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		final ElectionDataExtractionService dataExtractionService = new ElectionDataExtractionService(pathService,
				DomainObjectMapper.getNewInstance());

		verification = new VerifyEncryptedCKExponentiationProofs(new ExponentiationProofsVerificationService<>(dataExtractionService, pathService),
				new ZeroKnowledgeProofService(), applicationEventPublisherMock);
	}

	@Test
	void verifyChunking() throws Exception {
		try (MockedStatic<SecurityLevelConfig> securityLevelConfigMockedStatic = Mockito.mockStatic(SecurityLevelConfig.class)) {
			securityLevelConfigMockedStatic.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevel.DEFAULT);
			final var inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptedCKExponentiationProofs/chunking-ok/").toURI()).toString();
			final var event = new ConfigurationEvent(this, inputDirectory);

			final VerificationResultEvent resultEvent = verification.verify(event);

			assertEquals(Status.OK, resultEvent.getStatus());
		}
	}

	@Test
	void verifyOneVerificationCard() throws Exception {
		try (MockedStatic<SecurityLevelConfig> securityLevelConfigMockedStatic = Mockito.mockStatic(SecurityLevelConfig.class)) {
			securityLevelConfigMockedStatic.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevel.DEFAULT);
			final var inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptedCKExponentiationProofs/ok/").toURI()).toString();
			final var event = new ConfigurationEvent(this, inputDirectory);

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
			final var event = new ConfigurationEvent(this, inputDirectory);

			final VerificationResultEvent resultEvent = verification.verify(event);

			assertEquals(Status.NOK, resultEvent.getStatus());
		}
	}

	@Test
	void noVerificationCard() throws Exception {
		try (MockedStatic<SecurityLevelConfig> securityLevelConfigMockedStatic = Mockito.mockStatic(SecurityLevelConfig.class)) {
			securityLevelConfigMockedStatic.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevel.DEFAULT);
			final var inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptedCKExponentiationProofs/empty/").toURI()).toString();
			final var event = new ConfigurationEvent(this, inputDirectory);

			assertThrows(VerificationPreconditionException.class, () -> verification.verify(event));
		}
	}

	@RepeatedTest(4)
	void verify10VVerificationCardsEachFourVerificationCardSets() throws Exception {
		try (MockedStatic<SecurityLevelConfig> securityLevelConfigMockedStatic = Mockito.mockStatic(SecurityLevelConfig.class)) {
			securityLevelConfigMockedStatic.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevel.DEFAULT);
			final var inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptedCKExponentiationProofs/full/").toURI()).toString();
			final var event = new ConfigurationEvent(this, inputDirectory);

			final VerificationResultEvent resultEvent = verification.verify(event);

			assertEquals(Status.OK, resultEvent.getStatus());
		}
	}
}
