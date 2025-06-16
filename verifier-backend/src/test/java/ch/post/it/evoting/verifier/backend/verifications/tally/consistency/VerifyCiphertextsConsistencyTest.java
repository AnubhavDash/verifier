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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.electoralmodel.PrimesMappingTableAlgorithms;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifyCiphertextsConsistencyTest extends TallyVerificationTest {

	private static PrimesMappingTableAlgorithms primesMappingTableAlgorithms;

	@BeforeAll
	static void setupAll() {
		primesMappingTableAlgorithms = spy(new PrimesMappingTableAlgorithms());
		verification = new VerifyCiphertextsConsistency(resultPublisherServiceMock, electionDataExtractionService, primesMappingTableAlgorithms);
	}

	@Test
	void testVerifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void testVerifyNokBallotBoxCiphertexts() {
		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(datasetPath);
		final PrimesMappingTable primesMappingTable = electionEventContext.verificationCardSetContexts().get(0).getPrimesMappingTable();
		final int numberOfWriteInsPlusOne = primesMappingTableAlgorithms.getDelta(primesMappingTable);
		doReturn(numberOfWriteInsPlusOne + 1).when(primesMappingTableAlgorithms).getDelta(any());

		final VerifyCiphertextsConsistency verificationWithMock = new VerifyCiphertextsConsistency(resultPublisherServiceMock,
				electionDataExtractionService, primesMappingTableAlgorithms);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification809.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
