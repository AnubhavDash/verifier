/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.backend.tools;

import static com.google.common.base.Preconditions.checkState;
import static org.swisspush.supermachine.BeanScanner.from;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.google.common.collect.MoreCollectors;

import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0058._5.SendingApplicationType;
import ch.ech.xmlns.ech_0110._4.BallotResultType;
import ch.ech.xmlns.ech_0110._4.CandidateInformationType;
import ch.ech.xmlns.ech_0110._4.CandidateListResultType;
import ch.ech.xmlns.ech_0110._4.CandidateResultType;
import ch.ech.xmlns.ech_0110._4.CountOfVotersInformationType;
import ch.ech.xmlns.ech_0110._4.CountingCircleResultsType;
import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.ech.xmlns.ech_0110._4.ElectionGroupResultsType;
import ch.ech.xmlns.ech_0110._4.ElectionResultType;
import ch.ech.xmlns.ech_0110._4.EventResultDelivery;
import ch.ech.xmlns.ech_0110._4.ListInformationType;
import ch.ech.xmlns.ech_0110._4.ListResultsType;
import ch.ech.xmlns.ech_0110._4.ReportingBodyType;
import ch.ech.xmlns.ech_0110._4.ResultDetailType;
import ch.ech.xmlns.ech_0110._4.StandardBallotResultType;
import ch.ech.xmlns.ech_0110._4.VariantBallotResultType;
import ch.ech.xmlns.ech_0110._4.VoteResultType;
import ch.ech.xmlns.ech_0110._4.VotingCardsInformationType;
import ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.BallotQuestionType;
import ch.ech.xmlns.ech_0155._4.ContestType;
import ch.ech.xmlns.ech_0155._4.CountingCircleType;
import ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.ElectionGroupDescriptionType;
import ch.ech.xmlns.ech_0155._4.ElectionType;
import ch.ech.xmlns.ech_0155._4.TieBreakQuestionType;
import ch.ech.xmlns.ech_0155._4.TieBreakQuestionType.TieBreakQuestionInfo;
import ch.ech.xmlns.ech_0155._4.VoteDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.VoteType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.AuthorizationObjectType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CandidatePositionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CandidateType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionDescriptionInformationType.ElectionDescriptionInfo;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ListType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.RegisterType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotElectionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotVoteType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.DomainOfInfluenceType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

/**
 * Maps to {@link ch.ech.xmlns.ech_0110._4.Delivery}.
 */
@Mapper
public interface DeliveryMapper {

	DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

	BigInteger PROPORTIONAL = BigInteger.valueOf(2);
	BigInteger MAJORAL = BigInteger.ONE;

	String BLANK_STR = "BLANK";
	String EMPTY_STR = "EMPTY";
	String NO_STR = "NO";
	String YES_STR = "YES";

	@Mapping(target = "EVotingPeriod.EVotingPeriodFrom", source = "contestType.evotingFromDate")
	@Mapping(target = "EVotingPeriod.EVotingPeriodTill", source = "contestType.evotingToDate")
	ContestType mapToContestInformation(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ContestType contestType);

	VoteDescriptionInformationType mapToVoteDescription(
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteDescriptionInformationType voteDescriptionInformationType);

	BallotDescriptionInformationType mapToBallotDescriptionInformation(
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotDescriptionInformationType ballotDescriptionInformationType);

	BallotQuestionType mapToBallotQuestion(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotQuestionType ballotQuestionType);

	@Mapping(target = "language", source = "ballotQuestionInfo.language")
	@Mapping(target = "tieBreakQuestionTitle", source = "ballotQuestionInfo.ballotQuestionTitle")
	@Mapping(target = "tieBreakQuestion", source = "ballotQuestionInfo.ballotQuestion")
	TieBreakQuestionInfo mapToTieBreakQuestionInfo(
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotQuestionType.BallotQuestionInfo ballotQuestionInfo);

	ElectionType mapToElection(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionType electionType);

	@Mapping(target = "candidateReference", source = "candidateType.referenceOnPosition")
	@Mapping(target = "officialCandidateYesNo", expression = "java(candidateType.getCandidateIdentification() != null)")
	CandidateInformationType mapToCandidateInformation(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CandidateType candidateType);

	ListInformationType mapToListInformation(final ListType list);

	ElectionDescriptionInformationType.ElectionDescriptionInfo mapToElectionGroupDescription(
			final ElectionDescriptionInfo electionDescriptionInfo);

	default Delivery map(final String electionEventId, final Configuration configuration, final Results results) {

		return new Delivery()
				.withDeliveryHeader(mapToDeliveryHeader(electionEventId))
				.withResultDelivery(mapToResultDelivery(configuration, results));
	}

	private static HeaderType mapToDeliveryHeader(final String electionEventId) {

		final XMLGregorianCalendar messageDate;
		try {
			messageDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDateTime.now().toString());
		} catch (final DatatypeConfigurationException e) {
			throw new IllegalStateException("Could not instantiate message date.", e);
		}

		return new HeaderType()
				.withSenderId("http://www.post.ch")
				.withMessageId(electionEventId)
				.withMessageType("http://www.post.ch")
				.withSendingApplication(new SendingApplicationType()
						.withManufacturer("SwissPost")
						.withProduct("E-Voting")
						.withProductVersion("1"))
				.withMessageDate(messageDate)
				.withAction("1")
				.withTestDeliveryFlag(false);
	}

	private EventResultDelivery mapToResultDelivery(final Configuration configuration, final Results results) {

		return new EventResultDelivery()
				.withReportingBody(mapToReportingBodyType())
				.withContestInformation(mapToContestInformation(configuration.getContest()))
				.withCountingCircleResults(mapToCountingCircleResultsTypes(configuration, results));
	}

	private ReportingBodyType mapToReportingBodyType() {

		final XMLGregorianCalendar creationDateTime;
		try {
			creationDateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDateTime.now().toString());
		} catch (final DatatypeConfigurationException e) {
			throw new IllegalStateException("Could not instantiate message date.", e);
		}

		return new ReportingBodyType()
				.withReportingBodyIdentification("SwissPost")
				.withCreationDateTime(creationDateTime);
	}

	private List<CountingCircleResultsType> mapToCountingCircleResultsTypes(final Configuration configuration, final Results results) {

		record CountingCircleTypeRecord(String id, String name) {
		}

		return configuration.getAuthorizations().getAuthorization().stream()
				.map(AuthorizationType::getAuthorizationObject)
				.flatMap(Collection::stream)
				.map(AuthorizationObjectType::getCountingCircle)
				.map(countingCircleType -> new CountingCircleTypeRecord(countingCircleType.getId(), countingCircleType.getName()))
				.distinct()
				.map(countingCircleType -> new CountingCircleType()
						.withCountingCircleId(countingCircleType.id())
						.withCountingCircleName(countingCircleType.name()))
				.map(countingCircleType -> new CountingCircleResultsType()
						.withCountingCircle(countingCircleType)
						.withVotingCardsInformation(mapToVotingCardsInformation(countingCircleType.getCountingCircleId(), results))
						.withVoteResults(mapToVoteResults(configuration, results, countingCircleType.getCountingCircleId()))
						.withElectionGroupResults(mapToElectionGroupResults(configuration, results, countingCircleType.getCountingCircleId()))
				)
				.toList();
	}

	private VotingCardsInformationType mapToVotingCardsInformation(final String countingCircleIdentification, final Results results) {

		final BigInteger countOfReceivedValidVotingCardsTotal = BigInteger.valueOf(
				from(results)
						.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType.class)
						.filter(countingCircleType -> countingCircleType.getCountingCircleIdentification().equals(countingCircleIdentification))
						.find(BallotVoteType.class).stream()
						.count());

		return new VotingCardsInformationType()
				.withCountOfReceivedValidVotingCardsTotal(countOfReceivedValidVotingCardsTotal)
				.withCountOfReceivedInvalidVotingCardsTotal(BigInteger.ZERO);
	}

	private List<VoteResultType> mapToVoteResults(final Configuration configuration, final Results results,
			final String countingCircleIdentification) {

		final List<AuthorizationType> authorizations = configuration.getAuthorizations().getAuthorization();
		final List<VoteInformationType> voteInformationTypes = configuration.getContest().getVoteInformation();
		final RegisterType register = configuration.getRegister();

		return voteInformationTypes.stream()
				.map(VoteInformationType::getVote)
				.filter(voteType ->
						isDomainOfInfluenceInCountingCircle(voteType.getDomainOfInfluence(), countingCircleIdentification, authorizations))
				.map(voteType -> {
					final String domainOfInfluence = voteType.getDomainOfInfluence();

					return new VoteResultType()
							.withVote(new VoteType()
									.withVoteIdentification(voteType.getVoteIdentification())
									.withDomainOfInfluenceIdentification(domainOfInfluence)
									.withVoteDescription(mapToVoteDescription(voteType.getVoteDescription())))
							.withCountOfVotersInformation(new CountOfVotersInformationType()
									.withCountOfVotersTotal(
											getCountOfVotersTotal(register, countingCircleIdentification, domainOfInfluence, authorizations)))
							.withBallotResult(mapToBallotResultTypes(voteType, results, countingCircleIdentification));
				})
				.toList();
	}

	private List<ElectionGroupResultsType> mapToElectionGroupResults(final Configuration configuration, final Results results,
			final String countingCircleIdentification) {

		final List<AuthorizationType> authorizations = configuration.getAuthorizations().getAuthorization();
		final RegisterType register = configuration.getRegister();
		final List<ElectionInformationType> electionInformations = configuration.getContest().getElectionInformation();

		return configuration.getContest().getElectionInformation().stream()
				.map(ElectionInformationType::getElection)
				.filter(electionType -> isDomainOfInfluenceInCountingCircle(electionType.getDomainOfInfluence(), countingCircleIdentification,
						authorizations))
				.map(electionType -> mapToElectionGroupResults(results, countingCircleIdentification, electionInformations, authorizations, register,
						electionType))
				.toList();
	}

	private ElectionGroupResultsType mapToElectionGroupResults(final Results results, final String countingCircleIdentification,
			final List<ElectionInformationType> electionInformations, final List<AuthorizationType> authorizations, final RegisterType register,
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionType election) {

		final String domainOfInfluenceIdentification = election.getDomainOfInfluence();
		final String electionIdentification = election.getElectionIdentification();

		final ElectionInformationType electionInformation = electionInformations.stream()
				.filter(electionInformationType -> electionInformationType.getElection().getElectionIdentification().equals(electionIdentification))
				.collect(MoreCollectors.onlyElement());
		final List<ListType> lists = electionInformation.getList();

		final List<BallotElectionType> ballotElectionTypes = from(results)
				.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType.class)
				.filter(countingCircleType -> countingCircleType.getCountingCircleIdentification().equals(countingCircleIdentification))
				.find(DomainOfInfluenceType.class)
				.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification()
						.equals(domainOfInfluenceIdentification))
				.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType.class)
				.filter(electionType -> electionType.getElectionIdentification().equals(electionIdentification))
				.find(BallotElectionType.class).stream()
				.toList();

		final List<BallotElectionTypeExtended> ballotElectionTypesExtended = toBallotElectionTypeExtended(ballotElectionTypes, lists);

		final BigInteger countOfUnaccountedBlankBallots = getCountOfUnaccountedBlankBallots(ballotElectionTypesExtended);
		final BigInteger countOfUnaccountedInvalidBallots = BigInteger.ZERO;

		final ElectionGroupResultsType electionGroupResultsType = new ElectionGroupResultsType()
				.withElectionResults(mapToElectionResults(electionInformation, ballotElectionTypesExtended))
				.withDomainOfInfluenceIdentification(domainOfInfluenceIdentification)
				.withElectionGroupDescription(mapToElectionGroupDescription(electionInformation))
				.withCountOfVotersInformation(new CountOfVotersInformationType().withCountOfVotersTotal(
						getCountOfVotersTotal(register, countingCircleIdentification, domainOfInfluenceIdentification,
								authorizations)))
				.withCountOfReceivedBallotsTotal(new ResultDetailType().withTotal(BigInteger.valueOf(ballotElectionTypesExtended.size())))
				.withCountOfUnaccountedInvalidBallots(new ResultDetailType().withTotal(countOfUnaccountedInvalidBallots))
				.withCountOfUnaccountedBlankBallots(new ResultDetailType().withTotal(countOfUnaccountedBlankBallots))
				.withCountOfUnaccountedBallots(new ResultDetailType()
						.withTotal(countOfUnaccountedBlankBallots.add(countOfUnaccountedInvalidBallots)));

		final BigInteger countOfReceivedBallotsTotal = electionGroupResultsType.getCountOfReceivedBallotsTotal().getTotal();
		final BigInteger countOfUnaccountedBallots = electionGroupResultsType.getCountOfUnaccountedBallots().getTotal();
		return electionGroupResultsType
				.withCountOfAccountedBallots(new ResultDetailType()
						.withTotal(countOfReceivedBallotsTotal.subtract(countOfUnaccountedBallots)));
	}

	private ElectionGroupDescriptionType mapToElectionGroupDescription(final ElectionInformationType electionInformation) {

		final List<ElectionDescriptionInfo> electionDescriptionInfo = electionInformation.getElection().getElectionDescription()
				.getElectionDescriptionInfo();

		return new ElectionGroupDescriptionType()
				.withElectionDescriptionInfo(electionDescriptionInfo.stream()
						.map(this::mapToElectionGroupDescription)
						.toList());
	}

	private BigInteger getCountOfUnaccountedBlankBallots(final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {
		return BigInteger.valueOf(ballotElectionTypesExtended.stream()
				.filter(BallotElectionTypeExtended::isBlank)
				.count());
	}

	private ElectionResultType mapToElectionResults(final ElectionInformationType electionInformation,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {
		final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionType election = electionInformation.getElection();
		final BigInteger typeOfElection = election.getTypeOfElection();

		final ElectionResultType electionResultType = new ElectionResultType()
				.withElection(mapToElection(election));

		if (PROPORTIONAL.equals(typeOfElection)) {
			return electionResultType
					.withProportionalElection(mapToProportionalElection(typeOfElection, electionInformation, ballotElectionTypesExtended));
		} else if (MAJORAL.equals(typeOfElection)) {
			return electionResultType
					.withMajoralElection(mapToMajoralElection(typeOfElection, electionInformation, ballotElectionTypesExtended));
		} else {
			throw new IllegalStateException(String.format("Unknown type of election. [typeOfElection: %s]", typeOfElection));
		}
	}

	private ElectionResultType.ProportionalElection mapToProportionalElection(final BigInteger typeOfElection,
			final ElectionInformationType electionInformation, final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final List<CandidateType> candidates = electionInformation.getCandidate();
		final List<ListType> lists = electionInformation.getList();

		return new ElectionResultType.ProportionalElection()
				.withCountOfChangedBallotsWithPartyAffiliation(
						getCountOfChangedBallotsWithPartyAffiliation(ballotElectionTypesExtended))
				.withCountOfChangedBallotsWithoutPartyAffiliation(
						getCountOfChangedBallotsWithoutPartyAffiliation(ballotElectionTypesExtended))
				.withCountOfEmptyVotesOfChangedBallotsWithoutPartyAffiliation(
						getCountOfEmptyVotesOfChangedBallotsWithoutPartyAffiliation(ballotElectionTypesExtended))
				.withList(mapToListResults(lists, ballotElectionTypesExtended))
				.withCandidate(mapToCandidates(typeOfElection, lists, candidates, ballotElectionTypesExtended));
	}

	private ResultDetailType getCountOfEmptyVotesOfChangedBallotsWithoutPartyAffiliation(
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(ballotElectionTypesExtended.stream()
						.filter(ballotElectionTypeExtended -> ballotElectionTypeExtended.isChangedBallot
								&& !ballotElectionTypeExtended.isWithPartyAffiliation)
						.mapToLong(BallotElectionTypeExtended::emptyVotes)
						.sum()));
	}

	private ResultDetailType getCountOfChangedBallotsWithoutPartyAffiliation(final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(ballotElectionTypesExtended.stream()
						.filter(ballotElectionTypeExtended -> ballotElectionTypeExtended.isChangedBallot
								&& !ballotElectionTypeExtended.isWithPartyAffiliation)
						.count()));
	}

	private ResultDetailType getCountOfChangedBallotsWithPartyAffiliation(final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(ballotElectionTypesExtended.stream()
						.filter(ballotElectionTypeExtended -> ballotElectionTypeExtended.isChangedBallot
								&& ballotElectionTypeExtended.isWithPartyAffiliation)
						.count()));
	}

	private List<CandidateResultType> mapToCandidates(final BigInteger typeOfElection, final List<ListType> lists,
			final List<CandidateType> candidates, final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final List<CandidateResultType> candidateResults = new ArrayList<>();
		candidateResults.addAll(candidates.stream()
				.map(candidate -> {
					final String candidateIdentification = candidate.getCandidateIdentification();

					final CandidateResultType candidateResultType = new CandidateResultType()
							.withCandidateInformation(mapToCandidateInformation(candidate))
							.withListResults(mapToCandidateListResultsForProportional(candidateIdentification, lists, ballotElectionTypesExtended));

					return candidateResultType.withCountOfVotesTotal(
							getCountOfVotesTotal(typeOfElection, lists, ballotElectionTypesExtended, candidateResultType));
				})
				.toList());

		candidateResults.addAll(ballotElectionTypesExtended.stream()
				.map(ballotElectionTypeExtended -> {
					final List<String> chosenWriteInsCandidateValues = ballotElectionTypeExtended.ballotElectionType.getChosenWriteInsCandidateValue();

					return chosenWriteInsCandidateValues.stream()
							.map(chosenWriteInsCandidateValue -> new CandidateResultType()
									.withWriteIn(chosenWriteInsCandidateValue)
									.withListResults(lists.stream()
											.map(list -> {

												final boolean isChosenList = list.getListIdentification()
														.equals(ballotElectionTypeExtended.chosenListIdentification);

												return new CandidateListResultType()
														.withListIdentification(list.getListIdentification())
														.withCountOfvotesFromUnchangedBallots(
																new ResultDetailType().withTotal(isChosenList ? BigInteger.ONE : BigInteger.ZERO))
														.withCountOfvotesFromChangedBallots(new ResultDetailType().withTotal(BigInteger.ZERO));
											})
											.toList())
									.withCountOfVotesTotal(BigInteger.ONE)
							)
							.toList();
				})
				.flatMap(Collection::stream)
				.toList());

		return Collections.unmodifiableList(candidateResults);
	}

	private BigInteger getCountOfVotesTotal(final BigInteger typeOfElection, final List<ListType> lists,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended, final CandidateResultType candidateResultType) {

		final BigInteger countOfVotesTotal;

		if (PROPORTIONAL.equals(typeOfElection)) {
			record CountOfVotes(BigInteger fromUnchangedBallots, BigInteger fromChangedBallots) {
			}

			final List<CountOfVotes> countOfVotesList = candidateResultType.getListResults().stream()
					.map(listResult -> new CountOfVotes(listResult.getCountOfvotesFromUnchangedBallots().getTotal(),
							listResult.getCountOfvotesFromChangedBallots().getTotal()))
					.toList();

			final BigInteger countOfvotesFromUnchangedBallots = countOfVotesList.stream()
					.map(CountOfVotes::fromUnchangedBallots)
					.reduce(BigInteger.ZERO, BigInteger::add);

			final BigInteger countOfvotesFromChangedBallots = countOfVotesList.stream()
					.map(CountOfVotes::fromChangedBallots)
					.reduce(BigInteger.ZERO, BigInteger::add);

			countOfVotesTotal = countOfvotesFromUnchangedBallots.add(countOfvotesFromChangedBallots);

		} else if (MAJORAL.equals(typeOfElection)) {

			final List<CandidatePositionType> candidatePositions = lists.stream()
					.map(ListType::getCandidatePosition)
					.flatMap(Collection::stream)
					.toList();

			final String candidateIdentification = candidateResultType.getCandidateInformation().getCandidateIdentification();

			countOfVotesTotal = getCountOfVotesTotalWithoutWriteIns(candidateIdentification, ballotElectionTypesExtended, candidatePositions);

		} else {
			throw new IllegalStateException(String.format("Unknown type of election. [typeOfElection: %s]", typeOfElection));
		}

		return countOfVotesTotal;
	}

	private List<ListResultsType> mapToListResults(final List<ListType> lists, final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		return lists.stream()
				.map(list -> {
					final String listIdentification = list.getListIdentification();
					final boolean isEmptyList = list.isListEmpty();

					final ListResultsType listResults = new ListResultsType()
							.withListInformation(mapToListInformation(list))
							.withCountOfChangedBallots(getCountOfChangedBallots(listIdentification, ballotElectionTypesExtended))
							.withCountOfUnchangedBallots(getCountOfUnchangedBallots(listIdentification, ballotElectionTypesExtended))
							.withCountOfCandidateVotes(getCountOfCandidateVotes(listIdentification, ballotElectionTypesExtended))
							.withCountOfAdditionalVotes(
									getCountOfAdditionalVotes(listIdentification, isEmptyList, ballotElectionTypesExtended));

					final BigInteger countOfCandidateVotes = listResults.getCountOfCandidateVotes().getTotal();
					final BigInteger countOfAdditionalVotes = listResults.getCountOfAdditionalVotes().getTotal();

					return listResults
							.withCountOfPartyVotes(
									new ResultDetailType().withTotal(isEmptyList ?
											BigInteger.ZERO :
											countOfCandidateVotes.add(countOfAdditionalVotes)));
				})
				.toList();
	}

	private ResultDetailType getCountOfAdditionalVotes(final String listIdentification,
			final boolean isEmptyList, final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		if (isEmptyList) {
			return new ResultDetailType().withTotal(BigInteger.ZERO);
		}

		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(
						ballotElectionTypesExtended.stream()
								.filter(ballotElectionTypeExtended -> listIdentification.equals(ballotElectionTypeExtended.chosenListIdentification))
								.mapToLong(BallotElectionTypeExtended::emptyVotes)
								.sum()));
	}

	private ResultDetailType getCountOfCandidateVotes(final String listIdentification,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(
						ballotElectionTypesExtended.stream()
								.filter(ballotElectionTypeExtended -> listIdentification.equals(ballotElectionTypeExtended.chosenListIdentification))
								.mapToLong(BallotElectionTypeExtended::numberOfCandidatesFromChosenList)
								.sum()));
	}

	private ResultDetailType getCountOfChangedBallots(final String listIdentification,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {
		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(
						ballotElectionTypesExtended.stream()
								.filter(ballotElectionTypeExtended -> listIdentification.equals(ballotElectionTypeExtended.chosenListIdentification))
								.filter(BallotElectionTypeExtended::isChangedBallot)
								.count()));

	}

	private ResultDetailType getCountOfUnchangedBallots(final String listIdentification,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {
		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(
						ballotElectionTypesExtended.stream()
								.filter(ballotElectionTypeExtended -> listIdentification.equals(ballotElectionTypeExtended.chosenListIdentification))
								.filter(ballotElectionTypeExtended -> !ballotElectionTypeExtended.isChangedBallot)
								.count()));

	}

	private List<BallotElectionTypeExtended> toBallotElectionTypeExtended(final List<BallotElectionType> ballotElections,
			final List<ListType> lists) {

		final ListType emptyList = lists.stream()
				.filter(ListType::isListEmpty)
				.collect(MoreCollectors.onlyElement());

		final List<String> emptyListCandidateListIdentifications = emptyList.getCandidatePosition().stream()
				.map(CandidatePositionType::getCandidateListIdentification)
				.toList();

		return ballotElections.stream()
				.map(ballotElectionType -> {
					final String chosenListIdentification = ballotElectionType.getChosenListIdentification();

					final List<String> chosenWriteInsCandidateValues = ballotElectionType.getChosenWriteInsCandidateValue();
					final List<String> chosenCandidateListIdentifications = ballotElectionType.getChosenCandidateListIdentification();
					final List<String> chosenCandidateIdentifications = ballotElectionType.getChosenCandidateIdentification();

					final ListType chosenList = lists.stream()
							.filter(list -> list.getListIdentification().equals(chosenListIdentification))
							.findFirst()
							.orElse(null);

					final List<String> candidateListIdentificationsFromChosenList =
							chosenList == null ? List.of() : chosenList.getCandidatePosition().stream()
									.map(CandidatePositionType::getCandidateListIdentification)
									.toList();

					final long emptyVotes = chosenCandidateListIdentifications.stream()
							.filter(emptyListCandidateListIdentifications::contains)
							.count();

					final long numberOfCandidatesFromChosenList = chosenCandidateListIdentifications.stream()
							.filter(candidateListIdentificationsFromChosenList::contains)
							.count();

					final boolean isEmptyListChosen = emptyList.getListIdentification().equals(chosenListIdentification);

					final boolean isBlank =
							(Objects.isNull(chosenListIdentification) || (isEmptyListChosen && chosenCandidateListIdentifications.stream()
									.allMatch(emptyListCandidateListIdentifications::contains)))
									&& chosenCandidateIdentifications.stream().allMatch(Objects::isNull)
									&& chosenWriteInsCandidateValues.stream().allMatch(Objects::isNull);

					final boolean ballotHasWriteIn = !chosenWriteInsCandidateValues.isEmpty();

					final boolean allCandidatesFromListHaveBeenChosen = candidateListIdentificationsFromChosenList.stream()
							.allMatch(chosenCandidateListIdentifications::contains);

					final boolean allChosenCandidatesAreFromList = chosenCandidateListIdentifications.stream()
							.allMatch(candidateListIdentificationsFromChosenList::contains);

					final boolean chosenCandidatesNumberMatchNumberCandidatesFromList =
							chosenCandidateListIdentifications.size() == candidateListIdentificationsFromChosenList.size();

					final boolean isChangedBallot = ballotHasWriteIn || !allCandidatesFromListHaveBeenChosen || !allChosenCandidatesAreFromList
							|| !chosenCandidatesNumberMatchNumberCandidatesFromList;

					final boolean isWithPartyAffiliation = !isEmptyListChosen;

					return new BallotElectionTypeExtended(ballotElectionType, chosenListIdentification, isBlank, isChangedBallot,
							isWithPartyAffiliation, emptyVotes, numberOfCandidatesFromChosenList);
				})
				.toList();
	}

	private ElectionResultType.MajoralElection mapToMajoralElection(final BigInteger typeOfElection,
			final ElectionInformationType electionInformation,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final List<CandidateType> candidates = electionInformation.getCandidate();
		final List<ListType> lists = electionInformation.getList();

		final BigInteger countOfInvalidVotesTotal = BigInteger.ZERO;

		return new ElectionResultType.MajoralElection()
				.withCandidate(mapToCandidates(typeOfElection, lists, candidates, ballotElectionTypesExtended))
				.withCountOfInvalidVotesTotal(new ResultDetailType().withTotal(countOfInvalidVotesTotal));
	}

	private BigInteger getCountOfVotesTotalWithoutWriteIns(final String candidateIdentification,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended, final List<CandidatePositionType> candidatePositions) {
		return BigInteger.valueOf(ballotElectionTypesExtended.stream()
				.map(BallotElectionTypeExtended::ballotElectionType)
				.mapToLong(ballotElectionType ->

						// Candidate chosen directly
						ballotElectionType.getChosenCandidateIdentification().stream()
								.filter(chosenCandidateIdentification -> chosenCandidateIdentification.equals(candidateIdentification))
								.count()

								+

								// Candidate chosen through list
								ballotElectionType.getChosenCandidateListIdentification().stream()
										.map(chosenCandidateListIdentification -> candidatePositions.stream()
												.filter(candidatePosition ->
														candidateIdentification.equals(candidatePosition.getCandidateIdentification())
																&& candidatePosition.getCandidateListIdentification()
																.equals(chosenCandidateListIdentification))
												.toList())
										.mapToLong(Collection::size)
										.sum()
				)
				.sum());
	}

	private List<CandidateListResultType> mapToCandidateListResultsForProportional(final String candidateIdentification, final List<ListType> lists,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final List<CandidatePositionType> candidatePositions = lists.stream()
				.map(ListType::getCandidatePosition)
				.flatMap(Collection::stream)
				.toList();

		return lists.stream()
				.map(list -> new CandidateListResultType()
						.withListIdentification(list.getListIdentification())
						.withCountOfvotesFromUnchangedBallots(
								getCountOfvotesFromUnchangedBallot(candidateIdentification, list, ballotElectionTypesExtended))
						.withCountOfvotesFromChangedBallots(
								getCountOfvotesFromChangedBallot(candidateIdentification, list, candidatePositions, ballotElectionTypesExtended)))
				.toList();
	}

	private ResultDetailType getCountOfvotesFromChangedBallot(final String candidateIdentification, final ListType list,
			final List<CandidatePositionType> candidatePositions, final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final BigInteger countOfvotesFromChangedBallot = ballotElectionTypesExtended.stream()
				.filter(BallotElectionTypeExtended::isChangedBallot)
				.map(ballotElectionTypeExtended -> {
					final String chosenListIdentification = ballotElectionTypeExtended.chosenListIdentification;

					if (!list.getListIdentification().equals(chosenListIdentification)) {
						return BigInteger.ZERO;
					}

					final List<String> chosenCandidateListIdentifications = ballotElectionTypeExtended.ballotElectionType.getChosenCandidateListIdentification();

					return BigInteger.valueOf(chosenCandidateListIdentifications.stream()
							.filter(chosenCandidateListIdentification ->
									candidatePositions.stream()
											.anyMatch(candidatePosition ->
													candidateIdentification.equals(candidatePosition.getCandidateIdentification())
															&& candidatePosition.getCandidateListIdentification()
															.equals(chosenCandidateListIdentification)))
							.count());
				})
				.reduce(BigInteger.ZERO, BigInteger::add);

		return new ResultDetailType().withTotal(countOfvotesFromChangedBallot);
	}

	private ResultDetailType getCountOfvotesFromUnchangedBallot(final String candidateIdentification, final ListType list,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final BigInteger countOfvotesFromUnchangedBallot = ballotElectionTypesExtended.stream()
				.filter(ballotElectionTypeExtended -> !ballotElectionTypeExtended.isChangedBallot)
				.map(ballotElectionTypeExtended -> {
					final String chosenListIdentification = ballotElectionTypeExtended.chosenListIdentification;

					if (!list.getListIdentification().equals(chosenListIdentification) || list.isListEmpty()) {
						return BigInteger.ZERO;
					}

					final List<String> chosenCandidateListIdentifications = ballotElectionTypeExtended.ballotElectionType.getChosenCandidateListIdentification();

					final List<CandidatePositionType> candidatePositions = list.getCandidatePosition();

					return BigInteger.valueOf(chosenCandidateListIdentifications.stream()
							.filter(chosenCandidateListIdentification ->
									candidatePositions.stream()
											.anyMatch(candidatePosition ->
													candidateIdentification.equals(candidatePosition.getCandidateIdentification())
															&& candidatePosition.getCandidateListIdentification()
															.equals(chosenCandidateListIdentification)))
							.count());
				})
				.reduce(BigInteger.ZERO, BigInteger::add);

		return new ResultDetailType()
				.withTotal(countOfvotesFromUnchangedBallot);
	}

	private static boolean isDomainOfInfluenceInCountingCircle(final String domainOfInfluenceIdentification,
			final String countingCircleIdentification, final List<AuthorizationType> authorizations) {

		return authorizations.stream()
				.map(AuthorizationType::getAuthorizationObject)
				.flatMap(Collection::stream)
				.filter(authorizationObjectType -> authorizationObjectType.getDomainOfInfluence().getId().equals(domainOfInfluenceIdentification))
				.map(AuthorizationObjectType::getCountingCircle)
				.map(ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CountingCircleType::getId)
				.toList().contains(countingCircleIdentification);
	}

	private static BigInteger getCountOfVotersTotal(final RegisterType registerType, final String countingCircleIdentification,
			final String domainOfInfluenceIdentification, final List<AuthorizationType> authorizations) {
		final List<String> authorizationIdentificationList = getAuthorizationIdentificationList(countingCircleIdentification,
				domainOfInfluenceIdentification, authorizations);

		return BigInteger.valueOf(registerType.getVoter().stream()
				.filter(voterType -> authorizationIdentificationList.stream()
						.anyMatch(authorizationIdentification -> voterType.getAuthorization().equals(authorizationIdentification)))
				.count());
	}

	private static List<String> getAuthorizationIdentificationList(final String countingCircleIdentification,
			final String domainOfInfluenceIdentification, final List<AuthorizationType> authorizations) {

		return authorizations.stream()
				.filter(authorizationType -> authorizationType.getAuthorizationObject().stream()
						.anyMatch(authorizationObjectType ->
								authorizationObjectType.getDomainOfInfluence().getId().equals(domainOfInfluenceIdentification) &&
										authorizationObjectType.getCountingCircle().getId().equals(countingCircleIdentification)))
				.map(AuthorizationType::getAuthorizationIdentification)
				.toList();
	}

	private List<BallotResultType> mapToBallotResultTypes(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType voteType,
			final Results results, final String countingCircleIdentification) {

		final String voteIdentification = voteType.getVoteIdentification();
		final String domainOfInfluenceIdentification = voteType.getDomainOfInfluence();

		return voteType.getBallot().stream()
				.map(ballotType -> mapToBallotResult(ballotType, results, countingCircleIdentification, domainOfInfluenceIdentification,
						voteIdentification))
				.toList();
	}

	private BallotResultType mapToBallotResult(final BallotType ballotType, final Results results,
			final String countingCircleIdentification, final String domainOfInfluenceIdentification, final String voteIdentification) {

		final List<BallotVoteType> allBallotVotes = from(results)
				.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType.class)
				.filter(countingCircleType -> countingCircleType.getCountingCircleIdentification().equals(countingCircleIdentification))
				.find(DomainOfInfluenceType.class)
				.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification().equals(domainOfInfluenceIdentification))
				.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType.class)
				.filter(voteType -> voteType.getVoteIdentification().equals(voteIdentification))
				.find(BallotVoteType.class).stream()
				.toList();

		final List<String> allAnswersIdentifications = allBallotVotes.stream()
				.map(BallotVoteType::getChosenAnswerIdentification)
				.flatMap(Collection::stream)
				.toList();

		final BallotResultType ballotResultType = new BallotResultType()
				.withBallotIdentification(ballotType.getBallotIdentification())
				.withBallotPosition(ballotType.getBallotPosition())
				.withBallotDescription(mapToBallotDescriptionInformation(ballotType.getBallotDescription()))
				.withCountOfReceivedBallotsTotal(new ResultDetailType().withTotal(BigInteger.valueOf(allBallotVotes.size())))
				.withStandardBallot(mapToStandardBallotResult(ballotType.getStandardBallot(), allAnswersIdentifications))
				.withVariantBallot(mapToVariantBallotResult(ballotType.getVariantBallot(), allAnswersIdentifications));

		final Counts counts = getCounts(ballotType, allBallotVotes, ballotResultType);

		ballotResultType
				.withCountOfAccountedBallotsTotal(new ResultDetailType().withTotal(counts.countOfAccountedBallotsTotal))
				.withCountOfUnaccountedBlankBallots(new ResultDetailType().withTotal(counts.countOfUnaccountedBlankBallots))
				.withCountOfUnaccountedInvalidBallots(new ResultDetailType().withTotal(BigInteger.ZERO));

		ballotResultType
				.withCountOfUnaccountedBallotsTotal(new ResultDetailType().withTotal(ballotResultType.getCountOfUnaccountedBlankBallots().getTotal()
						.add(ballotResultType.getCountOfUnaccountedInvalidBallots().getTotal())));

		return ballotResultType;
	}

	private static Counts getCounts(final BallotType ballotType, final List<BallotVoteType> allBallotVotes, final BallotResultType ballotResultType) {
		if (ballotType.getStandardBallot() != null) {
			final StandardBallotResultType standardBallot = ballotResultType.getStandardBallot();

			final BigInteger countOfAccountedBallotsTotal = standardBallot.getCountOfAnswerYes().getTotal()
					.add(standardBallot.getCountOfAnswerNo().getTotal());
			final BigInteger countOfUnaccountedBlankBallots = standardBallot.getCountOfAnswerEmpty().getTotal();

			checkState(ballotResultType.getCountOfReceivedBallotsTotal().getTotal().subtract(countOfUnaccountedBlankBallots)
					.equals(countOfAccountedBallotsTotal));

			return new Counts(countOfAccountedBallotsTotal, countOfUnaccountedBlankBallots);

		} else if (ballotType.getVariantBallot() != null) {

			final List<List<String>> allVariantBallotsMapped = allBallotVotes.stream()
					.map(BallotVoteType::getChosenAnswerIdentification)
					.map(listOfAnswersPerVoter -> listOfAnswersPerVoter.stream()
							.map(answer -> mapAnswer(answer, ballotType.getVariantBallot()))
							.filter(Objects::nonNull)
							.toList())
					.toList();

			final BigInteger countOfAccountedBallotsTotal = BigInteger.valueOf(allVariantBallotsMapped.stream()
					.filter(listOfMappedAnswersPerVoter -> listOfMappedAnswersPerVoter.stream().anyMatch(answer -> !answer.equals(BLANK_STR)))
					.count());

			final BigInteger countOfUnaccountedBlankBallots = BigInteger.valueOf(allVariantBallotsMapped.stream()
					.filter(listOfMappedAnswersPerVoter -> listOfMappedAnswersPerVoter.stream().allMatch(answer -> answer.equals(BLANK_STR)))
					.count());

			return new Counts(countOfAccountedBallotsTotal, countOfUnaccountedBlankBallots);
		} else {
			throw new IllegalStateException("Ballot must either be standard or variant.");
		}
	}

	private static String mapAnswer(final String answer, final VariantBallotType variantBallot) {
		for (final StandardQuestionType standardQuestion : variantBallot.getStandardQuestion()) {

			final StandardAnswerType yesAnswerType = standardQuestion.getAnswer().stream()
					.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(YES_STR))
					.collect(MoreCollectors.onlyElement());

			final StandardAnswerType noAnswerType = standardQuestion.getAnswer().stream()
					.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(NO_STR))
					.collect(MoreCollectors.onlyElement());

			final StandardAnswerType emptyAnswerType = standardQuestion.getAnswer().stream()
					.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(EMPTY_STR))
					.collect(MoreCollectors.onlyElement());

			if (answer.equals(yesAnswerType.getAnswerIdentification())) {
				return yesAnswerType.getStandardAnswerType();
			} else if (answer.equals(noAnswerType.getAnswerIdentification())) {
				return noAnswerType.getStandardAnswerType();
			} else if (answer.equals(emptyAnswerType.getAnswerIdentification())) {
				return BLANK_STR;
			}
		}

		for (final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType tieBreakQuestion : variantBallot.getTieBreakQuestion()) {
			final TiebreakAnswerType emptyAnswerType = tieBreakQuestion.getAnswer().stream()
					.filter(TiebreakAnswerType::isHiddenAnswer)
					.collect(MoreCollectors.onlyElement());

			if (answer.equals(emptyAnswerType.getAnswerIdentification())) {
				return BLANK_STR;
			}

			for (final TiebreakAnswerType tiebreakAnswerType : tieBreakQuestion.getAnswer()) {
				final String questionIdentification = tiebreakAnswerType.getStandardQuestionReference();

				checkState(questionIdentification != null, "The question identification cannot be null.");

				if (answer.equals(questionIdentification)) {
					return questionIdentification;
				}
			}
		}

		return null;
	}

	private StandardBallotResultType mapToStandardBallotResult(final StandardBallotType standardBallot, final List<String> answersIdentifications) {

		if (standardBallot == null) {
			return null;
		}

		final StandardAnswerType yesAnswerType = standardBallot.getAnswer().stream()
				.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(YES_STR))
				.collect(MoreCollectors.onlyElement());

		final StandardAnswerType noAnswerType = standardBallot.getAnswer().stream()
				.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(NO_STR))
				.collect(MoreCollectors.onlyElement());

		final StandardAnswerType emptyAnswerType = standardBallot.getAnswer().stream()
				.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(EMPTY_STR))
				.collect(MoreCollectors.onlyElement());

		final long totalYes = answersIdentifications.stream()
				.filter(answerIdentification -> answerIdentification.equals(yesAnswerType.getAnswerIdentification()))
				.count();

		final long totalNo = answersIdentifications.stream()
				.filter(answerIdentification -> answerIdentification.equals(noAnswerType.getAnswerIdentification()))
				.count();

		final long totalEmpty = answersIdentifications.stream()
				.filter(answerIdentification -> answerIdentification.equals(emptyAnswerType.getAnswerIdentification()))
				.count();

		return new StandardBallotResultType()
				.withQuestionIdentification(standardBallot.getQuestionIdentification())
				.withQuestion(mapToBallotQuestion(standardBallot.getBallotQuestion()))
				.withCountOfAnswerYes(new ResultDetailType().withTotal(BigInteger.valueOf(totalYes)))
				.withCountOfAnswerNo(new ResultDetailType().withTotal(BigInteger.valueOf(totalNo)))
				.withCountOfAnswerInvalid(new ResultDetailType().withTotal(BigInteger.ZERO))
				.withCountOfAnswerEmpty(new ResultDetailType().withTotal(BigInteger.valueOf(totalEmpty)));
	}

	private VariantBallotResultType mapToVariantBallotResult(final VariantBallotType variantBallot, final List<String> answersIdentifications) {

		if (variantBallot == null) {
			return null;
		}

		final List<StandardBallotResultType> questionInformation = variantBallot.getStandardQuestion().stream()
				.map(standardQuestion -> {

					final StandardAnswerType yesAnswerType = standardQuestion.getAnswer().stream()
							.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(YES_STR))
							.collect(MoreCollectors.onlyElement());

					final StandardAnswerType noAnswerType = standardQuestion.getAnswer().stream()
							.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(NO_STR))
							.collect(MoreCollectors.onlyElement());

					final StandardAnswerType emptyAnswerType = standardQuestion.getAnswer().stream()
							.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(EMPTY_STR))
							.collect(MoreCollectors.onlyElement());

					final long totalYes = answersIdentifications.stream()
							.filter(answerIdentification -> answerIdentification.equals(yesAnswerType.getAnswerIdentification()))
							.count();

					final long totalNo = answersIdentifications.stream()
							.filter(answerIdentification -> answerIdentification.equals(noAnswerType.getAnswerIdentification()))
							.count();

					final long totalEmpty = answersIdentifications.stream()
							.filter(answerIdentification -> answerIdentification.equals(emptyAnswerType.getAnswerIdentification()))
							.count();

					return new StandardBallotResultType()
							.withQuestionIdentification(standardQuestion.getQuestionIdentification())
							.withQuestion(mapToBallotQuestion(standardQuestion.getBallotQuestion()))
							.withCountOfAnswerYes(new ResultDetailType().withTotal(BigInteger.valueOf(totalYes)))
							.withCountOfAnswerNo(new ResultDetailType().withTotal(BigInteger.valueOf(totalNo)))
							.withCountOfAnswerInvalid(new ResultDetailType().withTotal(BigInteger.ZERO))
							.withCountOfAnswerEmpty(new ResultDetailType().withTotal(BigInteger.valueOf(totalEmpty)));
				})
				.toList();

		final List<VariantBallotResultType.TieBreak> tieBreak = variantBallot.getTieBreakQuestion().stream()
				.map(tieBreakQuestion -> {

					final TiebreakAnswerType emptyAnswerType = tieBreakQuestion.getAnswer().stream()
							.filter(TiebreakAnswerType::isHiddenAnswer)
							.collect(MoreCollectors.onlyElement());

					final long totalEmpty = answersIdentifications.stream()
							.filter(answerIdentification -> answerIdentification.equals(emptyAnswerType.getAnswerIdentification()))
							.count();

					return new VariantBallotResultType.TieBreak()
							.withQuestionIdentification(tieBreakQuestion.getQuestionIdentification())
							.withTieBreakQuestion(mapToTieBreakQuestion(tieBreakQuestion.getBallotQuestion()))
							.withCountOfAnswerInvalid(new ResultDetailType().withTotal(BigInteger.ZERO))
							.withCountOfAnswerEmpty(new ResultDetailType().withTotal(BigInteger.valueOf(totalEmpty)))
							.withCountInFavourOf(mapToCountInFavourOf(tieBreakQuestion, answersIdentifications));
				})
				.toList();

		return new VariantBallotResultType()
				.withQuestionInformation(questionInformation)
				.withTieBreak(tieBreak);
	}

	private static List<VariantBallotResultType.TieBreak.CountInFavourOf> mapToCountInFavourOf(
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType tieBreakQuestion, final List<String> answersIdentifications) {

		return tieBreakQuestion.getAnswer().stream()
				.map(tiebreakAnswerType -> {

					final String questionIdentification = tiebreakAnswerType.getStandardQuestionReference();

					final long countOfValidAnswers = answersIdentifications.stream()
							.filter(answer -> answer.equals(questionIdentification))
							.count();

					return new VariantBallotResultType.TieBreak.CountInFavourOf()
							.withQuestionIdentification(questionIdentification)
							.withCountOfValidAnswers(new ResultDetailType().withTotal(BigInteger.valueOf(countOfValidAnswers)));

				})
				.toList();
	}

	private TieBreakQuestionType mapToTieBreakQuestion(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotQuestionType ballotQuestion) {

		return new TieBreakQuestionType()
				.withTieBreakQuestionInfo(ballotQuestion.getBallotQuestionInfo().stream()
						.map(this::mapToTieBreakQuestionInfo)
						.toList());
	}

	record BallotElectionTypeExtended(BallotElectionType ballotElectionType, String chosenListIdentification, boolean isBlank,
									  boolean isChangedBallot,
									  boolean isWithPartyAffiliation, long emptyVotes, long numberOfCandidatesFromChosenList) {
	}

	record Counts(BigInteger countOfAccountedBallotsTotal, BigInteger countOfUnaccountedBlankBallots) {
	}

}
