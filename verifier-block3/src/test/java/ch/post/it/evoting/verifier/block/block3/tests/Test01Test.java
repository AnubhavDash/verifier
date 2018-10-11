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
    public void testBGVerifier() {
        TestResult result = new Test01().executeTest(new File(getClass().getResource("/Test01/OK").getFile()));
        Assert.assertEquals(Status.OK, result.getStatus());
    }

    @Test
    @Ignore
    public void testDecryptVerifier() {
        DecryptVerifier decryptVerifier = new DecryptVerifier();
        int result = decryptVerifier.verify(Paths.get("c:\\work\\projects\\verifier\\dataset\\input3\\ballotboxes\\514f7615916a42419c1c351a53dfa79e\\0"));
        Assert.assertEquals(1, result);
    }
}