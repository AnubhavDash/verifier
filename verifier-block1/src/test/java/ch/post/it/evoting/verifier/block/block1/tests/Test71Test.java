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
 * Class Test71Test.
 * This represents a unit test for the Test71 class
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test71Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test71().executeTest(new File(getClass().getResource("/Test71/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NA, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test71().executeTest(new File(getClass().getResource("/Test71/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NA, testResult.getStatus());
    }

}