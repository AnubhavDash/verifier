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
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class CheckSigCodesMappingTablesContextDataTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckSigCodesMappingTablesContextData(pathService, certificateLoader, applicationEventPublisherMock);
	}

	@Test
	void executeTestAllSignValid() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigCodesMappingTablesContextDataTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block1Event(this, inputDirectory));

		final VerificationResultEvent expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);

	}

	@Test
	void executeTestOneSignInvalid() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigCodesMappingTablesContextDataTest/NOK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block1Event(this, inputDirectory));

		final var verifCardSetIdPathNode = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);
		final PathNode pathNode = pathService.buildFromDynamicAncestorPath(StructureKey.CODES_MAPPING_TABLES_CONTEXT_DATA,
				verifCardSetIdPathNode.getRegexPaths().get(0));
		final VerificationResultEvent expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification77.nok.message",
						pathNode.getPath().toString()));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKCodesMappingFileNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigCodesMappingTablesContextDataTest/NOK-NOFILE").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.CODES_MAPPING_TABLES_CONTEXT_DATA);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKSignFileNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigCodesMappingTablesContextDataTest/NOK-NOFILE2").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.CODES_MAPPING_TABLES_CONTEXT_DATA);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.SIGN.toFileExtension())
				.matches(Throwables.getRootCause(exception).getMessage()));
	}
}
