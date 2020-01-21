/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block4.verifications;

import ch.ech.xmlns.ech_0110._3.Delivery;
import ch.evoting.xmlns.config._4.CandidatePositionType;
import ch.evoting.xmlns.config._4.Configuration;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.CountMap;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import com.scytl.xmlns.decrypt._1.Results;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckTallyingCandidates extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition definition = new VerificationDefinition();
        definition.setBlockId(4);
        definition.setId(3);
        definition.setCategory(Category.COMPLETENESS);
        definition.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.description"));
        definition.setName("checkTallyingCandidates");
        return definition;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        //1, config file => map<candidateListId, candidateId> => map1
        Path path = inputDirectoryPath.resolve(Block4VerificationSuite.PATH_ELECTION_SETUP);
        Configuration configuration = Deserializer.fromXml(path.toFile(), "configuration-anonymized.xml",
                Configuration.class);
        Map<String, String> mapConfig = configuration.getContest().getElectionInformation().stream()
                .flatMap(ei -> ei.getList().stream())
                .flatMap(l -> l.getCandidatePosition().stream())
                .filter(cp -> StringUtils.isNotEmpty(cp.getCandidateIdentification()))
                .collect(Collectors.toMap(CandidatePositionType::getCandidateListIdentification,
                        CandidatePositionType::getCandidateIdentification, (id1, id2) -> id1));

        // 2, decrypt file => map<countingCircle, map<ListCandidateId||CandidateId, count>> => map2
        path = inputDirectoryPath.resolve(Block4VerificationSuite.PATH_RESULTS);
        Results results = Deserializer.fromXml(path.toFile(), "evoting-decrypt_.*\\.xml", Results.class);
        Map<String, Map<String, Long>> mapDecrypt = results.getBallotsBox().stream()
                .flatMap(bb -> bb.getCountingCircle().stream())
                .map(cc -> {
                    String ccId = cc.getCountingCircleIdentification();

                    Map<String, Long> answerCount =
                            cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
                            .flatMap(e -> e.getBallot().stream())
                            .flatMap(b -> Stream.of(b.getChosenCandidateIdentification().stream(),
                                    b.getChosenCandidateListIdentification().stream(),
                                    b.getChosenWriteInsCandidateValue().stream()).flatMap(Function.identity()))
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                    return new AbstractMap.SimpleEntry<>(ccId, answerCount);
                })
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue,
                        (ccId1, ccId2) -> {
                            Map<String, Long> concat = new HashMap<>(ccId1);
                            ccId2.forEach((k, v) -> concat.merge(k, v, Long::sum));
                            return concat;
                        }
                ));


        path = inputDirectoryPath.resolve(Block4VerificationSuite.PATH_RESULTS);
        Delivery ech110 = Deserializer.fromXml(path.toFile(), "eCH-0110_.*\\.xml", Delivery.class);
        ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
            String ccId = cc.getCountingCircle().getCountingCircleId();
            cc.getElectionResults().stream()
                    .filter(er -> er.getMajoralElection() != null)
                    .flatMap(er -> er.getMajoralElection().getCandidate().stream())
                    .forEach(c -> {
                        if (c.getCandidateInformation().isOfficialCandidateYesNo()) {
                            String cId = c.getCandidateInformation().getCandidateIdentification();
                            BigInteger decryptCount = getDecryptCount(mapConfig, mapDecrypt, ccId, cId);
                            if (!MathHelper.areEqual(c.getCountOfVotesTotal(), decryptCount)) {
                                throw buildVerificationFailureException(
                                        "The count of votes total for the candidate does not match in majoral election",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message",
                                        cId);
                            }
                        }
                    });
            cc.getElectionResults().stream()
                    .filter(er -> er.getProportionalElection() != null)
                    .flatMap(er -> er.getProportionalElection().getCandidate().stream())
                    .forEach(c -> {
                        if (c.getCandidateInformation().isOfficialCandidateYesNo()) {
                            String cId = c.getCandidateInformation().getCandidateIdentification();
                            BigInteger decryptCount = getDecryptCount(mapConfig, mapDecrypt, ccId, cId);
                            if (!MathHelper.areEqual(c.getCountOfVotesTotal(), decryptCount)) {
                                throw buildVerificationFailureException(
                                        "The count of votes total for the candidate does not match in proportional election",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message",
                                        cId);
                            }
                        }
                    });
        });

        //Write Ins
        // writeInsDecryptMap
        Map<String, Map<String, Long>> writeInsDecryptMap = results.getBallotsBox().stream()
                .flatMap(bb -> bb.getCountingCircle().stream())
                .map(cc -> {
                    String ccId = cc.getCountingCircleIdentification();

                    Map<String, Long> answerCount =
                            cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getElection().stream())
                            .flatMap(e -> e.getBallot().stream())
                            .flatMap(b -> b.getChosenWriteInsCandidateValue().stream())
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                    return new AbstractMap.SimpleEntry<>(ccId, answerCount);
                })
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue,
                        (ccId1, ccId2) -> {
                            Map<String, Long> concat = new HashMap<>(ccId1);
                            ccId2.forEach((k, v) -> concat.merge(k, v, Long::sum));
                            return concat;
                        }
                ));

        // writeInsEch110Map
        CountMap<String> writeInsEch110Map = new CountMap<>();
        //check writeIns content, fill writeInsEch110Map
        ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
            String ccId = cc.getCountingCircle().getCountingCircleId();
            cc.getElectionResults().stream()
                    .filter(er -> er.getMajoralElection() != null)
                    .flatMap(er -> er.getMajoralElection().getCandidate().stream())
                    .filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
                    .forEach(c -> {
                        String cId =
                                c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
                        Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
                        writeInsEch110Map.increment(cId);
                        if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
                            if (!writeInsAndCount.containsKey(cId)) {
                                throw buildVerificationFailureException(
                                        "The count for the candidate does not match in writeIns-containsKey majoral election",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message",
                                        cId);
                            }
                        }
                    });
            cc.getElectionResults().stream()
                    .filter(er -> er.getProportionalElection() != null)
                    .flatMap(er -> er.getProportionalElection().getCandidate().stream())
                    .filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
                    .forEach(c -> {
                        String cId =
                                c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
                        Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
                        writeInsEch110Map.increment(cId);
                        if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
                            if (!writeInsAndCount.containsKey(cId)) {
                                throw buildVerificationFailureException(
                                        "The count for the candidate does not match in writeIns-containsKey proportional election",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message", cId);
                            }
                        }
                    });
        });

        //check writeIns count
        ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
            String ccId = cc.getCountingCircle().getCountingCircleId();
            cc.getElectionResults().stream()
                    .filter(er -> er.getMajoralElection() != null)
                    .flatMap(er -> er.getMajoralElection().getCandidate().stream())
                    .filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
                    .forEach(c -> {
                        String cId =
                                c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
                        Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
                        if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
                            if (!writeInsAndCount.get(cId).equals(writeInsEch110Map.get(cId))) {
                                throw buildVerificationFailureException(
                                        "The count for the candidate does not match in writeIns-equals majoral election",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message", cId);
                            }
                        }
                    });
            cc.getElectionResults().stream()
                    .filter(er -> er.getProportionalElection() != null)
                    .flatMap(er -> er.getProportionalElection().getCandidate().stream())
                    .filter(c -> !c.getCandidateInformation().isOfficialCandidateYesNo())
                    .forEach(c -> {
                        String cId =
                                c.getCandidateInformation().getFamilyName() + " " + c.getCandidateInformation().getCallName();
                        Map<String, Long> writeInsAndCount = writeInsDecryptMap.get(ccId);
                        if (writeInsAndCount != null && !writeInsAndCount.isEmpty()) {
                            if (!writeInsAndCount.get(cId).equals(writeInsEch110Map.get(cId))) {
                                throw buildVerificationFailureException(
                                        "The count for the candidate does not match in writeIns-equals proportional election",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message", cId);
                            }
                        }
                    });
        });

        result.setStatus(Status.OK);
        return result;
    }

    private BigInteger getDecryptCount(Map<String, String> mapConfig, Map<String, Map<String, Long>> mapDecrypt,
                                       String ccId, String cId) {
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

}
