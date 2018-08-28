package ch.post.it.evoting.verifier.block.block4.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class Test04Test {

    @Test
    @Ignore
    public void executeTestOK() {
        TestResult testResult = new Test04().executeTest(new File(getClass().getResource("/Test04/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOK() {
        TestResult testResult = new Test04().executeTest(new File(getClass().getResource("/Test04/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }
}