package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.data.ShuffleDataLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import com.scytl.products.ov.mixnet.proofs.bg.ShuffleProofVerifier;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class Test01 extends Test {

    private static Logger LOGGER = Logger.getLogger(Test01.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition definition = new TestDefinition();

        definition.setBlockId(3);
        definition.setId(1);
        definition.setCategory(Category.COMPLETENESS);
        definition.setName("checkShuffleArgument");
        definition.setDescription(null); //TODO set description

        return definition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Path ballotBoxesPath = inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES);
            final File[] files = Arrays.stream(Objects.requireNonNull(ballotBoxesPath.toFile().listFiles(File::isDirectory)))
                    .flatMap(d -> Arrays.stream(Objects.requireNonNull(d.listFiles(File::isDirectory))))
                    .toArray(File[]::new);

            for (File file : files) {
                ShuffleDataLoader data = new ShuffleDataLoader(file);

                LOGGER.debug("Ballots before mixing");
                if (data.getEncryptedBallots().getBallots().isEmpty()) {
                    LOGGER.info("0 ballots, nothing to mix!");
                    continue;
                }

                LOGGER.debug("Re-encrypted ballots");
                if (data.getReencryptedBallots().getBallots().isEmpty()) {
                    LOGGER.info("0 ballots reencrypted, no mixing performed!");
                    continue;
                }

                final ShuffleProofVerifier shuffleProofVerifier = new ShuffleProofVerifier(data.getCryptoSystem(), data.getCommitmentParams(),
                        data.getEncryptedBallotsCiphertext(), data.getReencryptedBallotsCiphertext());

                if (!shuffleProofVerifier.verifyProof(data.getShuffleProof().getInitialMessage(),
                        data.getShuffleProof().getFirstAnswer(), data.getShuffleProof().getSecondAnswer())) {
                    throw new TestFailureException();
                }
            }
            result.setStatus(Status.OK);
        } catch (Exception e) {
            if (e instanceof TestFailureException) {
                result.setStatus(Status.NOK);
            } else {
                LOGGER.error("Unexpected error", e);
                result.setStatus(Status.NOK);
            }
        }
        return result;
    }
}
