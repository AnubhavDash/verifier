package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineEncryptedBallotsLoader;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Test11 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test11.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition testDefinition = new TestDefinition();
        testDefinition.setBlockId(3);
        testDefinition.setCategory(Category.COMPLETENESS);
        testDefinition.setId(11);
        testDefinition.setName("checkCiphertextConsistencyOfflineProofs");
        testDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test11.description"));

        return testDefinition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            File[] ballotboxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));

            for (File ballotbox : ballotboxes) {
                boolean isEquivalent = false;

                // OFFLINE
                OfflineEncryptedBallotsLoader offlineEncryptedBallotsLoader = new OfflineEncryptedBallotsLoader(ballotbox.toPath().resolve("0"), ballotbox.toPath().getParent().getParent());
                ElGamalEncryptedBallots offlineEncryptedBallots = offlineEncryptedBallotsLoader.getEncryptedBallots();

                // 3 ONLINES
                final File[] onlineMixings = ballotbox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
                Iterable<File> iterableFile = Arrays.asList(onlineMixings);
                Iterator iteratorFile = iterableFile.iterator();


                while (iteratorFile.hasNext() && !isEquivalent) {
                    File online = (File) iteratorFile.next();

                    //3 ONLINES
                    OnlineMixingProofLoader onlineMixingProofLoader = new OnlineMixingProofLoader(online.toPath());
                    ElGamalEncryptedBallots onlineEncryptedBallots = onlineMixingProofLoader.getVotes();

                    // CONTROL
                    if (isOfflineOnlineEncryptedBallotsEquals(offlineEncryptedBallots.getBallots(), onlineEncryptedBallots.getBallots())) {
                        isEquivalent = true;
                    }
                }

                if (!isEquivalent) {
                    throw new TestFailureException("Same vote not exist");
                }
            }
            result.setStatus(Status.OK);
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test11.nok.message"));
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e.getCause() instanceof FileNotFoundException) {
                LOGGER.error("a FileNotFoundException error occurred", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test11.file.not.found.message", e.getCause().getLocalizedMessage()));
            } else {
                LOGGER.error("an unexpected error occurred", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }

    private boolean isOfflineOnlineEncryptedBallotsEquals(List<ElGamalEncryptedBallot> offlineList, List<ElGamalEncryptedBallot> onelineList) {
        return offlineList.size() == onelineList.size() && Flux.fromIterable(offlineList).zipWith(Flux.fromIterable(onelineList)).all(t -> t.getT1().equals(t.getT2())).block();
    }
}
