/*
 * (c) Copyright 2024 Swiss Post Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static ch.post.it.evoting.evotinglibraries.domain.election.ElectionAttributesAliasConstants.ALIAS_JOIN_DELIMITER;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getAnswerInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getBlankCandidatePositionInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getCandidateInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getListInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getWriteInPositionInformation;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Predicate.not;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AnswerInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType.BallotQuestionInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType.ListDescriptionInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

public class VerifyPrimesMappingTableConsistencyAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyPrimesMappingTableConsistencyAlgorithm.class);

	/**
	 * Verifies that all PrimesMappingTables are consistent.
	 * <ul>
	 *     <li>A PrimesMappingTable must not contain duplicate encoded voting options. This is ensured by {@link PrimesMappingTable#from(List)}</li>
	 *     <li>The same encoded voting option must have the same actual voting option in each table</li>
	 *     <li>The same actual voting option must have the same semantic information in each table</li>
	 *     <li>The actual voting options and semantic information in the pTable correspond to the configuration XML</li>
	 *     <li>The number of tuples in the pTable correspond to the configuration XML taking into account possible accumulation of candidates.
	 * </ul>
	 *
	 * @param primesMappingTables the list of PrimesMappingTables, one per verification card set. Must be non-null and not empty.
	 * @param configuration       the configuration XML. Must be non-null.
	 * @return {@code true} if the PrimesMappingTables are consistent, {@code false} otherwise}
	 */
	public boolean verifyPrimesMappingTableConsistency(final List<PrimesMappingTable> primesMappingTables, final Configuration configuration) {
		checkNotNull(primesMappingTables);
		checkArgument(!primesMappingTables.isEmpty());
		primesMappingTables.stream().parallel().forEach(Preconditions::checkNotNull);
		checkNotNull(configuration);

		// Join the PrimesMappingTables of all verification card sets, deleting duplicates.
		final Set<PrimesMappingTableEntry> primesMappingTableEntries = List.copyOf(primesMappingTables).stream()
				.parallel()
				.map(PrimesMappingTable::getPTable)
				.flatMap(GroupVector::stream)
				.collect(Collectors.toSet());

		// Create the actual voting option and semantic information mapping using the configuration XML.
		final Set<PartialPrimesMappingTableEntry> election = getElectionPartialPrimesMappingTableEntries(configuration);
		final Set<PartialPrimesMappingTableEntry> vote = getVotePartialPrimesMappingTableEntries(configuration);
		final Set<PartialPrimesMappingTableEntry> configurationPartialPrimesMappingTableEntries = Stream.of(election, vote)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());

		final List<TriFunction<Set<PrimesMappingTableEntry>, Configuration, Set<PartialPrimesMappingTableEntry>, Boolean>> consistencyVerifications = new ArrayList<>();
		consistencyVerifications.add(this::verifyCorrectMappingInAllVerificationCardSets);
		consistencyVerifications.add(this::verifyInformationCorrespondsToConfiguration);
		consistencyVerifications.add(this::verifyNumberOfTuplesCorrespondsToConfiguration);

		return consistencyVerifications
				.stream()
				.parallel()
				.map(f -> f.apply(primesMappingTableEntries, configuration, configurationPartialPrimesMappingTableEntries))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	/**
	 * Verifies that the same encoded voting option has the same actual voting option and that the same actual voting option has the same semantic
	 * information in all PrimesMappingTables.
	 *
	 * @param configuration                                 ignored, needed for consistency in the signature of the verification methods.
	 * @param configurationPartialPrimesMappingTableEntries ignored, needed for consistency in the signature of the verification methods.
	 */
	@SuppressWarnings("java:S1172")
	private boolean verifyCorrectMappingInAllVerificationCardSets(final Set<PrimesMappingTableEntry> primesMappingTableEntries,
			final Configuration configuration, final Set<PartialPrimesMappingTableEntry> configurationPartialPrimesMappingTableEntries) {
		final Set<PrimeGqElement> encodedVotingOptions = primesMappingTableEntries.stream()
				.parallel()
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.collect(Collectors.toSet());

		final boolean correctMapping = primesMappingTableEntries.size() == encodedVotingOptions.size();
		if (!correctMapping) {
			LOGGER.error(
					"The encoded voting options, actual voting options and semantic information mapping is not the same in all verification card sets.");
		}
		return correctMapping;
	}

	/**
	 * Verifies that the actual voting options and semantic information in the PrimesMappingTable correspond to the configuration XML.
	 *
	 * @param configuration ignored, needed for consistency in the signature of the verification methods.
	 */
	@SuppressWarnings("java:S1172")
	private boolean verifyInformationCorrespondsToConfiguration(final Set<PrimesMappingTableEntry> primesMappingTableEntries,
			final Configuration configuration, final Set<PartialPrimesMappingTableEntry> configurationPartialPrimesMappingTableEntries) {
		final Set<PartialPrimesMappingTableEntry> partialPrimesMappingTableEntries = primesMappingTableEntries.stream()
				.parallel()
				.map(entry -> new PartialPrimesMappingTableEntry(entry.actualVotingOption(), entry.semanticInformation()))
				.collect(Collectors.toSet());

		final boolean informationCorrespondsToConfiguration = partialPrimesMappingTableEntries.equals(configurationPartialPrimesMappingTableEntries);
		if (!informationCorrespondsToConfiguration) {
			LOGGER.error("The actual voting options and semantic information do not correspond to the configuration XML.");
		}
		return informationCorrespondsToConfiguration;
	}

	/**
	 * Verifies that the number of tuples in the pTable corresponds to the configuration XML.
	 */
	private boolean verifyNumberOfTuplesCorrespondsToConfiguration(final Set<PrimesMappingTableEntry> primesMappingTableEntries,
			final Configuration configuration, final Set<PartialPrimesMappingTableEntry> configurationPartialPrimesMappingTableEntries) {
		final int expectedPrimesMappingTableEntriesSize = configurationPartialPrimesMappingTableEntries.size();

		final boolean numberOfTuplesCorrespondsToConfiguration = primesMappingTableEntries.size() == expectedPrimesMappingTableEntriesSize;
		if (!numberOfTuplesCorrespondsToConfiguration) {
			LOGGER.error(
					"The number of tuples in the pTable does not correspond to the configuration XML.");
		}

		return numberOfTuplesCorrespondsToConfiguration;
	}

	private Set<PartialPrimesMappingTableEntry> getElectionPartialPrimesMappingTableEntries(final Configuration configuration) {
		return configuration.getContest().getElectionGroupBallot().stream()
				.parallel()
				.map(ElectionGroupBallotType::getElectionInformation)
				.flatMap(List::stream)
				.map(electionInformationType -> {
					final ElectionType election = electionInformationType.getElection();
					final String electionIdentification = election.getElectionIdentification();
					final Map<Boolean, List<ListType>> isEmptyList = electionInformationType.getList().stream()
							.collect(Collectors.partitioningBy(ListType::isListEmpty));

					final Set<PartialPrimesMappingTableEntry> emptyList = getEmptyListEntries(electionInformationType, isEmptyList.get(true));
					final Set<PartialPrimesMappingTableEntry> nonEmptyLists = getNonEmptyListsEntries(electionIdentification, isEmptyList.get(false));
					final Set<PartialPrimesMappingTableEntry> candidates = getCandidatesEntries(electionInformationType);
					final Set<PartialPrimesMappingTableEntry> writeIns = getWriteInsEntries(electionInformationType);

					return Stream.of(emptyList, nonEmptyLists, candidates, writeIns)
							.flatMap(Set::stream)
							.collect(Collectors.toSet());
				})
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	private static Set<PartialPrimesMappingTableEntry> getEmptyListEntries(final ElectionInformationType electionInformationType,
			final List<ListType> emptyLists) {
		final ListType emptyList = emptyLists.stream().collect(MoreCollectors.onlyElement());
		final ElectionType election = electionInformationType.getElection();
		final Set<PartialPrimesMappingTableEntry> emptyListEntries = new HashSet<>();

		// empty position
		emptyList.getCandidatePosition()
				.forEach(candidatePositionType -> {
					final String candidateListIdentification = candidatePositionType.getCandidateListIdentification();
					final String emptyPositionSemanticInformation = getBlankCandidatePositionInformation(candidatePositionType.getPositionOnList());
					emptyListEntries.add(new PartialPrimesMappingTableEntry(candidateListIdentification, emptyPositionSemanticInformation));
				});

		// empty list - only added if an election includes at least one non-empty list (otherwise it would be a candidate-only election without the possibility of selecting lists)
		final boolean electionWithLists = electionInformationType.getList().stream().parallel().anyMatch(not(ListType::isListEmpty));
		if (electionWithLists) {
			emptyListEntries.add(getListEntry(election.getElectionIdentification(), emptyList));
		}

		return emptyListEntries;
	}

	private static Set<PartialPrimesMappingTableEntry> getNonEmptyListsEntries(final String electionIdentification, final List<ListType> nonEmpty) {
		return nonEmpty.stream()
				.parallel()
				.map(listType -> getListEntry(electionIdentification, listType))
				.collect(Collectors.toSet());
	}

	private static PartialPrimesMappingTableEntry getListEntry(final String electionIdentification, final ListType listType) {
		final String actualVotingOption = String.join(ALIAS_JOIN_DELIMITER, electionIdentification, listType.getListIdentification());
		final String semanticInformation = getListInformation(listType.isListEmpty(), listType.getListDescription().getListDescriptionInfo(),
				ListDescriptionInfo::getListDescription);
		return new PartialPrimesMappingTableEntry(actualVotingOption, semanticInformation);
	}

	private static Set<PartialPrimesMappingTableEntry> getCandidatesEntries(final ElectionInformationType electionInformationType) {
		if (electionInformationType.getCandidate() == null) {
			return Set.of();
		}

		return electionInformationType.getCandidate().stream()
				.parallel()
				.flatMap(candidateType ->
						IntStream.range(0, electionInformationType.getElection().getCandidateAccumulation().intValue())
								.mapToObj(String::valueOf)
								.map(acc -> String.join(ALIAS_JOIN_DELIMITER, electionInformationType.getElection().getElectionIdentification(),
										candidateType.getCandidateIdentification(), acc))
								.map(actualVotingOption -> {
									final String semanticInformation = getCandidateInformation(candidateType.getFamilyName(),
											candidateType.getFirstName(), candidateType.getCallName(), candidateType.getDateOfBirth().toXMLFormat());
									return new PartialPrimesMappingTableEntry(actualVotingOption, semanticInformation);
								})
				).collect(Collectors.toSet());
	}

	private static Set<PartialPrimesMappingTableEntry> getWriteInsEntries(final ElectionInformationType electionInformationType) {
		// write-ins position - only added if write-ins are allowed for an election
		return electionInformationType.getWriteInCandidate().stream()
				.map(writeInCandidate -> {
					final String writeInsPositionAlias = String.join(ALIAS_JOIN_DELIMITER,
							electionInformationType.getElection().getElectionIdentification(),
							writeInCandidate.getWriteInCandidateIdentification());
					final String writeInsPositionSemanticInformation = getWriteInPositionInformation(writeInCandidate.getPosition());
					return new PartialPrimesMappingTableEntry(writeInsPositionAlias, writeInsPositionSemanticInformation);
				}).collect(Collectors.toSet());
	}

	private Set<PartialPrimesMappingTableEntry> getVotePartialPrimesMappingTableEntries(final Configuration configuration) {
		return configuration.getContest().getVoteInformation().stream()
				.parallel()
				.map(VoteInformationType::getVote)
				.map(voteInformationType -> {
					final String voteIdentification = voteInformationType.getVoteIdentification();
					return voteInformationType.getBallot().stream()
							.parallel()
							.map(ballotType -> {
								final Set<PartialPrimesMappingTableEntry> standardBallotAnswers = getStandardBallotAnswersEntries(voteIdentification,
										ballotType);
								final Set<PartialPrimesMappingTableEntry> variantBallotStandardAnswers = getVariantBallotStandardAnswersEntries(
										voteIdentification, ballotType);
								final Set<PartialPrimesMappingTableEntry> tieBreakAnswers = getTieBreakAnswersEntries(voteIdentification, ballotType);

								return Stream.of(standardBallotAnswers, variantBallotStandardAnswers, tieBreakAnswers)
										.flatMap(Set::stream)
										.collect(Collectors.toSet());
							}).flatMap(Set::stream)
							.collect(Collectors.toSet());
				})
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	private static Set<PartialPrimesMappingTableEntry> getStandardBallotAnswersEntries(final String voteIdentification, final BallotType ballotType) {
		final StandardBallotType standardBallot = ballotType.getStandardBallot();
		if (standardBallot == null) {
			return Set.of();
		}

		return getStandardAnswersEntries(voteIdentification, standardBallot.getBallotQuestion(), standardBallot.getAnswer())
				.collect(Collectors.toSet());
	}

	private static Set<PartialPrimesMappingTableEntry> getVariantBallotStandardAnswersEntries(final String voteIdentification,
			final BallotType ballotType) {
		final VariantBallotType variantBallot = ballotType.getVariantBallot();
		if (variantBallot == null) {
			return Set.of();
		}

		return variantBallot.getStandardQuestion().stream()
				.parallel()
				.flatMap(standardQuestionType -> getStandardAnswersEntries(voteIdentification, standardQuestionType.getBallotQuestion(),
						standardQuestionType.getAnswer()))
				.collect(Collectors.toSet());
	}

	private static Stream<PartialPrimesMappingTableEntry> getStandardAnswersEntries(final String voteIdentification,
			final BallotQuestionType ballotQuestionType, final List<StandardAnswerType> standardAnswerTypes) {
		return standardAnswerTypes.stream()
				.parallel()
				.map(standardAnswerType -> {
					final String actualVotingOption = String.join(ALIAS_JOIN_DELIMITER, voteIdentification,
							standardAnswerType.getAnswerIdentification());
					final String semanticInformation = getAnswerInformation(standardAnswerType.isHiddenAnswer(),
							ballotQuestionType.getBallotQuestionInfo(), BallotQuestionInfo::getBallotQuestion,
							standardAnswerType.getAnswerInfo(), AnswerInformationType::getAnswer);
					return new PartialPrimesMappingTableEntry(actualVotingOption, semanticInformation);
				});
	}

	private static Set<PartialPrimesMappingTableEntry> getTieBreakAnswersEntries(final String voteIdentification, final BallotType ballotType) {
		final VariantBallotType variantBallot = ballotType.getVariantBallot();
		if (variantBallot == null || variantBallot.getTieBreakQuestion() == null) {
			return Set.of();
		}

		return variantBallot.getTieBreakQuestion().stream()
				.parallel()
				.flatMap(tieBreakQuestionType -> tieBreakQuestionType.getAnswer().stream()
						.parallel()
						.map(tiebreakAnswerType -> {
							final String actualVotingOption = String.join(ALIAS_JOIN_DELIMITER, voteIdentification,
									tiebreakAnswerType.getAnswerIdentification());
							final String semanticInformation = getAnswerInformation(tiebreakAnswerType.isHiddenAnswer(),
									tieBreakQuestionType.getBallotQuestion().getBallotQuestionInfo(), BallotQuestionInfo::getBallotQuestion,
									tiebreakAnswerType.getAnswerInfo(), AnswerInformationType::getAnswer);
							return new PartialPrimesMappingTableEntry(actualVotingOption, semanticInformation);
						}))
				.collect(Collectors.toSet());
	}

	private record PartialPrimesMappingTableEntry(String actualVotingOption, String semanticInformation) {
	}
}
