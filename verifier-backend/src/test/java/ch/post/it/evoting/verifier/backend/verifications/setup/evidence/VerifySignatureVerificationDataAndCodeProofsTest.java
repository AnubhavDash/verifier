/*
 * Copyright 2023 Post CH Ltd
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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static ch.post.it.evoting.evotinglibraries.domain.ControlComponentConstants.NODE_IDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.ZeroKnowledgeProofFactory;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.ControlComponentCodeSharesPayload;
import ch.post.it.evoting.evotinglibraries.domain.returncodes.SetupComponentVerificationDataPayload;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.path.StructureKey;
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
			ech0110XmlFileRepository,
			ech0222XmlFileRepository,
			configurationXmlFileRepository,
			resultsXmlFileRepository,
			electionEventContextPayloadDataExtractor,
			controlComponentCodeSharesPayloadDataExtractor,
			setupComponentVerificationDataPayloadDataExtractor,
			controlComponentPublicKeysPayloadDataExtractor,
			setupComponentTallyDataPayloadDataExtractor);

	@BeforeEach
	void setUpAll() throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
		verification = new VerifySignatureVerificationDataAndCodeProofs(resultPublisherServiceMock, electionDataExtractionService,
				signatureFactory.getTestSignatureVerification(),
				verifyEncryptedPCCExponentiationProofsAlgorithm, verifyEncryptedCKExponentiationProofsAlgorithm);
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
	void testSignatureSetupComponentVerificationData() throws SignatureException {
		final SetupComponentVerificationDataPayload setupComponentVerificationData = loadRandomSetupComponentVerificationData();
		setupComponentVerificationData.setSignature(new CryptoPrimitivesSignature(generateSignature(setupComponentVerificationData)));

		final boolean result = ((VerifySignatureVerificationDataAndCodeProofs) verification).verifySignatureSetupComponentVerificationData(
				setupComponentVerificationData);
		assertTrue(result);
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
	void testSignatureControlComponentCodeSharesPayload() throws SignatureException {
		final ControlComponentCodeSharesPayload controlComponentCodeShares = loadRandomControlComponentCodeShares();

		controlComponentCodeShares.setSignature(new CryptoPrimitivesSignature(generateSignature(controlComponentCodeShares)));
		final boolean result = ((VerifySignatureVerificationDataAndCodeProofs) verification).verifySignatureControlComponentCodeSharesPayload(
				controlComponentCodeShares);
		assertTrue(result);
	}

	@Test
	void testSignatureControlComponentCodeSharesPayloadWithWrongSignature() throws SignatureException {
		final ControlComponentCodeSharesPayload controlComponentCodeShares = loadRandomControlComponentCodeShares();

		final ControlComponentCodeSharesPayload another = new ControlComponentCodeSharesPayload(
				controlComponentCodeShares.getElectionEventId(),
				controlComponentCodeShares.getVerificationCardSetId(),
				controlComponentCodeShares.getChunkId() + 1,
				controlComponentCodeShares.getEncryptionGroup(),
				controlComponentCodeShares.getControlComponentCodeShares(),
				controlComponentCodeShares.getNodeId(),
				controlComponentCodeShares.getSignature());

		controlComponentCodeShares.setSignature(new CryptoPrimitivesSignature(generateSignature(controlComponentCodeShares)));
		final boolean result = ((VerifySignatureVerificationDataAndCodeProofs) verification).verifySignatureControlComponentCodeSharesPayload(
				another);
		assertFalse(result);
	}

	private SetupComponentVerificationDataPayload loadRandomSetupComponentVerificationData() {
		final List<Path> regexPaths = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, datasetPath).getRegexPaths();
		final int randomIndex = random.nextInt(0, regexPaths.size());
		final Path verificationCardSet = regexPaths.get(randomIndex);

		final int chunkCount = electionDataExtractionService.determineVerificationCardSetChunkCount(verificationCardSet);

		final int randomChunkId = random.nextInt(0, chunkCount);
		return electionDataExtractionService.getSetupComponentVerificationDataPayloadChunk(verificationCardSet, randomChunkId);
	}

	private ControlComponentCodeSharesPayload loadRandomControlComponentCodeShares() {
		final List<Path> regexPaths = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, datasetPath).getRegexPaths();
		final int randomIndex = random.nextInt(0, regexPaths.size());
		final Path verificationCardSet = regexPaths.get(randomIndex);

		final int chunkCount = electionDataExtractionService.determineVerificationCardSetChunkCount(verificationCardSet);

		final int randomChunkId = random.nextInt(0, chunkCount);
		return electionDataExtractionService.getControlComponentCodeSharesPayloadChunkOrderByNodeId(
				verificationCardSet, randomChunkId).get(random.nextInt(0, NODE_IDS.size()));
	}

	private byte[] generateSignature(final ControlComponentCodeSharesPayload controlComponentCodeShares) throws SignatureException {
		final Hashable hash = HashableList.from(controlComponentCodeShares.toHashableForm());
		final Hashable additionalContextData = ChannelSecurityContextData.controlComponentCodeShares(
				controlComponentCodeShares.getNodeId(),
				controlComponentCodeShares.getElectionEventId(),
				controlComponentCodeShares.getVerificationCardSetId());

		return signatureFactory.getTestSignatureGeneration(Alias.getControlComponentByNodeId(controlComponentCodeShares.getNodeId()))
				.genSignature(hash, additionalContextData);
	}

	private byte[] generateSignature(final SetupComponentVerificationDataPayload setupComponentVerificationData) throws SignatureException {
		final Hashable hash = HashableList.from(setupComponentVerificationData.toHashableForm());
		final Hashable additionalContextData = ChannelSecurityContextData.setupComponentVerificationData(
				setupComponentVerificationData.getElectionEventId(),
				setupComponentVerificationData.getVerificationCardSetId());

		return signatureFactory.getTestSignatureGeneration(Alias.SDM_CONFIG).genSignature(hash, additionalContextData);
	}

}