package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class Test01Test {

    @Test
    public void executeTest() {
        TestResult result = new Test01().executeTest(new File(getClass().getResource("/Test01/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }
}