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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.PathNode;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

@DisplayName("VerifyBallotBoxesConsistency with")
class VerifyBallotBoxesConsistencyTest extends TallyVerificationTest {

	private static ElectionDataExtractionService electionDataExtractionService;

	@BeforeAll
	static void setUpAll() {
		electionDataExtractionService = new ElectionDataExtractionService(pathService, objectMapper);

		verification = new VerifyBallotBoxesConsistency(pathService, applicationEventPublisherMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent ballot box ids is failed")
	void inconsistentVerificationCardSetIds() {
		final PathNode pathNodeMock = mock(PathNode.class);
		when(pathNodeMock.getRegexPaths()).thenReturn(List.of(Path.of("11111111111111111111111111111111")));

		final PathService pathServiceMock = mock(PathService.class);
		when(pathServiceMock.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, datasetPath)).thenReturn(pathNodeMock);

		final VerifyBallotBoxesConsistency verifyBallotBoxesConsistency = new VerifyBallotBoxesConsistency(pathServiceMock,
				applicationEventPublisherMock, electionDataExtractionService);

		final VerificationResult result = verifyBallotBoxesConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification303.nok.message"));
		assertEquals(expectedResult, result);
	}

}
