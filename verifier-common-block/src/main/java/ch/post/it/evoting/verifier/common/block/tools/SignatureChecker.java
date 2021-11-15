/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.common.block.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.common.block.dto.revised.Metadata;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class SignatureChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(SignatureChecker.class);
	private static final String SIGN_ALGO_NAME = "SHA256withRSAandMGF1";

	private SignatureChecker() {
		//private ctor, use static
	}

	public static boolean verifyPKCS7(byte[] sourceData, byte[] signatureData, byte[] rootCert) {
		try {
			if (Security.getProvider("BC") == null) {
				Security.addProvider(new BouncyCastleProvider());
			}
			CMSProcessable signedContent = new CMSProcessableByteArray(sourceData);
			var cms = new CMSSignedData(signedContent, signatureData);

			var store = cms.getCertificates();
			SignerInformationStore signers = cms.getSignerInfos();
			for (final SignerInformation signer : signers.getSigners()) {
				var certIt = store.getMatches(signer.getSID()).iterator();
				X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
				X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

				if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
					//signature is valid, checking certificate chain validity
					X509Certificate root = loadCertificate(rootCert);
					List<X509Certificate> intermediates =
							new ArrayList<>(store.getMatches(null)).stream().map(holder -> {
								try {
									return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
								} catch (CertificateException e) {
									throw new IllegalArgumentException("Unable to convert the certificate", e);
								}
							}).collect(Collectors.toList());

					verifyCertificateChain(cert, intermediates, root);

					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.warn("Error during signature check", e);
		}
		return false;
	}

	public static boolean verifyMetadata(byte[] sourceData, byte[] metadataData, byte[] signerCert, byte[] rootCert) {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		try {
			var metadata = Deserializer.fromJson(metadataData, Metadata.class);
			final X509Certificate sCert = loadCertificate(signerCert);

			if (!metadata.getVersion().equals("1.0")) {
				throw new UnsupportedOperationException("metadata version not supported : " + metadata.getVersion());
			}

			final String algoName = StringUtils.isNotEmpty(metadata.getAlgorithm()) ? metadata.getAlgorithm() : SIGN_ALGO_NAME;

			//signature
			byte[] signature = metadata.getSignature();

			//take fields to be added to the content
			var sb = new StringBuilder();
			metadata.getSignedItems().forEach(s -> sb.append(s.getValue()));
			byte[] fields = sb.toString().getBytes(StandardCharsets.UTF_8);

			//concatenate sourceData & fields
			var source = new byte[sourceData.length + fields.length];
			System.arraycopy(sourceData, 0, source, 0, sourceData.length);
			System.arraycopy(fields, 0, source, sourceData.length, fields.length);

			var signatureAlgorithm = Signature.getInstance(algoName);
			signatureAlgorithm.initVerify(sCert.getPublicKey());
			signatureAlgorithm.update(source);

			if (signatureAlgorithm.verify(signature)) {
				//signature is valid, checking certificate chain validity
				verifyCertificateChain(sCert, Collections.singletonList(sCert), loadCertificate(rootCert));
				return true;
			}
		} catch (Exception e) {
			LOGGER.warn("signature check failed", e);
		}
		return false;
	}

	/**
	 * Verify that the .sign signature of a file is correct.
	 *
	 * @param source                   The signed file.
	 * @param signature                The file signature.
	 * @param signingCertificate       The certificate used to sign the file.
	 * @param intermediateCertificates Intermediate certificates if any.
	 * @param rootCertificate          The root certificate.
	 * @return {@code true} if the provided {@code signature} is the correct signature for the {@code source} file.
	 */
	public static boolean verifySignature(byte[] source, byte[] signature, byte[] signingCertificate,
			byte[][] intermediateCertificates, byte[] rootCertificate) {

		// Init the BouncyCastle security provider if not done.
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		try {
			final X509Certificate sCert = loadCertificate(signingCertificate);

			var signatureAlgorithm = Signature.getInstance(SIGN_ALGO_NAME);
			signatureAlgorithm.initVerify(sCert.getPublicKey());
			signatureAlgorithm.update(source);

			// If signature is valid, check certificate chain validity.
			if (signatureAlgorithm.verify(signature)) {
				if (rootCertificate != null) {
					List<X509Certificate> intermediates = loadCertificatesChain(intermediateCertificates, sCert);
					verifyCertificateChain(sCert, intermediates, loadCertificate(rootCertificate));
				}
				return true;
			}
		} catch (IOException | GeneralSecurityException e) {
			LOGGER.error("Signature check failed.", e);
		}

		return false;
	}

	/**
	 * Verify that the .sign signature of a json file is correct. This method reconstruct a json tree from the information contained in the signature
	 * and compares the tree with the one of the signed file.
	 *
	 * @param sourceNode               The {@link JsonNode} representing the signed file.
	 * @param signatureNode            The {@link JsonNode} representing the signature of the file.
	 * @param signingCertificate       The certificate used to sign the file.
	 * @param intermediateCertificates Intermediate certificates if any.
	 * @param rootCertificate          The root certificate.
	 * @return {@code true} if the provided {@code signatureNode} is the correct signature for the {@code sourceNode} file.
	 */
	public static boolean verifyJsonSignature(JsonNode sourceNode, JsonNode signatureNode, byte[] signingCertificate,
			byte[][] intermediateCertificates, byte[] rootCertificate) {

		try {
			final X509Certificate sCert = loadCertificate(signingCertificate);

			// Extract the json fields back from the signature.
			final Jws<Claims> claimsJws = Jwts.parser().setSigningKey(sCert.getPublicKey()).parseClaimsJws(signatureNode.asText());
			final LinkedHashMap recoveredSignedObject = (LinkedHashMap) claimsJws.getBody().get("objectToSign");

			// Convert to strings node to make the comparison.
			final var mapper = new ObjectMapper();
			final var recoveredString = mapper.writeValueAsString(recoveredSignedObject);
			final var sourceString = mapper.writeValueAsString(sourceNode);

			// If signature is valid, check certificate chain validity.
			if (sourceString.equals(recoveredString)) {
				if (rootCertificate != null) {
					List<X509Certificate> intermediates = loadCertificatesChain(intermediateCertificates, sCert);
					verifyCertificateChain(sCert, intermediates, loadCertificate(rootCertificate));
				}
				return true;
			}
		} catch (IOException | GeneralSecurityException e) {
			LOGGER.error("JSON signature check failed.", e);
		}

		return false;
	}

	private static X509Certificate loadCertificate(byte[] certificate) throws IOException, CertificateException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		var parser = new PEMParser(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(certificate))));
		return new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder) parser.readObject());
	}

	private static PKIXCertPathBuilderResult verifyCertificateChain(X509Certificate cert, List<X509Certificate> intermediateCerts,
			X509Certificate rootCA) throws GeneralSecurityException {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		Set<TrustAnchor> trustAnchors = new HashSet<>();
		trustAnchors.add(new TrustAnchor(rootCA, null));

		var selector = new X509CertSelector();
		selector.setCertificate(cert);

		var params = new PKIXBuilderParameters(trustAnchors, selector);

		//disable CLR check because we are not online
		params.setRevocationEnabled(false);

		params.addCertStore(CertStore.getInstance("Collection",
				new CollectionCertStoreParameters(intermediateCerts), "BC"));

		var builder = CertPathBuilder.getInstance("PKIX", "BC");

		return (PKIXCertPathBuilderResult) builder.build(params);
	}

	private static List<X509Certificate> loadCertificatesChain(byte[][] intermediateCertificates, X509Certificate sCert) {
		List<X509Certificate> intermediates = intermediateCertificates != null ?
				Arrays.stream(intermediateCertificates).map(bytes -> {
					try {
						return loadCertificate(bytes);
					} catch (CertificateException | IOException e) {
						throw new IllegalArgumentException(e);
					}
				}).collect(Collectors.toList()) : new ArrayList<>(1);
		intermediates.add(sCert);

		return intermediates;
	}
}
