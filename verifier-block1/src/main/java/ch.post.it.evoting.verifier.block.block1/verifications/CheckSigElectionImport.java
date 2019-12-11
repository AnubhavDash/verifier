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
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class CheckSigElectionImport extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckSigElectionImport.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification75.description"));
        def.setId(75);
        def.setName("checkSigElectionImport");
        def.addVerificationTrait(VerificationTrait.PreDecryption);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());

        try {
            byte[] rootCertificate = Files.readAllBytes(inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_CERTIFICATES).resolve("integrationCA.pem"));

            File electionImport = PathHelper.getFile(inputDirectory.toPath()
                            .resolve(Block1VerificationSuite.PATH_ELECTION_SETUP)
                            .toFile(),
                    "AP_election_import_.*\\.json");

            byte[] content = Files.readAllBytes(inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_ELECTION_SETUP).resolve(electionImport.getName()));
            byte[] signature = Files.readAllBytes(inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_ELECTION_SETUP).resolve(electionImport.getName() + ".p7"));
            if (!SignatureChecker.verifyPKCS7(content, signature, rootCertificate)) {
                throw new VerificationFailureException(electionImport.getName());
            }
            result.setStatus(Status.OK);

        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification75.nok.message"));
        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification75.file.not.found.message", e.getFile()));
        } catch (Exception e) {
            LOGGER.error("unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
