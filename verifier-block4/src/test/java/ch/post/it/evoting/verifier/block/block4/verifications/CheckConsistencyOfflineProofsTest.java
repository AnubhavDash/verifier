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
package ch.post.it.evoting.verifier.block.block4.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.plugin.contract.event.FinalDecryptionEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

class CheckConsistencyOfflineProofsTest extends Block4VerificationTest {

	private static ElectionDataExtractionService extractionService;
	private static ObjectMapper objectMapper;

	@BeforeAll
	static void setupAll() {
		extractionService = new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance());
		objectMapper = new ObjectMapper();
		verification = new CheckConsistencyOfflineProofs(pathService, extractionService, objectMapper, applicationEventPublisherMock);
	}

	@Test
	void testVerifyOk() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckConsistencyOfflineProofsTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new FinalDecryptionEvent(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	private static Stream<Arguments> getBadInputTests() {
		return Stream.of(
				Arguments.of("/CheckConsistencyOfflineProofsTest/NOK_EncryptionGroup1", "verification41.nok.encryption.group.message"),
				Arguments.of("/CheckConsistencyOfflineProofsTest/NOK_EncryptionGroup2", "verification41.nok.encryption.group.message"),
				Arguments.of("/CheckConsistencyOfflineProofsTest/NOK_PublicKey1", "verification41.nok.public.keys.message"),
				Arguments.of("/CheckConsistencyOfflineProofsTest/NOK_PublicKey2", "verification41.nok.public.keys.message")
		);
	}

	@ParameterizedTest
	@MethodSource("getBadInputTests")
	void testVerifyNok(final String inputDirectory, final String messageIdentifier) throws URISyntaxException {
		final var event = new FinalDecryptionEvent(this, Paths.get(getClass().getResource(inputDirectory).toURI()).toString());
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(
						Block4VerificationSuite.RESOURCE_BUNDLE_NAME, messageIdentifier));
		assertEquals(expectedResultEvent, resultEvent);
	}
}
