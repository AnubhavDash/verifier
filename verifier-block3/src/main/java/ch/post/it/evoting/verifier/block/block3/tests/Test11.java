/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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

                // Offline
                OfflineEncryptedBallotsLoader offlineEncryptedBallotsLoader = new OfflineEncryptedBallotsLoader(ballotbox.toPath().resolve("0"), inputDirectory.toPath());

                ElGamalEncryptedBallots offlineEncryptedBallots = offlineEncryptedBallotsLoader.getEncryptedBallots();

                final File[] onlineMixings = ballotbox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
                Iterable<File> iterableFile = Arrays.asList(onlineMixings);
                Iterator iteratorFile = iterableFile.iterator();


                while (iteratorFile.hasNext() && !isEquivalent) {
                    File online = (File) iteratorFile.next();

                    OnlineMixingProofLoader onlineMixingProofLoader = new OnlineMixingProofLoader(online.toPath());
                    ElGamalEncryptedBallots onlineEncryptedBallots = onlineMixingProofLoader.getVotes();

                    // Business check
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
        } catch (FileNotFoundException e) {
            result.setStatus(Status.NOK);
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test11.file.not.found.message", e.getCause().getLocalizedMessage()));
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            LOGGER.error("an unexpected error occurred", e);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

    private boolean isOfflineOnlineEncryptedBallotsEquals(List<ElGamalEncryptedBallot> offlineList, List<ElGamalEncryptedBallot> onlineList) {
        return offlineList.size() == onlineList.size() && Flux.fromIterable(offlineList).zipWith(Flux.fromIterable(onlineList)).all(t -> t.getT1().equals(t.getT2())).block();
    }
}
