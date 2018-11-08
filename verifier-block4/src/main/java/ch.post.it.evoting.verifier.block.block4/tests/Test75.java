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
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class Test75 /*extends Test*/ {

    private static final Logger LOGGER = Logger.getLogger(Test75.class);

    /*@Override*/
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(4);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test75.description"));
        def.setId(75);
        def.setName("checkSigBulletinParBulletin");
        return def;
    }

    /*@Override*/
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            byte[] rootCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block4TestSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

            File pdfFile = PathHelper.getFile(inputDirectory.toPath()
                            .resolve(Block4TestSuite.PATH_RESULTS)
                            .toFile(),
                    ".*ballot*.*\\.pdf");

            byte[] content = Files.readAllBytes(inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS).resolve(pdfFile.getName()));
            byte[] signature = Files.readAllBytes(inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS).resolve(pdfFile.getName() + ".p7"));
            if (!SignatureChecker.verifyPKCS7(content, signature, rootCertificate)) {
                throw new TestFailureException(pdfFile.getName());
            }
            result.setStatus(Status.OK);

        } catch (Exception e) {
            if (e instanceof TestFailureException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test75.nok.message", ((TestFailureException) e).getArgs()));
            } else if (e instanceof NoSuchFileException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test75.file.not.found.message", ((NoSuchFileException) e).getFile()));
            } else if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test75.file.not.found.message", e.getMessage()));
            } else {
                LOGGER.error("unexpected error", e);
            }
            result.setStatus(Status.NOK);
        }
        return result;
    }

}
