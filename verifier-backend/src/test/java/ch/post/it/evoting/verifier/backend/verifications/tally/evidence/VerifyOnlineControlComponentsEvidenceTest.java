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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.mixnet.MixnetFactory;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsAlgorithm;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsAlgorithm;

@DisplayName("VerifyOnlineControlComponentsEvidence with")
class VerifyOnlineControlComponentsEvidenceTest extends TallyVerificationTest {

	private static VerifyOnlineControlComponentsVerification verifyOnlineControlComponentsVerification;

	@BeforeAll
	static void setUpAll() {
		final ElGamal elGamal = ElGamalFactory.createElGamal();
		final ZeroKnowledgeProof zeroKnowledgeProof = ZeroKnowledgeProofFactory.createZeroKnowledgeProof();

		final VerifyMixDecOfflineAlgorithm verifyMixDecOfflineAlgorithm = new VerifyMixDecOfflineAlgorithm(elGamal, MixnetFactory.createMixnet(),
				zeroKnowledgeProof);
		final VerifyVotingClientProofsAlgorithm verifyVotingClientProofsAlgorithm = new VerifyVotingClientProofsAlgorithm(zeroKnowledgeProof);
		final GetMixnetInitialCiphertextsAlgorithm getMixnetInitialCiphertextsAlgorithm = new GetMixnetInitialCiphertextsAlgorithm(elGamal);

		final VerifyOnlineControlComponentsBallotBoxAlgorithm verifyOnlineControlComponentsBallotBoxAlgorithm = new VerifyOnlineControlComponentsBallotBoxAlgorithm(
				verifyMixDecOfflineAlgorithm, verifyVotingClientProofsAlgorithm, getMixnetInitialCiphertextsAlgorithm);
		final ElectionDataExtractionService electionDataExtractionService = new ElectionDataExtractionService(pathService, objectMapper);

		verifyOnlineControlComponentsVerification = spy(
				new VerifyOnlineControlComponentsVerification(verifyOnlineControlComponentsBallotBoxAlgorithm));

		verification = new VerifyOnlineControlComponentsEvidence(applicationEventPublisherMock, electionDataExtractionService,
				verifyOnlineControlComponentsVerification);
	}

	@AfterEach
	void setUp() {
		reset(verifyOnlineControlComponentsVerification);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("algorithm returning false is failed")
	void algorithmReturningFalse() {
		doReturn(false).when(verifyOnlineControlComponentsVerification)
				.verifyOnlineControlComponents(any(), anyList(), anyMap(), anyMap(), anyMap(), anyMap(), any());

		final VerificationResult result = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification500.nok.message"));
		assertEquals(expectedResult, result);
	}

}
