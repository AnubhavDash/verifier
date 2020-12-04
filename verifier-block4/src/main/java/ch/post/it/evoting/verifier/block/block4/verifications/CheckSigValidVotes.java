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
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.RelationType;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class CheckSigValidVotes extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(4);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification77.description"));
        def.setId(77);
        def.setName("checkSigValidVotes");
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

        // Get all invalid votes results
        PathNode validVotesPathNode = pathService.buildFromRootPath(StructureKey.VALID_VOTES_RESULT, inputDirectoryPath);

        // Verify signature of each file
        for (Path validVotesPath : validVotesPathNode.getRegexPaths()) {
            byte[] content = Files.readAllBytes(validVotesPath);
            byte[] signature = Files.readAllBytes(validVotesPathNode.getRelation(RelationType.METADATA, validVotesPath));

            if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCertificate)) {
                throw buildVerificationFailureException(
                        "The signature verification of the svv_[EE_alias].csv report failed",
                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification77.nok.message"
                );
            }
        }

        result.setStatus(Status.OK);
        return result;
    }

}
