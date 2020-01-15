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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class CheckSigDataConfig extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification74.description"));
        def.setId(74);
        def.setName("checkSigDataConfig");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath
                        .resolve(Block1VerificationSuite.PATH_CERTIFICATES)
                        .resolve(Block1VerificationSuite.PATH_ADMINBOARD).toFile(),
                ".*\\.pem").toPath());

        byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectoryPath.resolve(Block1VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

        File[] dataConfigFiles = PathHelper.getFiles(inputDirectoryPath.resolve(Block1VerificationSuite.PATH_ELECTION_SETUP).toFile(), "dataConfig.*\\.json");

        for (File dataConfig : dataConfigFiles) {
            byte[] content = Files.readAllBytes(inputDirectoryPath.resolve(Block1VerificationSuite.PATH_ELECTION_SETUP).resolve(dataConfig.getName()));
            byte[] signature = Files.readAllBytes(inputDirectoryPath.resolve(Block1VerificationSuite.PATH_ELECTION_SETUP).resolve(dataConfig.getName() + ".metadata"));

            if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
                throw buildVerificationFailureException(
                        "The signature verification of the file dataConfig_updated failed",
                        Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification74.nok.message"
                );
            }
        }

        result.setStatus(Status.OK);
        return result;
    }
}
