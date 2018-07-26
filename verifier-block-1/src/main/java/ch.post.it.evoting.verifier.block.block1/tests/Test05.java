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
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Test03 of Block1, Step isStrongPrime(p, q)
 */
public class Test05 extends Test {

    private static final Logger log = Logger.getLogger(Test05.class);
    private List listNumbersInError;
    private boolean isError;

    public Test05() {
        this.listNumbersInError = new ArrayList();
        this.isError = false;
    }

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test05.description"));
        def.setId(3);
        def.setName("isStrongPrime(p,q)");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            DataConfigEE dataConfigEE = JsonMapper.mapFromJson(inputDirectory, "dataConfig_[EE].json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();

            ballotBoxes.stream().forEach(
                    ballotBox -> {
                        ballotBox.getCountingCircles().stream().forEach(
                                countingCircle -> {
                                    countingCircle.getDomainOfInfluence().stream().forEach(
                                            domainOfInfluence -> {
                                                domainOfInfluence.getVotes().stream().forEach(
                                                        vote -> {
                                                            vote.getQuestions().stream().forEach(
                                                                    question -> {
                                                                        question.getOptions().stream().forEach(
                                                                                option -> {
                                                                                    int pn = option.getPrimeNumber();
                                                                                    if(!TypeHelper.isPrime(BigInteger.valueOf(pn))){
                                                                                        this.isError = true;
                                                                                        this.listNumbersInError.add(pn);
                                                                                    }
                                                                                    else{
                                                                                        this.isError = (this.listNumbersInError.size() > 0) ? true : false;
                                                                                    }

                                                                                }
                                                                        );
                                                                    }
                                                            );
                                                        }
                                                );
                                            }
                                    );
                                }
                        );
                    }
            );

            if ( !this.isError ) {
                result.setStatus(Status.OK);
            } else {
                result.setStatus(Status.NOK);
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test05.nok.message"));
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if(e instanceof FileNotFoundException){
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test05.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(LanguageHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }
}
