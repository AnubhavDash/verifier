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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.function.Predicate.not;

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

import jakarta.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.verifier.backend.tools.path.PathService;

import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

@Service
public class DatasetService {
	private final DirectoryService directoryService;
	private final PathService pathService;

	@Value("${direct.trust.keystore.type}")
	private String keyStoreType;

	@Value("${direct.trust.keystore.location}")
	private String keyStoreLocation;

	@Value("${direct.trust.keystore.password.location}")
	private String keyStorePasswordLocation;

	public DatasetService(final DirectoryService directoryService, final PathService pathService) {
		this.directoryService = directoryService;
		this.pathService = pathService;
	}

	public Dataset unpack(final Dataset dataset) throws IOException {
		checkNotNull(dataset, "The dataset must be not null.");

		if (dataset.isUnpacked()) {
			return dataset;
		}

		try (final InputStream datasetInputStream = dataset.newInputStream();
				final ZipInputStream zipInputStream = new ZipInputStream(datasetInputStream)) {
			LocalFileHeader entry;
			boolean hasEntry = false;
			while ((entry = zipInputStream.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					hasEntry = true;

					setActualDatasetType(dataset, entry.getFileName());
					final Path fileLocation = dataset.getUnpackFolder().resolve(entry.getFileName());

					if (!Files.exists(fileLocation.getParent())) {
						Files.createDirectories(fileLocation.getParent());
					}

					try (final FileOutputStream fileOutputStream = new FileOutputStream(fileLocation.toFile())) {
						zipInputStream.transferTo(fileOutputStream);
					}
				}
			}

			if (!hasEntry) {
				throw new InvalidParameterException("input is not a ZIP file or is empty.");
			}
		}

		checkState(dataset.getActualType() != null, "input is not a %s dataset.", dataset.getExpectedType());

		dataset.setUnpacked(true);

		return dataset;
	}

	public void clean(final Dataset dataset, final boolean deleteDirectory) {
		checkNotNull(dataset);

		final Path unpackFolder = dataset.getUnpackFolder();
		if (deleteDirectory) {
			directoryService.deleteDirectory(unpackFolder);
		}
		dataset.removeUnpackFolder();
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
				.filter(not(Alias.VOTING_SERVER::equals).and(not(Alias.PRINTING_COMPONENT::equals)).and(not(Alias.VERIFIER::equals)))
				.map(Alias::get)
				.collect(Collectors.toMap(
						Function.identity(),
						aliasName -> {
							final Certificate certificate;
							try {
								certificate = keyStore.getCertificate(aliasName);
							} catch (final KeyStoreException e) {
								throw new IllegalStateException(String.format("Failed to get certificate. [alias: %s]", aliasName), e);
							}

							checkState(certificate != null, "No certificate found for given alias. [alias: %s]", aliasName);

							final byte[] encodedCertificate;
							try {
								encodedCertificate = certificate.getEncoded();
							} catch (final CertificateEncodingException e) {
								throw new IllegalStateException(String.format("Failed to get encoded certificate. [alias: %s]", aliasName), e);
							}

							final MessageDigest messageDigest;
							try {
								messageDigest = MessageDigest.getInstance("SHA-256");
							} catch (final NoSuchAlgorithmException e) {
								throw new IllegalArgumentException("Failed to get message digest instance.", e);
							}
							final byte[] digest = messageDigest.digest(encodedCertificate);
							final String sha256Fingerprint = DatatypeConverter.printHexBinary(digest);

							return sha256Fingerprint.toLowerCase();
						}));
	}

	/**
	 * This method is called for each file in the uploaded zip. It ensures the file is in the whitelist of the expected {@link DatasetType}, i.e. the
	 * dataset type chosen to upload. The verifications
	 * {@link ch.post.it.evoting.verifier.backend.verifications.setup.completeness.VerifySetupCompleteness} and
	 * {@link ch.post.it.evoting.verifier.backend.verifications.tally.completeness.VerifyTallyCompleteness} ensure all the needed files are present in
	 * the uploaded datasets.
	 *
	 * @param dataset  the dataset information as a {@link Dataset}.
	 * @param fileName the file to verify.
	 * @throws IllegalStateException if
	 *                               <ul>
	 *                                   <li>the file does not belong to any dataset type.</li>
	 *                                   <li>the file belongs to a dataset type different from the expected one.</li>
	 *                               </ul>
	 */
	private void setActualDatasetType(final Dataset dataset, final String fileName) {
		final DatasetType actualType = Arrays.stream(DatasetType.values())
				.filter(datasetType -> datasetType.getStructureKeys().stream()
						.anyMatch(structureKey -> pathService.matchesStructureKey(structureKey, fileName)))
				.findAny()
				.orElseThrow(() -> new IllegalStateException(
						String.format("The dataset does not have the expected type. [expectedType: %s]", dataset.getExpectedType())));

		checkState(dataset.getActualType() == null || dataset.getActualType() == actualType,
				"The dataset does not have the expected type. [expectedType: %s]", dataset.getExpectedType());

		dataset.setActualType(actualType);
	}
}
