package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.JsonMapper;
import ch.post.it.evoting.verifier.common.block.tools.LanguageHelper;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;

import java.io.File;
import java.io.IOException;

public class Test01 extends Test {

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test01isprimep.description"));
        def.setId(1);
        def.setName("isPrime(p)");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());

        try {
            EncryptionParameters encryptionParameters = JsonMapper.mapFromJson(inputDirectory, "encryptionParameters.json", EncryptionParameters.class);
            String p = encryptionParameters.getZpSubgroup().getP();

            result.setStatus(Status.OK);

        } catch (IOException e) {
            result.setStatus(Status.NOK);
            //TODO result.setMessage();
        }

        // businessTest here
        //	Check if p is a prime number
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
