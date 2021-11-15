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

class CheckSigFailedVotesTest extends Block2VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckSigFailedVotes(pathService, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigFailedVotesTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block2Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKInvalidSignature() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final Path ballotBoxPath = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath).getRegexPaths().get(0);
		final String fileName = pathService.buildFromDynamicAncestorPath(StructureKey.FAILED_VOTES, ballotBoxPath).getPath().toString();
		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification72.nok.message", fileName));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKFileNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.FAILED_VOTES);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKBallotFileNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE2").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.BALLOT_BOX);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKElectionFileNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOFILE3").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKSignCertNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final JsonMissingNodeException ex = assertThrows(JsonMissingNodeException.class, () -> verification.verify(event));
		assertEquals(String.format("%s certificate is missing!", CheckSigFailedVotes.BALLOT_BOX_CERT), ex.getMessage());
	}

	@Test
	void executeTestNOKInterCertNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT2").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final JsonMissingNodeException ex = assertThrows(JsonMissingNodeException.class, () -> verification.verify(event));
		assertEquals(String.format("%s certificate is missing!", CheckSigFailedVotes.SERVICES_CA), ex.getMessage());
	}

	@Test
	void executeTestNOKRootNotFound() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigFailedVotesTest/NOK-NOCERT3").toURI()).toString();
		final var event = new Block2Event(this, inputDirectory);

		final JsonMissingNodeException ex = assertThrows(JsonMissingNodeException.class, () -> verification.verify(event));
		assertEquals(String.format("%s certificate is missing!", CheckSigFailedVotes.ELECTION_ROOT_CA), ex.getMessage());
	}
}
