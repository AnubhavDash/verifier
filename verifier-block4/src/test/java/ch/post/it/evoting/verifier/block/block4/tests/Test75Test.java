package ch.post.it.evoting.verifier.block.block4.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test75Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test75().executeTest(new File(getClass().getResource("/Test75/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKPdfKo() {
        TestResult testResult = new Test75().executeTest(new File(getClass().getResource("/Test75/NOK/PDF-NOT-OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKCertKo() {
        TestResult testResult = new Test75().executeTest(new File(getClass().getResource("/Test75/NOK/CERT-NOT-OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }
    @Test
    public void executeTestNOKFileNotFound() {
        TestResult testResult = new Test75().executeTest(new File(getClass().getResource("/Test75/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

}