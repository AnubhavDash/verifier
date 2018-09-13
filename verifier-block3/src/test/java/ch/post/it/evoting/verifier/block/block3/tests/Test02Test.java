package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class Test02Test {

    @Test
    public void executeTest() {
        TestResult result = new Test02().executeTest(new File(getClass().getResource("/Test02/OK").getFile()));
        //TestResult result = new Test02().executeTest(new File("c:\\work\\projects\\verifier\\dataset\\input2"));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult result = new Test02().executeTest(new File(getClass().getResource("/Test02/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }
}