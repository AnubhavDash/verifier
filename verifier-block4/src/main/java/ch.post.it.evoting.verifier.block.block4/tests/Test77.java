/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
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

                if (!SignatureChecker.verifyMetdata(content, signature, signCertificate, rootCA)) {
                    throw new TestFailureException(svv.getName());
                }
            }
            result.setStatus(Status.OK);

        } catch (Exception e) {
            if (e instanceof TestFailureException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test77.nok.message"));
            }else if (e instanceof NoSuchFileException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test77.file.not.found.message", ((NoSuchFileException)e ).getFile()));
            }
            else {
                LOGGER.error("unexpected error", e);
            }
            result.setStatus(Status.NOK);
        }
        return result;
    }

}
