/*
 * (c) Copyright 2025 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.hashing.Hash;
import ch.post.it.evoting.cryptoprimitives.hashing.HashFactory;
import ch.post.it.evoting.cryptoprimitives.math.Base64;
import ch.post.it.evoting.cryptoprimitives.math.BaseEncodingFactory;
import ch.post.it.evoting.cryptoprimitives.mixnet.MixnetFactory;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProof;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.GetHashContextAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.PrimesMappingTableAlgorithms;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyMixDecOfflineAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.VerifyVotingClientProofsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixonline.GetMixnetInitialCiphertextsAlgorithm;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith({ SystemStubsExtension.class })
@DisplayName("VerifyOnlineControlComponentsEvidence with")
class VerifyOnlineControlComponentsTest extends TallyVerificationTest {

	private static VerifyOnlineControlComponentsAlgorithm verifyOnlineControlComponentsAlgorithm;

	@SystemStub
	private static EnvironmentVariables environmentVariables;

	@BeforeAll
	static void setUpAll() {
		environmentVariables.set("SECURITY_LEVEL", "STANDARD");

		final Hash hash = HashFactory.createHash();
		final Base64 base64 = BaseEncodingFactory.createBase64();
		final ElGamal elGamal = ElGamalFactory.createElGamal();
		final ZeroKnowledgeProof zeroKnowledgeProof = ZeroKnowledgeProofFactory.createZeroKnowledgeProof();
		final PrimesMappingTableAlgorithms primesMappingTableAlgorithms = new PrimesMappingTableAlgorithms();
		final GetHashContextAlgorithm getHashContextAlgorithm = new GetHashContextAlgorithm(base64, hash, primesMappingTableAlgorithms);

		final VerifyMixDecOfflineAlgorithm verifyMixDecOfflineAlgorithm = new VerifyMixDecOfflineAlgorithm(elGamal, MixnetFactory.createMixnet(),
				zeroKnowledgeProof);
		final VerifyVotingClientProofsAlgorithm verifyVotingClientProofsAlgorithm = new VerifyVotingClientProofsAlgorithm(zeroKnowledgeProof,
				getHashContextAlgorithm, primesMappingTableAlgorithms);
		final GetMixnetInitialCiphertextsAlgorithm getMixnetInitialCiphertextsAlgorithm = new GetMixnetInitialCiphertextsAlgorithm(hash, base64,
				elGamal);

		final VerifyOnlineControlComponentsBallotBoxAlgorithm verifyOnlineControlComponentsBallotBoxAlgorithm = new VerifyOnlineControlComponentsBallotBoxAlgorithm(
				verifyMixDecOfflineAlgorithm, verifyVotingClientProofsAlgorithm, getMixnetInitialCiphertextsAlgorithm, primesMappingTableAlgorithms);

		verifyOnlineControlComponentsAlgorithm = spy(
				new VerifyOnlineControlComponentsAlgorithm(verifyOnlineControlComponentsBallotBoxAlgorithm));

		verification = new VerifyOnlineControlComponents(resultPublisherServiceMock, electionDataExtractionService,
				verifyOnlineControlComponentsAlgorithm);
	}

	@BeforeEach
	void setUp() {
		reset(verifyOnlineControlComponentsAlgorithm);
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
		doReturn(false).when(verifyOnlineControlComponentsAlgorithm).verifyOnlineControlComponents(any(), any());

		final VerificationResult result = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification1001.nok.message"));
		assertEquals(expectedResult, result);
	}

}
