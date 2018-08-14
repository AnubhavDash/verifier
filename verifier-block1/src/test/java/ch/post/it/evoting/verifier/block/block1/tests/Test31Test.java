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
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Class Test31Test.
 * This represents a unit test for the Test31 class
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test31Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test31().executeTest(new File(getClass().getResource("/Test31/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test31().executeTest(new File(getClass().getResource("/Test31/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKFileNotFound() {
        TestResult testResult = new Test31().executeTest(new File(getClass().getResource("/Test31/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
        Assert.assertEquals(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test31.file.not.found.message"), testResult.getMessage());
    }

}