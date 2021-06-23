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
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckSigCodesMappingTablesContextData extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(1);
		def.setCategory(Category.AUTHENTICITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification77.description"));
		def.setId(77);
		def.setName("checkSigCodesMappingTablesContextData");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		def.addVerificationTrait(VerificationTrait.BLOCK_1);
		return def;
	}

	@Override
	public VerificationResult verify(final Path inputDirectoryPath) throws IOException {
		final VerificationResult result = new VerificationResult();

		// Get the certificate used for signing.
		final PathNode adminBoardCertPathNode = pathService.buildFromRootPath(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);
		final byte[] signingCertificate = Files.readAllBytes(adminBoardCertPathNode.getPath());

		// Get the intermediate certificates.
		final PathNode tenantPathNode = pathService.buildFromRootPath(StructureKey.TENANT_100, inputDirectoryPath);
		final byte[][] intermediateCertificates = new byte[][] { Files.readAllBytes(tenantPathNode.getPath()) };

		// Get the root certificate.
		final PathNode platformRootPathNode = pathService.buildFromRootPath(StructureKey.PLATFORM_ROOT_CA, inputDirectoryPath);
		final byte[] rootCertificate = Files.readAllBytes(platformRootPathNode.getPath());

		// Get directory where files to check signature are located.
		final PathNode verifCardSetIdPathNode = pathService.buildFromRootPath(StructureKey.VERIFICATION_CARD_SET_ID_DIR, inputDirectoryPath);

		// Iterate over all directories and do the verification for each codesMappingTablesContextData.
		for (final Path regexPath : verifCardSetIdPathNode.getRegexPaths()) {
			final PathNode pathNode = pathService.buildFromDynamicAncestorPath(StructureKey.CODES_MAPPING_TABLES_CONTEXT_DATA, regexPath);

			for (final Path codesMappingTablesContextDataPath : pathNode.getRegexPaths()) {

				// Get source.
				final byte[] source = Files.readAllBytes(codesMappingTablesContextDataPath);

				// Get and decode the signature.
				final byte[] signatureBase64 = Files.readAllBytes(pathNode.getRelation(RelationType.SIGN, codesMappingTablesContextDataPath));
				final byte[] signature = Base64.getDecoder().decode(signatureBase64);

				// Check signatures.
				if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates,
						rootCertificate)) {
					throw buildVerificationFailureException(
							"The signature verification of the file failed",
							Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification77.nok.message",
							regexPath.toString()
					);
				}
			}
		}

		result.setStatus(Status.OK);
		return result;
	}
}
