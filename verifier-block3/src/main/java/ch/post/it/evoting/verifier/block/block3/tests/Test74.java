/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

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
import ch.post.it.evoting.verifier.common.block.tools.SignatureChecker;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Test74 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test74.class);

    @Override
    public TestDefinition getTestDefinition() {

        TestDefinition testDefinition = new TestDefinition();
        testDefinition.setBlockId(3);
        testDefinition.setCategory(Category.AUTHENTICITY);
        testDefinition.setId(74);
        testDefinition.setName("checkSigCommitmentParameters");
        testDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test74.description"));

        return testDefinition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
        Path path = inputDirectory.toPath().resolve(Block3TestSuite.PATH_ELECTION_SETUP);
        DataConfigEE dataConfigEE = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", DataConfigEE.class);
        List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();
        List<File> commitmentParamFiles = new ArrayList<>();
        ballotBoxes.forEach(ballotBox -> {
            String ballotBoxId = ballotBox.getId();
            try {
                File[] commitmentParamFolders = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES).resolve(ballotBoxId));
                for(File folder : commitmentParamFolders){
                    commitmentParamFiles.add(PathHelper.getFile(folder, "commitmentParameters.*\\.json"));
                }
            } catch (FileNotFoundException e) {
                throw new TestFailureException("proofs.json not found", inputDirectory.getName(), ballotBoxId);
            }
        });

        byte[] signCertificate = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath()
                        .resolve(Block3TestSuite.PATH_CERTIFICATES)
                        .resolve(Block3TestSuite.PATH_ADMINBOARD).toFile(),
                ".*\\.pem").toPath());

        byte[] rootCA = Files.readAllBytes(PathHelper.getFile(inputDirectory.toPath().resolve(Block3TestSuite.PATH_CERTIFICATES).toFile(), "tenant_.*\\.pem").toPath());

        for (File proof : commitmentParamFiles) {
            byte[] content = Files.readAllBytes(proof.toPath());
            byte[] signature = Files.readAllBytes(proof.toPath().getParent().resolve(proof.getName() + ".metadata"));

            if (!SignatureChecker.verifyMetdata(content, signature, signCertificate, rootCA)) {
                throw new TestFailureException(proof.getName());
            }
        }
        result.setStatus(Status.OK);

    } catch (Exception e) {
        if (e instanceof TestFailureException) {
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test71.nok.message"));
        } else if (e instanceof NoSuchFileException) {
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test71.file.not.found.message", ((NoSuchFileException) e).getFile()));
        } else if (e instanceof FileNotFoundException) {
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test71.file.not.found.message", e.getMessage()));
        } else {
            LOGGER.error("unexpected error", e);
        }
        result.setStatus(Status.NOK);
    }
        return result;
    }
}
