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
import ch.post.it.evoting.verifier.common.block.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureNode;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

import io.jsonwebtoken.SignatureException;

class CheckSigBallotBoxConfigurationTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckSigBallotBoxConfiguration(pathService, certificateLoader, applicationEventPublisherMock);
	}

	@Test
	void executeTestSignValid() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block1Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestSignInvalid() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new Block1Event(this, inputDirectory);

		final VerificationResultEvent resultEvent = verification.verify(event);
		final PathNode ballotIdsPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		final String fileName = pathService.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX, ballotIdsPathNode.getRegexPaths().get(0)).getPath()
				.toString();
		final VerificationResultEvent expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(
						Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification80.nok.message", fileName));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKFileNotFoundCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ADMIN_BOARD_CERT);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundIntermediateCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE2").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.TENANT_100);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundRootCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE3").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.PLATFORM_ROOT_CA);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundBallotBox() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE4").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.BALLOT_BOX);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundBallotBoxSign() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOFILE5").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.BALLOT_BOX);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKMissingJWT() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-NOJWT").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(JsonMissingNodeException.class, () -> verification.verify(event));
		assertEquals("The signature is missing from the file!", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	void executeTestNOKModifiedJWT() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigBallotBoxConfigurationTest/NOK-JWT").toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);

		final var exception = assertThrows(SignatureException.class, () -> verification.verify(event));
		assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted" +
				".", Throwables.getRootCause(exception).getMessage());
	}

}
