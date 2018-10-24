package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.*;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test08 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test08.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(3);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test08.description"));
        def.setId(8);
        def.setName("checkCommitmentParameters(cp)");
        return def;
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
                    for (File folder : commitmentParamFolders) {
                        commitmentParamFiles.add(PathHelper.getFile(folder, "commitmentParameters.*\\.json"));
                    }
                } catch (FileNotFoundException e) {
                    throw new TestFailureException("commitmentParameters.json not found");
                }
            });

            if (commitmentParamFiles.isEmpty()) {
                throw new TestFailureException("no commitmentParameters.json found");
            }

            final BigInteger p = TypeConverter.stringToBigInteger(commitmentParamFiles.stream().flatMap(f -> {
                try {
                    Optional<String> optionalfirst = Files.lines(f.toPath()).findFirst();
                    if (optionalfirst.isPresent()) {
                        return Stream.of(optionalfirst.get());
                    } else {
                        throw new RuntimeException("no first line in file");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).reduce(null, (s1, s2) -> {
                if (s1 != null && !s1.equals(s2)) {
                    throw new RuntimeException("P parameter not unique");
                }
                return s2;
            }));

            List<BigInteger> errors = commitmentParamFiles.stream()
                    .flatMap(f -> {
                        try {
                            return Files.lines(f.toPath()).skip(3);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(s -> TypeConverter.stringToBigInteger(s))
                    .filter(bi -> !MathHelper.isEulerCriterionValid(bi, p)).collect(Collectors.toList());

            if (errors.isEmpty()) {
                result.setStatus(Status.OK);
            } else {
                result.setStatus(Status.NOK);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test08.nok.message", errors.toString()));
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message"));
            }
            else if (e instanceof TestFailureException && ((TestFailureException) e).getArgs()[0].equals("commitmentParameters.json not found")) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message"));
            }else {
                LOGGER.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
