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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence;

import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.mixnet.MixnetFactory;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifyTallyControlComponentTest extends TallyVerificationTest {

	@BeforeAll
	static void setUpAll() {
		final VerifyTallyControlComponentBallotBoxAlgorithm verifyTallyControlComponentBallotBoxAlgorithm = new VerifyTallyControlComponentBallotBoxAlgorithm(
				MixnetFactory.createMixnet(), ZeroKnowledgeProofFactory.createZeroKnowledgeProof(), ElGamalFactory.createElGamal());
		verification = new VerifyTallyControlComponent(new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance()),
				verifyTallyControlComponentBallotBoxAlgorithm, applicationEventPublisherMock);
	}

	@Test
	void verifyOk() {
		final VerificationResult result = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, result);
	}

	@Test
	void verifyNok() {
		final VerifyTallyControlComponentBallotBoxAlgorithm algorithmMock = mock(VerifyTallyControlComponentBallotBoxAlgorithm.class);
		when(algorithmMock.verifyTallyControlComponentBallotBox(any(), any())).thenReturn(false);
		final VerifyTallyControlComponent verificationWithMock = new VerifyTallyControlComponent(
				new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance()),
				algorithmMock, applicationEventPublisherMock);
		final VerificationResult result = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification501.nok.message"));
		assertEquals(expectedResult, result);
	}
}
