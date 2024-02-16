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

package ch.post.it.evoting.verifier.backend.verifications.authenticity;

import static java.time.LocalDate.now;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import ch.post.it.evoting.cryptoprimitives.signing.AuthorityInformation;
import ch.post.it.evoting.cryptoprimitives.signing.GenKeysAndCert;
import ch.post.it.evoting.cryptoprimitives.signing.KeysAndCert;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureFactory;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureGeneration;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;

/**
 * Utility functions to generate and verify signatures for unit testing authenticity checks.
 */
public class TestDigitalSignaturesFactory {

	private final Map<Alias, KeysAndCert> keysAndCertMap;

	public TestDigitalSignaturesFactory() {
		keysAndCertMap = new HashMap<>();
		for (Alias alias: Alias.values()) {
			final KeysAndCert keysAndCert = generatePrivateKeyAndCertificate();
			keysAndCertMap.put(alias, keysAndCert);
		}
	}

	static KeysAndCert generatePrivateKeyAndCertificate() {
		final SignatureFactory factory = SignatureFactory.getInstance();
		final GenKeysAndCert digitalSignatures = factory.createGenKeysAndCert(
				AuthorityInformation.builder().setCountry("dummy-C").setCommonName("dummy-Cn").setOrganisation("dummy-O").setLocality("dummy-L")
						.setState("dummy-St").build());
		return digitalSignatures.genKeysAndCert(now(), now().plusDays(1));
	}

	/**
	 * Get a signature verifier with generated certificates.
	 */
	public SignatureVerification getTestSignatureVerification()
			throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException {
		final KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(null, new char[]{});
		for (Map.Entry<Alias, KeysAndCert> entry :keysAndCertMap.entrySet()) {
			keyStore.setCertificateEntry(entry.getKey().get(), entry.getValue().certificate());
		}
		return SignatureFactory.getInstance().createSignatureVerification(keyStore);
	}

	/**
	 * To create a signature in the tests. It can then be verified with the verifier obtainer with method getTestSignatureVerification.
	 * @param alias with which to sign
	 * @return a SignatureGeneration service that can sign.
	 */
	public SignatureGeneration getTestSignatureGeneration(Alias alias) {
		return SignatureFactory.getInstance().createSignatureGeneration(keysAndCertMap.get(alias).privateKey(), keysAndCertMap.get(alias).certificate());
	}


}
