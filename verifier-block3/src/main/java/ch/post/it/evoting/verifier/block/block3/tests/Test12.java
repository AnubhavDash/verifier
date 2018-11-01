package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Test12 extends Test {
    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition testDefinition = new TestDefinition();
        testDefinition.setBlockId(3);
        testDefinition.setCategory(Category.COMPLETENESS);
        testDefinition.setId(12);
        testDefinition.setName("checkDecryptionFactorization");
        testDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.description"));

        return testDefinition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));
            List<BigInteger> products = new ArrayList<>();
            for (File balloBox : ballotBoxes) {
                products.addAll(Flux.fromIterable(Deserializer.fromCsv(balloBox, "decompressedVotes\\.csv", ";", tab -> {
                    BigInteger bigInt = BigInteger.ONE;
                    for (int i = 0; i < tab.length; i++) {
                        bigInt = bigInt.multiply(new BigInteger(tab[i]));
                    }
                    return bigInt;
                })).collectList().block());
            }

            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof TestFailureException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.nok.message", ((TestFailureException) e).getArgs()[1]));
            } else if (e instanceof RuntimeException) {
                if (e.getCause() instanceof FileNotFoundException) {
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.file.not.found.message", e.getCause().getLocalizedMessage()));
                }
            }
        }
        return result;
    }
}
