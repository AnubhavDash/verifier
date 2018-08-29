package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class Test03Test {

    @Test
    public void executeTest() {
        //TestResult result = new Test03().executeTest(new File(getClass().getResource("/Test03/OK").getFile()));
        TestResult result = new Test03().executeTest(new File("c:\\work\\projects\\verifier\\dataset\\input1"));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    @Ignore
    public void executeTestNOK() {
        TestResult result = new Test03().executeTest(new File(getClass().getResource("/Test03/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }
}