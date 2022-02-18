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
package ch.post.it.evoting.verifier.block.block3.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.hashing.HashService;
import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.core.internal.tools.CertificateLoader;
import ch.post.it.evoting.verifier.core.internal.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.core.internal.tools.SignatureService;
import ch.post.it.evoting.verifier.core.internal.tools.TranslationHelper;
import ch.post.it.evoting.verifier.plugin.contract.event.PreDecryptionEvent;
import ch.post.it.evoting.verifier.plugin.contract.event.VerificationResultEvent;

class CheckSigOnlineProofsTest extends Block3VerificationTest {

	@BeforeAll
	static void setUpAll() {
		final ElectionDataExtractionService extractionService = new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance());
		final CertificateLoader certificateLoader = new CertificateLoader(pathService);
		verification = new CheckSigOnlineProofs(extractionService, HashService.getInstance(), new SignatureService(), certificateLoader, pathService,
				applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigOnlineProofsTest/OK").toURI()).toString();
		final var event = new PreDecryptionEvent(this, inputDirectory);

		final VerificationResultEvent resultEvent = verification.verify(event);
		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigOnlineProofsTest/NOK").toURI()).toString();
		final var event = new PreDecryptionEvent(this, inputDirectory);

		final VerificationResultEvent resultEvent = verification.verify(event);
		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification61.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}

}
