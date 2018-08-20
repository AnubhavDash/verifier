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
 * Class Test72Test.
 * This represents a unit test for the Test72 class
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test72Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test72().executeTest(new File(getClass().getResource("/Test72/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NA, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test72().executeTest(new File(getClass().getResource("/Test72/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NA, testResult.getStatus());
    }


}