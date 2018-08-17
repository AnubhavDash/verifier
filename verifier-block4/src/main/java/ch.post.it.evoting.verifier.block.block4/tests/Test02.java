/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block4.tests;

import ch.evoting.xmlns.config._3.AuthorizationType;
import ch.evoting.xmlns.config._3.Configuration;
import ch.evoting.xmlns.config._3.StandardAnswerType;
import ch.evoting.xmlns.config._3.TiebreakAnswerType;
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
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
            Path path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_ELECTION_SETUP);
            Configuration configuration = Deserializer.fromXml(path.toFile(), "configuration-anonymized.xml", Configuration.class);

            // 1, config file => map<Tuple<Qid, Atype>, answerId> => map1
            // 2, decrypt file => map<countingCircle, map<answerId, count>> => map2
            // 3, e110 file foreach cc do a loop for each Question get the count
            // ask the map1 and get the right answerId
            // check the count by asking map2


            //1, config file => map<Tuple<Qid, Atype>, answerId> => map1
            Map<Map<String, String>, String> map1 = new HashMap<>();
            configuration.getContest().getVoteInformation().forEach
                    (vi -> {
                        String id = vi.getVote().getVoteIdentification();
                        vi.getVote().getBallot().forEach
                                (b -> {
                                    //standard ballot
                                    if (b.getStandardBallot() != null) {
                                        String qId = b.getStandardBallot().getQuestionIdentification();
                                        b.getStandardBallot().getAnswer().forEach( a -> {
                                                    String aType = a.getStandardAnswerType();
                                                    String answerId = a.getAnswerIdentification();
                                                    AbstractMap.SimpleEntry<String, String> se = new AbstractMap.SimpleEntry<>(qId, aType);
                                                    AbstractMap.SimpleEntry<AbstractMap.SimpleEntry<String, String>, String> se2 = new AbstractMap.SimpleEntry<>(se, answerId);
                                                    // TypeError below
                                                    // map1.put(se2);
                                                }
                                        );
                                    }
                                    //variant ballot
                                    else {

                                    }
                                });

                    });

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
