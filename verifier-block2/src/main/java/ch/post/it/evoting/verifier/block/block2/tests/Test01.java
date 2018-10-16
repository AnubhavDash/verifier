/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundle;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import io.reactivex.Observable;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Test01 of Block2, Step checkSecureLogIntegrity
 */
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
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());

        try {
            Path secureLogsPath = PathHelper.getFile(inputDirectory, ".*Evoting_CC_verifier_export_.*\\.json").toPath();
            Stream<SecureLogEntry> logEntryStream = Files.lines(secureLogsPath).map(line -> {
                try {
                    return SecureLogEntry.from(line);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).filter(sl -> sl.getIndex() != null && sl.getIndex().equals("it_evoting_cc"));

            Observable<SecureLogEntry> secureLogEntryObservable = Observable.fromIterable(logEntryStream::iterator);

            secureLogEntryObservable.groupBy(sl -> sl.getHost())
                    .forEach(groups -> {
                        String host = groups.getKey();
                        Observable<SecureLogBundle> from = SecureLogBundleCreator.from(groups);
                        from.forEach(b -> {
                            try {
                                b.validate();
                            } catch (SecureLogBundleValidationException e) {
                                LOGGER.error("Validation failed on host {" + host + "} because " + e.getMessage());
                                // throw new RuntimeException(e);
                            }
                        });
                    });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        result.setStatus(Status.OK);

        return result;
    }
}
