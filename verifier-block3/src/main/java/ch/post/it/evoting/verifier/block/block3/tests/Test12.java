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
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import com.scytl.decrypt.DecryptVerifier;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
            for(File balloBox : ballotBoxes){
                List<BigInteger> products = Flux.fromIterable(Deserializer.fromCsv(balloBox, "decompressedVotes\\.csv", ";", tab -> {
                    BigInteger b1 = new BigInteger(tab[0]);
                    BigInteger b2 = new BigInteger(tab[1]);
                    BigInteger b3 = new BigInteger(tab[2]);
                    BigInteger b4 = new BigInteger(tab[3]);
                    BigInteger b5 = new BigInteger(tab[4]);
                    return (b1.multiply(b2).multiply(b3).multiply(b4).multiply(b5));
                })).collectList().block();

                int size = products.size();

            }
            

            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof TestFailureException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.nok.message", ((TestFailureException) e).getArgs()[1]));
            } else if(e instanceof RuntimeException){
                if(e.getCause() instanceof FileNotFoundException) {
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.file.not.found.message",  e.getCause().getLocalizedMessage()));
                }
            }
        }
        return result;
    }
}
