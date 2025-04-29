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
package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.evotinglibraries.protocol.algorithms.preliminaries.channelsecurity.XMLSignatureService;
import ch.post.it.evoting.verifier.backend.verifications.authenticity.DatasetSignatureFactory;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureTallyComponentEch0222Test extends TallyVerificationTest {

	private KeyStore keyStoreMock;
	private XMLSignatureService xmlSignatureServiceMock;

	@BeforeEach
	void setUpAll() {
		keyStoreMock = spy(new DatasetSignatureFactory().getKeystore());
		xmlSignatureServiceMock = spy(new XMLSignatureService());
		verification = new VerifySignatureTallyComponentEch0222(resultPublisherServiceMock, electionDataExtractionService, xmlSignatureServiceMock, keyStoreMock);
	}

	@Test
	void testOK() {
		final Path deliveryPath = electionDataExtractionService.getTallyComponentEch0222Path(datasetPath);

		assertTrue(((VerifySignatureTallyComponentEch0222) verification).verifySignature(deliveryPath), "the signature is not valid");
	}

	@Test
	void testNOK() {
		final Path deliveryPath = electionDataExtractionService.getTallyComponentEch0222Path(datasetPath);
		doReturn(false).when(xmlSignatureServiceMock).verifyXMLSignature(any(), any());

		assertFalse(((VerifySignatureTallyComponentEch0222) verification).verifySignature(deliveryPath), "the signature is not valid");
	}

	@Test
	void testVerificationKeyNOK() throws KeyStoreException, NoSuchAlgorithmException {
		final Path deliveryPath = electionDataExtractionService.getTallyComponentEch0222Path(datasetPath);
		final Certificate certificateMock = mock(Certificate.class);
		final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(3072);
		final KeyPair keyPair = keyPairGenerator.generateKeyPair();
		doReturn(keyPair.getPublic()).when(certificateMock).getPublicKey();
		doReturn(certificateMock).when(keyStoreMock).getCertificate(any());

		assertFalse(((VerifySignatureTallyComponentEch0222) verification).verifySignature(deliveryPath), "the signature is not valid");
	}
}