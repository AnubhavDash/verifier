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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientPublicKey;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyElectionPublicKeyConsistencyTest extends SetupVerificationTest {

	private final static ElGamal EL_GAMAL = ElGamalFactory.createElGamal();

	private static ElectionDataExtractionService extractionService = new ElectionDataExtractionService(pathService, objectMapper);

	@BeforeAll
	static void setupAll() {
		extractionService = new ElectionDataExtractionService(pathService, objectMapper);
		verification = new VerifyElectionPublicKeyConsistency(EL_GAMAL, extractionService, applicationEventPublisherMock);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent EL_pk fails.")
	void inconsistentElectionEventPublicKey() {
		final ElectionEventContextPayload electionEventContextPayload = extractionService.getElectionEventContextPayload(datasetPath);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();

		final List<GqElement> modifiedKeyElements = electionEventContext.choiceReturnCodesEncryptionPublicKey()
				.getKeyElements()
				.subList(0, electionEventContext.electoralBoardPublicKey().size());
		final ElectionEventContext modifiedElectionEventContext = spy(electionEventContext);
		doReturn(new ElGamalMultiRecipientPublicKey(modifiedKeyElements)).when(modifiedElectionEventContext).electoralBoardPublicKey();
		final ElectionEventContextPayload modifiedElectionEventContextPayload = new ElectionEventContextPayload(
				electionEventContextPayload.getEncryptionGroup(), modifiedElectionEventContext);

		final ElectionDataExtractionService extractionServiceMock = spy(extractionService);
		doReturn(modifiedElectionEventContextPayload).when(extractionServiceMock).getElectionEventContextPayload(datasetPath);

		final VerifyElectionPublicKeyConsistency verificationWithMock = new VerifyElectionPublicKeyConsistency(EL_GAMAL, extractionServiceMock,
				applicationEventPublisherMock);

		final VerificationResult result = verificationWithMock.verify(datasetPath);
		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification34.nok.message"));
		assertEquals(expectedResult, result);
	}
}