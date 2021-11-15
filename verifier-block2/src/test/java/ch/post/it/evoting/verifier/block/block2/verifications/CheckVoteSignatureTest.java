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
package ch.post.it.evoting.verifier.block.block2.verifications;

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

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.block.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.Block2Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class CheckVoteSignatureTest extends Block2VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckVoteSignature(pathService, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckVoteSignatureTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block2Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKInvalidSignature() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification73.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKFileNotFoundDownloadedBallot() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOFILE").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.DOWNLOADED_BALLOT_BOX);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundElectionInfo() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOFILE2").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKInterCertNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOCERT").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final JsonMissingNodeException ex = assertThrows(JsonMissingNodeException.class, () -> verification.verify(event));
		assertEquals(String.format("%s certificate is missing!", CheckVoteSignature.CREDENTIALS_CA), ex.getMessage());
	}

	@Test
	void executeTestNOKRootNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckVoteSignatureTest/NOK-NOCERT2").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final JsonMissingNodeException ex = assertThrows(JsonMissingNodeException.class, () -> verification.verify(event));
		assertEquals(String.format("%s certificate is missing!", CheckVoteSignature.ELECTION_ROOT_CA), ex.getMessage());
	}

}
