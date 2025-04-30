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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.domain.UUIDGenerator;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

@DisplayName("VerifyElectionEventIdConsistency with")
class VerifyElectionEventIdConsistencyTest extends SetupVerificationTest {

	private final UUIDGenerator uuidGenerator = UUIDGenerator.getInstance();

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyElectionEventIdConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent setup component tally data payload failed")
	void inconsistentSetupComponentTallyDataPayload() {
		final String inconsistentElectionEventId = uuidGenerator.generate();

		final Stream<SetupComponentTallyDataPayload> stream = electionDataExtractionService.getSetupComponentTallyDataPayloads(datasetPath)
				.map(setupComponentTallyDataPayload -> new SetupComponentTallyDataPayload(
						setupComponentTallyDataPayload.getEncryptionGroup(),
						inconsistentElectionEventId,
						setupComponentTallyDataPayload.getVerificationCardSetId(),
						setupComponentTallyDataPayload.getVerificationCardIds(),
						setupComponentTallyDataPayload.getBallotBoxDefaultTitle(),
						setupComponentTallyDataPayload.getVerificationCardPublicKeys(),
						setupComponentTallyDataPayload.getSignature())
				);

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(extractionServiceSpy).getSetupComponentTallyDataPayloads(datasetPath);

		final VerifyElectionEventIdConsistency verifyElectionEventIdConsistency = new VerifyElectionEventIdConsistency(resultPublisherServiceMock,
				extractionServiceSpy);

		final VerificationResult result = verifyElectionEventIdConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification309.nok.message"));
		assertEquals(expectedResult, result);
	}

	@Test
	@DisplayName("inconsistent control component public keys payload failed")
	void inconsistentControlComponentPublicKeysPayload() {
		final String inconsistentElectionEventId = uuidGenerator.generate();

		final Stream<ControlComponentPublicKeysPayload> stream = electionDataExtractionService.getControlComponentPublicKeysPayloads(
						datasetPath)
				.map(controlComponentPublicKeysPayload -> new ControlComponentPublicKeysPayload(
						controlComponentPublicKeysPayload.getEncryptionGroup(),
						inconsistentElectionEventId,
						controlComponentPublicKeysPayload.getControlComponentPublicKeys(),
						controlComponentPublicKeysPayload.getSignature())
				);

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(extractionServiceSpy).getControlComponentPublicKeysPayloads(datasetPath);

		final VerifyElectionEventIdConsistency verifyElectionEventIdConsistency = new VerifyElectionEventIdConsistency(resultPublisherServiceMock,
				extractionServiceSpy);

		final VerificationResult result = verifyElectionEventIdConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification309.nok.message"));
		assertEquals(expectedResult, result);
	}
}
