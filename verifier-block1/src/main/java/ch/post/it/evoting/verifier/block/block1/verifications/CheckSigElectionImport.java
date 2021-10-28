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
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.nio.file.Files;
import java.nio.file.Path;

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

public class CheckSigElectionImport extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(1);
		def.setCategory(Category.AUTHENTICITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification75.description"));
		def.setId(75);
		def.setName("checkSigElectionImport");
		def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		def.addVerificationTrait(VerificationTrait.BLOCK_1);
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		final PathNode integrationPathNode = pathService.buildFromRootPath(StructureKey.INTEGRATION_CA, inputDirectoryPath);
		byte[] rootCertificate = Files.readAllBytes(integrationPathNode.getPath());

		final PathNode apImportPathNode = pathService.buildFromRootPath(StructureKey.AP_ELECTION_IMPORT, inputDirectoryPath);

		byte[] content = Files.readAllBytes(apImportPathNode.getPath());
		byte[] signature = Files.readAllBytes(apImportPathNode.getRelation(RelationType.P7));

		if (!SignatureChecker.verifyPKCS7(content, signature, rootCertificate)) {
			throw buildVerificationFailureException(
					"The signature verification of the file failed",
					Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
					"verification75.nok.message",
					apImportPathNode.getPath().toString()
			);
		}

		result.setStatus(Status.OK);
		return result;
	}
}
