package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test22Test {

    @Test
    public void executeTestOK() {
        TestResult result = new Test22().executeTest(new File(getClass().getResource("/Test22/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult result = new Test22().executeTest(new File(getClass().getResource("/Test22/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }
}