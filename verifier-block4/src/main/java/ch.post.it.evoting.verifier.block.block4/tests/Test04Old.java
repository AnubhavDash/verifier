/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block4.tests;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.ech.xmlns.ech_0110._3.ListResultsType;
import ch.post.it.evoting.verifier.block.block4.Block4TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.xmlns.decrypt._1.Results;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test04Old extends Test {

    private static final Logger log = Logger.getLogger(Test04Old.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition definition = new TestDefinition();
        definition.setBlockId(4);
        definition.setId(4);
        definition.setCategory(Category.COMPLETENESS);
        definition.setDescription(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test04.description"));
        definition.setName("checkTallyingLists");
        return definition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());

        try {
            // 2, decrypt file => map<countingCircle, map<electionId, map<ListId, count>>> => map2
            Path path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS);
            Results results = Deserializer.fromXml(path.toFile(), "evoting-decrypt_.*\\.xml", Results.class);
            Map<String, Map<String, Map<String, Long>>> mapDecrypt = results.getBallotsBox().stream()
                    .flatMap(bb -> bb.getCountingCircle().stream())
                    .map(cc -> {
                        String ccId = cc.getCountingCircleIdentification();

                        Map<String, Map<String, Long>> electionCount = cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
                                .map(e -> {
                                    String electionId = e.getElectionIdentification();
                                    Map<String, Long> answerCount = e.getBallot().stream()
                                            .map(b -> b.getChosenListIdentification())
                                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                                    return new AbstractMap.SimpleEntry<>(electionId, answerCount);
                                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


                        return new AbstractMap.SimpleEntry<>(ccId, electionCount);
                    })
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


            path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS);
            Delivery ech110 = Deserializer.fromXml(path.toFile(), "eCH-0110_.*\\.xml", Delivery.class);
            ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
                String ccId = cc.getCountingCircle().getCountingCircleId();
                cc.getElectionResults().stream()
                        .filter(er -> er.getProportionalElection() != null)
                        .forEach(er -> {
                            String electionId = er.getElection().getElectionIdentification();

                            er.getProportionalElection().getList().stream()
                                    .forEach(l -> {
                                        String listId = l.getListInformation().getListIdentification();
                                        BigInteger decryptCount = getDecryptCount(mapDecrypt, ccId, electionId, listId);
                                        BigInteger listCount = getListCount(l);
                                        if (!listCount.equals(decryptCount)) {
                                            log.debug(String.format("count not equal : CC:%s electionId:%s list:%s decrypt:%s 110:%s", ccId, electionId, listId, decryptCount, listCount));
                                            getListCount(l);
                                            throw new RuntimeException("TODO");
                                        }
                                    });
                        });
            });


            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);

            if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test04.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;

    }


    private BigInteger getListCount(ListResultsType l) {
        BigInteger count = BigInteger.ZERO;

        if (l.getCountOfAdditionalVotes() != null) {
            count = count.add(l.getCountOfAdditionalVotes().getTotal());
        }
        if (l.getCountOfCandidateVotes() != null) {
            count = count.add(l.getCountOfCandidateVotes().getTotal());
        }
        if (l.getCountOfChangedBallots() != null) {
            count = count.add(l.getCountOfChangedBallots().getTotal());
        }
        if (l.getCountOfPartyVotes() != null) {
            count = count.add(l.getCountOfPartyVotes().getTotal());
        }
        return count;
    }

    private BigInteger getDecryptCount(Map<String,Map<String,Map<String,Long>>> mapDecrypt, String ccId, String electionId, String listId) {
        Map<String, Map<String, Long>> countByCC = mapDecrypt.get(ccId);
        if (countByCC == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
        }
        Map<String, Long> countByElection = countByCC.get(electionId);
        if (countByElection == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given election : " + electionId);
        }

        return BigInteger.valueOf(Optional.ofNullable(countByElection.get(listId)).orElse(0L));
    }
}
