/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
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
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.decrypt.DecryptVerifier;

import java.io.File;
import java.nio.file.Path;

public class CheckDecryptionProof extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition verificationDefinition = new VerificationDefinition();
        verificationDefinition.setBlockId(3);
        verificationDefinition.setCategory(Category.COMPLETENESS);
        verificationDefinition.setId(7);
        verificationDefinition.setName("checkDecryptionProof");
        verificationDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification07.description"));

        return verificationDefinition;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        File[] ballotBoxes = PathHelper.listDirectories(inputDirectoryPath.resolve(Block3VerificationSuite.PATH_BALLOTBOXES));
        for (File ballotBox : ballotBoxes) {
            Path ballotboxPath = ballotBox.toPath().resolve("0");
            OfflineDataLoader offlineDataLoader = new OfflineDataLoader();
            offlineDataLoader.setEncryptionParametersLoader(new OfflineEncryptionParametersLoader(inputDirectoryPath));
            offlineDataLoader.setPublicKeyLoader(new OfflinePublicKeyLoader(ballotboxPath));
            offlineDataLoader.setVoterWithProofLoader(new OfflineVoterWithProofLoader(ballotboxPath));

            int verificationResultCode = DecryptVerifier.verify(offlineDataLoader);
            if (verificationResultCode != 1 && verificationResultCode != -1) {
                throw buildVerificationFailureException(
                        "The verification failed",
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification07.nok.message",
                        ballotBox.getName()
                );
            }
        }

        result.setStatus(Status.OK);
        return result;
    }
}
