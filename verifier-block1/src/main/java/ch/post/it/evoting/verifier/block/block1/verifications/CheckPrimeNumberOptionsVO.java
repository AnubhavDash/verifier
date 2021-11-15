/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.block.block1.verifications;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ch.evoting.xmlns.config._4.Configuration;
import ch.evoting.xmlns.config._4.ListType;
import ch.evoting.xmlns.config._4.StandardAnswerType;
import ch.evoting.xmlns.config._4.TiebreakAnswerType;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.AbstractVerification;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationTrait;
import ch.post.it.evoting.verifier.common.block.dto.revised.CandidateList;
import ch.post.it.evoting.verifier.common.block.dto.revised.DomainOfInfluence;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.dto.revised.VoteOption;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathService;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;
import ch.post.it.evoting.verifier.common.event.VerifierEvent;

import lombok.Getter;
import lombok.Setter;

@Component
public class CheckPrimeNumberOptionsVO extends AbstractVerification {

	private final PathService pathService;

	public CheckPrimeNumberOptionsVO(final PathService pathService, final ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.pathService = pathService;
	}

	@Override
	public VerificationDefinition getVerificationDefinition() {
		final var definition = new VerificationDefinition();
		definition.setBlockId(1);
		definition.setCategory(Category.INTEGRITY);
		definition.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
				"verification09.description"));
		definition.setId(9);
		definition.setName("checkPrimeNumberOptions([vo])");
		definition.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
		definition.addVerificationTrait(VerificationTrait.BLOCK_1);
		return definition;
	}

	@Override
	public VerificationResultEvent verify(final VerifierEvent event) {
		final var inputDirectoryPath = event.getInputDirectoryPath();

		final var configPathNode = pathService.buildFromRootPath(StructureKey.CONFIG_ANONYMIZED, inputDirectoryPath);
		final Configuration configuration;
		try {
			configuration = Deserializer.fromXml(configPathNode.getPath(), Configuration.class);
		} catch (IOException | JAXBException | XMLStreamException e) {
			throw new VerificationPreconditionException("Failed to deserialize anonymized config.", e);
		}

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
					var electionDetail = new ElectionDetail();

					int candidateCount = ei.getCandidate().size();
					BigInteger numberOfMandates = ei.getElection().getNumberOfMandates();
					boolean writeInsAllowed = ei.getElection().isWriteInsAllowed();
					BigInteger candidateAccumulation = ei.getElection().getCandidateAccumulation();

					BigInteger optionCount =
							(candidateAccumulation.multiply(BigInteger.valueOf(candidateCount)))
									.add(numberOfMandates.multiply(BigInteger.valueOf(1L + (writeInsAllowed ? 1L : 0L))));
					electionDetail.setOptionCount(optionCount.intValue());

					boolean candidateOnlyElection = ei.getList().stream().allMatch(ListType::isListEmpty) && ei.getList().size() == 1;
					electionDetail.setListCount(candidateOnlyElection ? 0 : ei.getList().size());

					return new AbstractMap.SimpleEntry<>(ei.getElection().getElectionIdentification(), electionDetail);
				}).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

		// Check correspondences between config and dataConfig.
		final var dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
		final ElectionEvent electionEvent;
		try {
			electionEvent = Deserializer.fromJson(dataConfigPathNode.getPath(), ElectionEvent.class);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to deserialize data config updated.", e);
		}

		electionEvent.getBallotBoxes().stream()
				.flatMap(bb -> bb.getCountingCircles().stream())
				.flatMap(cc -> cc.getDomainsOfInfluence().stream())
				.forEach((DomainOfInfluence doi) -> {
					doi.getVotes().forEach(v -> {
						String voteIdentification = v.getAlias();
						if (!voteAnswersCount.containsKey(voteIdentification)) {
							throw new VerificationFailureException(
									"alias does not correspond to voteIdentification",
									TranslationHelper.getFromResourceBundle(
											Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
											"verification09.nok.message.alias.vote.nok",
											voteIdentification)
							);
						}

						List<BigInteger> options =
								v.getQuestions().stream().flatMap(q -> q.getOptions().stream()).map(VoteOption::getPrimeNumber)
										.collect(Collectors.toList());
						long optionsDistinctCount =
								v.getQuestions().stream().flatMap(q -> q.getOptions().stream()).map(VoteOption::getPrimeNumber).distinct().count();
						if (options.size() != optionsDistinctCount) {
							throw new VerificationFailureException(
									"the prime number fields are not mutually distinct",
									TranslationHelper.getFromResourceBundle(
											Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
											"verification09.nok.message.prime.number.mutually.distinct.nok",
											getDuplicates(options).toString())
							);
						}

						if (options.size() != voteAnswersCount.get(voteIdentification)) {
							throw new VerificationFailureException(
									"The number of prime numbers does not correspond to the number of answerElements",
									TranslationHelper.getFromResourceBundle(
											Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
											"verification09.nok.message.number.of.prime.number.and.answer.nok")
							);
						}
					});

					doi.getElections().forEach(e -> {
						String electionIdentification = e.getAlias();
						if (!electionOptionCount.containsKey(electionIdentification)) {
							throw new VerificationFailureException(
									"alias does not correspond to electionIdentification",
									TranslationHelper.getFromResourceBundle(
											Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
											"verification09.nok.message.alias.election.nok",
											electionIdentification)
							);
						}

						List<BigInteger> listCount = e.getLists().stream().map(CandidateList::getPrimeNumber).collect(Collectors.toList());
						long listDistinctCount = e.getLists().stream().map(CandidateList::getPrimeNumber).distinct().count();
						if (listCount.size() != listDistinctCount) {
							throw new VerificationFailureException(
									"The prime numbers are repeated",
									TranslationHelper.getFromResourceBundle(
											Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
											"verification09.nok.message",
											getDuplicates(listCount).toString())
							);
						}
						if (listCount.size() != electionOptionCount.get(electionIdentification).getListCount()) {
							throw new VerificationFailureException(
									"The number of distinct prime numbers does not correspond to the number of list elements",
									TranslationHelper.getFromResourceBundle(
											Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
											"verification09.nok.message.number.of.distinct.prime.number.and.lists.nok")
							);
						}
						long optionDistinctCount = e.getLists().stream()
								.flatMap(l -> l.getCandidatePositions().stream())
								.flatMap(cp -> cp.getPrimeNumbers().stream()).distinct().count();
						long optionCandidateOnlyCount = e.getCandidates().stream()
								.flatMap(c -> c.getPrimeNumbers().stream()).distinct().count();
						int writeInsCount = e.getWriteIns().size();

						if ((optionDistinctCount + writeInsCount + optionCandidateOnlyCount) != electionOptionCount.get(electionIdentification)
								.getOptionCount()) {
							throw new VerificationFailureException(
									"The number of candidate prime numbers does not correspond to the expected number of voting options " +
											"for candidates",
									TranslationHelper.getFromResourceBundle(
											Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
											"verification09.nok.message.number.of.candidate.prime.number.and.vo.nok",
											String.valueOf((optionDistinctCount + writeInsCount + optionCandidateOnlyCount)),
											String.valueOf(electionOptionCount.get(electionIdentification).getOptionCount()))
							);
						}
					});
				});

		return VerificationResultEvent.success(this, getVerificationDefinition());
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
