/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block4.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

/**
 * Class Test01Test
 * This represents a unit test for the Test01 class
 * @author lalandret
 * @version $$Revision$$
 */
public class Test01Test {

    @Ignore
    @Test
    public void executeTestOK() {
        TestResult testResult = new Test01().executeTest(new File(getClass().getResource("/Test01/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        /*

        TestResult testResult = new Test01().executeTest(new File(getClass().getResource("/Test01/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
         */
    }
}