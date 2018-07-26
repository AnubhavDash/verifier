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
import ch.post.it.evoting.verifier.common.block.tools.JsonMapper;
import ch.post.it.evoting.verifier.common.block.tools.LanguageHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeHelper;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;

/**
 * Test04 of Block1, Step hasSufficientSize(p)
 */
public class Test04 extends Test {

    private static final Logger log = Logger.getLogger(Test04.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test04.description"));
        def.setId(4);
        def.setName("hasSufficientSize(p)");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            EncryptionParameters encryptionParameters = JsonMapper.mapFromJson(inputDirectory, "encryptionParameters.json", EncryptionParameters.class);
            String pString = encryptionParameters.getZpSubgroup().getP();
            BigInteger p = TypeHelper.base64ToBigInteger(pString);

            if (p.bitLength() >= 2048) {
                result.setStatus(Status.OK);
            } else {
                result.setStatus(Status.NOK);
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test04.nok.message"));
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if(e instanceof FileNotFoundException){
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test04.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
