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

public class CheckSigInvalidVotesDecryption extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(4);
		def.setCategory(Category.AUTHENTICITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification78.description"));
		def.setId(78);
		def.setName("checkSigInvalidVotesDecryption");
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		// Get the certificate used for signing.
		final PathNode adminCertPathNode = pathService.buildFromRootPath(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);
		byte[] signCertificate = Files.readAllBytes(adminCertPathNode.getPath());

		// Get root certificate
		PathNode rootCertificatePathNode = pathService.buildFromRootPath(StructureKey.TENANT_100, inputDirectoryPath);
		byte[] rootCertificate = Files.readAllBytes(rootCertificatePathNode.getPath());

		// Get all invalid votes decryption results
		PathNode invalidVotesDecryptionPathNode = pathService.buildFromRootPath(StructureKey.INVALID_VOTES_DECRYPT_RESULT, inputDirectoryPath);

		for (Path invalidVotesDecryptionPath : invalidVotesDecryptionPathNode.getRegexPaths()) {
			byte[] content = Files.readAllBytes(invalidVotesDecryptionPath);
			byte[] signature = Files.readAllBytes(invalidVotesDecryptionPathNode.getRelation(RelationType.METADATA, invalidVotesDecryptionPath));

			if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCertificate)) {
				throw buildVerificationFailureException(
						"The signature verification of the siv_[EE_alias].csv report failed",
						Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification78.nok.message"
				);
			}
		}

		result.setStatus(Status.OK);
		return result;
	}

}
