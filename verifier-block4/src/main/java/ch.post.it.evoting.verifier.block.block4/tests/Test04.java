package ch.post.it.evoting.verifier.block.block4.tests;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.ech.xmlns.ech_0110._3.ListResultsType;
import ch.evoting.xmlns.config._3.Configuration;
import ch.post.it.evoting.verifier.block.block4.Block4TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.xmlns.decrypt._1.Results;
import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test04 extends Test {

    private static final Logger log = Logger.getLogger(Test04.class);

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

            //1, config file => map<listIdentification, List<candidateListIdentification>> => map1 (listEmpty = true) empty positions
            //config file => map<listIdentification, List<candidateListIdentification>> => map2 list candidate positions
            Path path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_ELECTION_SETUP);
            Configuration configuration = Deserializer.fromXml(path.toFile(), "configuration-anonymized.xml", Configuration.class);
            Map<String, Map<String, Long>> map1 = configuration.getContest().getElectionInformation().stream()
                    .flatMap(ei -> ei.getList().stream())
                    .filter(l -> l.isListEmpty())
                    .map(l -> {
                        String listIden = l.getListIdentification();
                        Map<String, Long> cpList = l.getCandidatePosition().stream()
                                .map(cp -> cp.getCandidateListIdentification())
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                        return new AbstractMap.SimpleEntry<>(listIden, cpList);
                    }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            Map<String, Map<String, Long>> map2 = configuration.getContest().getElectionInformation().stream()
                    .flatMap(ei -> ei.getList().stream())
                    .filter(l -> !l.isListEmpty())
                    .map(l -> {
                        String listIden = l.getListIdentification();
                        Map<String, Long> cpList = l.getCandidatePosition().stream()
                                .map(cp -> cp.getCandidateListIdentification())
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                        return new AbstractMap.SimpleEntry<>(listIden, cpList);
                    }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


            // 2, decrypt file => map<countingCircle, map<electionId, map<list, map<ListId, count>>>> => mapDecrypt
            path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS);
            Results results = Deserializer.fromXml(path.toFile(), "evoting-decrypt_.*\\.xml", Results.class);
            Map<String, Map<String, Map<String, Map<String, Long>>>> mapDecrypt = results.getBallotsBox().stream()
                    .flatMap(bb -> bb.getCountingCircle().stream())
                    .map(cc -> {
                        String ccId = cc.getCountingCircleIdentification();
                        Map<String, Map<String, Map<String, Long>>> electionCount = cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
                                .map(e -> {
                                    String electionId = e.getElectionIdentification();
                                    Map<String, Map<String, Long>> listAndListIdCountMap = new HashMap<>();
                                    e.getBallot().forEach(ballot -> {
                                            String chosenListIdentification = ballot.getChosenListIdentification();
                                            Map<String, Long> listIdCountMap = ballot.getChosenCandidateListIdentification().stream()
                                                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                                            Pair<String, Map<String, Long>> pair = new Pair<String, Map<String, Long>>(chosenListIdentification, listIdCountMap);
                                            listAndListIdCountMap.computeIfPresent(pair.getKey(), (key, value) -> {
                                                Map<String, Long> newValue = new HashMap<>();
                                                newValue.putAll(listAndListIdCountMap.get(key));
                                                value.keySet().forEach(k -> {
                                                    if(!newValue.containsKey(k)){
                                                        newValue.put(k, value.get(k));
                                                    }else{
                                                        Long newCount = value.get(k) + newValue.get(k);
                                                        newValue.put(k, newCount);
                                                    }
                                                });
                                                return newValue;
                                            });
                                            listAndListIdCountMap.putIfAbsent(pair.getKey(), pair.getValue());
                                        });
                                    return new AbstractMap.SimpleEntry<>(electionId, listAndListIdCountMap);
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
                            BigInteger electionType = er.getElection().getTypeOfElection();
                            String electionId = er.getElection().getElectionIdentification();
                            er.getProportionalElection().getList().stream()
                                    .forEach(l -> {
                                        String listId = l.getListInformation().getListIdentification();
                                        BigInteger countOfCandidatesVotes = getCountOfCandidatesVotes(l);
                                        BigInteger lcpCount = getListCandidatesPositionCount(mapDecrypt, ccId, electionId, listId, map2);
                                        BigInteger countOfAdditionnalVotes = getCountOfAdditionnalVotes(l);
                                        BigInteger emptyCount = getEmptyPositionCount(mapDecrypt, ccId, electionId, listId, map1);
                                        if (!countOfCandidatesVotes.equals(lcpCount) || !countOfAdditionnalVotes.equals(emptyCount) ) {
                                            log.debug(String.format("count not equal : CC:%s electionId:%s list:%s decrypt:%s 110:%s", ccId, electionId, listId, lcpCount, countOfCandidatesVotes));
                                            throw new TestFailureException(ccId, listId);
                                        }
                                    });
                        });
            });
            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof TestFailureException) {
                TestFailureException ex = ((TestFailureException) e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test04.nok.message", ex.getArgs()));
            }
            else if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test04.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;

    }

    private BigInteger getCountOfCandidatesVotes(ListResultsType l) {
        BigInteger count = BigInteger.ZERO;
        if (l.getCountOfCandidateVotes() != null) {
            count = count.add(l.getCountOfCandidateVotes().getTotal());
        }
        return count;
    }

    private BigInteger getCountOfAdditionnalVotes(ListResultsType l) {
        BigInteger count = BigInteger.ZERO;
        if (l.getCountOfCandidateVotes() != null) {
            count = count.add(l.getCountOfAdditionalVotes().getTotal());
        }
        return count;
    }

    private BigInteger getListCandidatesPositionCount(Map<String, Map<String, Map<String, Map<String, Long>>>> mapDecrypt, String ccId, String electionId, String listId, Map<String, Map<String, Long>> map) {
        Map<String, Map<String, Map<String, Long>>> countByCC = mapDecrypt.get(ccId);
        if (countByCC == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
        }
        Map<String, Map<String, Long>> countByElection = countByCC.get(electionId);
        if (countByElection == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given election : " + electionId);
        }
        BigInteger result = BigInteger.ZERO;

        Map<String, Long> listAndCountMap = map.get(listId);
        if (listAndCountMap != null ){
            List<String> listsToCheck = countByElection.keySet().stream()
                    .filter(key -> map.get(key) != null)
                    .collect(Collectors.toList());

            if(listsToCheck != null && !listsToCheck.isEmpty()){
                long sum = listsToCheck.stream()
                        .map(list -> countByElection.get(list))
                        .map(i -> i.values().stream())
                        .flatMap(Function.identity())
                        .mapToLong(Long::longValue).sum();
                result = result.add(BigInteger.valueOf(sum));
            }
        }
        return result;
    }

    private BigInteger getEmptyPositionCount(Map<String, Map<String, Map<String, Map<String, Long>>>> mapDecrypt, String ccId, String electionId, String listId, Map<String, Map<String, Long>> map) {
        Map<String, Map<String, Map<String, Long>>> countByCC = mapDecrypt.get(ccId);
        if (countByCC == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
        }
        Map<String, Map<String, Long>> countByElection = countByCC.get(electionId);
        if (countByElection == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given election : " + electionId);
        }
        BigInteger result = BigInteger.ZERO;
        List<String> listOfEmptyValuea =  map.values().stream()
                                            .flatMap(m -> m.keySet().stream())
                                            .collect(Collectors.toList());

        List<String> collect = countByElection.keySet().stream()
                .filter(list -> !map.containsKey(list))
                .map(key -> countByElection.get(key))
                .map(m -> m.keySet().stream())
                .filter(l -> listOfEmptyValuea.contains(l))
                .flatMap(Function.identity())
                .collect(Collectors.toList());

        return result;
    }


    class Test04FailureException extends RuntimeException {
        private String ccId;
        private String listId;

        public Test04FailureException(String ccId, String listId) {
            this.ccId = ccId;
            this.listId = listId;
        }
        public String getCcId() { return ccId; }

        public String getListId() { return listId; }

    }

}
