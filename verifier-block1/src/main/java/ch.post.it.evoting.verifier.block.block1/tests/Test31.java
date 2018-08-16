/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.evoting.xmlns.config._3.Configuration;
import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.dto.CredentialDataElement;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Test31 of Block1, Step checkNumberCredentials()
 */
public class Test31 extends Test {

    private static final Logger log = Logger.getLogger(Test31.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.COMPLETENESS);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test31.description"));
        def.setId(31);
        def.setName("checkNumberCredentials()");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            // number of voters
            Path path = inputDirectory.toPath().resolve(Block1TestSuite.PATH_ELECTION_SETUP);
            Configuration configuration = Deserializer.fromXml(path.toFile(), "configuration-anonymized.xml", Configuration.class);
            int votersCount = configuration.getRegister().getVoter().size();

            // number of lines
            Path votingCardSetsPath = path.resolve(Block1TestSuite.PATH_VOTING_CARD_SETS);

            final int[] linesCount = {0};
            Stream.of(PathHelper.listDirectories(votingCardSetsPath)).forEach(f -> {
                try {
                    Iterable<CredentialDataElement> iterable = Deserializer.fromCsv(f, "credentialData\\.csv", Deserializer.toCredentialDataElement);
                    for (CredentialDataElement credentialDataElement : iterable) {
                        linesCount[0]++;
                    }
                } catch (IOException e) {
                    throw new Test31WrapperException(e);
                }
            });

            if (votersCount == linesCount[0]) {
                result.setStatus(Status.OK);
            } else {
                result.setStatus(Status.NOK);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test31.nok.message", "" + linesCount, "" + votersCount));
            }

        } catch (Exception e) {
            result.setStatus(Status.NOK);

            if (e instanceof Test31WrapperException) {
                e = (Exception) e.getCause();
            }

            if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test31.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }

    class Test31WrapperException extends RuntimeException {
        public Test31WrapperException(Exception e) {
            super(e);
        }
    }
}
