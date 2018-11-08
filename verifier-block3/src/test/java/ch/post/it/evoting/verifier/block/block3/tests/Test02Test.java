package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test02Test {

    @Test
    public void executeTestOK() {
        TestResult result = new Test02().executeTest(new File(getClass().getResource("/Test02/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult result = new Test02().executeTest(new File(getClass().getResource("/Test02/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }
}