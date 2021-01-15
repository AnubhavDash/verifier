/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.securelog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SecureLogBundleCertificates {
	private byte[] certificate;
	private byte[] intermediate;
	private byte[] root;

	public static Map<String, SecureLogBundleCertificates> loadAllHostsBundleCertificates(Path inputDirectoryPath) {
		try {
			// create host/CC mapping
			Map<String, String> hostCcMapping = HostMappingElement.loadHostMapping(inputDirectoryPath);

			// loading certificates
			File[] certificates = PathHelper
					.getFiles(inputDirectoryPath.resolve(Block2VerificationSuite.PATH_CC_LOG_SIGN_CERTIFICATES).toFile(), ".*cc.*_log_sign.pem");
			Map<String, byte[]> ccCertificateMapping = loadCertificates(certificates);

			File[] intermediates = PathHelper
					.getFiles(inputDirectoryPath.resolve(Block2VerificationSuite.PATH_CC_CA_CERTIFICATES).toFile(), ".*cc.*_CA.pem");
			Map<String, byte[]> ccIntermediateMapping = loadCertificates(intermediates);

			byte[] root = Files.readAllBytes(
					PathHelper.getFile(inputDirectoryPath.resolve(Block2VerificationSuite.PATH_CERTIFICATES).toFile(), "platformRootCA.pem")
							.toPath());

			return hostCcMapping.entrySet().stream()
					.map(entry -> {
						SecureLogBundleCertificates certs = new SecureLogBundleCertificates();
						certs.setCertificate(ccCertificateMapping.get(entry.getValue()));
						certs.setIntermediate(ccIntermediateMapping.get(entry.getValue()));
						certs.setRoot(root);
						return new AbstractMap.SimpleEntry<>(entry.getKey(), certs);
					}).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load certificates", e);
		}
	}

	private static Map<String, byte[]> loadCertificates(File[] certificates) {
		return Arrays.stream(certificates).map(f -> {
			try {
				return new AbstractMap.SimpleEntry<>(f.getName().substring(0, 3).toUpperCase(), Files.readAllBytes(f.toPath()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
	}
}
