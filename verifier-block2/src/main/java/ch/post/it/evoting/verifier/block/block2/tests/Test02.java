package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.securelog.CheckPointLogEntry;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class Test02 /*extends Test*/ {

    private static final Logger LOGGER = Logger.getLogger(Test02.class);

    /*@Override*/
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test02.description"));
        def.setId(2);
        def.setName("checkSecureLogSignature");
        return def;
    }

    /*@Override*/
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Stream<SecureLogEntry> logEntryStream = Deserializer.fromLines(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), ".*\\.json",
                    line -> {
                        try {
                            return SecureLogEntry.from(line);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to deserialize SecureLogEntry", e);
                        }
                    });

            Flux.fromIterable(logEntryStream::iterator)
                    .filter(s -> s.getPreview() != null && !s.getPreview())
                    .groupBy(s -> String.format("%s|%s", s.getHost(), s.getSource()))
                    .flatMap(SecureLogBundleCreator::from)
                    .subscribe(b -> {
                        try {
                            b.validateSignature();
                        } catch (SecureLogBundleValidationException e) {
                            LOGGER.error("Validation failed because on host {" + e.getHost() + "} " + e.getMessage());
                            throw new TestFailureException(b.getBeginCheckPoint().toString(), b.getBeginCheckPoint().getMetadata().toString() );
                        }
                    });

            result.setStatus(Status.OK);

        } catch (IOException e) {
            LOGGER.error("a IOException error occurred", e);
            result.setStatus(Status.NOK);
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            String[] args = e.getArgs();
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test02.nok.message", args[0], args[1]));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
