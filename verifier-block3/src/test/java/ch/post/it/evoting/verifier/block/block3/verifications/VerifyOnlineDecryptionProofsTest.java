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
package ch.post.it.evoting.verifier.block.block3.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofService;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.core.internal.exceptions.MissingFileException;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.plugin.contract.event.PreDecryptionEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

class VerifyOnlineDecryptionProofsTest extends Block3VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyOnlineDecryptionProofs(pathService, applicationEventPublisherMock, new ZeroKnowledgeProofService(),
				new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance()));
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyOnlineDecryptionProofsTest/OK").toURI()).toString();
		final var event = new PreDecryptionEvent(this, inputDirectory);

		final VerificationResultEvent resultEvent = verification.verify(event);
		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyOnlineDecryptionProofsTest/NOK").toURI()).toString();
		final var event = new PreDecryptionEvent(this, inputDirectory);

		final VerificationResultEvent resultEvent = verification.verify(event);
		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification02.failure"));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKFileNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyOnlineDecryptionProofsTest/NOK_missingFiles").toURI()).toString();
		final var event = new PreDecryptionEvent(this, inputDirectory);

		final var exception = assertThrows(MissingFileException.class, () -> verification.verify(event));
		assertEquals("Missing shufflePayload file(s)", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	void executeTestNOKCorruptedFile() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyOnlineDecryptionProofsTest/NOK_corruptedFile").toURI()).toString();
		final var event = new PreDecryptionEvent(this, inputDirectory);

		assertThrows(UncheckedIOException.class, () -> verification.verify(event));
	}
}
