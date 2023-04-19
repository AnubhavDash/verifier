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
import ch.post.it.evoting.evotinglibraries.domain.configuration.ControlComponentPublicKeysPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
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
		final List<ControlComponentPublicKeysPayload> publicKeysPayloads = electionDataExtractionService.getControlComponentPublicKeysPayloads(
				datasetPath);
		doReturn(publicKeysPayloads.subList(0, publicKeysPayloads.size() - 1)).when(extractionServiceMock)
				.getControlComponentPublicKeysPayloads(datasetPath);

		final VerifyNodeIdsConsistency verificationWithMock = new VerifyNodeIdsConsistency(resultPublisherServiceMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokControlComponentPublicKeysUniqueness() {
		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		final List<ControlComponentPublicKeysPayload> publicKeysPayloads = electionDataExtractionService.getControlComponentPublicKeysPayloads(
				datasetPath);
		final List<ControlComponentPublicKeysPayload> publicKeysWithDuplicateNodeIds = Streams.concat(publicKeysPayloads.stream(),
				Stream.of(publicKeysPayloads.get(0))).toList();
		doReturn(publicKeysWithDuplicateNodeIds).when(extractionServiceMock).getControlComponentPublicKeysPayloads(datasetPath);

		final VerifyNodeIdsConsistency verificationWithMock = new VerifyNodeIdsConsistency(resultPublisherServiceMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokCodeShares() {
		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		final Stream<Stream<ControlComponentCodeSharesPayload>> codeSharesPayloads = electionDataExtractionService.getControlComponentCodeSharesPayloadsByChunkAndVcs(
				datasetPath);
		final Stream<Stream<ControlComponentCodeSharesPayload>> codeSharesPayloadsMissingNodeId = codeSharesPayloads
				.parallel()
				.map(codeSharesList -> codeSharesList
						.parallel()
						.filter(codeShare -> codeShare.getNodeId() != 1));
		doReturn(codeSharesPayloadsMissingNodeId).when(extractionServiceMock).getControlComponentCodeSharesPayloadsByChunkAndVcs(datasetPath);

		final VerifyNodeIdsConsistency verificationWithMock = new VerifyNodeIdsConsistency(resultPublisherServiceMock, extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification313.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
