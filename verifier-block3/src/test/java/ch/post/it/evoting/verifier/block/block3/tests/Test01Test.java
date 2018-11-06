package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import com.scytl.decrypt.DecryptVerifier;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

public class Test01Test {

    @Test
    public void executeTestOK() {
        TestResult result = new Test01().executeTest(new File(getClass().getResource("/Test01/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    public void executeTestNOK() {
        TestResult result = new Test01().executeTest(new File(getClass().getResource("/Test01/NOK").getFile()));
        Assert.assertEquals(Status.NOK, result.getStatus());
    }

}