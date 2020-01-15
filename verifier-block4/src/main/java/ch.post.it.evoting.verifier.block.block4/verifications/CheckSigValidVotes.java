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
package ch.post.it.evoting.verifier.block.block4.verifications;

import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

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

        byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath
                        .resolve(Block4VerificationSuite.PATH_CERTIFICATES)
                        .resolve(Block4VerificationSuite.PATH_ADMINBOARD).toFile(),
                ".*\\.pem").toPath());

        byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath.resolve(Block4VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

        File[] svvFiles = PathHelper.getFiles(inputDirectoryPath.resolve(Block4VerificationSuite.PATH_RESULTS).toFile(), "svv_.*\\.csv");

        for (File svv : svvFiles) {
            byte[] content = Files.readAllBytes(inputDirectoryPath.resolve(Block4VerificationSuite.PATH_RESULTS).resolve(svv.getName()));
            byte[] signature = Files.readAllBytes(inputDirectoryPath.resolve(Block4VerificationSuite.PATH_RESULTS).resolve(svv.getName() + ".metadata"));

            if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
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
