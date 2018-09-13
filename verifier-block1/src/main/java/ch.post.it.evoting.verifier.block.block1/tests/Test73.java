/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
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

/**
 * Test73 of Block1, Step checkSigConfig
 */
public class Test73 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test73.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test73.description"));
        def.setId(73);
        def.setName("checkSigConfig");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            byte[] rootCertificate = Files.readAllBytes(inputDirectory.toPath().resolve(Block1TestSuite.PATH_CERTIFICATES).resolve("integrationCA.pem"));

            File dataConfig = PathHelper.getFile(inputDirectory.toPath()
                            .resolve(Block1TestSuite.PATH_ELECTION_SETUP)
                            .toFile(),
                    ".*configuration-anonymized.*\\.xml");

                byte[] content = Files.readAllBytes(inputDirectory.toPath().resolve(Block1TestSuite.PATH_ELECTION_SETUP).resolve(dataConfig.getName()));
                byte[] signature = Files.readAllBytes(inputDirectory.toPath().resolve(Block1TestSuite.PATH_ELECTION_SETUP).resolve(dataConfig.getName() + ".p7"));
                if (!SignatureChecker.verifyPKCS7(content, signature, rootCertificate)) {
                    throw new TestFailureException(dataConfig.getName());
                }
            result.setStatus(Status.OK);

        } catch (Exception e) {
            if (e instanceof TestFailureException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test73.nok.message", ((TestFailureException) e).getArgs()));
            } else if (e instanceof NoSuchFileException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test73.file.not.found.message", ((NoSuchFileException)e ).getFile()));
            }
            else {
                LOGGER.error("unexpected error", e);
            }
            result.setStatus(Status.NOK);
        }
        return result;
    }
}
