/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test12Test {

    @Test
    public void executeTestOK() {
        TestResult result = new Test12().executeTest(new File(getClass().getResource("/Test12/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }
}