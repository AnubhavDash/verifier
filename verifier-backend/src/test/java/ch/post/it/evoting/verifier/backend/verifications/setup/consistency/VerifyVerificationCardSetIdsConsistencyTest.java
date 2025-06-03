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
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.math.Random;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.evotinglibraries.domain.UUIDGenerator;
import ch.post.it.evoting.evotinglibraries.domain.configuration.SetupComponentTallyDataPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyVerificationCardSetIdsConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyVerificationCardSetIdsConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent verification card set ids is failed")
	void inconsistentVerificationCardIds() {
		final ImmutableList<Path> regexPaths = electionDataExtractionService.getContextVerificationCardSetPaths(datasetPath);

		assumeTrue(!regexPaths.isEmpty(), "This test assumes at least one verification card set exists.");

		final Random random = RandomFactory.createRandom();
		final int randomIndex = random.genRandomInteger(regexPaths.size());
		final Path verificationCardSetPath = regexPaths.get(randomIndex);
		final String verificationCardSetId = verificationCardSetPath.getFileName().toString();

		final SetupComponentTallyDataPayload setupComponentTallyDataPayload = electionDataExtractionService.getSetupComponentTallyDataPayload(
				datasetPath, verificationCardSetId);

		final UUIDGenerator uuidGenerator = UUIDGenerator.getInstance();
		final String otherVerificationCardSetId = uuidGenerator.generate();

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(new SetupComponentTallyDataPayload(
				setupComponentTallyDataPayload.getEncryptionGroup(),
				setupComponentTallyDataPayload.getElectionEventId(),
				otherVerificationCardSetId,
				setupComponentTallyDataPayload.getVerificationCardIds(),
				setupComponentTallyDataPayload.getBallotBoxDefaultTitle(),
				setupComponentTallyDataPayload.getVerificationCardPublicKeys(),
				setupComponentTallyDataPayload.getSignature()))
				.when(electionDataExtractionServiceSpy).getSetupComponentTallyDataPayload(datasetPath, verificationCardSetId);

		final VerifyVerificationCardSetIdsConsistency verifyVerificationCardSetIdsConsistency = new VerifyVerificationCardSetIdsConsistency(
				resultPublisherServiceMock, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyVerificationCardSetIdsConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification310.nok.message"));
		assertEquals(expectedResult, result);
	}
}
