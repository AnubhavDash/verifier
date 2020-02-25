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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.evoting.xmlns.config._4.Configuration;
import ch.evoting.xmlns.config._4.ListType;
import ch.evoting.xmlns.config._4.StandardAnswerType;
import ch.evoting.xmlns.config._4.TiebreakAnswerType;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.CandidateList;
import ch.post.it.evoting.verifier.common.block.dto.revised.DomainOfInfluence;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.dto.revised.VoteOption;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckPrimeNumberOptionsVO extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification09.description"));
        def.setId(9);
        def.setName("checkPrimeNumberOptions([vo])");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        final PathNode configPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
        Configuration configuration = Deserializer.fromXml(configPathNode.getPath(), Configuration.class);

        // Vote.
        Map<String, Long> voteAnswersCount = configuration.getContest().getVoteInformation().stream()
                .map(vi -> {
                    String id = vi.getVote().getVoteIdentification();
                    long nbAnswer = vi.getVote().getBallot().stream()
                            .flatMap(b -> {
                                if (b.getStandardBallot() != null) {
                                    return b.getStandardBallot().getAnswer().stream();
                                } else {
                                    Stream<StandardAnswerType> s1 =
                                            b.getVariantBallot().getStandardQuestion().stream().flatMap(sq -> sq.getAnswer().stream());
                                    Stream<TiebreakAnswerType> s2 =
                                            b.getVariantBallot().getTieBreakQuestion().stream().flatMap(tq -> tq.getAnswer().stream());
                                    return Stream.concat(s1, s2);
                                }
                            }).count();

                    return new AbstractMap.SimpleEntry<>(id, nbAnswer);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        // Election.
        Map<String, ElectionDetail> electionOptionCount = configuration.getContest().getElectionInformation().stream()
                .map(ei -> {
                    ElectionDetail electionDetail = new ElectionDetail();

                    int candidateCount = ei.getCandidate().size();
                    BigInteger numberOfMandates = ei.getElection().getNumberOfMandates();
                    boolean writeInsAllowed = ei.getElection().isWriteInsAllowed();
                    BigInteger candidateAccumulation = ei.getElection().getCandidateAccumulation();

                    BigInteger optionCount =
                            (candidateAccumulation.multiply(BigInteger.valueOf(candidateCount))).add(numberOfMandates.multiply(BigInteger.valueOf(1 + (writeInsAllowed ? 1 : 0))));
                    electionDetail.setOptionCount(optionCount.intValue());

                    boolean candidateOnlyElection = ei.getList().stream().allMatch(ListType::isListEmpty) && ei.getList().size() == 1;
                    electionDetail.setListCount(candidateOnlyElection ? 0 : ei.getList().size());

                    return new AbstractMap.SimpleEntry<>(ei.getElection().getElectionIdentification(), electionDetail);
                }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        // Check correspondences between config and dataConfig.
        final PathNode dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
        ElectionEvent electionEvent = Deserializer.fromJson(dataConfigPathNode.getPath(), ElectionEvent.class);

        electionEvent.getBallotBoxes().stream()
                .flatMap(bb -> bb.getCountingCircles().stream())
                .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                .forEach((DomainOfInfluence doi) -> {
                    doi.getVotes().forEach(v -> {
                        String voteIdentification = v.getAlias();
                        if (!voteAnswersCount.containsKey(voteIdentification)) {
                            throw buildVerificationFailureException(
                                    "alias does not correspond to voteIdentification",
                                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification09.nok.message.alias.vote.nok",
                                    voteIdentification
                            );
                        }

                        List<BigInteger> options =
                                v.getQuestions().stream().flatMap(q -> q.getOptions().stream()).map(VoteOption::getPrimeNumber).collect(Collectors.toList());
                        long optionsDistinctCount =
                                v.getQuestions().stream().flatMap(q -> q.getOptions().stream()).map(VoteOption::getPrimeNumber).distinct().count();
                        if (options.size() != optionsDistinctCount) {
                            throw buildVerificationFailureException(
                                    "the prime number fields are not mutually distinct",
                                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification09.nok.message.prime.number.mutually.distinct.nok",
                                    getDuplicates(options).toString()
                            );
                        }

                        if (options.size() != voteAnswersCount.get(voteIdentification)) {
                            throw buildVerificationFailureException(
                                    "The number of prime numbers does not correspond to the number of answerElements",
                                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification09.nok.message.number.of.prime.number.and.answer.nok"
                            );
                        }
                    });

                    doi.getElections().forEach(e -> {
                        String electionIdentification = e.getAlias();
                        if (!electionOptionCount.containsKey(electionIdentification)) {
                            throw buildVerificationFailureException(
                                    "alias does not correspond to electionIdentification",
                                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification09.nok.message.alias.election.nok",
                                    electionIdentification
                            );
                        }

                        List<BigInteger> listCount = e.getLists().stream().map(CandidateList::getPrimeNumber).collect(Collectors.toList());
                        long listDistinctCount = e.getLists().stream().map(CandidateList::getPrimeNumber).distinct().count();
                        if (listCount.size() != listDistinctCount) {
                            throw buildVerificationFailureException(
                                    "The prime numbers are repeated",
                                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification09.nok.message",
                                    getDuplicates(listCount).toString()
                            );
                        }
                        if (listCount.size() != electionOptionCount.get(electionIdentification).getListCount()) {
                            throw buildVerificationFailureException(
                                    "The number of distinct prime numbers does not correspond to the number of list elements",
                                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification09.nok.message.number.of.distinct.prime.number.and.lists.nok"
                            );
                        }
                        long optionDistinctCount = e.getLists().stream()
                                .flatMap(l -> l.getCandidatePositions().stream())
                                .flatMap(cp -> cp.getPrimeNumbers().stream()).distinct().count();
                        long optionCandidateOnlyCount = e.getCandidates().stream()
                                .flatMap(c -> c.getPrimeNumbers().stream()).distinct().count();
                        int writeInsCount = e.getWriteIns().size();

                        if ((optionDistinctCount + writeInsCount + optionCandidateOnlyCount) != electionOptionCount.get(electionIdentification).getOptionCount()) {
                            throw buildVerificationFailureException(
                                    "The number of candidate prime numbers does not correspond to the expected number of voting options " +
                                            "for candidates",
                                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification09.nok.message.number.of.candidate.prime.number.and.vo.nok",
                                    String.valueOf((optionDistinctCount + writeInsCount + optionCandidateOnlyCount)),
                                    String.valueOf(electionOptionCount.get(electionIdentification).getOptionCount())
                            );
                        }
                    });
                });

        result.setStatus(Status.OK);
        return result;
    }

    private <T> List<T> getDuplicates(List<T> entries) {
        return entries.stream()
                .distinct()
                .map(e -> new AbstractMap.SimpleEntry<>(e, entries.stream().filter(e::equals).count()))
                .filter(e -> e.getValue() > 1)
                .map(AbstractMap.SimpleEntry::getKey)
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    private static class ElectionDetail {
        private int optionCount;
        private int listCount;
    }

}
