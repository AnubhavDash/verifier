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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.security.SignatureException;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.evotinglibraries.domain.ControlComponentNode;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifySignatureVerificationDataAndCodeProofsTest extends SetupVerificationTest {
	private final VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm verifyCKProofsVerificationCardSetAlgorithm = spy(
			new VerifyEncryptedCKExponentiationProofsVerificationCardSetAlgorithm(
					ZeroKnowledgeProofFactory.createZeroKnowledgeProof()));
	private final VerifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm verifyPCCProofsVerificationCardSetAlgorithm = spy(
			new VerifyEncryptedPCCExponentiationProofsVerificationCardSetAlgorithm(
					ZeroKnowledgeProofFactory.createZeroKnowledgeProof()));
	private final VerifyEncryptedPCCExponentiationProofsAlgorithm verifyEncryptedPCCExponentiationProofsAlgorithm = new VerifyEncryptedPCCExponentiationProofsAlgorithm(
			verifyPCCProofsVerificationCardSetAlgorithm);
	private final VerifyEncryptedCKExponentiationProofsAlgorithm verifyEncryptedCKExponentiationProofsAlgorithm = new VerifyEncryptedCKExponentiationProofsAlgorithm(
			verifyCKProofsVerificationCardSetAlgorithm);
	private final Random random = new Random();
	private final ElectionDataExtractionService electionDataExtractionService = new ElectionDataExtractionService(
			pathService,
			objectMapper,
			ech0222XmlFileRepository,
			configurationXmlFileRepository,
			electionEventContextPayloadDataExtractor,
			controlComponentCodeSharesPayloadDataExtractor,
			setupComponentVerificationDataPayloadDataExtractor,
			controlComponentPublicKeysPayloadDataExtractor,
			setupComponentTallyDataPayloadDataExtractor);

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureVerificationDataAndCodeProofs(resultPublisherServiceMock, electionDataExtractionService,
				datasetSignatureVerification, verifyEncryptedPCCExponentiationProofsAlgorithm, verifyEncryptedCKExponentiationProofsAlgorithm);
	}

	@Test
	void testOK() throws SignatureException {
		final SignatureVerification signatureVerification = spy(SignatureVerification.class);

		verification = new VerifySignatureVerificationDataAndCodeProofs(
				resultPublisherServiceMock,
				electionDataExtractionService,
				signatureVerification,
				verifyEncryptedPCCExponentiationProofsAlgorithm, verifyEncryptedCKExponentiationProofsAlgorithm);

		when(signatureVerification.verifySignature(any(), any(), any(), any())).thenReturn(true);

		final VerificationResult result = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, result);
	}

	@Test
	void testSignatureSetupComponentVerificationData() {
		final SetupComponentVerificationDataPayload setupComponentVerificationData = loadRandomSetupComponentVerificationData();

		assertTrue(((VerifySignatureVerificationDataAndCodeProofs) verification).verifySignatureSetupComponentVerificationData(
				setupComponentVerificationData));
	}

	@Test
	void testSignatureSetupComponentVerificationDataWithWrongSignature() {
		final SetupComponentVerificationDataPayload setupComponentVerificationData = loadRandomSetupComponentVerificationData();

		final SetupComponentVerificationDataPayload another = new SetupComponentVerificationDataPayload(
				setupComponentVerificationData.getElectionEventId(),
				setupComponentVerificationData.getVerificationCardSetId(),
				setupComponentVerificationData.getPartialChoiceReturnCodesAllowList(),
				setupComponentVerificationData.getChunkId() + 1,
				setupComponentVerificationData.getEncryptionGroup(),
				setupComponentVerificationData.getSetupComponentVerificationData(),
				setupComponentVerificationData.getSignature());

		final boolean result = ((VerifySignatureVerificationDataAndCodeProofs) verification).verifySignatureSetupComponentVerificationData(another);
		assertFalse(result);
	}

	@Test
	void testSignatureControlComponentCodeSharesPayload() {
		final ControlComponentCodeSharesPayload controlComponentCodeShares = loadRandomControlComponentCodeShares();

		assertTrue(((VerifySignatureVerificationDataAndCodeProofs) verification).verifySignatureControlComponentCodeSharesPayload(
				controlComponentCodeShares));
	}

	@Test
	void testSignatureControlComponentCodeSharesPayloadWithWrongSignature() throws SignatureException {
		final ControlComponentCodeSharesPayload controlComponentCodeSharesPayload = loadRandomControlComponentCodeShares();

		final int nodeId = controlComponentCodeSharesPayload.getNodeId();
		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(controlComponentCodeSharesPayload,
				ChannelSecurityContextData.controlComponentCodeShares(nodeId, controlComponentCodeSharesPayload.getElectionEventId(),
						controlComponentCodeSharesPayload.getVerificationCardSetId()),
				Alias.getControlComponentByNodeId(nodeId));
		controlComponentCodeSharesPayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureVerificationDataAndCodeProofs) verification).verifySignatureControlComponentCodeSharesPayload(
				controlComponentCodeSharesPayload));
	}

	private SetupComponentVerificationDataPayload loadRandomSetupComponentVerificationData() {
		final ImmutableList<Path> regexPaths = electionDataExtractionService.getSetupVerificationCardSetPaths(datasetPath);
		final int randomIndex = random.nextInt(0, regexPaths.size());
		final Path verificationCardSet = regexPaths.get(randomIndex);

		final int chunkCount = electionDataExtractionService.determineVerificationCardSetChunkCount(verificationCardSet);

		final int randomChunkId = random.nextInt(0, chunkCount);
		return electionDataExtractionService.getSetupComponentVerificationDataPayloadChunk(verificationCardSet, randomChunkId);
	}

	private ControlComponentCodeSharesPayload loadRandomControlComponentCodeShares() {
		final ImmutableList<Path> regexPaths = electionDataExtractionService.getSetupVerificationCardSetPaths(datasetPath);
		final int randomIndex = random.nextInt(0, regexPaths.size());
		final Path verificationCardSet = regexPaths.get(randomIndex);

		final int chunkCount = electionDataExtractionService.determineVerificationCardSetChunkCount(verificationCardSet);

		final int randomChunkId = random.nextInt(0, chunkCount);
		return electionDataExtractionService.getControlComponentCodeSharesPayloadChunkOrderByNodeId(
				verificationCardSet, randomChunkId).get(random.nextInt(0, ControlComponentNode.ids().size()));
	}
}
