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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap.toImmutableMap;
import static java.time.LocalDate.now;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableByteArray;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.signing.AuthorityInformation;
import ch.post.it.evoting.cryptoprimitives.signing.GenKeysAndCert;
import ch.post.it.evoting.cryptoprimitives.signing.KeysAndCert;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureFactory;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureGeneration;
import ch.post.it.evoting.cryptoprimitives.signing.SignatureVerification;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;

/**
 * Utility functions to test the authenticity verifications.
 */
public class DatasetSignatureFactory {

	private final SignatureFactory signatureFactory;
	private final ImmutableMap<Alias, KeysAndCert> dummyKeysAndCertMap;

	public DatasetSignatureFactory() {
		signatureFactory = SignatureFactory.getInstance();
		dummyKeysAndCertMap = Arrays.stream(Alias.values())
				.collect(toImmutableMap(alias -> alias, alias -> generateDummyPrivateKeyAndCertificate()));
	}

	/**
	 * @return a new {@link SignatureVerification} from the datasets' direct-trust keystore.
	 */
	public SignatureVerification getSignatureVerification() {
		return signatureFactory.createSignatureVerification(getKeystore());
	}

	/**
	 * @param hashable              the payload to sign.
	 * @param additionalContextData the additional context data.
	 * @param alias                 the signing alias.
	 * @return a dummy signature.
	 */
	public CryptoPrimitivesSignature getDummySignature(final Hashable hashable, final Hashable additionalContextData, final Alias alias)
			throws SignatureException {
		final SignatureGeneration dummySignatureGeneration = getDummySignatureGeneration(alias);
		final ImmutableByteArray dummySignature = dummySignatureGeneration.genSignature(hashable, additionalContextData);
		return new CryptoPrimitivesSignature(dummySignature);
	}

	/**
	 * @return the datasets' direct-trust keystore.
	 */
	private KeyStore getKeystore() {
		final String directTrustLocation = "datasets/direct-trust";
		final String keystoreLocation = directTrustLocation + "/local_direct_trust_keystore_verifier.p12";
		final String passwordLocation = directTrustLocation + "/local_direct_trust_pw_verifier.txt";
		final ClassLoader classLoader = this.getClass().getClassLoader();

		try (final InputStream keystoreInputStream = classLoader.getResourceAsStream(keystoreLocation);
			 final InputStream passwordInputStream = classLoader.getResourceAsStream(passwordLocation)) {
			final char[] pwLocation =  new String(passwordInputStream.readAllBytes()).toCharArray();
			final KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(keystoreInputStream, pwLocation);
			return keyStore;
		} catch (final IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException("Error loading datasets direct-trust keystore", e);
		}
	}

	/**
	 * @param alias the signing alias.
	 * @return a dummy {@link SignatureGeneration} to generate dummy signatures.
	 */
	private SignatureGeneration getDummySignatureGeneration(final Alias alias) {
		return signatureFactory.createSignatureGeneration(dummyKeysAndCertMap.get(alias).privateKey(), dummyKeysAndCertMap.get(alias).certificate());
	}

	/**
	 * @return a dummy {@link KeysAndCert}.
	 */
	private KeysAndCert generateDummyPrivateKeyAndCertificate() {
		final SignatureFactory factory = SignatureFactory.getInstance();
		final GenKeysAndCert digitalSignatures = factory.createGenKeysAndCert(
				AuthorityInformation.builder()
						.setCountry("dummy-C")
						.setCommonName("dummy-Cn")
						.setOrganisation("dummy-O")
						.setLocality("dummy-L")
						.setState("dummy-St")
						.build()
		);
		return digitalSignatures.genKeysAndCert(now(), now().plusDays(1));
	}
}
