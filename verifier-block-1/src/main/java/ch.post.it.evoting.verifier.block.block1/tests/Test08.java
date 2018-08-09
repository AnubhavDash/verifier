/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.JsonMapper;
import ch.post.it.evoting.verifier.common.block.tools.LanguageHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeHelper;
import ch.post.it.evoting.verifier.dto.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test07 of Block1, Step checkCommitmentParameters(cp)
 */
public class Test08 extends Test {

    private static final Logger log = Logger.getLogger(Test08.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test08.description"));
        def.setId(8);
        def.setName("checkCommitmentParameters(cp)");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {

            DataConfigEE dataConfigEE = JsonMapper.mapFromJson(inputDirectory, "dataConfig_[EE].json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();

            //votations
            Collection<Integer> errors = ballotBoxes.stream()
                    .flatMap(bb -> bb.getCountingCircles().stream())
                    .flatMap(cc -> cc.getDomainOfInfluence().stream())
                    .flatMap(doi -> doi.getVotes().stream())
                    .flatMap(v -> v.getQuestions().stream())
                    .flatMap(q -> q.getOptions().stream())
                    //.filter(o -> isBigIntInError(BigInteger.valueOf(o.getPrimeNumber()), p))
                    .map(Option::getPrimeNumber)
                    .collect(Collectors.toList());

            //lists
            errors.addAll(
                    ballotBoxes.stream()
                            .flatMap(bb -> bb.getCountingCircles().stream())
                            .flatMap(cc -> cc.getDomainOfInfluence().stream())
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getLists().stream())
                          //  .filter(l -> isBigIntInError(BigInteger.valueOf(l.getPrimeNumber()), p))
                            .map(ch.post.it.evoting.verifier.dto.List::getPrimeNumber)
                            .collect(Collectors.toList()));

            //candidates
            errors.addAll(
                    ballotBoxes.stream()
                            .flatMap(bb -> bb.getCountingCircles().stream())
                            .flatMap(cc -> cc.getDomainOfInfluence().stream())
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getLists().stream())
                            .flatMap(l -> l.getCandidatePositions().stream())
                            .flatMap(cp -> cp.getPrimeNumber().stream())
                           // .filter(v -> isBigIntInError(BigInteger.valueOf(v), p))
                            .collect(Collectors.toList()));

            //TODO check candidates without lists --> not in this example

            if (errors.isEmpty()) {
                result.setStatus(Status.OK);
            } else {
                result.setStatus(Status.NOK);
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test08.nok.message", errors.toString()));
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if(e instanceof FileNotFoundException){
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
