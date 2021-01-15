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
package ch.post.it.evoting.verifier.block.block3.verifications;

import java.nio.file.Path;

import com.scytl.decrypt.DecryptVerifier;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineEncryptionParametersLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflinePublicKeyLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineVoterWithProofLoader;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OfflineDataLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

public class CheckDecryptionProof extends AbstractVerification {

	@Override
	public VerificationDefinition getVerificationDefinition() {
		VerificationDefinition verificationDefinition = new VerificationDefinition();
		verificationDefinition.setBlockId(3);
		verificationDefinition.setCategory(Category.COMPLETENESS);
		verificationDefinition.setId(7);
		verificationDefinition.setName("checkDecryptionProof");
		verificationDefinition
				.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification07.description"));

		return verificationDefinition;
	}

	@Override
	public VerificationResult verify(Path inputDirectoryPath) throws Exception {
		VerificationResult result = new VerificationResult();

		PathNode ballotBoxIdDirectoriesPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
		for (Path ballotBoxIdDirectoryPath : ballotBoxIdDirectoriesPathNode.getRegexPaths()) {

			// Get "0" directory
			PathNode ballotBoxOfflineDirectoriesPathNode = pathService
					.buildFromDynamicAncestorPath(StructureKey.BALLOT_BOX_OFFLINE_DIR, ballotBoxIdDirectoryPath);

			OfflineDataLoader offlineDataLoader = new OfflineDataLoader();
			offlineDataLoader.setEncryptionParametersLoader(new OfflineEncryptionParametersLoader(inputDirectoryPath));
			offlineDataLoader.setPublicKeyLoader(new OfflinePublicKeyLoader(ballotBoxOfflineDirectoriesPathNode.getPath()));
			offlineDataLoader.setVoterWithProofLoader(new OfflineVoterWithProofLoader(ballotBoxOfflineDirectoriesPathNode.getPath()));

			int verificationResultCode = DecryptVerifier.verify(offlineDataLoader);
			if (verificationResultCode != 1 && verificationResultCode != -1) {
				throw buildVerificationFailureException(
						"The verification failed",
						Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
						"verification07.nok.message",
						ballotBoxIdDirectoryPath.getFileName().toString()
				);
			}
		}

		result.setStatus(Status.OK);
		return result;
	}
}
