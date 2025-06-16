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
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;

class VerifyFileNameNodeIdsConsistencyTest extends SetupVerificationTest {

	private final Random random = new Random();

	@BeforeAll
	static void setupAll() {
		verification = new VerifyFileNameNodeIdsConsistency(resultPublisherServiceMock, pathService, electionDataExtractionService);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNok() {
		final ImmutableList<Path> regexPaths = pathService.buildFromRootPath(StructureKey.CONTROL_COMPONENT_PUBLIC_KEYS, datasetPath).getRegexPaths();
		final int regexPathsSize = regexPaths.size();

		assumeTrue(regexPathsSize > 1, "This test assumes at least two control component public keys payloads.");

		final int firstRandomIndex = random.nextInt(0, regexPathsSize);
		int secondRandomIndex = firstRandomIndex;
		while (secondRandomIndex == firstRandomIndex) {
			secondRandomIndex = random.nextInt(0, regexPathsSize);
		}

		final ControlComponentPublicKeysPayload controlComponentPublicKeysPayload = electionDataExtractionService.getControlComponentPublicKeysPayload(
				regexPaths.get(firstRandomIndex));

		final ElectionDataExtractionService extractionServiceSpy = spy(electionDataExtractionService);
		doReturn(controlComponentPublicKeysPayload).when(extractionServiceSpy)
				.getControlComponentPublicKeysPayload(regexPaths.get(secondRandomIndex));

		final VerifyFileNameNodeIdsConsistency failingVerification = new VerifyFileNameNodeIdsConsistency(resultPublisherServiceMock, pathService,
				extractionServiceSpy);
		final VerificationResult verificationResult = failingVerification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification303.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
