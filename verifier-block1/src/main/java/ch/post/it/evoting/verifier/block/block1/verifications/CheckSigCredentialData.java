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

public class CheckSigCredentialData extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(1);
		def.setCategory(Category.AUTHENTICITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification78.description"));
		def.setId(78);
		def.setName("checkSigCredentialData");
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

		final PathNode votingCardIdPathNode = pathService.buildFromRootPath(StructureKey.VOTING_CARD_SETS_ID_DIR, inputDirectoryPath);

		// Iterate over all directories and do the verification for each credentialData.
		for (final Path regexPath : votingCardIdPathNode.getRegexPaths()) {
			final PathNode credentialDataPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.CREDENTIAL_DATA, regexPath);

			for (final Path credentialDataPath : credentialDataPathNode.getRegexPaths()) {

				// Get source.
				final byte[] source = Files.readAllBytes(credentialDataPath);

				// Get and decode the signature.
				final byte[] signatureBase64 = Files.readAllBytes(credentialDataPathNode.getRelation(RelationType.SIGN, credentialDataPath));
				final byte[] signature = Base64.getDecoder().decode(signatureBase64);

				// Check signatures.
				if (!SignatureChecker.verifySignature(source, signature, signingCertificate, intermediateCertificates,
						rootCertificate)) {
					throw buildVerificationFailureException(
							"The signature verification of the file failed",
							Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
							"verification78.nok.message",
							regexPath.toString()
					);
				}
			}
		}

		result.setStatus(Status.OK);
		return result;
	}

}
