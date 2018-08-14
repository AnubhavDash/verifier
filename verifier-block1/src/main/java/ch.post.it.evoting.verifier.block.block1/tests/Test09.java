/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block1.tests;

import ch.evoting.xmlns.config._3.Configuration;
import ch.evoting.xmlns.config._3.StandardAnswerType;
import ch.evoting.xmlns.config._3.TiebreakAnswerType;
import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import ch.post.it.evoting.verifier.dto.DomainOfInfluence;
import ch.post.it.evoting.verifier.dto.Option;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Test09 of Block1, Step checkPrimeNumberOptions([vo])
 */
public class Test09 extends Test {

    private static final Logger log = Logger.getLogger(Test09.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test09.description"));
        def.setId(9);
        def.setName("checkPrimeNumberOptions([vo])");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Configuration configuration = Deserializer.fromXml(inputDirectory, "configuration-anonymized.xml", Configuration.class);

            // vote
            Map<String, Long> voteAnswersCount = configuration.getContest().getVoteInformation().stream()
                    .map(vi -> {
                        String id = vi.getVote().getVoteIdentification();
                        long nbAnswer = vi.getVote().getBallot().stream()
                                .flatMap(b -> {
                                    if (b.getStandardBallot() != null) {
                                        return b.getStandardBallot().getAnswer().stream();
                                    } else {
                                        Stream<StandardAnswerType> s1 = b.getVariantBallot().getStandardQuestion().stream().flatMap(sq -> sq.getAnswer().stream());
                                        Stream<TiebreakAnswerType> s2 = b.getVariantBallot().getTieBreakQuestion().stream().flatMap(tq -> tq.getAnswer().stream());
                                        return Stream.concat(s1, s2);
                                    }
                                }).count();

                        return new AbstractMap.SimpleEntry<>(id, nbAnswer);
                    })
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            // election
            Map<String, ElectionDetail> electionOptionCount = configuration.getContest().getElectionInformation().stream()
                    .map(ei -> {
                        ElectionDetail electionDetail = new ElectionDetail();

                        int candidateCount = ei.getCandidate().size();
                        BigInteger numberOfMandates = ei.getElection().getNumberOfMandates();
                        boolean writeInsAllowed = ei.getElection().isWriteInsAllowed();
                        BigInteger candidateAccumulation = ei.getElection().getCandidateAccumulation();

                        BigInteger optionCount = (candidateAccumulation.multiply(BigInteger.valueOf(candidateCount))).add(numberOfMandates.multiply(BigInteger.valueOf(1 + (writeInsAllowed ? 1 : 0))));
                        electionDetail.setOptionCount(optionCount.intValue());
                        electionDetail.setListCount(ei.getList().size());

                        return new AbstractMap.SimpleEntry<>(ei.getElection().getElectionIdentification(), electionDetail);
                    }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


            //check correspondences between config and dataConfig
            DataConfigEE dataConfigEE = Deserializer.fromJson(inputDirectory, "dataConfig_[EE].json", DataConfigEE.class);
            dataConfigEE.getElectionEvent().getBallotBoxes().stream()
                    .flatMap(bb -> bb.getCountingCircles().stream())
                    .flatMap(cc -> cc.getDomainOfInfluence().stream())
                    .forEach((DomainOfInfluence doi) -> {
                        doi.getVotes().forEach(v -> {
                            String voteIdentification = v.getAlias();
                            if (!voteAnswersCount.containsKey(voteIdentification)) {
                                //TODO set correct key
                                throw new Test09Exception("KEY", voteIdentification);
                            }

                            List<Integer> options = v.getQuestions().stream().flatMap(q -> q.getOptions().stream()).map(Option::getPrimeNumber).collect(Collectors.toList());
                            long optionsDistinctCount = v.getQuestions().stream().flatMap(q -> q.getOptions().stream()).map(Option::getPrimeNumber).distinct().count();
                            if (options.size() != optionsDistinctCount) {
                                //TODO set correct key
                                throw new Test09Exception("KEY", getDoubles(options).toString());
                            }

                            if (options.size() != voteAnswersCount.get(voteIdentification)) {
                                //TODO set correct key
                                throw new Test09Exception("KEY");
                            }
                        });

                        doi.getElections().forEach(e -> {
                            String electionIdentification = e.getAlias();
                            if (!electionOptionCount.containsKey(electionIdentification)) {
                                //TODO set correct key
                                throw new Test09Exception("KEY", electionIdentification);
                            }

                            List<Integer> listCount = e.getLists().stream().map(l -> l.getPrimeNumber()).collect(Collectors.toList());
                            long listDistinctCount = e.getLists().stream().map(l -> l.getPrimeNumber()).distinct().count();
                            if (listCount.size() != listDistinctCount) {
                                //TODO set correct key
                                throw new Test09Exception("KEY", getDoubles(listCount).toString());
                            }
                            if (listCount.size() != electionOptionCount.get(electionIdentification).getListCount()) {
                                //TODO set correct key
                                throw new Test09Exception("KEY");
                            }
                            long optionDistinctCount = e.getLists().stream()
                                    .flatMap(l -> l.getCandidatePositions().stream())
                                    .flatMap(cp -> cp.getPrimeNumber().stream()).distinct().count();
                            //TODO check that with Olivier
                            int writeInsCount = e.getWriteIns().size();

                            if ((optionDistinctCount + writeInsCount) != electionOptionCount.get(electionIdentification).getOptionCount()) {
                                //TODO set correct key
                                throw new Test09Exception("KEY");
                            }
                        });
                    });

            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof Test09Exception) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, ((Test09Exception) e).getKey(), ((Test09Exception) e).getParams()));
            } else if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "test09.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }

    private <T> List<T> getDoubles(List<T> entries) {
        return entries.stream()
                .distinct()
                .map(e -> new AbstractMap.SimpleEntry<>(e, entries.stream().filter(e::equals).count()))
                .filter(e -> e.getValue() > 1)
                .map(AbstractMap.SimpleEntry::getKey)
                .collect(Collectors.toList());
    }

    class ElectionDetail {
        private int optionCount;
        private int listCount;

        public int getOptionCount() {
            return optionCount;
        }

        public void setOptionCount(int optionCount) {
            this.optionCount = optionCount;
        }

        public int getListCount() {
            return listCount;
        }

        public void setListCount(int listCount) {
            this.listCount = listCount;
        }
    }

    class Test09Exception extends RuntimeException {
        String key;
        String[] params;

        Test09Exception(String key, String... params) {
            this.key = key;
            this.params = params;
        }

        public String[] getParams() {
            return params;
        }

        public String getKey() {
            return key;
        }
    }

}
