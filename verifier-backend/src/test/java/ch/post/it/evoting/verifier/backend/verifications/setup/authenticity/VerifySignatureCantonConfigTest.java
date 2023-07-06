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

package ch.post.it.evoting.verifier.backend.verifications.setup.authenticity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.xml.hashable.HashableCantonConfigFactory;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifySignatureCantonConfigTest extends SetupVerificationTest {

	@BeforeEach
	void setUpAll() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
		final SignatureVerification testSignatureVerification = signatureFactory.getTestSignatureVerification();
		verification = new VerifySignatureCantonConfig(resultPublisherServiceMock, electionDataExtractionService,
				testSignatureVerification);
	}

	@Test
	void testOK() throws SignatureException {
		final Configuration configuration = electionDataExtractionService.getCantonConfig(datasetPath);

		configuration.setSignature(generateSignature(configuration));

		assertTrue(((VerifySignatureCantonConfig) verification).verifySignature(configuration), "the signature is not valid");
	}

	@Test
	void testNOK() throws SignatureException {
		final Configuration configuration = electionDataExtractionService.getCantonConfig(datasetPath);

		configuration.setSignature(generateSignature(configuration));
		configuration.getContest().setContestIdentification("new value");

		assertFalse(((VerifySignatureCantonConfig) verification).verifySignature(configuration), "the signature is valid but it should not");
	}

	private byte[] generateSignature(final Configuration configuration) throws SignatureException {
		final Hashable hash = HashableCantonConfigFactory.fromConfiguration(configuration);
		final Hashable additionalContextData = ChannelSecurityContextData.cantonConfig();

		return signatureFactory.getTestSignatureGeneration(Alias.CANTON).genSignature(hash, additionalContextData);
	}
}