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
package ch.post.it.evoting.verifier.block.block4.verifications;

import java.nio.file.Files;
import java.nio.file.Path;

import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckSigEvotingDecrypt extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(4);
		def.setCategory(Category.AUTHENTICITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification72.description"));
		def.setId(72);
		def.setName("checkSigEvotingDecrypt");
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		// Get root certificate
		PathNode rootCertificatePathNode = pathService.buildFromRootPath(StructureKey.TENANT_100, inputDirectoryPath);
		byte[] rootCertificate = Files.readAllBytes(rootCertificatePathNode.getPath());

		// Get eVoting decrypt result and its signature
		PathNode eVotingDecryptXmlPathNode = pathService.buildFromRootPath(StructureKey.EVOTING_DECRYPT_RESULT, inputDirectoryPath);
		byte[] content = Files.readAllBytes(eVotingDecryptXmlPathNode.getPath());
		byte[] signature = Files.readAllBytes(eVotingDecryptXmlPathNode.getRelation(RelationType.P7));

		// Verify signature of the eVoting decrypt result
		if (!SignatureChecker.verifyPKCS7(content, signature, rootCertificate)) {
			throw buildVerificationFailureException(
					"The signature verification of the evoting-decrypt.xml failed",
					Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification72.nok.message"
			);
		}

		result.setStatus(Status.OK);
		return result;
	}

}
