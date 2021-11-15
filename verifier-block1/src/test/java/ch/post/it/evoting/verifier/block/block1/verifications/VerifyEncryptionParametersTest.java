/*
 * Copyright 2021 Post CH Ltd
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

import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class VerifyEncryptionParametersTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyEncryptionParameters(pathService, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block1Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOK_P() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/NOK_P").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(IllegalArgumentException.class, () -> verification.verify(event));
		assertEquals("p must be equal to 2 * q + 1.", exception.getMessage());
	}

	@Test
	void executeTestNOK_Q() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/NOK_Q").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(IllegalArgumentException.class, () -> verification.verify(event));
		assertEquals("p must be equal to 2 * q + 1.", exception.getMessage());
	}

	@Test
	void executeTestNOK_G() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/NOK_G").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification01.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyEncryptionParametersTest/NOK").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification01.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}
}