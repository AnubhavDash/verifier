package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test27Test {

    @Test
    public void executeTestOK() {
        TestResult result = new Test27().executeTest(new File(getClass().getResource("/Test27/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult result = new Test27().executeTest(new File(getClass().getResource("/Test27/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

    @Test
    public void executeTestNOKNotFile() {
        TestResult result = new Test27().executeTest(new File(getClass().getResource("/Test27/NOK-NOTFILE").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

}