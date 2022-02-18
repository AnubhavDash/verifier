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

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;
import ch.post.it.evoting.verifier.plugin.contract.event.ConfigurationEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

class CheckSigPrimesTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckSigPrimes(pathService, certificateLoader, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigPrimesTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new ConfigurationEvent(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKCertKo() throws URISyntaxException {
		final Path inputDirectoryPath = Paths.get(getClass().getResource("/CheckSigPrimesTest/NOK").toURI());
		final String inputDirectory = inputDirectoryPath.toString();
		final var event = new ConfigurationEvent(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var primesNode = pathService.buildFromRootPath(StructureKey.PRIMES, inputDirectoryPath);
		final var fileName = primesNode.getRegexPaths().get(0).toString();
		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification82.nok.message", fileName));
		assertEquals(expectedResultEvent, resultEvent);
	}
}
