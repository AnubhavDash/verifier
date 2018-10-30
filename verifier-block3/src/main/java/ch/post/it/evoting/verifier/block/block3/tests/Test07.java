package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import com.scytl.decrypt.DecryptVerifier;

import java.io.File;

public class Test07 extends Test {
    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition result = new TestDefinition();
        //TODO
        return result;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));
            for (File ballotBox : ballotBoxes) {
                if (DecryptVerifier.verify(ballotBox.toPath()) != 1) {
                    throw new TestFailureException("TODO", ballotBox.getName());
                }
            }
            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);
        }
        return result;
    }
}
