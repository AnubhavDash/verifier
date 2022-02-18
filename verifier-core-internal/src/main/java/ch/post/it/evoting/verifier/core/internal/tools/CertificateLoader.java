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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.verifier.core.internal.tools.path.PathService;
import ch.post.it.evoting.verifier.core.internal.tools.path.StructureKey;

/**
 * Utility class to read the various certificates needed by the verifications.
 */
@Service
public class CertificateLoader {

	private final PathService pathService;

	public CertificateLoader(final PathService pathService) {
		this.pathService = pathService;
	}

	/**
	 * Loads a certificate file as bytes.
	 *
	 * @param structureKey       The {@link StructureKey} of the wanted certificate.
	 * @param inputDirectoryPath The path to the Verifier's dataset.
	 * @return The certificate as a byte array.
	 */
	public byte[] loadBytes(final StructureKey structureKey, final Path inputDirectoryPath) {
		final var certificatePathNode = pathService.buildFromRootPath(structureKey, inputDirectoryPath);
		return loadBytes(certificatePathNode.getPath());
	}

	/**
	 * Loads a certificate file as {@link X509Certificate}.
	 *
	 * @param structureKey       The {@link StructureKey} of the wanted certificate.
	 * @param inputDirectoryPath The path to the Verifier's dataset.
	 * @return The certificate as a X509Certificate.
	 */
	public X509Certificate loadCertificate(final StructureKey structureKey, final Path inputDirectoryPath) {
		final byte[] certificateBytes = loadBytes(structureKey, inputDirectoryPath);
		return this.parseCertificate(certificateBytes);
	}

	/**
	 * Loads a certificate file as {@link X509Certificate}.
	 *
	 * @param certificatePath The absolute path to the certificate.
	 * @return The certificate as a X509Certificate.
	 */
	public X509Certificate loadCertificate(final Path certificatePath) {
		final byte[] certificateBytes = loadBytes(certificatePath);
		return this.parseCertificate(certificateBytes);
	}

	private byte[] loadBytes(final Path certificatePath) {
		try {
			return Files.readAllBytes(certificatePath);
		} catch (IOException e) {
			throw new UncheckedIOException(String.format("Failed to read certificate at this path %s.", certificatePath), e);
		}
	}

	private X509Certificate parseCertificate(byte[] certificate) {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		final var parser = new PEMParser(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(certificate))));
		try {
			return new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder) parser.readObject());
		} catch (CertificateException e) {
			throw new IllegalArgumentException("Failed to parse certificate or cannot find the BC provider", e);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to parse/read certificate at this path", e);
		}
	}

}
