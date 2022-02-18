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
package ch.post.it.evoting.verifier.core.internal.tools;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SignatureService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SignatureService.class);
	private static final String SIGNATURE_ALGORITHM_NAME = "SHA256withRSAandMGF1";

	public boolean verify(final byte[] payloadSignature, final byte[] payloadHash, final X509Certificate signingCertificate,
			final List<X509Certificate> intermediateCertificates, final X509Certificate rootCertificate) {

		checkNotNull(payloadSignature);
		checkNotNull(payloadHash);
		checkNotNull(signingCertificate);
		checkArgument(!intermediateCertificates.isEmpty());
		checkNotNull(rootCertificate);

		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		final var publicKey = signingCertificate.getPublicKey();

		try {
			verifyCertificateChain(signingCertificate, intermediateCertificates, rootCertificate);
			final var signature = Signature.getInstance(SIGNATURE_ALGORITHM_NAME);
			signature.initVerify(publicKey);
			signature.update(payloadHash);
			return signature.verify(payloadSignature);
		} catch (GeneralSecurityException e) {
			LOGGER.error("An error occurs during the check of the payload signature.", e);
			return false;
		}
	}

	private static void verifyCertificateChain(final X509Certificate signingCertificate, final List<X509Certificate> intermediateCertificates,
			final X509Certificate platformRootCA) throws GeneralSecurityException {

		final Set<TrustAnchor> trustAnchors = new HashSet<>();
		trustAnchors.add(new TrustAnchor(platformRootCA, null));

		final var selector = new X509CertSelector();
		selector.setCertificate(signingCertificate);

		final var params = new PKIXBuilderParameters(trustAnchors, selector);

		//disable CLR check because we are not online
		params.setRevocationEnabled(false);

		params.addCertStore(CertStore.getInstance("Collection",
				new CollectionCertStoreParameters(intermediateCertificates), "BC"));

		final var builder = CertPathBuilder.getInstance("PKIX", "BC");
		builder.build(params);
	}
}
