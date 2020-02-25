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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
