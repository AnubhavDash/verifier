package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class Test07Test {

    @Test
    public void executeTest() {
        TestResult testResult = new Test07().executeTest(new File(getClass().getResource("/Test07/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());}
}