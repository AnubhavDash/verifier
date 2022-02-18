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
import ch.post.it.evoting.verifier.core.internal.exceptions.JsonMissingNodeException;
import ch.post.it.evoting.verifier.core.internal.tools.RegexHelper;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.path.RelationType;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureNode;
import ch.post.it.evoting.verifier.plugin.contract.event.ConfigurationEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

import io.jsonwebtoken.SignatureException;

class CheckSigElectionInformationContentsTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckSigElectionInformationContents(pathService, certificateLoader, applicationEventPublisherMock);
	}

	@Test
	void executeTestSignValid() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new ConfigurationEvent(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestSignInvalid() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new ConfigurationEvent(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var electionInfoPathNode = pathService.buildFromRootPath(StructureKey.ELECTION_INFORMATION_CONTENTS, inputDirectoryPath);
		final var fileName = electionInfoPathNode.getRegexPaths().get(0).toString();
		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification81.nok.message", fileName));
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKFileNotFoundCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE").toURI()).toString();
		final var event = new ConfigurationEvent(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ADMIN_BOARD_CERT);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundIntermediateCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE2").toURI()).toString();
		final var event = new ConfigurationEvent(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.TENANT_100);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundRootCertificate() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE3").toURI()).toString();
		final var event = new ConfigurationEvent(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.PLATFORM_ROOT_CA);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundElectionInfo() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE4").toURI()).toString();
		final var event = new ConfigurationEvent(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(structureNode.getQualifier()));
	}

	@Test
	void executeTestNOKFileNotFoundElectionInfoSign() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOFILE5").toURI()).toString();
		final var event = new ConfigurationEvent(this, inputDirectory);

		final var exception = assertThrows(UncheckedIOException.class, () -> verification.verify(event));
		final StructureNode structureNode = pathService.getStructureNode(StructureKey.ELECTION_INFORMATION_CONTENTS);
		assertTrue(RegexHelper.regexMatcher(structureNode.getQualifier() + RelationType.SIGN.toFileExtension())
				.matches(Throwables.getRootCause(exception).getMessage()));
	}

	@Test
	void executeTestNOKMissingJWT() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-NOJWT").toURI()).toString();
		final var event = new ConfigurationEvent(this, inputDirectory);

		final var exception = assertThrows(JsonMissingNodeException.class, () -> verification.verify(event));
		assertEquals("The signature is missing from the file!", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	void executeTestNOKModifiedJWT() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigElectionInformationContentsTest/NOK-JWT").toURI()).toString();
		final var event = new ConfigurationEvent(this, inputDirectory);

		final var exception = assertThrows(SignatureException.class, () -> verification.verify(event));
		assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted" +
				".", Throwables.getRootCause(exception).getMessage());
	}

}
