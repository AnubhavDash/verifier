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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class CheckNumberCredentialsTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckNumberCredentials(pathService, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckNumberCredentialsTest/OK").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final VerificationResultEvent resultEvent = verification.verify(event);
		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOK() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckNumberCredentialsTest/NOK/NOK").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification31.nok.message", "99", "100"));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKFileNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckNumberCredentialsTest/NOK/NOK-NOFILE").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final String fileQualifier = pathService.getStructureNode(StructureKey.CONFIG_ANONYMIZED).getQualifier();
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(fileQualifier));
	}

	@Test
	void executeTestNOKFileNotFound2() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckNumberCredentialsTest/NOK/NOK-NOFILE2").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final String fileQualifier = pathService.getStructureNode(StructureKey.CREDENTIAL_DATA).getQualifier();
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(fileQualifier));
	}
}
