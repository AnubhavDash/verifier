/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.stream.Stream;

public class Test01 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test01.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test01.description"));
        def.setId(1);
        def.setName("checkSecureLogIntegrity");
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    @Override
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
                    .filter(s -> s.getPreview() != null && !s.getPreview())
                    .groupBy(s -> String.format("%s|%s", s.getHost(), s.getSource()))
                    .flatMap(SecureLogBundleCreator::from)
                    .subscribe(b -> {
                        try {
                            b.validateIntegrity();
                        } catch (SecureLogBundleValidationException e) {
                            LOGGER.error("Validation failed on host {" + e.getHost() + "}, source {" + e.getSource() + "} : " + e.getMessage());
                            throw new TestFailureException(b.getBeginCheckPoint().toString(), b.getBeginCheckPoint().getMetadata().toString());
                        }
                    }, e -> {
                        if (e instanceof TestFailureException) {
                            throw (TestFailureException) e;
                        } else {
                            throw new RuntimeException(e);
                        }
                    });

            result.setStatus(Status.OK);

        } catch (TestFailureException e) {
            LOGGER.error("Test in error, cause : " + Arrays.toString(e.getArgs()), e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test01.nok.message", e.getArgs()));
        } catch (NoSuchFileException e) {
            LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test01.file.not.found.message", e.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test01.file.not.found.message", e.getMessage()));
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            LOGGER.error("Unexpected error occured", e);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }

        return result;
    }
}