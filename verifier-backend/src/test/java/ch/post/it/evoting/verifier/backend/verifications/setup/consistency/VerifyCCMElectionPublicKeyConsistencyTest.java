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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.election.ControlComponentPublicKeys;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyCCMElectionPublicKeyConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyCCMElectionPublicKeyConsistency(electionDataExtractionService, resultPublisherServiceMock);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent EL_pk_j fails.")
	void inconsistentCcmElectionPublicKeys() {
		final ImmutableList<ControlComponentPublicKeysPayload> controlComponentPublicKeysPayloads = electionDataExtractionService.getControlComponentPublicKeysPayloads(
				datasetPath).collect(toImmutableList());
		final ControlComponentPublicKeysPayload controlComponentPublicKeysPayload3 = controlComponentPublicKeysPayloads.get(3);
		final ControlComponentPublicKeys controlComponentPublicKeys = controlComponentPublicKeysPayload3.getControlComponentPublicKeys();

		final ControlComponentPublicKeys modifiedControlComponentPublicKeys = spy(controlComponentPublicKeys);
		doReturn(controlComponentPublicKeys.ccrjChoiceReturnCodesEncryptionPublicKey())
				.when(modifiedControlComponentPublicKeys).ccmjElectionPublicKey();
		final ControlComponentPublicKeysPayload modifiedControlComponentPublicKeysPayload3 = new ControlComponentPublicKeysPayload(
				controlComponentPublicKeysPayload3.getEncryptionGroup(),
				controlComponentPublicKeysPayload3.getElectionEventId(),
				modifiedControlComponentPublicKeys);

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(Stream.of(controlComponentPublicKeysPayloads.get(0), controlComponentPublicKeysPayloads.get(1),
				controlComponentPublicKeysPayloads.get(2), modifiedControlComponentPublicKeysPayload3))
				.when(extractionServiceSpy).getControlComponentPublicKeysPayloads(datasetPath);

		final VerifyCCMElectionPublicKeyConsistency verificationWithMock = new VerifyCCMElectionPublicKeyConsistency(
				extractionServiceSpy, resultPublisherServiceMock);

		final VerificationResult result = verificationWithMock.verify(datasetPath);
		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification304.nok.message"));
		assertEquals(expectedResult, result);
	}
}
