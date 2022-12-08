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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Predicate.not;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.cryptoprimitives.domain.election.ElectionEventContext;
import ch.post.it.evoting.cryptoprimitives.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.cryptoprimitives.domain.signature.Alias;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;

import jakarta.xml.bind.DatatypeConverter;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

@Service
public class DatasetService {

	public static final String SETUP_CONFIGURATION_ANONYMIZED_XML = "setup/configuration-anonymized.xml";
	public static final String SETUP_ELECTION_EVENT_CONTEXT_PAYLOAD_JSON = "setup/electionEventContextPayload.json";
	private final ObjectMapper objectMapper;
	private final DirectoryService directoryService;
	private final XmlFileRepository<Configuration> configurationXmlFileRepository;

	@Value("${direct.trust.keystore.type}")
	private String keyStoreType;

	@Value("${direct.trust.keystore.location}")
	private String keyStoreLocation;

	@Value("${direct.trust.keystore.password.location}")
	private String keyStorePasswordLocation;

	public DatasetService(final ObjectMapper objectMapper, final DirectoryService directoryService,
			final XmlFileRepository<Configuration> configurationXmlFileRepository) {
		this.objectMapper = objectMapper;
		this.directoryService = directoryService;
		this.configurationXmlFileRepository = configurationXmlFileRepository;
	}

	public Dataset unpack(final Dataset dataset) throws IOException {
		checkNotNull(dataset, "The dataset must be not null");

		if (dataset.getUnpackFolder().isPresent()) {
			return dataset;
		}

		final Path tempDirectory = directoryService.createSecuredTemporaryDirectory("verifier-dataset");
		try (final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(dataset.getZip()))) {
			LocalFileHeader entry;
			boolean hasEntry = false;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					hasEntry = true;
					final Path fileLocation = tempDirectory.resolve(entry.getFileName());
					if (!Files.exists(fileLocation.getParent())) {
						Files.createDirectories(fileLocation.getParent());
					}
					final byte[] buffer = new byte[1024];
					try (final FileOutputStream fos = new FileOutputStream(fileLocation.toFile())) {
						int len;
						while ((len = zis.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
					}
				}
			}
			if (!hasEntry) {
				throw new InvalidParameterException("input is not a ZIP file or is empty.");
			}
		}
		dataset.setUnpackFolder(tempDirectory);
		return dataset;
	}

	public void clean(final Dataset dataset) {
		checkNotNull(dataset);

		dataset.getUnpackFolder().ifPresent(unpackFolder -> {
			directoryService.deleteTemporaryDirectory(unpackFolder);
			dataset.removeUnpackFolder();
		});
	}

	public ElectionEventContext extractElectionEventContext(final Dataset dataset) throws DatasetExtractionException {
		checkNotNull(dataset);

		try (final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(dataset.getZip()))) {
			LocalFileHeader entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory() && SETUP_ELECTION_EVENT_CONTEXT_PAYLOAD_JSON.equals(entry.getFileName())) {
					return objectMapper.readValue(zis, ElectionEventContextPayload.class).getElectionEventContext();
				}
			}
		} catch (final IOException e) {
			throw new UncheckedIOException("Failed to open zip.", e);
		}
		throw new DatasetExtractionException("Failed to find election event context payload in zip file.");
	}

	public Configuration extractConfiguration(final Dataset dataset) throws DatasetExtractionException {
		checkNotNull(dataset);

		try (final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(dataset.getZip()))) {
			LocalFileHeader entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory() && SETUP_CONFIGURATION_ANONYMIZED_XML.equals(entry.getFileName())) {
					return configurationXmlFileRepository.read(zis, XsdConstants.CANTON_CONFIG_XSD,
							Configuration.class);
				}
			}
		} catch (final IOException e) {
			throw new UncheckedIOException("Failed to open zip.", e);
		}
		throw new DatasetExtractionException("Failed to find configuration in zip file.");
	}

	/**
	 * Extracts the fingerprints for the direct trust certificates, except the voting server one.
	 *
	 * @return a map with the alias names as keys and fingerprints as values.
	 */
	public Map<String, String> extractFingerprints() {
		final Path keyStorePasswordPath = Paths.get(keyStorePasswordLocation);
		final char[] password;
		try {
			password = Files.readString(keyStorePasswordPath).toCharArray();
		} catch (final IOException e) {
			throw new UncheckedIOException(String.format("Failed to read keystore password. [path: %s]", keyStorePasswordPath), e);
		}

		final KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance(keyStoreType);
		} catch (final KeyStoreException e) {
			throw new IllegalArgumentException(String.format("Failed to get keystore instance. [type: %s]", keyStoreType), e);
		}

		final Path keyStorePath = Paths.get(keyStoreLocation);
		try (final InputStream keyStoreInputStream = Files.newInputStream(keyStorePath)) {
			keyStore.load(keyStoreInputStream, password);
		} catch (final IOException | NoSuchAlgorithmException | CertificateException e) {
			throw new IllegalStateException(String.format("Failed to load keystore. [path: %s]", keyStorePath), e);
		}

		return Arrays.stream(Alias.values())
				.filter(not(Alias.VOTING_SERVER::equals))
				.map(Alias::get)
				.collect(Collectors.toMap(
						Function.identity(),
						aliasName -> {
							final Certificate certificate;
							final byte[] encodedCertificate;
							try {
								certificate = keyStore.getCertificate(aliasName);
								encodedCertificate = certificate.getEncoded();
							} catch (final KeyStoreException | CertificateEncodingException e) {
								throw new IllegalStateException("Failed to get encoded certificate.", e);
							}

							final MessageDigest messageDigest;
							try {
								messageDigest = MessageDigest.getInstance("SHA-256");
							} catch (final NoSuchAlgorithmException e) {
								throw new IllegalArgumentException("Failed to get message digest instance.", e);
							}
							final byte[] digest = messageDigest.digest(encodedCertificate);
							final String sha256Fingerprint = DatatypeConverter.printHexBinary(digest);

							return String.join(":", sha256Fingerprint.split("(?<=\\G.{2})"));
						}));
	}

}
