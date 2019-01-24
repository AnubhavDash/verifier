package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.BGOnlineVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.TestType;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.AbstractMap;

public class Test24 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test24.class);
    private final BGOnlineVerificationProcessor processor = BGOnlineVerificationProcessor.getInstanceAndRegister(this);

    @Override
    public TestDefinition getTestDefinition() {

        TestDefinition def = new TestDefinition();
        def.setBlockId(3);
        def.setCategory(Category.COMPLETENESS);
        def.setId(24);
        def.setName("checkZeroArgumentOnline");
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test24.description"));
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            processor.register(this);
            processor.executeProcess(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));

            AbstractMap.SimpleEntry<Status, String> status = processor.getStatus(TestType.ZeroProof);
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
