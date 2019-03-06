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

import ch.post.it.evoting.verifier.block.block4.Block4TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class Test77 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test77.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(4);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test77.description"));
        def.setId(77);
        def.setName("checkSigValidVotes");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {

            byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath()
                            .resolve(Block4TestSuite.PATH_CERTIFICATES)
                            .resolve(Block4TestSuite.PATH_ADMINBOARD).toFile(),
                    ".*\\.pem").toPath());

            byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block4TestSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

            File[] svvFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS).toFile(), "svv_.*\\.csv");

            for (File svv : svvFiles) {
                byte[] content = Files.readAllBytes(inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS).resolve(svv.getName()));
                byte[] signature = Files.readAllBytes(inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS).resolve(svv.getName() + ".metadata"));

                if (!SignatureChecker.verifyMetadata(content, signature, signCertificate, rootCA)) {
                    throw new TestFailureException(svv.getName());
                }
            }
            result.setStatus(Status.OK);

        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test77.nok.message"));
        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test77.file.not.found.message", e.getFile()));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

}
