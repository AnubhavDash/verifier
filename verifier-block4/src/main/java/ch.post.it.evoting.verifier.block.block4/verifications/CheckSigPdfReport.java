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
package ch.post.it.evoting.verifier.block.block4.verifications;

import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class CheckSigPdfReport /*extends Test*/ {

    private static final Logger LOGGER = Logger.getLogger(CheckSigPdfReport.class);

    /*@Override*/
    public VerificationDefinition getTestDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(4);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test74.description"));
        def.setId(74);
        def.setName("checkSigPdfReport");
        return def;
    }

    /*@Override*/
    public VerificationResult executeTest(File inputDirectory) {
        VerificationResult result = new VerificationResult(getTestDefinition());
        try {
            byte[] rootCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

            File pdfFile = PathHelper.getFile(inputDirectory.toPath()
                            .resolve(Block4VerificationSuite.PATH_RESULTS)
                            .toFile(),
                    ".*report.*\\.pdf");

            byte[] content = Files.readAllBytes(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_RESULTS).resolve(pdfFile.getName()));
            byte[] signature = Files.readAllBytes(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_RESULTS).resolve(pdfFile.getName() + ".p7"));
            if (!SignatureChecker.verifyPKCS7(content, signature, rootCertificate)) {
                throw new VerificationFailureException(pdfFile.getName());
            }
            result.setStatus(Status.OK);

        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test74.nok.message", e.getArgs()));
        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test74.file.not.found.message", e.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "test74.file.not.found.message", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
