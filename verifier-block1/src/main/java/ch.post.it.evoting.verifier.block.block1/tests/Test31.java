/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.block.dto.CredentialDataElement;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

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
        def.setName("TODO");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());

        try {
            Iterable<CredentialDataElement> iterable = Deserializer.fromCsv(inputDirectory, "credentialData\\.csv", Deserializer.toCredentialDataElement);
            for (CredentialDataElement credentialDataElement : iterable) {

            }

        } catch (IOException e) {
            //TODO
            result.setStatus(Status.NOK);
        }


        return result;
    }
}
