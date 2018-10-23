package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class Test08Test {

    @Test
    public void executeTestOK() {
        TestResult testResult = new Test08().executeTest(new File(getClass().getResource("/Test08/OK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.OK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKpIsNotUnique() {
        TestResult testResult = new Test08().executeTest(new File(getClass().getResource("/Test08/NOK/P-NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKEulerCriterionKo() {
        TestResult testResult = new Test08().executeTest(new File(getClass().getResource("/Test08/NOK/BIGINT-NOK").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
    }

    @Test
    public void executeTestNOKFileNotFound() {
        TestResult testResult = new Test08().executeTest(new File(getClass().getResource("/Test08/NOK-NOTFILE").getFile()));
        Assert.assertNotNull(testResult);
        Assert.assertEquals(Status.NOK, testResult.getStatus());
        Assert.assertEquals(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message"), testResult.getMessage());
    }

}