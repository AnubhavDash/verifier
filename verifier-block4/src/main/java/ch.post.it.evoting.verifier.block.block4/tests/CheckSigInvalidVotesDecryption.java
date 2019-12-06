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
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class CheckSigInvalidVotesDecryption extends Verification {

    private static final Logger LOGGER = Logger.getLogger(CheckSigInvalidVotesDecryption.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(4);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test78.description"));
        def.setId(78);
        def.setName("checkSigInvalidVotesDecryption");
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {

            byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath()
                            .resolve(Block4VerificationSuite.PATH_CERTIFICATES)
                            .resolve(Block4VerificationSuite.PATH_ADMINBOARD).toFile(),
                    ".*\\.pem").toPath());

            byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

            File[] sivDecryFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_RESULTS).toFile(), "siv_decryption_.*\\.csv");

            for (File siv : sivDecryFiles) {
                byte[] content = Files.readAllBytes(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_RESULTS).resolve(siv.getName()));
                byte[] signature = Files.readAllBytes(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_RESULTS).resolve(siv.getName() + ".metadata"));

                if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
                    throw new VerificationFailureException(siv.getName());
                }
            }
            result.setStatus(Status.OK);

        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test78.nok.message"));

        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test78.file.not.found.message", e.getFile()));

        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

}
