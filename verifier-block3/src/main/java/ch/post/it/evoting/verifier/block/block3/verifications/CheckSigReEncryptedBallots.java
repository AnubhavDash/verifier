/**
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
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CheckSigReEncryptedBallots extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckSigReEncryptedBallots.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {

        VerificationDefinition verificationDefinition = new VerificationDefinition();
        verificationDefinition.setBlockId(3);
        verificationDefinition.setCategory(Category.AUTHENTICITY);
        verificationDefinition.setId(72);
        verificationDefinition.setName("checkSigReEncryptedBallots");
        verificationDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test72.description"));

        return verificationDefinition;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_ELECTION_SETUP);
            DataConfigEE dataConfigEE = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();
            List<File> rEncBallotsFiles = new ArrayList<>();
            ballotBoxes.forEach(ballotBox -> {
                String ballotBoxId = ballotBox.getId();
                try {
                    File[] rEncBallotsFolders = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_BALLOTBOXES).resolve(ballotBoxId));
                    for (File folder : rEncBallotsFolders) {
                        rEncBallotsFiles.add(PathHelper.getFile(folder, "reencryptedBallots.*\\.csv"));
                    }
                } catch (FileNotFoundException e) {
                    throw new VerificationFailureException("reencryptedBallots.csv not found", inputDirectory.getName(), ballotBoxId);
                }
            });

            byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath()
                            .resolve(Block3VerificationSuite.PATH_CERTIFICATES)
                            .resolve(Block3VerificationSuite.PATH_ADMINBOARD).toFile(),
                    ".*\\.pem").toPath());

            byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

            for (File rEncBallot : rEncBallotsFiles) {
                byte[] content = Files.readAllBytes(rEncBallot.toPath());
                byte[] signature = Files.readAllBytes(rEncBallot.toPath().getParent().resolve(rEncBallot.getName() + ".metadata"));

                if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
                    throw new VerificationFailureException(rEncBallot.getName());
                }
            }
            result.setStatus(Status.OK);

        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test72.nok.message"));
        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test72.file.not.found.message", e.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test72.file.not.found.message", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
