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

public class CheckSigPdfReport extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(4);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification74.description"));
        def.setId(74);
        def.setName("checkSigPdfReport");
        def.setDeactivated(true);
        return def;
    }

    @Override
    public VerificationResult verify(File inputDirectory) throws Exception {
        VerificationResult result = new VerificationResult();

        byte[] rootCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

        File pdfFile = PathHelper.getFile(inputDirectory.toPath()
                        .resolve(Block4VerificationSuite.PATH_RESULTS)
                        .toFile(),
                ".*report.*\\.pdf");

        byte[] content = Files.readAllBytes(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_RESULTS).resolve(pdfFile.getName()));
        byte[] signature = Files.readAllBytes(inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_RESULTS).resolve(pdfFile.getName() + ".p7"));
        if (!SignatureChecker.verifyPKCS7(content, signature, rootCertificate)) {
            throw buildVerificationFailureException(
                    "The signature verification of PDF Report failed",
                    Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification74.nok.message"
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
