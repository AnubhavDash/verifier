package ch.post.it.evoting.verifier.block.block3.tests;

import ch.post.it.evoting.verifier.block.block3.Block3TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.*;
import ch.post.it.evoting.verifier.dto.onlinemixing.OnlineMixing;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Test28 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test28.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(3);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test28.description"));
        def.setId(28);
        def.setName("checkCommitmentParametersOnline");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3TestSuite.PATH_BALLOTBOXES));
            for (File ballotBox : ballotBoxes) {
                final File[] onlineMixings = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
                for (File file : onlineMixings) {

                    OnlineMixing onlineMixing = Deserializer.fromJson(Files.readAllBytes(file.toPath()), OnlineMixing.class);
                    List<String> commitmentParameters = onlineMixing.getCommitmentParameters();
                    if (commitmentParameters.isEmpty()) {
                        throw new TestFailureException("no commitmentParameters found");
                    }
                    final BigInteger p;
                    Optional<String> optional = commitmentParameters.stream().findFirst();
                    if (optional.isPresent()) {
                        p = TypeConverter.stringToBigInteger(optional.get());
                    } else {
                        throw new RuntimeException("no first element in commitmentParameters");
                    }

                    List<BigInteger> errors = commitmentParameters.stream()
                            .skip(3)
                            .map(s -> TypeConverter.stringToBigInteger(s))
                            .filter(bi -> !MathHelper.isEulerCriterionValid(bi, p)).collect(Collectors.toList());

                    if (errors.isEmpty()) {
                        result.setStatus(Status.OK);
                    } else {
                        result.setStatus(Status.NOK);
                        result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test28.nok.message", errors.toString()));
                    }

                }
            }

        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test28.file.not.found.message"));
            }
            else if (e instanceof TestFailureException && ((TestFailureException) e).getArgs()[0].equals("commitmentParameters not found")) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "test28.file.not.found.message"));
            }else {
                LOGGER.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
