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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.evotinglibraries.domain.election.ActualVotingOptionUtils.getAnswerActualVotingOption;
import static ch.post.it.evoting.evotinglibraries.domain.election.ActualVotingOptionUtils.getCandidateActualVotingOption;
import static ch.post.it.evoting.evotinglibraries.domain.election.ActualVotingOptionUtils.getEmptyPositionActualVotingOption;
import static ch.post.it.evoting.evotinglibraries.domain.election.ActualVotingOptionUtils.getListActualVotingOption;
import static ch.post.it.evoting.evotinglibraries.domain.election.ActualVotingOptionUtils.getWriteInPositionActualVotingOption;
import static ch.post.it.evoting.evotinglibraries.domain.election.CorrectnessInformationUtils.getCandidateCorrectnessInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.CorrectnessInformationUtils.getEmptyPositionCorrectnessInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.CorrectnessInformationUtils.getListCorrectnessInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.CorrectnessInformationUtils.getWriteInPositionCorrectnessInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getAnswerSemanticInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getCandidateSemanticInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getEmptyPositionSemanticInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getListSemanticInformation;
import static ch.post.it.evoting.evotinglibraries.domain.election.SemanticInformationUtils.getWriteInPositionSemanticInformation;
import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.AnswerInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotQuestionType.BallotQuestionInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.BallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionGroupBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ElectionType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.EmptyListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListDescriptionInformationType.ListDescriptionInfo;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.VoteInformationType;

@Service
public class VerifyPrimesMappingTableConsistencyAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyPrimesMappingTableConsistencyAlgorithm.class);

	/**
	 * Verifies that all PrimesMappingTables of the given election event context are consistent.
	 * <ul>
	 *     <li>A PrimesMappingTable must not contain duplicate encoded voting options.</li>
	 *     <li>The same actual voting option must have the same encoded voting option in each table.</li>
	 *     <li>The same actual voting option must have the same semantic information in each table.</li>
	 *     <li>The same actual voting option must have the same correctness information in each table.</li>
	 *     <li>The actual voting options, semantic information and correctness information in the pTable correspond to the configuration XML.</li>
	 *     <li>The number of tuples in the pTable correspond to the configuration XML taking into account possible accumulation of candidates.</li>
	 * </ul>
	 *
	 * @param electionEventContext the election event context, containing a list of PrimesMappingTables, one per verification card set. Must be
	 *                             non-null.
	 * @param configuration        the configuration XML. Must be non-null.
	 * @return {@code true} if the PrimesMappingTables are consistent, {@code false} otherwise
	 */
	public boolean verifyPrimesMappingTableConsistency(final ElectionEventContext electionEventContext, final Configuration configuration) {
		checkNotNull(electionEventContext);
		checkNotNull(configuration);

		final ImmutableList<PrimesMappingTable> primesMappingTables = electionEventContext.verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::getPrimesMappingTable)
				.collect(toImmutableList());
		checkArgument(!primesMappingTables.isEmpty());

		primesMappingTables.forEach(primesMappingTable -> checkArgument(hasNoDuplicates(primesMappingTable.getPTable().stream()
						.map(PrimesMappingTableEntry::encodedVotingOption)
						.collect(GroupVector.toGroupVector())),
				"The primes mapping table entries contain duplicated encoded voting options."));

		// Join the PrimesMappingTables of all verification card sets, deleting duplicates.
		final Set<PrimesMappingTableEntry> primesMappingTableEntries = primesMappingTables.stream()
				.parallel()
				.map(PrimesMappingTable::getPTable)
				.flatMap(GroupVector::stream)
				.collect(Collectors.toSet());

		// Create the actual voting option, semantic information and correctness information mapping using the configuration XML.
		final Set<PrimesMappingTableEntrySubset> election = getElectionPartialPrimesMappingTableEntries(configuration);
		final Set<PrimesMappingTableEntrySubset> vote = getVotePartialPrimesMappingTableEntries(configuration);
		final Set<PrimesMappingTableEntrySubset> configurationPartialPrimesMappingTableEntries = Stream.of(election, vote)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());

		final ImmutableList<BiFunction<Set<PrimesMappingTableEntry>, Set<PrimesMappingTableEntrySubset>, Boolean>> consistencyVerifications = ImmutableList.of(
				this::verifyCorrectMappingInAllVerificationCardSets,
				this::verifyInformationCorrespondsToConfiguration,
				this::verifyNumberOfTuplesCorrespondsToConfiguration);

		return consistencyVerifications
				.stream()
				.parallel()
				.map(f -> f.apply(primesMappingTableEntries, configurationPartialPrimesMappingTableEntries))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	/**
	 * Verifies that the same actual voting option has the same encoded voting option, that the same actual voting option has the same semantic
	 * information and that the same actual voting option has the same correctness information in all PrimesMappingTables.
	 *
	 * @param configurationPartialPrimesMappingTableEntries ignored, needed for consistency in the signature of the verification methods.
	 */
	@SuppressWarnings("java:S1172")
	private boolean verifyCorrectMappingInAllVerificationCardSets(final Set<PrimesMappingTableEntry> primesMappingTableEntries,
			final Set<PrimesMappingTableEntrySubset> configurationPartialPrimesMappingTableEntries) {
		final Set<PrimeGqElement> encodedVotingOptions = primesMappingTableEntries.stream()
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.collect(Collectors.toSet());

		final Set<String> actualVotingOptions = primesMappingTableEntries.stream()
				.map(PrimesMappingTableEntry::actualVotingOption)
				.collect(Collectors.toSet());

		final boolean correctMapping = primesMappingTableEntries.size() == encodedVotingOptions.size() &&
				primesMappingTableEntries.size() == actualVotingOptions.size();
		if (!correctMapping) {
			LOGGER.error(
					"The encoded voting options, actual voting options, semantic information and correctness information mapping is not the same in all verification card sets.");
		}
		return correctMapping;
	}

	/**
	 * Verifies that the actual voting options, semantic information and correctness information in the PrimesMappingTable correspond to the
	 * configuration XML.
	 */
	private boolean verifyInformationCorrespondsToConfiguration(final Set<PrimesMappingTableEntry> primesMappingTableEntries,
			final Set<PrimesMappingTableEntrySubset> configurationPartialPrimesMappingTableEntries) {
		final Set<PrimesMappingTableEntrySubset> partialPrimesMappingTableEntries = primesMappingTableEntries.stream()
				.parallel()
				.map(entry -> new PrimesMappingTableEntrySubset(entry.actualVotingOption(), entry.semanticInformation(),
						entry.correctnessInformation()))
				.collect(Collectors.toSet());

		final boolean informationCorrespondsToConfiguration = partialPrimesMappingTableEntries.equals(configurationPartialPrimesMappingTableEntries);
		if (!informationCorrespondsToConfiguration) {
			LOGGER.error("The actual voting options, semantic information and correctness information do not correspond to the configuration XML.");
		}
		return informationCorrespondsToConfiguration;
	}

	/**
	 * Verifies that the number of tuples in the pTable corresponds to the configuration XML.
	 */
	private boolean verifyNumberOfTuplesCorrespondsToConfiguration(final Set<PrimesMappingTableEntry> primesMappingTableEntries,
			final Set<PrimesMappingTableEntrySubset> configurationPartialPrimesMappingTableEntries) {
		final int expectedPrimesMappingTableEntriesSize = configurationPartialPrimesMappingTableEntries.size();

		final boolean numberOfTuplesCorrespondsToConfiguration = primesMappingTableEntries.size() == expectedPrimesMappingTableEntriesSize;
		if (!numberOfTuplesCorrespondsToConfiguration) {
			LOGGER.error(
					"The number of tuples in the pTable does not correspond to the configuration XML.");
		}

		return numberOfTuplesCorrespondsToConfiguration;
	}

	private Set<PrimesMappingTableEntrySubset> getElectionPartialPrimesMappingTableEntries(final Configuration configuration) {
		return configuration.getContest().getElectionGroupBallot().stream()
				.parallel()
				.map(ElectionGroupBallotType::getElectionInformation)
				.flatMap(List::stream)
				.map(electionInformationType -> {
					final ElectionType election = electionInformationType.getElection();
					final String electionIdentification = election.getElectionIdentification();

					final Set<PrimesMappingTableEntrySubset> emptyList = getEmptyListEntries(electionInformationType,
							electionInformationType.getEmptyList());
					final Set<PrimesMappingTableEntrySubset> nonEmptyLists = getNonEmptyListsEntries(electionIdentification,
							electionInformationType.getList());
					final Set<PrimesMappingTableEntrySubset> candidates = getCandidatesEntries(electionInformationType);
					final Set<PrimesMappingTableEntrySubset> writeIns = getWriteInsEntries(electionInformationType);

					return Stream.of(emptyList, nonEmptyLists, candidates, writeIns)
							.flatMap(Set::stream)
							.collect(Collectors.toSet());
				})
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	private static Set<PrimesMappingTableEntrySubset> getEmptyListEntries(final ElectionInformationType electionInformationType,
			final EmptyListType emptyList) {
		final String electionIdentification = electionInformationType.getElection().getElectionIdentification();
		final Set<PrimesMappingTableEntrySubset> emptyListEntries = new HashSet<>();

		// empty position
		emptyList.getEmptyPosition()
				.forEach(emptyPosition -> {
					final String actualVotingOption = getEmptyPositionActualVotingOption(electionIdentification,
							emptyPosition.getEmptyPositionIdentification());
					final String semanticInformation = getEmptyPositionSemanticInformation(emptyPosition.getPositionOnList());
					final String correctnessInformation = getEmptyPositionCorrectnessInformation(electionIdentification);
					emptyListEntries.add(new PrimesMappingTableEntrySubset(actualVotingOption, semanticInformation, correctnessInformation));
				});

		// empty list - only added if an election includes at least one non-empty list (otherwise it would be a candidate-only election without the possibility of selecting lists)
		final boolean electionWithLists = !electionInformationType.getList().isEmpty();
		if (electionWithLists) {
			emptyListEntries.add(getListEntry(electionIdentification, emptyList.getListIdentification(), emptyList.getListDescription(), true));
		}

		return emptyListEntries;
	}

	private static Set<PrimesMappingTableEntrySubset> getNonEmptyListsEntries(final String electionIdentification, final List<ListType> nonEmpty) {
		return nonEmpty.stream()
				.parallel()
				.map(listType -> getListEntry(electionIdentification, listType.getListIdentification(), listType.getListDescription(), false))
				.collect(Collectors.toSet());
	}

	private static PrimesMappingTableEntrySubset getListEntry(final String electionIdentification, final String listIdentification,
			final ListDescriptionInformationType listDescriptionInformation, final boolean isEmptyList) {
		final String actualVotingOption = getListActualVotingOption(electionIdentification, listIdentification);
		final String semanticInformation = getListSemanticInformation(isEmptyList,
				ImmutableList.from(listDescriptionInformation.getListDescriptionInfo()),
				ListDescriptionInfo::getListDescription);
		final String correctnessInformation = getListCorrectnessInformation(electionIdentification);
		return new PrimesMappingTableEntrySubset(actualVotingOption, semanticInformation, correctnessInformation);
	}

	private static Set<PrimesMappingTableEntrySubset> getCandidatesEntries(final ElectionInformationType electionInformationType) {
		if (electionInformationType.getCandidate() == null) {
			return Set.of();
		}

		final String electionIdentification = electionInformationType.getElection().getElectionIdentification();
		return electionInformationType.getCandidate().stream()
				.parallel()
				.flatMap(candidateType ->
						IntStream.range(0, electionInformationType.getElection().getCandidateAccumulation().intValue())
								.mapToObj(acc -> getCandidateActualVotingOption(electionIdentification, candidateType.getCandidateIdentification(),
										acc))
								.map(actualVotingOption -> {
									final String semanticInformation = getCandidateSemanticInformation(candidateType.getFamilyName(),
											candidateType.getFirstName(), candidateType.getCallName(), candidateType.getDateOfBirth().toXMLFormat());
									final String correctnessInformation = getCandidateCorrectnessInformation(electionIdentification);
									return new PrimesMappingTableEntrySubset(actualVotingOption, semanticInformation, correctnessInformation);
								})
				).collect(Collectors.toSet());
	}

	private static Set<PrimesMappingTableEntrySubset> getWriteInsEntries(final ElectionInformationType electionInformationType) {
		final String electionIdentification = electionInformationType.getElection().getElectionIdentification();

		// write-ins position - only added if write-ins are allowed for an election
		return electionInformationType.getWriteInCandidate().stream()
				.map(writeInCandidate -> {
					final String actualVotingOption = getWriteInPositionActualVotingOption(electionIdentification,
							writeInCandidate.getWriteInCandidateIdentification());
					final String semanticInformation = getWriteInPositionSemanticInformation(writeInCandidate.getPosition());
					final String correctnessInformation = getWriteInPositionCorrectnessInformation(electionIdentification);
					return new PrimesMappingTableEntrySubset(actualVotingOption, semanticInformation, correctnessInformation);
				}).collect(Collectors.toSet());
	}

	private Set<PrimesMappingTableEntrySubset> getVotePartialPrimesMappingTableEntries(final Configuration configuration) {
		return configuration.getContest().getVoteInformation().stream()
				.parallel()
				.map(VoteInformationType::getVote)
				.map(voteInformationType -> voteInformationType.getBallot().stream()
						.parallel()
						.map(ballotType -> {
							final Set<PrimesMappingTableEntrySubset> standardBallotAnswers = getStandardBallotAnswersEntries(ballotType);
							final Set<PrimesMappingTableEntrySubset> variantBallotStandardAnswers = getVariantBallotStandardAnswersEntries(
									ballotType);
							final Set<PrimesMappingTableEntrySubset> tieBreakAnswers = getTieBreakAnswersEntries(ballotType);

							return Stream.of(standardBallotAnswers, variantBallotStandardAnswers, tieBreakAnswers)
									.flatMap(Set::stream)
									.collect(Collectors.toSet());
						}).flatMap(Set::stream)
						.collect(Collectors.toSet()))
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	private static Set<PrimesMappingTableEntrySubset> getStandardBallotAnswersEntries(final BallotType ballotType) {
		final StandardBallotType standardBallot = ballotType.getStandardBallot();
		if (standardBallot == null) {
			return Set.of();
		}

		return getStandardAnswersEntries(standardBallot.getQuestionIdentification(), standardBallot.getBallotQuestion(), standardBallot.getAnswer())
				.collect(Collectors.toSet());
	}

	private static Set<PrimesMappingTableEntrySubset> getVariantBallotStandardAnswersEntries(final BallotType ballotType) {
		final VariantBallotType variantBallot = ballotType.getVariantBallot();
		if (variantBallot == null) {
			return Set.of();
		}

		return variantBallot.getStandardQuestion().stream()
				.parallel()
				.flatMap(standardQuestionType -> getStandardAnswersEntries(standardQuestionType.getQuestionIdentification(),
						standardQuestionType.getBallotQuestion(), standardQuestionType.getAnswer()))
				.collect(Collectors.toSet());
	}

	private static Stream<PrimesMappingTableEntrySubset> getStandardAnswersEntries(final String questionIdentification,
			final BallotQuestionType ballotQuestionType, final List<StandardAnswerType> standardAnswerTypes) {
		return standardAnswerTypes.stream()
				.parallel()
				.map(standardAnswerType -> {
					final String actualVotingOption = getAnswerActualVotingOption(questionIdentification,
							standardAnswerType.getAnswerIdentification());
					final String semanticInformation = getAnswerSemanticInformation(standardAnswerType.isHiddenAnswer(),
							ImmutableList.from(ballotQuestionType.getBallotQuestionInfo()),
							BallotQuestionInfo::getBallotQuestion,
							ImmutableList.from(standardAnswerType.getAnswerInfo()),
							AnswerInformationType::getAnswer);
					return new PrimesMappingTableEntrySubset(actualVotingOption, semanticInformation, questionIdentification);
				});
	}

	private static Set<PrimesMappingTableEntrySubset> getTieBreakAnswersEntries(final BallotType ballotType) {
		final VariantBallotType variantBallot = ballotType.getVariantBallot();
		if (variantBallot == null || variantBallot.getTieBreakQuestion() == null) {
			return Set.of();
		}

		return variantBallot.getTieBreakQuestion().stream()
				.parallel()
				.flatMap(tieBreakQuestionType -> tieBreakQuestionType.getAnswer().stream()
						.parallel()
						.map(tiebreakAnswerType -> {
							final String questionIdentification = tieBreakQuestionType.getQuestionIdentification();
							final String actualVotingOption = getAnswerActualVotingOption(questionIdentification,
									tiebreakAnswerType.getAnswerIdentification());
							final String semanticInformation = getAnswerSemanticInformation(tiebreakAnswerType.isHiddenAnswer(),
									ImmutableList.from(tieBreakQuestionType.getBallotQuestion().getBallotQuestionInfo()),
									BallotQuestionInfo::getBallotQuestion,
									ImmutableList.from(tiebreakAnswerType.getAnswerInfo()),
									AnswerInformationType::getAnswer);
							return new PrimesMappingTableEntrySubset(actualVotingOption, semanticInformation, questionIdentification);
						}))
				.collect(Collectors.toSet());
	}

	private record PrimesMappingTableEntrySubset(String actualVotingOption, String semanticInformation, String correctnessInformation) {
	}
}
