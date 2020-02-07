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

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.path.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CheckSigProofs extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {

        VerificationDefinition verificationDefinition = new VerificationDefinition();
        verificationDefinition.setBlockId(3);
        verificationDefinition.setCategory(Category.AUTHENTICITY);
        verificationDefinition.setId(71);
        verificationDefinition.setName("checkSigProofs");
        verificationDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification71.description"));

        return verificationDefinition;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        Path path = inputDirectoryPath.resolve(Block3VerificationSuite.PATH_ELECTION_SETUP);
        ElectionEvent electionEvent = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", ElectionEvent.class);
        List<BallotBox> ballotBoxes = electionEvent.getBallotBoxes();
        List<File> proofsFiles = new ArrayList<>();
        for (BallotBox ballotBox : ballotBoxes) {
            String ballotBoxId = ballotBox.getId().toString();
            File[] proofFolders = PathHelper.listDirectories(inputDirectoryPath.resolve(Block3VerificationSuite.PATH_BALLOTBOXES).resolve(ballotBoxId));
            for (File folder : proofFolders) {
                proofsFiles.add(PathHelper.getFile(folder, "proofs.*\\.json"));
            }
        }

        byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath
                        .resolve(Block3VerificationSuite.PATH_CERTIFICATES)
                        .resolve(Block3VerificationSuite.PATH_ADMINBOARD).toFile(),
                ".*\\.pem").toPath());

        byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath.resolve(Block3VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

        for (File proof : proofsFiles) {
            byte[] content = Files.readAllBytes(proof.toPath());
            byte[] signature = Files.readAllBytes(proof.toPath().getParent().resolve(proof.getName() + ".metadata"));

            if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
                throw buildVerificationFailureException(
                        "The signature verification of the proofs.json failed",
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification71.nok.message"
                );
            }
        }

        result.setStatus(Status.OK);
        return result;
    }
}
