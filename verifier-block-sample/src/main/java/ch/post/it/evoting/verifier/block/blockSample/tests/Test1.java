package ch.post.it.evoting.verifier.block.blockSample.tests;

import ch.post.it.evoting.verifier.block.blockSample.SampleTestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.LanguageHelper;

import java.io.File;

public class Test1 extends Test {
    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(255);
        def.setCategory(Category.COMPLETENESS);
        def.setDescription(LanguageHelper.getFromResourceBundle(SampleTestSuite.RESOURCE_BUNDLE_NAME, "test1.description"));
        def.setId(1);
        def.setName("test 1");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());

        //businessTest here
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        result.setStatus(Status.OK);
        return result;
    }
}
