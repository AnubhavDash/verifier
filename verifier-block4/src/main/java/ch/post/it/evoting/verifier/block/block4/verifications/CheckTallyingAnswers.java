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
import ch.evoting.xmlns.config._4.Configuration;
import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import com.scytl.xmlns.decrypt._1.Results;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CheckTallyingAnswers extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(4);
        def.setCategory(Category.COMPLETENESS);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification02.description"));
        def.setId(2);
        def.setName("checkTallyingAnswers");
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        // 1, config file => map<Tuple<Qid, Atype>, answerId> => map1
        // 2, decrypt file => map<countingCircle, map<answerId, count>> => map2
        // 3, ech0110 file foreach cc do a loop for each Question get the count
        // ask the map1 and get the right answerId
        // check the count by asking map2

        // 1, config file => map<Tuple<Qid, Atype>, answerId> => map1
        PathNode configurationAnonymizedPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
        Configuration configuration = Deserializer.fromXml(configurationAnonymizedPathNode.getPath(), Configuration.class);
        Map<Map.Entry<String, String>, String> mapConfig = configuration.getContest().getVoteInformation().stream()
                .flatMap(vi -> vi.getVote().getBallot().stream())
                .flatMap(b -> {
                    List<AbstractMap.SimpleEntry<AbstractMap.SimpleEntry<String, String>, String>> answers = new LinkedList<>();

                    if (b.getStandardBallot() != null) {
                        String qId = b.getStandardBallot().getQuestionIdentification();
                        answers.addAll(b.getStandardBallot().getAnswer().stream().map(a -> {
                            String aType = a.getStandardAnswerType();
                            String answerId = a.getAnswerIdentification();
                            AbstractMap.SimpleEntry<String, String> se = new AbstractMap.SimpleEntry<>(qId, aType);
                            return new AbstractMap.SimpleEntry<>(se, answerId);
                        }).collect(Collectors.toList()));
                    }

                    if (b.getVariantBallot() != null) {
                        answers.addAll(b.getVariantBallot().getStandardQuestion().stream().flatMap(q -> {
                            String qId = q.getQuestionIdentification();
                            return q.getAnswer().stream().map(a -> {
                                String aType = a.getStandardAnswerType();
                                String answerId = a.getAnswerIdentification();
                                AbstractMap.SimpleEntry<String, String> se = new AbstractMap.SimpleEntry<>(qId, aType);
                                return new AbstractMap.SimpleEntry<>(se, answerId);
                            });
                        }).collect(Collectors.toList()));
                    }

                    return answers.stream();
                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        // 2, decrypt file => map<countingCircle, map<answerId, count>> => map2
        PathNode eVotingDecryptResultPathNode = pathService.buildFromRootPath(StructureKey.EVOTING_DECRYPT_RESULT, inputDirectoryPath);
        Results results = Deserializer.fromXml(eVotingDecryptResultPathNode.getPath(), Results.class);
        Map<String, Map<String, Long>> mapDecrypt = results.getBallotsBox().stream()
                .flatMap(bb -> bb.getCountingCircle().stream())
                .map(cc -> {
                    String ccId = cc.getCountingCircleIdentification();

                    Map<String, Long> answerCount = cc.getDomainOfInfluence().stream().flatMap(doi -> doi.getVote().stream())
                            .flatMap(v -> v.getBallot().stream())
                            .flatMap(b -> b.getChosenAnswerIdentification().stream())
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

        // 3, ech0110 file foreach cc do a loop for each Question get the count
        PathNode eCH0110PathNode = pathService.buildFromRootPath(StructureKey.ECH0110, inputDirectoryPath);
        Delivery ech110 = Deserializer.fromXml(eCH0110PathNode.getPath(), Delivery.class);
        ech110.getResultDelivery().getCountingCircleResults().forEach(cc -> {
            String ccId = cc.getCountingCircle().getCountingCircleId();
            cc.getVoteResults().stream().flatMap(vr -> vr.getBallotResult().stream())
                    .forEach(br -> {
                        BigInteger nbUnaccountedBlanks = br.getCountOfUnaccountedBlankBallots().getTotal();
                        if (br.getStandardBallot() != null) {
                            String qId = br.getStandardBallot().getQuestionIdentification();
                            if (!MathHelper.areEqual(br.getStandardBallot().getCountOfAnswerYes().getTotal(),
                                    getDecryptCount(mapDecrypt, mapConfig, ccId, qId, "YES"))) {
                                throw buildVerificationFailureException(
                                        "Number of YES votes verification failed in standard ballot",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                                        "verification02.nok.message",
                                        qId,
                                        "YES"
                                );
                            }
                            if (!MathHelper.areEqual(br.getStandardBallot().getCountOfAnswerNo().getTotal(),
                                    getDecryptCount(mapDecrypt, mapConfig, ccId, qId, "NO"))) {
                                throw buildVerificationFailureException(
                                        "Number of NO votes verification failed in standard ballot",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                                        "verification02.nok.message",
                                        qId,
                                        "NO"
                                );
                            }
                            if (!MathHelper.areEqual(
                                    br.getStandardBallot().getCountOfAnswerEmpty().getTotal().add(nbUnaccountedBlanks),
                                    getDecryptCount(mapDecrypt, mapConfig, ccId, qId, "EMPTY"))) {
                                throw buildVerificationFailureException(
                                        "Number of EMPTY votes verification failed in standard ballot",
                                        Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                                        "verification02.nok.message",
                                        qId,
                                        "EMPTY"
                                );
                            }
                        }
                        if (br.getVariantBallot() != null) {
                            br.getVariantBallot().getQuestionInformation().forEach(q -> {
                                String qId = q.getQuestionIdentification();
                                if (!MathHelper.areEqual(q.getCountOfAnswerYes().getTotal(),
                                        getDecryptCount(mapDecrypt, mapConfig, ccId, qId, "YES"))) {
                                    throw buildVerificationFailureException(
                                            "Number of YES votes verification failed in variant ballot",
                                            Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                                            "verification02.nok.message",
                                            qId,
                                            "YES"
                                    );
                                }
                                if (!MathHelper.areEqual(q.getCountOfAnswerNo().getTotal(),
                                        getDecryptCount(mapDecrypt, mapConfig, ccId, qId, "NO"))) {
                                    throw buildVerificationFailureException(
                                            "Number of NO votes verification failed in variant ballot",
                                            Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                                            "verification02.nok.message",
                                            qId,
                                            "NO"
                                    );
                                }
                                if (!MathHelper.areEqual(q.getCountOfAnswerEmpty().getTotal().add(nbUnaccountedBlanks),
                                        getDecryptCount(mapDecrypt, mapConfig, ccId, qId, "EMPTY"))) {
                                    throw buildVerificationFailureException(
                                            "Number of EMPTY votes verification failed in variant ballot",
                                            Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                                            "verification02.nok.message",
                                            qId,
                                            "EMPTY"
                                    );
                                }
                            });
                        }
                    });
        });

        result.setStatus(Status.OK);
        return result;
    }

    private BigInteger getDecryptCount(Map<String, Map<String, Long>> mapDecrypt, Map<Map.Entry<String, String>, String> mapConfig, String ccId, String qId, String answerType) {
        Map<String, Long> countByCC = mapDecrypt.get(ccId);
        String answerId = mapConfig.get(new AbstractMap.SimpleEntry<>(qId, answerType));

        if (countByCC == null) {
            throw new IllegalArgumentException("cannot find the decrypt data for given countingCircle : " + ccId);
        }
        if (StringUtils.isEmpty(answerId)) {
            throw new IllegalArgumentException(String.format("cannot find the answerId for answer %s on question %s", answerType, qId));
        }
        return countByCC.get(answerId) == null ? BigInteger.ZERO : BigInteger.valueOf(countByCC.get(answerId));
    }

    private class Test02Exception extends RuntimeException {
        private String questionId;
        private String answerType;

        Test02Exception(String questionIdentification, String answerType) {
            this.questionId = questionIdentification;
            this.answerType = answerType;
        }

        String getQuestionId() {
            return questionId;
        }

        String getAnswerType() {
            return answerType;
        }
    }

}
