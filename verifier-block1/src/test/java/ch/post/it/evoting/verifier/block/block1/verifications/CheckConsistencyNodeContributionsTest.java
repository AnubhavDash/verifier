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

import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.plugin.contract.event.ConfigurationEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

class CheckConsistencyNodeContributionsTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		final var extractionService = new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance());
		verification = new CheckConsistencyNodeContributions(pathService, extractionService, applicationEventPublisherMock,
				DomainObjectMapper.getNewInstance());
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckConsistencyNodeContributionsTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new ConfigurationEvent(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@ParameterizedTest
	@MethodSource("provideInputDirectoryPathForNOK")
	void executeTestNOK(String inputDirectoryPath) throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckConsistencyNodeContributionsTest/NOK_" + inputDirectoryPath).toURI())
				.toString();
		final VerificationResultEvent resultEvent = verification.verify(new ConfigurationEvent(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification41.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}

	private static Stream<Arguments> provideInputDirectoryPathForNOK() {
		return Stream.of(
				Arguments.of("electionEventId"),
				Arguments.of("verificationCardSetId"),
				Arguments.of("encryptionGroup"),
				Arguments.of("verificationCardIds")
		);
	}

	@Test
	void executeTestNok_unreadableNodeContributions() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckConsistencyNodeContributionsTest/NOK_unreadableNodeContributions").toURI()).toString();
		final var event = new ConfigurationEvent(this, inputDirectory);
		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		assertEquals("Failed to deserialize the node contributions chunk file.", exception.getMessage());
	}
}
