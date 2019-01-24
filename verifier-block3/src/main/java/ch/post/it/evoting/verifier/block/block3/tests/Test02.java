package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.BGOfflineVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.TestType;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.AbstractMap;

public class Test02 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test02.class);
    private final BGOfflineVerificationProcessor processor = BGOfflineVerificationProcessor.getInstanceAndRegister(this);

    @Override
    public TestDefinition getTestDefinition() {

        TestDefinition testDefinition = new TestDefinition();
        testDefinition.setBlockId(3);
        testDefinition.setCategory(Category.COMPLETENESS);
        testDefinition.setId(2);
        testDefinition.setName("checkProductArgument");
        testDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test02.description"));

        return testDefinition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            processor.register(this);
            processor.executeProcess(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));

            AbstractMap.SimpleEntry<Status, String> status = processor.getStatus(TestType.ProductProof);
            result.setStatus(status.getKey());
            result.setMessage(TranslationHelper.getSameMessageMultiLanguage(status.getValue()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        } finally {
            processor.unregister(this);
        }
        return result;
    }
}
