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

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class IsQuadraticResidueVOTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new IsQuadraticResidueVO(pathService, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/IsQuadraticResidueVOTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block1Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOK() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/IsQuadraticResidueVOTest/NOK/NOK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		assertEquals(Status.NOK, resultEvent.getStatus());
	}

	@Test
	void executeTestNOKFileNotFoundEncryptionParamaters() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/IsQuadraticResidueVOTest/NOK/NOK-NOFILE").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ENCRYPTION_PARAMETERS);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundDataConfig() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/IsQuadraticResidueVOTest/NOK/NOK-NOFILE2").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.DATA_CONFIG_UPDATED);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}
}
