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
package ch.post.it.evoting.verifier.block.block4.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.block.test.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import ch.post.it.evoting.verifier.common.event.Block4Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class CheckSigInvalidVotesDecryptionTest extends Block4VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckSigInvalidVotesDecryption(pathService, certificateLoader, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigInvalidVotesDecryptionTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block4Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKCsvKo() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigInvalidVotesDecryptionTest/NOK/CSV-NOT-OK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new Block4Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification78.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKCertKo() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigInvalidVotesDecryptionTest/NOK/CERT-NOT-OK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new Block4Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification78.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKFileNotFoundCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigInvalidVotesDecryptionTest/NOK-NOTFILE").toURI()).toString();
		final var event = new Block4Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ADMIN_BOARD_CERT);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundRootCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigInvalidVotesDecryptionTest/NOK-NOTFILE2").toURI()).toString();
		final var event = new Block4Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.TENANT_100);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundInvalidVotesDecrypt() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigInvalidVotesDecryptionTest/NOK-NOTFILE3").toURI()).toString();
		final var event = new Block4Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.INVALID_VOTES_DECRYPT_RESULT);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundInvalidVotesDecryptMetadata() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigInvalidVotesDecryptionTest/NOK-NOTFILE4").toURI()).toString();
		final var event = new Block4Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.INVALID_VOTES_DECRYPT_RESULT);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.METADATA.toFileExtension())
				.matches(Throwables.getRootCause(exception).getMessage()));
	}
}
