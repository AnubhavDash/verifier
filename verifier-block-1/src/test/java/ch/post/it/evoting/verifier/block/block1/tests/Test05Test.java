/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.tools.LanguageHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Class Test05Test.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test05Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test05().executeTest(new File(getClass().getResource("/Test05/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        // TODO
        /*
        TestResult testResult = new Test05().executeTest(new File(getClass().getResource("/Test05/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
        Assert.assertEquals(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test05.nok.message"), testResult.getMessage());
         */
    }

    @Test
    public void executeTestNOKFileNotFound() {
        TestResult testResult = new Test05().executeTest(new File(getClass().getResource("/Test05/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
        Assert.assertEquals(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test05.file.not.found.message"), testResult.getMessage());
    }

}