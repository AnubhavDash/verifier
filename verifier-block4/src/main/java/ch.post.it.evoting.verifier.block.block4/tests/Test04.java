package ch.post.it.evoting.verifier.block.block4.tests;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.ech.xmlns.ech_0110._3.ListResultsType;
import ch.evoting.xmlns.config._4.Configuration;
import ch.post.it.evoting.verifier.block.block4.Block4TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.CountMap;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.xmlns.decrypt._1.Results;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Test04 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test04.class);

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

            Map<String, String> mapLcIdListId = configuration.getContest().getElectionInformation().stream()
                    .flatMap(ei -> ei.getList().stream())
                    .map(l -> {
                        String listIden = l.getListIdentification();
                        Map<String, String> map = l.getCandidatePosition().stream()
                                .map(cp -> new AbstractMap.SimpleEntry<>(cp.getCandidateListIdentification(), listIden))
                                .collect(Collectors.toMap(
                                        AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue,
                                        (listId1, listId2) -> listId1));
                        return map;
                    }).flatMap(map -> map.entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


            // 2, decrypt file => map<countingCircle, map<ElectionId, map<listId, count>>>
            path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS);
            Results results = Deserializer.fromXml(path.toFile(), "evoting-decrypt_.*\\.xml", Results.class);
            Map<String, Map<String, Map<String, Long>>> countByListId = results.getBallotsBox().stream()
                    .flatMap(bb -> bb.getCountingCircle().stream())
                    .map(cc -> {
                        String ccId = cc.getCountingCircleIdentification();
                        Map<String, Map<String, Long>> electionCount = cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
                                .map(e -> {
                                    String electionId = e.getElectionIdentification();
                                    CountMap<String> listIdCountMap = new CountMap<>();
                                    e.getBallot().forEach(ballot -> {
                                        if (ballot.getChosenListIdentification() == null) {
                                            //candidate only election, nothing to do
                                        } else {
                                            // empty list
                                            if (mapListIsEmpty.get(ballot.getChosenListIdentification())) {
                                                ballot.getChosenCandidateListIdentification().forEach(lcId -> {
                                                    String candidateListId = mapLcIdListId.get(lcId);
                                                    if (!mapListIsEmpty.get(candidateListId)) {
                                                        listIdCountMap.increment(candidateListId);
                                                    }
                                                });
                                            } else {
                                                // normal list
                                                ballot.getChosenCandidateListIdentification().forEach(lcId -> {
                                                    String candidateListId = mapLcIdListId.get(lcId);
                                                    if (!mapListIsEmpty.get(candidateListId)) {
                                                        //real candidate.
                                                        listIdCountMap.increment(candidateListId);
                                                    } else {
                                                        //empty candidate
                                                        listIdCountMap.increment(ballot.getChosenListIdentification());
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    return new AbstractMap.SimpleEntry<>(electionId, listIdCountMap);
                                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
                        return new AbstractMap.SimpleEntry<>(ccId, electionCount);
                    })
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue,
                            (electionMap1, electionMap2) -> {
                                return concatMap(electionMap1, electionMap2, (listmap1, listmap2) -> {
                                    return concatMap(listmap1, listmap2, Long::sum);
                                });
                            }));

            Map<String, Map<String, Map<String, Long>>> countOfEmptyValuesByListId = results.getBallotsBox().stream()
                    .flatMap(bb -> bb.getCountingCircle().stream())
                    .map(cc -> {
                        String ccId = cc.getCountingCircleIdentification();
                        Map<String, Map<String, Long>> electionCount = cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
                                .map(e -> {
                                    String electionId = e.getElectionIdentification();
                                    CountMap<String> listIdCountMap = new CountMap<>();
                                    e.getBallot().forEach(ballot -> {
                                        if (ballot.getChosenListIdentification() == null) {
                                            //candidate only election, nothing to do
                                        } else {
                                            if (!mapListIsEmpty.get(ballot.getChosenListIdentification())) {
                                                //normal list
                                                ballot.getChosenCandidateListIdentification().forEach(lcId -> {
                                                    String choosenList = ballot.getChosenListIdentification();
                                                    String candidateListId = mapLcIdListId.get(lcId);
                                                    if (mapListIsEmpty.get(candidateListId)) {
                                                        //empty candidate
                                                        listIdCountMap.increment(choosenList);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    return new AbstractMap.SimpleEntry<>(electionId, listIdCountMap);
                                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
                        return new AbstractMap.SimpleEntry<>(ccId, electionCount);
                    })
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue,
                            (electionMap1, electionMap2) -> {
                                return concatMap(electionMap1, electionMap2, (listCountMap1, listCountMap2) -> {
                                    return concatMap(listCountMap1, listCountMap2, Long::sum);
                                });
                            }));

            path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS);
            Delivery ech110 = Deserializer.fromXml(path.toFile(), "eCH-0110_.*\\.xml", Delivery.class);
            ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
                String ccId = cc.getCountingCircle().getCountingCircleId();
                cc.getElectionResults().stream()
                        .filter(er -> er.getProportionalElection() != null)
                        .forEach(er -> {
                            String electionId = er.getElection().getElectionIdentification();
                            er.getProportionalElection().getList()
                                    .forEach(l -> {
                                        String listId = l.getListInformation().getListIdentification();
                                        // BigInteger countOfPartyVotes = getCountOfCandidatesVotes(l);
                                        BigInteger countOfPartyVotes = getCountOfPartyVotes(l);
                                        BigInteger lcpCount = getVoteCount(countByListId, ccId, electionId, listId);
                                        BigInteger countOfAdditionalVotes = getCountOfAdditionalVotes(l);
                                        BigInteger emptyCount = getVoteCount(countOfEmptyValuesByListId, ccId, electionId, listId);
                                        if (!countOfPartyVotes.equals(lcpCount) || !countOfAdditionalVotes.equals(emptyCount)) {
                                            LOGGER.debug(String.format("count not equal : CC:%s electionId:%s list:%s decrypt:%s 110:%s", ccId, electionId, listId, lcpCount, countOfPartyVotes));
                                            throw new TestFailureException(ccId, listId);
                                        }
                                    });
                        });
            });
            result.setStatus(Status.OK);
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test04.nok.message", e.getArgs()));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test04.file.not.found.message"));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;

    }


    private <K, V> Map<K, V> concatMap(Map<K, V> map1, Map<K, V> map2, BiFunction<V, V, V> mergeFunction) {
        Map<K, V> result = new HashMap<>(map2);
        for (Map.Entry<K, V> entry : map1.entrySet()) {
            if (result.containsKey(entry.getKey())) {
                //map2 also contains this key, then merge both values together
                result.replace(entry.getKey(), mergeFunction.apply(entry.getValue(), map2.get(entry.getKey())));
            } else {
                //map2 doesn't contains the key, then adding it
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private BigInteger getCountOfPartyVotes(ListResultsType l) {
        if (l.getCountOfPartyVotes() != null) {
            return l.getCountOfPartyVotes().getTotal();
        } else {
            return BigInteger.ZERO;
        }
    }

    private BigInteger getCountOfAdditionalVotes(ListResultsType l) {
        if (l.getCountOfCandidateVotes() != null) {
            return l.getCountOfAdditionalVotes().getTotal();
        } else {
            return BigInteger.ZERO;
        }
    }


    private BigInteger getVoteCount(Map<String, Map<String, Map<String, Long>>> resultMap, String ccId, String electionId, String listId) {
        Map<String, Map<String, Long>> countByCC = resultMap.get(ccId);
        if (countByCC == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
        }
        Map<String, Long> countByElection = countByCC.get(electionId);
        if (countByElection == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given election : " + electionId);
        }
        return BigInteger.valueOf(countByElection.get(listId) == null ? 0L : countByElection.get(listId));
    }
}
