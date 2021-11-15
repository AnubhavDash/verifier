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

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.hashing.HashService;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.block.tools.SignatureService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.event.Block4Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class CheckSigOfflineProofsTest extends Block4VerificationTest {

	@BeforeAll
	static void setUpAll() {
		final var extractionService = new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance());
		final var signatureService = new SignatureService();
		final var hashService = HashService.getInstance();
		verification = new CheckSigOfflineProofs(pathService, extractionService, hashService, signatureService, certificateLoader,
				applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigOfflineProofsTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block4Event(this, inputDirectory));
		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigOfflineProofsTest/NOK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block4Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification61.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}

}
