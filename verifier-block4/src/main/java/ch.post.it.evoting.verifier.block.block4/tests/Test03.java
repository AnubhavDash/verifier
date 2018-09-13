package ch.post.it.evoting.verifier.block.block4.tests;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.evoting.xmlns.config._3.CandidatePositionType;
import ch.evoting.xmlns.config._3.Configuration;
import ch.post.it.evoting.verifier.block.block4.Block4TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.xmlns.decrypt._1.Results;
import org.apache.commons.lang.StringUtils;
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
import java.util.stream.Stream;

public class Test03 extends Test {

    private static final Logger log = Logger.getLogger(Test03.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition definition = new TestDefinition();
        definition.setBlockId(4);
        definition.setId(3);
        definition.setCategory(Category.COMPLETENESS);
        definition.setDescription(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test03.description"));
        definition.setName("checkTallyingCandidates");
        return definition;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());

        try {
            //1, config file => map<candidateListId, candidateId> => map1
            Path path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_ELECTION_SETUP);
            Configuration configuration = Deserializer.fromXml(path.toFile(), "configuration-anonymized.xml", Configuration.class);
            Map<String, String> mapConfig = configuration.getContest().getElectionInformation().stream()
                    .flatMap(ei -> ei.getList().stream())
                    .flatMap(l -> l.getCandidatePosition().stream())
                    .filter(cp -> StringUtils.isNotEmpty(cp.getCandidateIdentification()))
                    .collect(Collectors.toMap(CandidatePositionType::getCandidateListIdentification, CandidatePositionType::getCandidateIdentification));

            // 2, decrypt file => map<countingCircle, map<ListCandidateId||CandidateId, count>> => map2
            path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS);
            Results results = Deserializer.fromXml(path.toFile(), "evoting-decrypt_.*\\.xml", Results.class);
            Map<String, Map<String, Long>> mapDecrypt = results.getBallotsBox().stream()
                    .flatMap(bb -> bb.getCountingCircle().stream())
                    .map(cc -> {
                        String ccId = cc.getCountingCircleIdentification();

                        Map<String, Long> answerCount = cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
                                .flatMap(e -> e.getBallot().stream())
                                .flatMap(b -> Stream.concat(b.getChosenCandidateIdentification().stream(),
                                        b.getChosenCandidateListIdentification().stream()))
                                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                        return new AbstractMap.SimpleEntry<>(ccId, answerCount);
                    })
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));


            path = inputDirectory.toPath().resolve(Block4TestSuite.PATH_RESULTS);
            Delivery ech110 = Deserializer.fromXml(path.toFile(), "eCH-0110_.*\\.xml", Delivery.class);
            ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
                String ccId = cc.getCountingCircle().getCountingCircleId();
                cc.getElectionResults().stream()
                        .filter(er -> er.getMajoralElection() != null)
                        .flatMap(er -> er.getMajoralElection().getCandidate().stream())
                        .forEach(c -> {
                            String cId = c.getCandidateInformation().getCandidateIdentification();
                            BigInteger decryptCount = getDecryptCount(mapConfig, mapDecrypt, ccId, cId);
                            if (!c.getCountOfVotesTotal().equals(decryptCount)) {
                                throw new Test03FailureException(cId);
                            }
                        });
                cc.getElectionResults().stream()
                        .filter(er -> er.getProportionalElection() != null)
                        .flatMap(er -> er.getProportionalElection().getCandidate().stream())
                        .forEach(c -> {
                            String cId = c.getCandidateInformation().getCandidateIdentification();
                            BigInteger decryptCount = getDecryptCount(mapConfig, mapDecrypt, ccId, cId);
                            if (!c.getCountOfVotesTotal().equals(decryptCount)) {
                                throw new Test03FailureException(cId);
                            }
                        });
            });


            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);

            if (e instanceof Test03FailureException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test03.nok.message", ((Test03FailureException) e).getCandidateId()));
            } else if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test03.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;

    }

    private BigInteger getDecryptCount(Map<String, String> mapConfig, Map<String, Map<String, Long>> mapDecrypt, String ccId, String cId) {
        Map<String, Long> countByCC = mapDecrypt.get(ccId);
        if (countByCC == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
        }

        long count = 0;

        //candidateListId
        if (mapConfig.values().contains(cId)) {
            count += mapConfig.entrySet().stream()
                    .filter(e -> e.getValue().equals(cId))
                    .mapToLong(e -> Optional.ofNullable(countByCC.get(e.getKey())).orElse(0L))
                    .sum();
        }

        //candidateId
        count += Optional.ofNullable(countByCC.get(cId)).orElse(0L);

        return BigInteger.valueOf(count);
    }

    class Test03FailureException extends RuntimeException {
        private String candidateId;

        public Test03FailureException(String candidateId) {
            this.candidateId = candidateId;
        }

        public String getCandidateId() {
            return candidateId;
        }
    }
}
