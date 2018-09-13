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

            Path path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_ELECTION_SETUP);
            Configuration configuration = Deserializer.fromXml(path.toFile(), "configuration-anonymized.xml", Configuration.class);

            Map<String, Boolean> mapListIsEmpty = configuration.getContest().getElectionInformation().stream()
                    .flatMap(ei -> ei.getList().stream())
                    .map(l -> new AbstractMap.SimpleEntry<>(l.getListIdentification(), l.isListEmpty()))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            Map<String, Boolean> mapLcIdIsEmpty = configuration.getContest().getElectionInformation().stream()
                    .flatMap(ei -> ei.getList().stream())
                    .map(l -> {
                        String listIden = l.getListIdentification();
                        Map<String, Boolean> map = l.getCandidatePosition().stream()
                            .map(cp -> new AbstractMap.SimpleEntry<>(cp.getCandidateListIdentification(), mapListIsEmpty.get(listIden)))
                            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
                        return map;
                    }).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(entry -> entry.getKey(), entry-> entry.getValue()));

            Map<String, String> mapLcIdListId = configuration.getContest().getElectionInformation().stream()
                    .flatMap(ei -> ei.getList().stream())
                    .map(l -> {
                        String listIden = l.getListIdentification();
                        Map<String, String> map = l.getCandidatePosition().stream()
                                .map(cp -> new AbstractMap.SimpleEntry<>(cp.getCandidateListIdentification(), listIden))
                                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
                        return map;
                    }).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(entry -> entry.getKey(), entry-> entry.getValue()));


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
                                        BigInteger lcpCount = BigInteger.ZERO;
                                        BigInteger countOfAdditionnalVotes = getCountOfAdditionnalVotes(l);
                                        BigInteger emptyCount = BigInteger.ZERO;;
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





}
