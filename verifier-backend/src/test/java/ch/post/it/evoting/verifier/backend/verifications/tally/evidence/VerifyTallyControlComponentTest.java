/*
 * (c) Copyright 2024 Swiss Post Ltd.
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

import static ch.post.it.evoting.verifier.backend.tools.TranslationHelper.getFromResourceBundle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.internal.hashing.HashService;
import ch.post.it.evoting.cryptoprimitives.mixnet.MixnetFactory;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.FactorizeAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.votingoptions.PrimesMappingTableAlgorithms;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.DecodeWriteInsAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.IntegerToWriteInAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.IsWriteInOptionAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.writeins.QuadraticResidueToWriteInAlgorithm;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.tally.mixoffline.ProcessPlaintextsAlgorithm;
import ch.post.it.evoting.evotinglibraries.xml.XmlNormalizer;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith({ SystemStubsExtension.class })
class VerifyTallyControlComponentTest extends TallyVerificationTest {

	private static final PrimesMappingTableAlgorithms PRIMES_MAPPING_TABLE_ALGORITHMS = new PrimesMappingTableAlgorithms();
	private static final IsWriteInOptionAlgorithm IS_WRITE_IN_OPTION_ALGORITHM = new IsWriteInOptionAlgorithm();
	private static final IntegerToWriteInAlgorithm INTEGER_TO_WRITE_IN_ALGORITHM = new IntegerToWriteInAlgorithm();
	private static final QuadraticResidueToWriteInAlgorithm QUADRATIC_RESIDUE_TO_WRITE_IN_ALGORITHM = new QuadraticResidueToWriteInAlgorithm(
			INTEGER_TO_WRITE_IN_ALGORITHM);
	private static final DecodeWriteInsAlgorithm DECODE_WRITE_INS_ALGORITHM = new DecodeWriteInsAlgorithm(IS_WRITE_IN_OPTION_ALGORITHM,
			QUADRATIC_RESIDUE_TO_WRITE_IN_ALGORITHM);
	private static final ProcessPlaintextsAlgorithm PROCESS_PLAINTEXTS_ALGORITHM = new ProcessPlaintextsAlgorithm(ElGamalFactory.createElGamal(),
			new FactorizeAlgorithm(), DECODE_WRITE_INS_ALGORITHM, PRIMES_MAPPING_TABLE_ALGORITHMS);
	private static final VerifyProcessPlaintextsAlgorithm VERIFY_PROCESS_PLAINTEXTS_ALGORITHM = new VerifyProcessPlaintextsAlgorithm(
			PROCESS_PLAINTEXTS_ALGORITHM);
	private static final VerifyTallyControlComponentBallotBoxAlgorithm VERIFY_TALLY_CONTROL_COMPONENT_BALLOT_BOX_ALGORITHM = new VerifyTallyControlComponentBallotBoxAlgorithm(
			MixnetFactory.createMixnet(), ZeroKnowledgeProofFactory.createZeroKnowledgeProof(), PRIMES_MAPPING_TABLE_ALGORITHMS,
			VERIFY_PROCESS_PLAINTEXTS_ALGORITHM);
	private static final VerifyTallyFilesAlgorithm VERIFY_TALLY_FILES_ALGORITHM = new VerifyTallyFilesAlgorithm(HashService.getInstance(),
			new XmlNormalizer());

	@SystemStub
	private static EnvironmentVariables environmentVariables;

	@BeforeAll
	static void setUpAll() {
		environmentVariables.set("SECURITY_LEVEL", "STANDARD");
		final VerifyTallyControlComponentAlgorithm verifyTallyControlComponentAlgorithm = new VerifyTallyControlComponentAlgorithm(
				VERIFY_TALLY_CONTROL_COMPONENT_BALLOT_BOX_ALGORITHM, VERIFY_TALLY_FILES_ALGORITHM);

		verification = new VerifyTallyControlComponent(electionDataExtractionService, verifyTallyControlComponentAlgorithm,
				resultPublisherServiceMock);
	}

	@Test
	void verifyOk() {
		final VerificationResult result = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, result);
	}

	@Test
	void verifyTallyControlComponentBallotBoxNok() {
		final VerifyTallyControlComponentBallotBoxAlgorithm algorithmMock = mock(VerifyTallyControlComponentBallotBoxAlgorithm.class);
		when(algorithmMock.verifyTallyControlComponentBallotBox(any(), any())).thenReturn(false);
		final VerifyTallyControlComponentAlgorithm verifyTallyControlComponentAlgorithm = new VerifyTallyControlComponentAlgorithm(algorithmMock,
				VERIFY_TALLY_FILES_ALGORITHM);
		final VerifyTallyControlComponent verificationWithMock = new VerifyTallyControlComponent(electionDataExtractionService,
				verifyTallyControlComponentAlgorithm, resultPublisherServiceMock);
		final VerificationResult result = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification501.nok.message"));
		assertEquals(expectedResult, result);
	}

	@Test
	void verifyTallyFilesNok() {
		final VerifyTallyFilesAlgorithm algorithmMock = mock(VerifyTallyFilesAlgorithm.class);
		when(algorithmMock.verifyTallyFiles(any(), any())).thenReturn(false);
		final VerifyTallyControlComponentAlgorithm verifyTallyControlComponentAlgorithm = new VerifyTallyControlComponentAlgorithm(
				VERIFY_TALLY_CONTROL_COMPONENT_BALLOT_BOX_ALGORITHM, algorithmMock);
		final VerifyTallyControlComponent verificationWithMock = new VerifyTallyControlComponent(electionDataExtractionService,
				verifyTallyControlComponentAlgorithm, resultPublisherServiceMock);
		final VerificationResult result = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification501.nok.message"));
		assertEquals(expectedResult, result);
	}
}
