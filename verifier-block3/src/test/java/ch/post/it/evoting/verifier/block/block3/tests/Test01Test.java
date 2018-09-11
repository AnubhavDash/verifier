package ch.post.it.evoting.verifier.block.block3.tests;

import com.scytl.products.ov.mixnet.BGVerifier;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

public class Test01Test {

    @Test
    public void executeTest() {
        //TestResult result = new Test01().executeTest(new File(getClass().getResource("/Test01/OK").getFile()));
        //Assert.assertEquals(Status.OK, result.getStatus());
        boolean result = BGVerifier.verify(Paths.get("c:\\work\\projects\\verifier\\dataset\\input3\\ballotboxes\\514f7615916a42419c1c351a53dfa79e"));
        Assert.assertTrue(result);
    }
}