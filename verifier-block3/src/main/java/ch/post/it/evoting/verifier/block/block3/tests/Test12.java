package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineVoterWithProofLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                // decompressedVotes
                List<BigInteger> temp = Flux.fromIterable(Deserializer.fromCsv(balloBox, "decompressedVotes\\.csv", ";", tab -> {
                    BigInteger bigInt = BigInteger.ONE;
                    for (int i = 0; i < tab.length; i++) {
                        bigInt = bigInt.multiply(new BigInteger(tab[i]));
                    }
                    return bigInt;
                })).collectList().block();

                if (temp != null){
                    products.addAll(temp);
                } else {
                    throw new TestFailureException("error occurs while parsing data in decompressedVotes.csv");
                }

                // votes with proof
                OfflineVoterWithProofLoader offlineVoterWithProofLoader = new OfflineVoterWithProofLoader(balloBox.toPath().resolve("0"));
                List<GjosteenElGamalPlaintext> plaintexts = offlineVoterWithProofLoader.getPlaintexts();
                List<BigInteger> voterWithProofbigIntList = plaintexts.stream().map(plaintext -> plaintext.getValue(0).getValue()).collect(Collectors.toList());

                // finally to the check

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
