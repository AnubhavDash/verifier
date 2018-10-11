package ch.post.it.evoting.verifier.block.block4.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test03Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test03().executeTest(new File(getClass().getResource("/Test03/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test03().executeTest(new File(getClass().getResource("/Test03/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void executeTestOKWriteIns() {
        TestResult testResult = new Test03().executeTest(new File(getClass().getResource("/Test03/OK-WRITEINS").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKWriteIns() {
        TestResult testResult = new Test03().executeTest(new File(getClass().getResource("/Test03/NOK-WRITEINS").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }
}