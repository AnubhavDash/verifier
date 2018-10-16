package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundle;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Test01Test {

    @Ignore
    @Test
    public void executeTestOK() {
        TestResult testResult = new Test01().executeTest(new File(getClass().getResource("/Test01/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Ignore
    @Test
    public void executeTestNOK() {
        TestResult testResult = new Test01().executeTest(new File(getClass().getResource("/Test01/NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void testReduceToBundle() throws IOException {
        Path path = new File(getClass().getResource("/Test01/OK/Evoting_CC_verifier_export_7d-json.json").getFile()).toPath();
        Stream<SecureLogEntry> logEntryStream = Files.lines(path).map(line -> {
            try {
                return SecureLogEntry.from(line);
            } catch (IOException e) {
                //TODO handle exception if deserialize failed
                e.printStackTrace();
                return null;
            }
        });

        /*Stream<SecureLogBundle> secureLogBundleStream = SecureLogBundleCreator.from(logEntryStream);
        Assert.assertEquals(59717, secureLogBundleStream.count());*/
        /*secureLogBundleStream.forEach(b -> {
            try {
                b.validate();
            } catch (SecureLogBundleValidationException e) {
                e.printStackTrace();
            }
        });*/
    }
}