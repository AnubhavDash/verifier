package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineEncryptionParametersLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflinePublicKeyLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineVoterWithProofLoader;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OfflineDataLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.decrypt.DecryptVerifier;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class Test07 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test07.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition testDefinition = new TestDefinition();
        testDefinition.setBlockId(3);
        testDefinition.setCategory(Category.COMPLETENESS);
        testDefinition.setId(7);
        testDefinition.setName("checkDecryptionProof");
        testDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.description"));

        return testDefinition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));
            for (File ballotBox : ballotBoxes) {
                Path ballotboxPath = ballotBox.toPath().resolve("0");
                OfflineDataLoader offlineDataLoader = new OfflineDataLoader();
                offlineDataLoader.setEncryptionParametersLoader(new OfflineEncryptionParametersLoader(inputDirectory.toPath()));
                offlineDataLoader.setPublicKeyLoader(new OfflinePublicKeyLoader(ballotboxPath));
                offlineDataLoader.setVoterWithProofLoader(new OfflineVoterWithProofLoader(ballotboxPath));

                int verificationResultCode = DecryptVerifier.verify(offlineDataLoader);
                if (verificationResultCode != 1 && verificationResultCode != -1) {
                    throw new TestFailureException("The verification failed", ballotBox.getName());
                }
            }
            result.setStatus(Status.OK);
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.nok.message", e.getArgs()[1]));
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e.getCause() instanceof FileNotFoundException) {
                LOGGER.error("a FileNotFoundException error occurred", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test07.file.not.found.message", e.getCause().getLocalizedMessage()));
            } else {
                LOGGER.error("an unexpected error occurred", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
