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
import ch.post.it.evoting.verifier.common.block.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.tools.*;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;

import java.io.File;
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

        Path path = inputDirectoryPath.resolve(Block4VerificationSuite.PATH_ELECTION_SETUP);
        ElectionEvent electionEvent = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", ElectionEvent.class);
        List<BallotBox> ballotBoxes = electionEvent.getBallotBoxes();
        List<File> decompressedVotesFiles = new ArrayList<>(ballotBoxes.size());
        for (BallotBox ballotBox : ballotBoxes) {
            String ballotBoxId = TypeConverter.UUIDToStringWithoutDash(ballotBox.getId());
            decompressedVotesFiles.add(PathHelper.getFile(inputDirectoryPath.resolve(Block4VerificationSuite.PATH_BALLOTBOXES).resolve(ballotBoxId).toFile(), "decompressedVotes.*\\.csv"));
        }

        byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath
                        .resolve(Block4VerificationSuite.PATH_CERTIFICATES)
                        .resolve(Block4VerificationSuite.PATH_ADMINBOARD).toFile(),
                ".*\\.pem").toPath());

        byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath.resolve(Block4VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

        for (File decompressedVote : decompressedVotesFiles) {
            byte[] content = Files.readAllBytes(decompressedVote.toPath());
            byte[] signature = Files.readAllBytes(decompressedVote.toPath().getParent().resolve(decompressedVote.getName() + ".metadata"));

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
