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
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineEncryptionParametersLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflinePublicKeyLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineVoterWithProofLoader;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OfflineDataLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.Verification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.decrypt.DecryptVerifier;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class CheckDecryptionProof extends Verification {

    private static final Logger LOGGER = Logger.getLogger(CheckDecryptionProof.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition verificationDefinition = new VerificationDefinition();
        verificationDefinition.setBlockId(3);
        verificationDefinition.setCategory(Category.COMPLETENESS);
        verificationDefinition.setId(7);
        verificationDefinition.setName("checkDecryptionProof");
        verificationDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test07.description"));

        return verificationDefinition;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_BALLOTBOXES));
            for (File ballotBox : ballotBoxes) {
                Path ballotboxPath = ballotBox.toPath().resolve("0");
                OfflineDataLoader offlineDataLoader = new OfflineDataLoader();
                offlineDataLoader.setEncryptionParametersLoader(new OfflineEncryptionParametersLoader(inputDirectory.toPath()));
                offlineDataLoader.setPublicKeyLoader(new OfflinePublicKeyLoader(ballotboxPath));
                offlineDataLoader.setVoterWithProofLoader(new OfflineVoterWithProofLoader(ballotboxPath));

                int verificationResultCode = DecryptVerifier.verify(offlineDataLoader);
                if (verificationResultCode != 1 && verificationResultCode != -1) {
                    throw new VerificationFailureException("The verification failed", ballotBox.getName());
                }
            }
            result.setStatus(Status.OK);
        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test07.nok.message", e.getArgs()[1]));
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e.getCause() instanceof FileNotFoundException) {
                LOGGER.error("a FileNotFoundException error occurred", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "test07.file.not.found.message", e.getCause().getLocalizedMessage()));
            } else {
                LOGGER.error("an unexpected error occurred", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
