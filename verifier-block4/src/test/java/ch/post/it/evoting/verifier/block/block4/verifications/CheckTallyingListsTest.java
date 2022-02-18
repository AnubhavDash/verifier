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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.verifier.plugin.contract.Status;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureNode;
import ch.post.it.evoting.verifier.plugin.contract.event.FinalDecryptionEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

class CheckTallyingListsTest extends Block4VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckTallyingLists(pathService, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckTallyingListsTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new FinalDecryptionEvent(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOK() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new FinalDecryptionEvent(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		assertEquals(Status.NOK, resultEvent.getStatus());
	}

	@Test
	void executeTestNOKVoteCountCountingCircle() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-VOTECOUNT-COUNTINGCIRCLE").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new FinalDecryptionEvent(this, inputDirectory);

		final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> verification.verify(event));
		assertTrue(ex.getMessage().contains("cannot find the decrypt data for given countingCircle"));
	}

	@Test
	void executeTestNOKVoteCountElection() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-VOTECOUNT-ELECTION").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new FinalDecryptionEvent(this, inputDirectory);

		final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> verification.verify(event));
		assertEquals("cannot find the decrypt data for given election : WRONG-IDENTIFIER", ex.getMessage());
	}

	@Test
	void executeTestNOKFileNotFoundConfiguration() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-CONFIG").toURI()).toString();
		final var event = new FinalDecryptionEvent(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.CONFIG_ANONYMIZED);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundEVoting() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-EVOTING").toURI()).toString();
		final var event = new FinalDecryptionEvent(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.EVOTING_DECRYPT_RESULT);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundECH0110() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckTallyingListsTest/NOK-NOFILE-eCH").toURI()).toString();
		final var event = new FinalDecryptionEvent(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ECH0110);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}
}
