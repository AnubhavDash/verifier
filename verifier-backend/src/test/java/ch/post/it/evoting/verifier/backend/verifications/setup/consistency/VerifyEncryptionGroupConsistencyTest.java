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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

@DisplayName("VerifySetupEncryptionGroupConsistency with")
class VerifyEncryptionGroupConsistencyTest extends SetupVerificationTest {

	private static final GqGroup otherGqGroup = new GqGroup(BigInteger.valueOf(23), BigInteger.valueOf(11), BigInteger.valueOf(3));

	@BeforeAll
	static void setupAll() {
		verification = new VerifyEncryptionGroupConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("ElectionEventContext having different encryption group parameters fails")
	void invalidElectionEventContextPayload() {
		final ElectionEventContextPayload mock = mock(ElectionEventContextPayload.class);
		when(mock.getEncryptionGroup()).thenReturn(otherGqGroup);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(mock).when(electionDataExtractionServiceSpy).getElectionEventContextPayload(datasetPath);

		assertInvalidVerification(electionDataExtractionServiceSpy);
	}

	@Test
	@DisplayName("ControlComponentPublicKeys containing different encryption group parameters fails")
	void invalidControlComponentPublicKeysPayload() {
		final ControlComponentPublicKeysPayload mock = mock(ControlComponentPublicKeysPayload.class);
		when(mock.getEncryptionGroup()).thenReturn(otherGqGroup);

		final Stream<ControlComponentPublicKeysPayload> stream = Stream.concat(
				Stream.of(mock),
				electionDataExtractionService.getControlComponentPublicKeysPayloads(datasetPath)
		);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(electionDataExtractionServiceSpy).getControlComponentPublicKeysPayloads(datasetPath);

		assertInvalidVerification(electionDataExtractionServiceSpy);
	}

	@Test
	@DisplayName("SetupComponentTallyData containing different encryption group parameters fails")
	void invalidSetupComponentTallyDataPayload() {
		final SetupComponentTallyDataPayload mock = mock(SetupComponentTallyDataPayload.class);
		when(mock.getEncryptionGroup()).thenReturn(otherGqGroup);


		final Stream<SetupComponentTallyDataPayload> stream = Stream.concat(
				Stream.of(mock),
				electionDataExtractionService.getSetupComponentTallyDataPayloads(datasetPath)
		);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(stream).when(electionDataExtractionServiceSpy).getSetupComponentTallyDataPayloads(datasetPath);

		assertInvalidVerification(electionDataExtractionServiceSpy);
	}

	private void assertInvalidVerification(final ElectionDataExtractionService electionDataExtractionServiceSpy) {
		final VerifyEncryptionGroupConsistency invalidVerification = new VerifyEncryptionGroupConsistency(resultPublisherServiceMock,
				electionDataExtractionServiceSpy);
		final VerificationResult verificationResult = invalidVerification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(invalidVerification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification301.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
