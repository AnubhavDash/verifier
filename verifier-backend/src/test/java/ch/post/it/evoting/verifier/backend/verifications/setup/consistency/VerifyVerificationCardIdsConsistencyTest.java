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
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationData;
import ch.post.it.evoting.cryptoprimitives.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyVerificationCardIdsConsistencyTest extends SetupVerificationTest {

	private final Random random = new Random();

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyVerificationCardIdsConsistency(pathService, resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	@DisplayName("inconsistent verification card ids is failed")
	void inconsistentVerificationCardIds() {
		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doAnswer(invocationOnMock -> Stream.of()).when(electionDataExtractionServiceSpy)
				.deserializeSetupComponentVerificationDataPayloadOrderByChunkId(any());

		final VerifyVerificationCardIdsConsistency verifyVerificationCardIdsConsistency = new VerifyVerificationCardIdsConsistency(
				pathService, resultPublisherServiceMock, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyVerificationCardIdsConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification311.nok.message"));
		assertEquals(expectedResult, result);
	}

	@Test
	@DisplayName("wrong order of verification card ids in chunk is failed")
	void wrongOrderVerificationCardIdsInChunk() {
		final List<Path> regexPaths = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, datasetPath).getRegexPaths();
		final int randomIndex = random.nextInt(0, regexPaths.size());
		final Path verificationCardSet = regexPaths.get(randomIndex);

		final List<SetupComponentVerificationDataPayload> swappedPayloads = new ArrayList<>(
				electionDataExtractionService.deserializeSetupComponentVerificationDataPayloadOrderByChunkId(verificationCardSet).toList());
		assumeTrue(swappedPayloads.size() > 1, "This test assumes at least two verification cards in the set.");

		final SetupComponentVerificationDataPayload setupComponentVerificationDataPayload = swappedPayloads.get(0);
		final List<SetupComponentVerificationData> swappedSetupComponentVerificationData = new ArrayList<>(
				setupComponentVerificationDataPayload.getSetupComponentVerificationData());
		Collections.swap(swappedSetupComponentVerificationData, 0, 1);

		final SetupComponentVerificationDataPayload swappedPayload = new SetupComponentVerificationDataPayload(
				setupComponentVerificationDataPayload.getElectionEventId(), setupComponentVerificationDataPayload.getVerificationCardSetId(),
				setupComponentVerificationDataPayload.getPartialChoiceReturnCodesAllowList(), setupComponentVerificationDataPayload.getChunkId(),
				setupComponentVerificationDataPayload.getEncryptionGroup(), swappedSetupComponentVerificationData,
				setupComponentVerificationDataPayload.getCombinedCorrectnessInformation());
		swappedPayloads.set(0, swappedPayload);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(swappedPayloads.stream()).when(electionDataExtractionServiceSpy)
				.deserializeSetupComponentVerificationDataPayloadOrderByChunkId(verificationCardSet);
		final VerifyVerificationCardIdsConsistency verifyVerificationCardIdsConsistency = new VerifyVerificationCardIdsConsistency(
				pathService, resultPublisherServiceMock, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyVerificationCardIdsConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification311.nok.message"));
		assertEquals(expectedResult, result);
	}

	@Test
	@DisplayName("wrong order of verification card ids between chunks is failed")
	void wrongOrderVerificationCardIdsBetweenChunks() {
		final List<Path> regexPaths = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, datasetPath).getRegexPaths();
		final int randomIndex = random.nextInt(0, regexPaths.size());
		final Path verificationCardSet = regexPaths.get(randomIndex);
		final List<SetupComponentVerificationDataPayload> swappedPayloads = new ArrayList<>(
				electionDataExtractionService.deserializeSetupComponentVerificationDataPayloadOrderByChunkId(verificationCardSet).toList());
		assumeTrue(swappedPayloads.size() > 1, "This test assumes at least two verification cards in the set.");

		Collections.swap(swappedPayloads, 0, 1);

		final ElectionDataExtractionService electionDataExtractionServiceSpy = spy(electionDataExtractionService);
		doReturn(swappedPayloads.stream()).when(electionDataExtractionServiceSpy)
				.deserializeSetupComponentVerificationDataPayloadOrderByChunkId(verificationCardSet);
		final VerifyVerificationCardIdsConsistency verifyVerificationCardIdsConsistency = new VerifyVerificationCardIdsConsistency(
				pathService, resultPublisherServiceMock, electionDataExtractionServiceSpy);

		final VerificationResult result = verifyVerificationCardIdsConsistency.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification311.nok.message"));
		assertEquals(expectedResult, result);
	}
}
