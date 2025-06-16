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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

@DisplayName("VerifyVerificationCardSetsConsistency with")
class VerifyFileNameVerificationCardSetIdsConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyFileNameVerificationCardSetIdsConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent verification card set ids in context dataset fails")
	void inconsistentVerificationCardSetIdsContextDataset() {
		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		when(electionDataExtractionServiceSpy.getContextVerificationCardSetIdPaths(datasetPath)).thenReturn(
				ImmutableList.of(Path.of("11111111111111111111111111111111")));

		final VerifyFileNameVerificationCardSetIdsConsistency verifyFileNameVerificationCardSetIdsConsistency = new VerifyFileNameVerificationCardSetIdsConsistency(
				resultPublisherServiceMock, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyFileNameVerificationCardSetIdsConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification306.nok.message"));
		assertEquals(expectedResult, result);
	}

}
