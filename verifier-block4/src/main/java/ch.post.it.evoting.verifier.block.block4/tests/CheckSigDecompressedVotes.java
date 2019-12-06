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
package ch.post.it.evoting.verifier.block.block4.tests;

import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.Verification;
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

public class CheckSigDecompressedVotes extends Verification {

    private static final Logger LOGGER = Logger.getLogger(CheckSigDecompressedVotes.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(4);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test71.description"));
        def.setId(71);
        def.setName("checkSigDecompressedVotes");
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());

        try {
            Path path = inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_ELECTION_SETUP);
            DataConfigEE dataConfigEE = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();
            List<File> decompressedVotesFiles = new ArrayList<>(ballotBoxes.size());
            ballotBoxes.forEach(ballotBox -> {
                String ballotBoxId = ballotBox.getId();
                try {
                    decompressedVotesFiles.add(PathHelper.getFile(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_BALLOTBOXES).resolve(ballotBoxId).toFile(), "decompressedVotes.*\\.csv"));
                } catch (FileNotFoundException e) {
                    throw new VerificationFailureException("decompressedVotes.csv not found", inputDirectory.getName(), ballotBoxId);
                }
            });

            byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath()
                            .resolve(Block4VerificationSuite.PATH_CERTIFICATES)
                            .resolve(Block4VerificationSuite.PATH_ADMINBOARD).toFile(),
                    ".*\\.pem").toPath());

            byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

            for (File decompressedVote : decompressedVotesFiles) {
                byte[] content = Files.readAllBytes(decompressedVote.toPath());
                byte[] signature = Files.readAllBytes(decompressedVote.toPath().getParent().resolve(decompressedVote.getName() + ".metadata"));

                if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
                    throw new VerificationFailureException(decompressedVote.getName());
                }
            }
            result.setStatus(Status.OK);

        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test71.nok.message"));
        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test71.file.not.found.message", e.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test71.file.not.found.message", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

}
