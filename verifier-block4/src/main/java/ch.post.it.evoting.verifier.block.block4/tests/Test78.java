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

/**
 * Class Test78.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test78 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test78.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(4);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test78.description"));
        def.setId(78);
        def.setName("checkSigInvalidVotesDecryption");
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

            File[] sivDecryFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS).toFile(), "siv_decryption_*.*\\.csv");

            for (File siv : sivDecryFiles) {
                byte[] content = Files.readAllBytes(inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS).resolve(siv.getName()));
                byte[] signature = Files.readAllBytes(inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS).resolve(siv.getName() + ".metadata"));

                if (!SignatureChecker.verifyMetdata(content, signature, signCertificate, rootCA)) {
                    throw new TestFailureException(siv.getName());
                }
            }
            result.setStatus(Status.OK);

        } catch (Exception e) {
            if (e instanceof TestFailureException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test78.nok.message"));
            }else if (e instanceof NoSuchFileException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test78.file.not.found.message", ((NoSuchFileException)e ).getFile()));
            }
            else {
                LOGGER.error("unexpected error", e);
            }
            result.setStatus(Status.NOK);
        }
        return result;
    }

}
