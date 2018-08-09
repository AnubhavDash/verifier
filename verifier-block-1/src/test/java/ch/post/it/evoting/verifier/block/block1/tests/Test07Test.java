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

/**
 * Class Test07Test.
 * This represents a unit test for the Test07 class
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test07Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test07().executeTest(new File(getClass().getResource("/Test07/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        /*
        TestResult testResult = new Test06().executeTest(new File(getClass().getResource("/Test06/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
        */
    }

    @Test
    public void executeTestNOKFileNotFound() {
        /*
        TestResult testResult = new Test06().executeTest(new File(getClass().getResource("/Test06/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
        Assert.assertEquals(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test06.file.not.found.message"), testResult.getMessage());
        */
    }

}