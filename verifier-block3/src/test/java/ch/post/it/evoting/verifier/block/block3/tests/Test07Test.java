package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public class Test07Test {

    @Test
    public void executeTestOK() {
        //TODO change to test data
        TestResult result = new Test07().executeTest(Paths.get("c:\\work\\projects\\verifier\\dataset\\input5").toFile());
        Assert.assertEquals(Status.OK, result.getStatus());
    }
}