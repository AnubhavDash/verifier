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

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;
import ch.post.it.evoting.verifier.protocol.domain.configuration.ControlComponentPublicKeysPayload;

class VerifySetupNodeIdsConsistencyTest extends SetupVerificationTest {

	private static ElectionDataExtractionService extractionService;

	@BeforeAll
	static void setupAll() {
		extractionService = new ElectionDataExtractionService(pathService, objectMapper);
		verification = new VerifySetupNodeIdsConsistency(applicationEventPublisherMock, extractionService);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokControlComponentPublicKeysCompleteness() {
		final ElectionDataExtractionService extractionServiceMock = spy(extractionService);
		final List<ControlComponentPublicKeysPayload> publicKeysPayloads = extractionService.getControlComponentPublicKeysPayloads(
				datasetPath);
		doReturn(publicKeysPayloads.subList(0, publicKeysPayloads.size() - 1)).when(extractionServiceMock)
				.getControlComponentPublicKeysPayloads(datasetPath);

		final VerifySetupNodeIdsConsistency verificationWithMock = new VerifySetupNodeIdsConsistency(applicationEventPublisherMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification311.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokControlComponentPublicKeysUniqueness() {
		final ElectionDataExtractionService extractionServiceMock = spy(extractionService);
		final List<ControlComponentPublicKeysPayload> publicKeysPayloads = extractionService.getControlComponentPublicKeysPayloads(
				datasetPath);
		final List<ControlComponentPublicKeysPayload> publicKeysWithDuplicateNodeIds = Streams.concat(publicKeysPayloads.stream(),
				Stream.of(publicKeysPayloads.get(0))).toList();
		doReturn(publicKeysWithDuplicateNodeIds).when(extractionServiceMock).getControlComponentPublicKeysPayloads(datasetPath);

		final VerifySetupNodeIdsConsistency verificationWithMock = new VerifySetupNodeIdsConsistency(applicationEventPublisherMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification311.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokCodeShares() {
		final ElectionDataExtractionService extractionServiceMock = spy(extractionService);
		final Stream<List<ControlComponentCodeSharesPayload>> codeSharesPayloads = extractionService.getControlComponentCodeSharesPayloadsByChunkAndVcs(
				datasetPath);
		final Stream<List<ControlComponentCodeSharesPayload>> codeSharesPayloadsMissingNodeId = codeSharesPayloads.map(
				codeSharesList -> codeSharesList.stream()
						.filter(codeShare -> codeShare.getNodeId() != 1)
						.toList());
		doReturn(codeSharesPayloadsMissingNodeId).when(extractionServiceMock).getControlComponentCodeSharesPayloadsByChunkAndVcs(datasetPath);

		final VerifySetupNodeIdsConsistency verificationWithMock = new VerifySetupNodeIdsConsistency(applicationEventPublisherMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification311.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
