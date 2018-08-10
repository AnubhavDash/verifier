/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * Class Test08Test.
 * This represents a unit test for the Test09 class
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test09Test {

    @Ignore
    @Test
    public void executeTestOK() {
        TestResult testResult = new Test09().executeTest(new File(getClass().getResource("/Test09/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        /*
        TestResult testResult = new Test09().executeTest(new File(getClass().getResource("/Test09/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
        */
    }

    @Test
    public void executeTestNOKFileNotFound() {
        /*
        TestResult testResult = new Test09().executeTest(new File(getClass().getResource("/Test09/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
        Assert.assertEquals(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test06.file.not.found.message"), testResult.getMessage());
        */
    }

}