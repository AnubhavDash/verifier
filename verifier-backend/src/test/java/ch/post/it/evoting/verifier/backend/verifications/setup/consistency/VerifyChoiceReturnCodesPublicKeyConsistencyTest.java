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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyChoiceReturnCodesPublicKeyConsistencyTest extends SetupVerificationTest {

	private static final ElGamal EL_GAMAL = ElGamalFactory.createElGamal();
	@BeforeAll
	static void setupAll() {
		verification = new VerifyChoiceReturnCodesPublicKeyConsistency(EL_GAMAL, electionDataExtractionService, applicationEventPublisherMock);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent pk_CCR fails.")
	void inconsistentChoiceReturnCodesPublicKey() {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(datasetPath);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();

		final ElectionEventContext modifiedElectionEventContext = spy(electionEventContext);
		doReturn(electionEventContext.electionPublicKey()).when(modifiedElectionEventContext).choiceReturnCodesEncryptionPublicKey();
		final ElectionEventContextPayload modifiedElectionEventContextPayload = new ElectionEventContextPayload(
				electionEventContextPayload.getEncryptionGroup(), modifiedElectionEventContext);

		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		doReturn(modifiedElectionEventContextPayload).when(extractionServiceMock).getElectionEventContextPayload(datasetPath);

		final VerifyChoiceReturnCodesPublicKeyConsistency verificationWithMock = new VerifyChoiceReturnCodesPublicKeyConsistency(EL_GAMAL,
				extractionServiceMock, applicationEventPublisherMock);

		final VerificationResult result = verificationWithMock.verify(datasetPath);
		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification303.nok.message"));
		assertEquals(expectedResult, result);
	}
}
