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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * /**
 * Test1 of Block4, Step checkOptionsMapping
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test01 extends Test {

    private static final Logger log = Logger.getLogger(Test01.class);


    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(4);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test01.description"));
        def.setId(1);
        def.setName("checkOptionsMapping");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_ELECTION_SETUP);
            DataConfigEE dataConfigEE = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();

            ballotBoxes.forEach(ballotBox -> {
                String ballotBoxId = ballotBox.getId();
                String ballotBoxAuthId = ballotBox.getAuthId();

                ballotBox.getCountingCircles().forEach(countingCircle -> {
                    try {
                        String countingCircleId = countingCircle.getId();
                        //1 Generate map<prime, alias>
                        //votations
                        Map<Integer, String> primeAliasMap = countingCircle.getDomainOfInfluence().stream()
                                .flatMap(doi -> doi.getVotes().stream())
                                .flatMap(v -> v.getQuestions().stream())
                                .flatMap(q -> q.getOptions().stream())
                                .collect(Collectors.toMap(Option::getPrimeNumber, Option::getAlias));
                        //lists
                        primeAliasMap.putAll(countingCircle.getDomainOfInfluence().stream()
                                .flatMap(doi -> doi.getElections().stream())
                                .flatMap(e -> e.getLists().stream())
                                .collect(Collectors.toMap(ch.post.it.evoting.verifier.dto.List::getPrimeNumber, ch.post.it.evoting.verifier.dto.List::getAlias))
                        );
                        //candidates
                        primeAliasMap.putAll(countingCircle.getDomainOfInfluence().stream()
                                .flatMap(doi -> doi.getElections().stream())
                                .flatMap(e -> e.getLists().stream())
                                .flatMap(l -> l.getCandidatePositions().stream())
                                .flatMap(cp -> cp.getPrimeNumber().stream().map(prime -> new AbstractMap.SimpleEntry<>(prime, cp.getCandidateListId())))
                                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
                        );
                        //TODO candidates without list


                        //2 Generate map<prime, count>, but before retrieve the ballotbox file
                        Map<String, Long> primesCountMap = getCorrectFileAndExtractPrimesCount(inputDirectory, ballotBoxId);

                        //3 Generate map<alias, count>
                        final Path resultsPath = inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS);
                        Results decryptResult = Deserializer.fromXml(resultsPath.toFile(), "evoting-decrypt_.*\\.xml", Results.class);
                        Map<String, Long> aliasCountMap = decryptResult.getBallotsBox().stream()
                                .filter(bb -> ballotBoxAuthId.equals(bb.getBallotBoxIdentification()))
                                .flatMap(theBb -> theBb.getCountingCircle().stream())
                                .filter(cc -> countingCircleId.equals(cc.getCountingCircleIdentification()))
                                .flatMap(theCc -> theCc.getDomainOfInfluence().stream())
                                .flatMap(doi -> doi.getVote().stream())
                                .flatMap(v -> v.getBallot().stream())
                                .flatMap(b -> b.getChosenAnswerIdentification().stream())
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                        aliasCountMap.putAll(decryptResult.getBallotsBox().stream()
                                .filter(bb -> {
                                    return ballotBoxAuthId.equals(bb.getBallotBoxIdentification());
                                })
                                .flatMap(theBb -> theBb.getCountingCircle().stream())
                                .filter(cc -> {
                                    return countingCircleId.equals(cc.getCountingCircleIdentification());
                                })
                                .flatMap(theCc -> {
                                    return theCc.getDomainOfInfluence().stream();
                                })
                                .flatMap(doi -> doi.getElection().stream())
                                .flatMap(e -> e.getBallot().stream())
                                .flatMap(b -> Stream.of(b.getChosenCandidateListIdentification().stream(),
                                        b.getChosenCandidateIdentification().stream(),
                                        b.getChosenWriteInsCandidateValue().stream().map(s -> "#"+s),
                                        Stream.of(b.getChosenListIdentification())).flatMap(Function.identity()))
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));


                        // Finally do the check
                        aliasCountMap.forEach((alias, aliasCount) -> {
                            if (alias.startsWith("#")) {
                                Long nb = primesCountMap.entrySet().stream()
                                        .filter(e -> e.getKey().endsWith(alias))
                                        .mapToLong(e -> {
                                            Long aLong = e.getValue();
                                            return aLong != null ? aLong : 0L;
                                        })
                                        .sum();
                                if (!nb.equals(aliasCount)) {
                                    throw new Test01FailException(alias);
                                }
                            } else {
                                Long nb = primeAliasMap.entrySet().stream()
                                        .filter(e -> e.getValue().equals(alias))
                                        .map(e -> e.getKey())
                                        .mapToLong(p -> {
                                            Long aLong = primesCountMap.get(p.toString());
                                            return aLong != null ? aLong : 0L;
                                        })
                                        .sum();
                                if (!nb.equals(aliasCount)) {
                                    throw new Test01FailException(alias);
                                }
                            }
                        });
                    } catch (IOException | JAXBException e) {
                        throw new Test01WrapperException(e);
                    }
                });

            });

            result.setStatus(Status.OK);

        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof Test01WrapperException) {
                //unwrap the wrapped exception
                e = (Exception) e.getCause();
            }
            if (e instanceof Test01FailException) {
                result.setStatus(Status.NOK);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test01.nok.message", ((Test01FailException) e).getAliasInError()));
            } else if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test01.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }

    private Map<String, Long> getCorrectFileAndExtractPrimesCount(File inputDirectory, String ballotboxId) throws IOException {
        //TODO get the correct file regarding the countingCircleId
        Path path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_BALLOTBOXES).resolve(ballotboxId);
        Iterable<List<String>> iterable = Deserializer.fromCsv(path.toFile(),
                "decompressedVotes\\.csv", ";", Arrays::asList);

        return StreamSupport.stream(iterable.spliterator(), false)
                .flatMap(l -> l.stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    class Test01WrapperException extends RuntimeException {
        public Test01WrapperException(Exception root) {
            super(root);
        }
    }

    class Test01FailException extends RuntimeException {
        private String aliasInError;

        public Test01FailException(String aliasInError) {
            this.aliasInError = aliasInError;
        }

        public String getAliasInError() {
            return aliasInError;
        }
    }
}
