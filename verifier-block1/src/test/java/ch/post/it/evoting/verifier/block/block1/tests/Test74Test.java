package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test74Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test74().executeTest(new File(getClass().getResource("/Test74/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test74().executeTest(new File(getClass().getResource("/Test74/NOK/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKFileNotFound() {
        TestResult testResult = new Test74().executeTest(new File(getClass().getResource("/Test74/NOK/NOK-NOFILE").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }
}