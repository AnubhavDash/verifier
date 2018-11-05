package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationDataExtractor;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationStruct;
import ch.post.it.evoting.verifier.block.block2.secureLog.RegularLogEntry;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Test05 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test05.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test05.description"));
        def.setId(5);
        def.setName("checkVoteBallotBox");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectory);

            //count in the logs
            Stream<SecureLogEntry> logEntry = Deserializer.fromLines(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), ".*\\.json",
                    line -> {
                        try {
                            return SecureLogEntry.from(line);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to deserialize SecureLogEntry", e);
                        }
                    });
            Pattern pattern = Pattern.compile(".*\\|000\\|(.*)\\|.*\\|.*\\|#encryptedOptions=\"(.*);(.*)\" #ccx_id=.*");
            Map<String, Tuple2<String, String>> votes = Flux.fromStream(logEntry)
                    .filter(sl -> sl.getPreview() != null && !sl.getPreview())
                    .filter(s1 -> s1 instanceof RegularLogEntry)
                    .cast(RegularLogEntry.class)
                    .filter(s1 -> s1.getRaw().matches(".*\\|VOTVAL\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|.*"))
                    .map(s1 -> {
                        Matcher matcher = pattern.matcher(s1.getRaw());
                        matcher.matches();
                        return Tuples.of(matcher.group(1), matcher.group(2), matcher.group(3));
                    })
                    .collectMap(t -> t.getT1(), t -> Tuples.of(t.getT2(), t.getT3())).block();


            result.setStatus(Status.OK);

        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof RuntimeException) {
                LOGGER.error("Test failed, cause : " + e.getMessage(), e);
                if (e.getCause() instanceof SecureLogBundleValidationException) {
                    //TODO
                }
            }
            if (e instanceof TestFailureException) {
                String[] args = ((TestFailureException) e).getArgs();
                LOGGER.debug("Test failed, cause : " + args[0] + ". Count for the CCs : " + args[1].toString());
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test04.nok.message"));
            } else if (e instanceof NoSuchFileException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test04.file.not.found.message", ((NoSuchFileException) e).getFile()));
            } else if (e instanceof FileNotFoundException) {
                LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test04.file.not.found.message", e.getMessage()));
            }
        }
        return result;
    }

}
