package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.stream.Stream;

public class Test01 /*extends Test*/ {

    private static final Logger LOGGER = Logger.getLogger(Test01.class);

    /*@Override*/
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test01.description"));
        def.setId(1);
        def.setName("checkSecureLogIntegrity");
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
                    })
                    .filter(sl -> sl.getPreview() != null && !sl.getPreview());

            Flux.fromIterable(logEntryStream::iterator)
                    .groupBy(s -> String.format("%s|%s", s.getHost(), s.getSource()))
                    .flatMap(SecureLogBundleCreator::from)
                    .subscribe(b -> {
                        try {
                            b.validateIntegrity();
                        } catch (SecureLogBundleValidationException e) {
                            LOGGER.error("Validation failed on host {" + e.getHost() + "}, source {" + e.getSource() + "} : " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    });

            result.setStatus(Status.OK);

        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof NoSuchFileException) {
                LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test01.file.not.found.message", ((NoSuchFileException) e).getFile()));
            } else if (e instanceof FileNotFoundException) {
                LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test01.file.not.found.message", e.getMessage()));
            } else {
                LOGGER.error("SecureLogs integrity validation failed", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test01.nok.message", e.getMessage()));
            }
        }

        return result;
    }
}
