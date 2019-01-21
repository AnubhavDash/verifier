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
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class Test12 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test12.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition testDefinition = new TestDefinition();
        testDefinition.setBlockId(3);
        testDefinition.setCategory(Category.CONSISTENCY);
        testDefinition.setId(12);
        testDefinition.setName("checkDecryptionFactorization");
        testDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test12.description"));

        return testDefinition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));
            for (File balloBox : ballotBoxes) {
                // decompressedVotes
                List<BigInteger> decompVotesbigIntList = Flux.fromIterable(Deserializer.fromCsv(balloBox, "decompressedVotes\\.csv", ";", tab -> {
                    BigInteger bigInt = BigInteger.ONE;
                    for (int i = 0; i < tab.length; i++) {
                        // ignore write ins
                        if (!tab[i].contains("#")) {
                            bigInt = bigInt.multiply(new BigInteger(tab[i]));
                        }
                    }
                    return bigInt;
                })).collectList().block();

                if (decompVotesbigIntList == null) {
                    throw new TestFailureException("error occurs while parsing data in decompressedVotes.csv");
                }

                // votes with proof
                OfflineVoterWithProofLoader offlineVoterWithProofLoader = new OfflineVoterWithProofLoader(balloBox.toPath().resolve("0"));
                List<BigInteger> voterWithProofbigIntList = offlineVoterWithProofLoader.getPlaintexts()
                        .stream()
                        .map(plaintext -> plaintext.getValue(0).getValue())
                        .collect(Collectors.toList());

                if (voterWithProofbigIntList == null) {
                    throw new TestFailureException("error occurs while parsing data in voterWithProofbigIntList.csv", balloBox.getName());
                }

                // finally to the check
                if (decompVotesbigIntList.size() != voterWithProofbigIntList.size()) {
                    throw new TestFailureException("factorization not correct !", decompVotesbigIntList.toString());
                }

                Boolean allMatch = Flux.fromIterable(decompVotesbigIntList)
                        .zipWith(Flux.fromIterable(voterWithProofbigIntList))
                        .all(t -> t.getT1().equals(t.getT2()))
                        .block();

                if (!allMatch) {
                    throw new TestFailureException("factorization not correct !", decompVotesbigIntList.toString());
                }
            }
            result.setStatus(Status.OK);
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test12.nok.message", e.getArgs()[1]));
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e.getCause() instanceof FileNotFoundException) {
                LOGGER.error("a FileNotFoundException error occurred", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test12.file.not.found.message", e.getCause().getLocalizedMessage()));
            } else {
                LOGGER.error("an unexpected error occurred", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
