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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.block.test.helper.RegexHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class CheckSigElectionImportTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckSigElectionImport(pathService, certificateLoader, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionImportTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block1Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKCertKo() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/CERT-NOT-OK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var apElectionImportNode = pathService.buildFromRootPath(StructureKey.AP_ELECTION_IMPORT, inputDirectoryPath);
		final var fileName = apElectionImportNode.getRegexPaths().get(0).toString();
		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification75.nok.message", fileName));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKJsonKo() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/JSON-NOT-OK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var apElectionImportNode = pathService.buildFromRootPath(StructureKey.AP_ELECTION_IMPORT, inputDirectoryPath);
		final var fileName = apElectionImportNode.getRegexPaths().get(0).toString();
		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification75.nok.message", fileName));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKFileNotFoundRootCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/NOK-NOFILE").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.INTEGRATION_CA);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundApElectionImport() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/NOK-NOFILE2").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.AP_ELECTION_IMPORT);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundApElectionImportP7() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionImportTest/NOK/NOK-NOFILE3").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.AP_ELECTION_IMPORT);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.P7.toFileExtension())
				.matches(Throwables.getRootCause(exception).getMessage()));

	}
}
