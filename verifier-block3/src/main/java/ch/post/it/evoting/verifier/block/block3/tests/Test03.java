package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.data.ProductDataLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ProductProofMessage;
import com.scytl.products.ov.mixnet.proofs.bg.HadamardProductProofVerifier;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class Test03 extends Test {


    private static final Logger LOGGER = Logger.getLogger(Test03.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition definition = new TestDefinition();

        definition.setBlockId(3);
        definition.setId(3);
        definition.setCategory(Category.COMPLETENESS);
        definition.setName("checkHadamardArgument");
        definition.setDescription(null); //TODO
        return definition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Path ballotBoxesPath = inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES);
            final File[] mixingDirectories = Arrays.stream(Objects.requireNonNull(ballotBoxesPath.toFile().listFiles(File::isDirectory)))
                    .flatMap(d -> Arrays.stream(Objects.requireNonNull(d.listFiles(File::isDirectory))))
                    .toArray(File[]::new);

            for (File mixingDirectory : mixingDirectories) {
                ProductDataLoader data = new ProductDataLoader(mixingDirectory);

                ProductProofMessage msgPA = data.getShuffleData().getShuffleProof().getSecondAnswer().getMsgPA();

                final HadamardProductProofVerifier verifHA = new HadamardProductProofVerifier(
                        data.getShuffleData().getCommitmentParams(),
                        data.getCPA(),
                        msgPA.getCommitmentPublicB(),
                        data.getShuffleData().getCommitmentParams().getGroup().getOrder());

                if (!verifHA.verify(msgPA.getIniHPA(), msgPA.getAnsHPA())) {
                    throw new TestFailureException();
                }
            }
            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);
        }

        return result;
    }


}
