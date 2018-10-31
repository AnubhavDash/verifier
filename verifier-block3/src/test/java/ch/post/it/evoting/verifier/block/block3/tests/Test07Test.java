package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

public class Test07Test {

    @Test
    public void executeTestOK() {
        TestResult result = new Test07().executeTest(new File(getClass().getResource("/Test07/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestOKFileNotFound() {
        TestResult result = new Test07().executeTest(new File(getClass().getResource("/Test07/NOK-NOTFILE").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

}