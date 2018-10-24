package ch.post.it.evoting.verifier.block.block4.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test02Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test02().executeTest(new File(getClass().getResource("/Test02/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test02().executeTest(new File(getClass().getResource("/Test02/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }
}