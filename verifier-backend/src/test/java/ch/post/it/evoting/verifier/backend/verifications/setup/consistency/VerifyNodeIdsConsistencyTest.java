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
import org.junit.jupiter.api.Test;

import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentCodeSharesPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.dataextractors.ControlComponentPublicKeysPayloadDataExtractor;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyNodeIdsConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyNodeIdsConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokControlComponentPublicKeysCompleteness() {
		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		final ImmutableList<ControlComponentPublicKeysPayloadDataExtractor.DataExtraction> dataExtractions = electionDataExtractionService.getControlComponentPublicKeysPayloadsDataExtractions(
				datasetPath).collect(toImmutableList());
		doReturn(dataExtractions.subList(0, dataExtractions.size() - 1).stream()).when(extractionServiceMock)
				.getControlComponentPublicKeysPayloadsDataExtractions(datasetPath);

		final VerifyNodeIdsConsistency verificationWithMock = new VerifyNodeIdsConsistency(resultPublisherServiceMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification314.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokControlComponentPublicKeysUniqueness() {
		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		final ImmutableList<ControlComponentPublicKeysPayloadDataExtractor.DataExtraction> dataExtractions = electionDataExtractionService.getControlComponentPublicKeysPayloadsDataExtractions(
				datasetPath).collect(toImmutableList());
		final Stream<ControlComponentPublicKeysPayloadDataExtractor.DataExtraction> publicKeysWithDuplicateNodeIds = Streams.concat(
				dataExtractions.stream(),
				Stream.of(dataExtractions.get(0)));
		doReturn(publicKeysWithDuplicateNodeIds).when(extractionServiceMock).getControlComponentPublicKeysPayloadsDataExtractions(datasetPath);

		final VerifyNodeIdsConsistency verificationWithMock = new VerifyNodeIdsConsistency(resultPublisherServiceMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification314.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokCodeShares() {
		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);

		final Stream<ControlComponentCodeSharesPayloadDataExtractor.DataExtraction> dataExtractions = electionDataExtractionService.getAllControlComponentCodeSharesPayloadsDataExtractions(
						datasetPath)
				.map(dataExtraction -> new ControlComponentCodeSharesPayloadDataExtractor.DataExtraction(
								dataExtraction.chunkIds(),
								dataExtraction.electionEventIds(),
						dataExtraction.nodeIds().stream().filter(nodeId -> nodeId != 1).collect(toImmutableList()),
								dataExtraction.verificationCardSetIds(),
								dataExtraction.verificationCardIdsNode1(),
								dataExtraction.verificationCardIdsNode2(),
								dataExtraction.verificationCardIdsNode3(),
								dataExtraction.verificationCardIdsNode4(),
								dataExtraction.p(),
								dataExtraction.q(),
								dataExtraction.g()
						)
				);

		doReturn(dataExtractions).when(extractionServiceMock).getAllControlComponentCodeSharesPayloadsDataExtractions(datasetPath);

		final VerifyNodeIdsConsistency verificationWithMock = new VerifyNodeIdsConsistency(resultPublisherServiceMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification314.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
