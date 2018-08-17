/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block4.tests;

import ch.post.it.evoting.verifier.block.block4.Block4TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import ch.post.it.evoting.verifier.dto.Option;
import com.scytl.xmlns.decrypt._1.Results;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * /**
 * Test2 of Block4, Step checkTallyingAnswers
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test02 extends Test {

    private static final Logger log = Logger.getLogger(Test02.class);


    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(4);
        def.setCategory(Category.COMPLETENESS);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test02.description"));
        def.setId(2);
        def.setName("checkTallyingAnswers");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try{

            result.setStatus(Status.OK);

        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test02.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }

}
