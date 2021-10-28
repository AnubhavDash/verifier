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
import java.util.ArrayList;
import java.util.List;

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

public class CheckSigDecompressedVotes extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition def = new VerificationDefinition();
		def.setBlockId(4);
		def.setCategory(Category.AUTHENTICITY);
		def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification71.description"));
		def.setId(71);
		def.setName("checkSigDecompressedVotes");
		return def;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		// Get signed certificate
		PathNode adminBoardCertPathNode = pathService.buildFromRootPath(StructureKey.ADMIN_BOARD_CERT, inputDirectoryPath);
		byte[] signCertificate = Files.readAllBytes(adminBoardCertPathNode.getPath());

		// Get root certificate
		PathNode rootCAPathNode = pathService.buildFromRootPath(StructureKey.TENANT_100, inputDirectoryPath);
		byte[] rootCA = Files.readAllBytes(rootCAPathNode.getPath());

		// Collect all the decompressed votes
		List<PathNode> decompressedVotesPathNodes = new ArrayList<>();
		PathNode ballotBoxIdDirectoriesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		for (Path ballotBoxIdDirectoryPath : ballotBoxIdDirectoriesPathNode.getRegexPaths()) {
			decompressedVotesPathNodes.add(pathService.buildFromDynamicAncestorPath(StructureKey.DECOMPRESSED_VOTES, ballotBoxIdDirectoryPath));
		}

		// Verify signature of each decompressed votes
		for (PathNode decompressedVotePathNode : decompressedVotesPathNodes) {
			byte[] content = Files.readAllBytes(decompressedVotePathNode.getPath());
			byte[] signature = Files.readAllBytes(decompressedVotePathNode.getRelation(RelationType.METADATA));

			if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
				throw buildVerificationFailureException(
						"The signature verification of the decompressedVotes.csv failed",
						Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification71.nok.message"
				);
			}
		}

		result.setStatus(Status.OK);
		return result;
	}

}
