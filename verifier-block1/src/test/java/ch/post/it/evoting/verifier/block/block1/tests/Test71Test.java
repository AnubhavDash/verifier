package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test71Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test71().executeTest(new File(getClass().getResource("/Test71/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKCertKo() {
        TestResult testResult = new Test71().executeTest(new File(getClass().getResource("/Test71/NOK/CERT-NOT-OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKXmlKo() {
        TestResult testResult = new Test71().executeTest(new File(getClass().getResource("/Test71/NOK/XML-NOT-OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKFileNotFound() {
        TestResult testResult = new Test71().executeTest(new File(getClass().getResource("/Test71/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }
}