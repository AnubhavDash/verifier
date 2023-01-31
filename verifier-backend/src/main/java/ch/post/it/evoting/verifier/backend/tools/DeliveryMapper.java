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

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotBoxType;
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

	ElectionType mapToElectionType(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionType electionType);

	@Mapping(target = "candidateReference", source = "candidateType.referenceOnPosition")
	@Mapping(target = "officialCandidateYesNo", expression = "java(candidateType.getCandidateIdentification() != null)")
	CandidateInformationType mapToCandidateInformationType(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CandidateType candidateType);

	ListInformationType mapToListInformationType(final ListType listType);

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
				.sorted(Comparator.comparing(AuthorizationType::getAuthorizationName))
				.map(AuthorizationType::getAuthorizationObject)
				.flatMap(Collection::stream)
				.map(AuthorizationObjectType::getCountingCircle)
				.map(countingCircleType -> new CountingCircleTypeRecord(countingCircleType.getId(), countingCircleType.getName()))
				.distinct()
				.map(countingCircleTypeRecord -> new CountingCircleType()
						.withCountingCircleId(countingCircleTypeRecord.id())
						.withCountingCircleName(countingCircleTypeRecord.name()))
				.map(countingCircleType -> new CountingCircleResultsType()
						.withCountingCircle(countingCircleType)
						.withVotingCardsInformation(mapToVotingCardsInformationType(countingCircleType.getCountingCircleId(), results))
						.withVoteResults(mapToVoteResults(configuration, results, countingCircleType.getCountingCircleId()))
						.withElectionGroupResults(mapToElectionGroupResultsType(configuration, results, countingCircleType.getCountingCircleId()))
				)
				.toList();
	}

	private VotingCardsInformationType mapToVotingCardsInformationType(final String countingCircleIdentification, final Results results) {

		final BigInteger countOfReceivedValidVotingCardsTotal = BigInteger.valueOf(
				results.getBallotsBox().stream().parallel()
						.map(BallotBoxType::getCountingCircle)
						.flatMap(Collection::stream)
						.filter(countingCircleType -> countingCircleType.getCountingCircleIdentification().equals(countingCircleIdentification))
						.map(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType::getDomainOfInfluence)
						.flatMap(Collection::stream)
						.map(DomainOfInfluenceType::getVote)
						.flatMap(Collection::stream)
						.map(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType::getBallot)
						.mapToLong(Collection::size)
						.reduce(0, Math::addExact));

		return new VotingCardsInformationType()
				.withCountOfReceivedValidVotingCardsTotal(countOfReceivedValidVotingCardsTotal)
				.withCountOfReceivedInvalidVotingCardsTotal(BigInteger.ZERO);
	}

	private List<VoteResultType> mapToVoteResults(final Configuration configuration, final Results results,
			final String countingCircleIdentification) {

		final List<AuthorizationType> authorizationTypes = configuration.getAuthorizations().getAuthorization();
		final List<VoteInformationType> voteInformationTypes = configuration.getContest().getVoteInformation();
		final RegisterType registerType = configuration.getRegister();

		return voteInformationTypes.stream()
				.map(VoteInformationType::getVote)
				.filter(voteType ->
						isDomainOfInfluenceInCountingCircle(voteType.getDomainOfInfluence(), countingCircleIdentification, authorizationTypes))
				.map(voteType -> {
					final String domainOfInfluence = voteType.getDomainOfInfluence();

					return new VoteResultType()
							.withVote(new VoteType()
									.withVoteIdentification(voteType.getVoteIdentification())
									.withDomainOfInfluenceIdentification(domainOfInfluence)
									.withVoteDescription(mapToVoteDescription(voteType.getVoteDescription())))
							.withCountOfVotersInformation(new CountOfVotersInformationType()
									.withCountOfVotersTotal(
											getCountOfVotersTotal(registerType, countingCircleIdentification, domainOfInfluence, authorizationTypes)))
							.withBallotResult(mapToBallotResultTypes(voteType, results, countingCircleIdentification));
				})
				.toList();
	}

	private List<ElectionGroupResultsType> mapToElectionGroupResultsType(final Configuration configuration, final Results results,
			final String countingCircleIdentification) {

		final List<AuthorizationType> authorizationTypes = configuration.getAuthorizations().getAuthorization();
		final RegisterType registerType = configuration.getRegister();
		final List<ElectionInformationType> electionInformationTypes = configuration.getContest().getElectionInformation();

		return configuration.getContest().getElectionInformation().stream()
				.map(ElectionInformationType::getElection)
				.filter(electionType -> isDomainOfInfluenceInCountingCircle(electionType.getDomainOfInfluence(), countingCircleIdentification,
						authorizationTypes))
				.map(electionType -> mapToElectionGroupResultsType(results, countingCircleIdentification, electionInformationTypes,
						authorizationTypes, registerType,
						electionType))
				.toList();
	}

	private ElectionGroupResultsType mapToElectionGroupResultsType(final Results results, final String countingCircleIdentification,
			final List<ElectionInformationType> electionInformationTypes, final List<AuthorizationType> authorizationTypes,
			final RegisterType registerType,
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionType electionType) {

		final String domainOfInfluenceIdentification = electionType.getDomainOfInfluence();
		final String electionIdentification = electionType.getElectionIdentification();

		final ElectionInformationType electionInformationType = electionInformationTypes.stream()
				.filter(eit -> eit.getElection().getElectionIdentification().equals(electionIdentification))
				.collect(MoreCollectors.onlyElement());

		final List<ListType> listTypes = electionInformationType.getList();

		final List<BallotElectionType> ballotElectionTypes = results.getBallotsBox().stream().parallel()
				.map(BallotBoxType::getCountingCircle)
				.flatMap(Collection::stream)
				.filter(countingCircleType -> countingCircleType.getCountingCircleIdentification().equals(countingCircleIdentification))
				.map(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType::getDomainOfInfluence)
				.flatMap(Collection::stream)
				.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification().equals(domainOfInfluenceIdentification))
				.map(DomainOfInfluenceType::getElection)
				.flatMap(Collection::stream)
				.filter(et -> et.getElectionIdentification().equals(electionIdentification))
				.map(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType::getBallot)
				.flatMap(Collection::stream)
				.toList();

		final List<BallotElectionTypeExtended> ballotElectionTypesExtended = toBallotElectionTypeExtended(ballotElectionTypes, listTypes);

		final BigInteger countOfUnaccountedBlankBallots = getCountOfUnaccountedBlankBallots(ballotElectionTypesExtended);
		final BigInteger countOfUnaccountedInvalidBallots = BigInteger.ZERO;

		final ElectionGroupResultsType electionGroupResultsType = new ElectionGroupResultsType()
				.withElectionResults(mapToElectionResultType(electionInformationType, ballotElectionTypesExtended))
				.withDomainOfInfluenceIdentification(domainOfInfluenceIdentification)
				.withElectionGroupDescription(mapToElectionGroupDescriptionType(electionInformationType))
				.withCountOfVotersInformation(new CountOfVotersInformationType().withCountOfVotersTotal(
						getCountOfVotersTotal(registerType, countingCircleIdentification, domainOfInfluenceIdentification,
								authorizationTypes)))
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

	private ElectionGroupDescriptionType mapToElectionGroupDescriptionType(final ElectionInformationType electionInformationType) {

		final List<ElectionDescriptionInfo> electionDescriptionInfo = electionInformationType.getElection().getElectionDescription()
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

	private ElectionResultType mapToElectionResultType(final ElectionInformationType electionInformationType,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {
		final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionType election = electionInformationType.getElection();
		final BigInteger typeOfElection = election.getTypeOfElection();

		final ElectionResultType electionResultType = new ElectionResultType()
				.withElection(mapToElectionType(election));

		if (PROPORTIONAL.equals(typeOfElection)) {
			return electionResultType
					.withProportionalElection(mapToProportionalElection(typeOfElection, electionInformationType, ballotElectionTypesExtended));
		} else if (MAJORAL.equals(typeOfElection)) {
			return electionResultType
					.withMajoralElection(mapToMajoralElection(typeOfElection, electionInformationType, ballotElectionTypesExtended));
		} else {
			throw new IllegalStateException(String.format("Unknown type of election. [typeOfElection: %s]", typeOfElection));
		}
	}

	private ElectionResultType.ProportionalElection mapToProportionalElection(final BigInteger typeOfElection,
			final ElectionInformationType electionInformationType, final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final List<CandidateType> candidateTypes = electionInformationType.getCandidate();
		final List<ListType> listTypes = electionInformationType.getList();

		return new ElectionResultType.ProportionalElection()
				.withCountOfChangedBallotsWithPartyAffiliation(
						getCountOfChangedBallotsWithPartyAffiliation(ballotElectionTypesExtended))
				.withCountOfChangedBallotsWithoutPartyAffiliation(
						getCountOfChangedBallotsWithoutPartyAffiliation(ballotElectionTypesExtended))
				.withCountOfEmptyVotesOfChangedBallotsWithoutPartyAffiliation(
						getCountOfEmptyVotesOfChangedBallotsWithoutPartyAffiliation(ballotElectionTypesExtended))
				.withList(mapToListResults(listTypes, ballotElectionTypesExtended))
				.withCandidate(mapToCandidateResultTypes(typeOfElection, listTypes, candidateTypes, ballotElectionTypesExtended));
	}

	private ResultDetailType getCountOfEmptyVotesOfChangedBallotsWithoutPartyAffiliation(
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(ballotElectionTypesExtended.stream()
						.filter(ballotElectionTypeExtended -> ballotElectionTypeExtended.isChangedBallot
								&& !ballotElectionTypeExtended.isWithPartyAffiliation)
						.mapToLong(BallotElectionTypeExtended::emptyVotes)
						.reduce(0, Math::addExact)));
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

	private List<CandidateResultType> mapToCandidateResultTypes(final BigInteger typeOfElection, final List<ListType> listTypes,
			final List<CandidateType> candidateTypes, final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final List<CandidateResultType> candidateResultTypes = new ArrayList<>();
		candidateResultTypes.addAll(candidateTypes.stream()
				.map(candidate -> {
					final String candidateIdentification = candidate.getCandidateIdentification();

					final CandidateResultType candidateResultType = new CandidateResultType()
							.withCandidateInformation(mapToCandidateInformationType(candidate))
							.withListResults(
									mapToCandidateListResultsForProportional(candidateIdentification, listTypes, ballotElectionTypesExtended));

					return candidateResultType.withCountOfVotesTotal(
							getCountOfVotesTotal(typeOfElection, listTypes, ballotElectionTypesExtended, candidateResultType));
				})
				.toList());

		candidateResultTypes.addAll(ballotElectionTypesExtended.stream()
				.map(ballotElectionTypeExtended -> {
					final List<String> chosenWriteInsCandidateValues = ballotElectionTypeExtended.ballotElectionType.getChosenWriteInsCandidateValue();

					return chosenWriteInsCandidateValues.stream()
							.map(chosenWriteInsCandidateValue -> new CandidateResultType()
									.withWriteIn(chosenWriteInsCandidateValue)
									.withListResults(listTypes.stream()
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

		return Collections.unmodifiableList(candidateResultTypes);
	}

	private BigInteger getCountOfVotesTotal(final BigInteger typeOfElection, final List<ListType> listTypes,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended, final CandidateResultType candidateResultType) {

		final BigInteger countOfVotesTotal;

		if (PROPORTIONAL.equals(typeOfElection)) {
			record CountOfVotes(BigInteger fromUnchangedBallots, BigInteger fromChangedBallots) {
			}

			final List<CountOfVotes> countOfVotes = candidateResultType.getListResults().stream()
					.map(listResult -> new CountOfVotes(listResult.getCountOfvotesFromUnchangedBallots().getTotal(),
							listResult.getCountOfvotesFromChangedBallots().getTotal()))
					.toList();

			final BigInteger countOfvotesFromUnchangedBallots = countOfVotes.stream()
					.map(CountOfVotes::fromUnchangedBallots)
					.reduce(BigInteger.ZERO, BigInteger::add);

			final BigInteger countOfvotesFromChangedBallots = countOfVotes.stream()
					.map(CountOfVotes::fromChangedBallots)
					.reduce(BigInteger.ZERO, BigInteger::add);

			countOfVotesTotal = countOfvotesFromUnchangedBallots.add(countOfvotesFromChangedBallots);

		} else if (MAJORAL.equals(typeOfElection)) {

			final List<CandidatePositionType> candidatePositionTypes = listTypes.stream()
					.map(ListType::getCandidatePosition)
					.flatMap(Collection::stream)
					.toList();

			final String candidateIdentification = candidateResultType.getCandidateInformation().getCandidateIdentification();

			countOfVotesTotal = getCountOfVotesTotalWithoutWriteIns(candidateIdentification, ballotElectionTypesExtended, candidatePositionTypes);

		} else {
			throw new IllegalStateException(String.format("Unknown type of election. [typeOfElection: %s]", typeOfElection));
		}

		return countOfVotesTotal;
	}

	private List<ListResultsType> mapToListResults(final List<ListType> listTypes,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		return listTypes.stream()
				.map(list -> {
					final String listIdentification = list.getListIdentification();
					final boolean isEmptyList = list.isListEmpty();

					final ListResultsType listResultsType = new ListResultsType()
							.withListInformation(mapToListInformationType(list))
							.withCountOfChangedBallots(getCountOfChangedBallots(listIdentification, ballotElectionTypesExtended))
							.withCountOfUnchangedBallots(getCountOfUnchangedBallots(listIdentification, ballotElectionTypesExtended))
							.withCountOfCandidateVotes(getCountOfCandidateVotes(listIdentification, ballotElectionTypesExtended))
							.withCountOfAdditionalVotes(
									getCountOfAdditionalVotes(listIdentification, isEmptyList, ballotElectionTypesExtended));

					final BigInteger countOfCandidateVotes = listResultsType.getCountOfCandidateVotes().getTotal();
					final BigInteger countOfAdditionalVotes = listResultsType.getCountOfAdditionalVotes().getTotal();

					return listResultsType
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
								.reduce(0, Math::addExact)));
	}

	private ResultDetailType getCountOfCandidateVotes(final String listIdentification,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		return new ResultDetailType()
				.withTotal(BigInteger.valueOf(
						ballotElectionTypesExtended.stream()
								.filter(ballotElectionTypeExtended -> listIdentification.equals(ballotElectionTypeExtended.chosenListIdentification))
								.mapToLong(BallotElectionTypeExtended::numberOfCandidatesFromChosenList)
								.reduce(0, Math::addExact)));
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

	private List<BallotElectionTypeExtended> toBallotElectionTypeExtended(final List<BallotElectionType> ballotElectionTypes,
			final List<ListType> listTypes) {

		final ListType emptyListType = listTypes.stream()
				.filter(ListType::isListEmpty)
				.collect(MoreCollectors.onlyElement());

		final List<String> emptyListCandidateListIdentifications = emptyListType.getCandidatePosition().stream()
				.map(CandidatePositionType::getCandidateListIdentification)
				.toList();

		return ballotElectionTypes.stream()
				.map(ballotElectionType -> {
					final String chosenListIdentification = ballotElectionType.getChosenListIdentification();

					final List<String> chosenWriteInsCandidateValues = ballotElectionType.getChosenWriteInsCandidateValue();
					final List<String> chosenCandidateListIdentifications = ballotElectionType.getChosenCandidateListIdentification();
					final List<String> chosenCandidateIdentifications = ballotElectionType.getChosenCandidateIdentification();

					final ListType chosenListType = listTypes.stream()
							.filter(list -> list.getListIdentification().equals(chosenListIdentification))
							.findFirst()
							.orElse(null);

					final List<String> candidateListIdentificationsFromChosenList =
							chosenListType == null ? List.of() : chosenListType.getCandidatePosition().stream()
									.map(CandidatePositionType::getCandidateListIdentification)
									.toList();

					final long emptyVotes = chosenCandidateListIdentifications.stream()
							.filter(emptyListCandidateListIdentifications::contains)
							.count();

					final long numberOfCandidatesFromChosenList = chosenCandidateListIdentifications.stream()
							.filter(candidateListIdentificationsFromChosenList::contains)
							.count();

					final boolean isEmptyListChosen = emptyListType.getListIdentification().equals(chosenListIdentification);

					final boolean isBlank =
							(Objects.isNull(chosenListIdentification) || (isEmptyListChosen && emptyListCandidateListIdentifications.containsAll(
									chosenCandidateListIdentifications)))
									&& chosenCandidateIdentifications.stream().allMatch(Objects::isNull)
									&& chosenWriteInsCandidateValues.stream().allMatch(Objects::isNull);

					final boolean ballotHasWriteIn = !chosenWriteInsCandidateValues.isEmpty();

					final boolean allCandidatesFromListHaveBeenChosen = chosenCandidateListIdentifications.containsAll(
							candidateListIdentificationsFromChosenList);

					final boolean allChosenCandidatesAreFromList = candidateListIdentificationsFromChosenList.containsAll(
							chosenCandidateListIdentifications);

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
			final ElectionInformationType electionInformationType,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final List<CandidateType> candidateTypes = electionInformationType.getCandidate();
		final List<ListType> listTypes = electionInformationType.getList();

		final BigInteger countOfInvalidVotesTotal = BigInteger.ZERO;

		return new ElectionResultType.MajoralElection()
				.withCandidate(mapToCandidateResultTypes(typeOfElection, listTypes, candidateTypes, ballotElectionTypesExtended))
				.withCountOfInvalidVotesTotal(new ResultDetailType().withTotal(countOfInvalidVotesTotal));
	}

	private BigInteger getCountOfVotesTotalWithoutWriteIns(final String candidateIdentification,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended, final List<CandidatePositionType> candidatePositionTypes) {
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
										.map(chosenCandidateListIdentification -> candidatePositionTypes.stream()
												.filter(candidatePosition ->
														candidateIdentification.equals(candidatePosition.getCandidateIdentification())
																&& candidatePosition.getCandidateListIdentification()
																.equals(chosenCandidateListIdentification))
												.toList())
										.mapToLong(Collection::size)
										.reduce(0, Math::addExact)
				)
				.reduce(0, Math::addExact));
	}

	private List<CandidateListResultType> mapToCandidateListResultsForProportional(final String candidateIdentification,
			final List<ListType> listTypes,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final List<CandidatePositionType> candidatePositionTypes = listTypes.stream()
				.map(ListType::getCandidatePosition)
				.flatMap(Collection::stream)
				.toList();

		return listTypes.stream()
				.map(list -> new CandidateListResultType()
						.withListIdentification(list.getListIdentification())
						.withCountOfvotesFromUnchangedBallots(
								getCountOfvotesFromUnchangedBallot(candidateIdentification, list, ballotElectionTypesExtended))
						.withCountOfvotesFromChangedBallots(
								getCountOfvotesFromChangedBallot(candidateIdentification, list, candidatePositionTypes, ballotElectionTypesExtended)))
				.toList();
	}

	private ResultDetailType getCountOfvotesFromChangedBallot(final String candidateIdentification, final ListType listType,
			final List<CandidatePositionType> candidatePositionTypes, final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final BigInteger countOfvotesFromChangedBallot = ballotElectionTypesExtended.stream()
				.filter(BallotElectionTypeExtended::isChangedBallot)
				.map(ballotElectionTypeExtended -> {
					final String chosenListIdentification = ballotElectionTypeExtended.chosenListIdentification;

					if (!listType.getListIdentification().equals(chosenListIdentification)) {
						return BigInteger.ZERO;
					}

					final List<String> chosenCandidateListIdentifications = ballotElectionTypeExtended.ballotElectionType.getChosenCandidateListIdentification();

					return BigInteger.valueOf(chosenCandidateListIdentifications.stream()
							.filter(chosenCandidateListIdentification ->
									candidatePositionTypes.stream()
											.anyMatch(candidatePosition ->
													candidateIdentification.equals(candidatePosition.getCandidateIdentification())
															&& candidatePosition.getCandidateListIdentification()
															.equals(chosenCandidateListIdentification)))
							.count());
				})
				.reduce(BigInteger.ZERO, BigInteger::add);

		return new ResultDetailType().withTotal(countOfvotesFromChangedBallot);
	}

	private ResultDetailType getCountOfvotesFromUnchangedBallot(final String candidateIdentification, final ListType listTypes,
			final List<BallotElectionTypeExtended> ballotElectionTypesExtended) {

		final BigInteger countOfvotesFromUnchangedBallot = ballotElectionTypesExtended.stream()
				.filter(ballotElectionTypeExtended -> !ballotElectionTypeExtended.isChangedBallot)
				.map(ballotElectionTypeExtended -> {
					final String chosenListIdentification = ballotElectionTypeExtended.chosenListIdentification;

					if (!listTypes.getListIdentification().equals(chosenListIdentification) || listTypes.isListEmpty()) {
						return BigInteger.ZERO;
					}

					final List<String> chosenCandidateListIdentifications = ballotElectionTypeExtended.ballotElectionType.getChosenCandidateListIdentification();

					final List<CandidatePositionType> candidatePositionTypes = listTypes.getCandidatePosition();

					return BigInteger.valueOf(chosenCandidateListIdentifications.stream()
							.filter(chosenCandidateListIdentification ->
									candidatePositionTypes.stream()
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
			final String countingCircleIdentification, final List<AuthorizationType> authorizationTypes) {

		return authorizationTypes.stream()
				.map(AuthorizationType::getAuthorizationObject)
				.flatMap(Collection::stream)
				.filter(authorizationObjectType -> authorizationObjectType.getDomainOfInfluence().getId().equals(domainOfInfluenceIdentification))
				.map(AuthorizationObjectType::getCountingCircle)
				.map(ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CountingCircleType::getId)
				.toList().contains(countingCircleIdentification);
	}

	private static BigInteger getCountOfVotersTotal(final RegisterType registerType, final String countingCircleIdentification,
			final String domainOfInfluenceIdentification, final List<AuthorizationType> authorizationTypes) {
		final List<String> authorizationIdentificationList = getAuthorizationIdentificationList(countingCircleIdentification,
				domainOfInfluenceIdentification, authorizationTypes);

		return BigInteger.valueOf(registerType.getVoter().stream()
				.filter(voterType -> authorizationIdentificationList.stream()
						.anyMatch(authorizationIdentification -> voterType.getAuthorization().equals(authorizationIdentification)))
				.count());
	}

	private static List<String> getAuthorizationIdentificationList(final String countingCircleIdentification,
			final String domainOfInfluenceIdentification, final List<AuthorizationType> authorizationTypes) {

		return authorizationTypes.stream()
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
				.map(ballotType -> mapToBallotResultType(ballotType, results, countingCircleIdentification, domainOfInfluenceIdentification,
						voteIdentification))
				.toList();
	}

	private BallotResultType mapToBallotResultType(final BallotType ballotType, final Results results,
			final String countingCircleIdentification, final String domainOfInfluenceIdentification, final String voteIdentification) {

		final List<BallotVoteType> allBallotVoteTypes = results.getBallotsBox().stream().parallel()
				.map(BallotBoxType::getCountingCircle)
				.flatMap(Collection::stream)
				.filter(countingCircleType -> countingCircleType.getCountingCircleIdentification().equals(countingCircleIdentification))
				.map(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType::getDomainOfInfluence)
				.flatMap(Collection::stream)
				.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification().equals(domainOfInfluenceIdentification))
				.map(DomainOfInfluenceType::getVote)
				.flatMap(Collection::stream)
				.filter(voteType -> voteType.getVoteIdentification().equals(voteIdentification))
				.map(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType::getBallot)
				.flatMap(Collection::stream)
				.toList();

		final List<String> allAnswersIdentifications = allBallotVoteTypes.stream()
				.map(BallotVoteType::getChosenAnswerIdentification)
				.flatMap(Collection::stream)
				.toList();

		final BallotResultType ballotResultType = new BallotResultType()
				.withBallotIdentification(ballotType.getBallotIdentification())
				.withBallotPosition(ballotType.getBallotPosition())
				.withBallotDescription(mapToBallotDescriptionInformation(ballotType.getBallotDescription()))
				.withCountOfReceivedBallotsTotal(new ResultDetailType().withTotal(BigInteger.valueOf(allBallotVoteTypes.size())))
				.withStandardBallot(mapToStandardBallotResultType(ballotType.getStandardBallot(), allAnswersIdentifications))
				.withVariantBallot(mapToVariantBallotResultType(ballotType.getVariantBallot(), allAnswersIdentifications));

		final Counts counts = getCounts(ballotType, allBallotVoteTypes, ballotResultType);

		ballotResultType
				.withCountOfAccountedBallotsTotal(new ResultDetailType().withTotal(counts.countOfAccountedBallotsTotal))
				.withCountOfUnaccountedBlankBallots(new ResultDetailType().withTotal(counts.countOfUnaccountedBlankBallots))
				.withCountOfUnaccountedInvalidBallots(new ResultDetailType().withTotal(BigInteger.ZERO));

		ballotResultType
				.withCountOfUnaccountedBallotsTotal(new ResultDetailType().withTotal(ballotResultType.getCountOfUnaccountedBlankBallots().getTotal()
						.add(ballotResultType.getCountOfUnaccountedInvalidBallots().getTotal())));

		return ballotResultType;
	}

	private static Counts getCounts(final BallotType ballotType, final List<BallotVoteType> allBallotVoteTypes,
			final BallotResultType ballotResultType) {
		if (ballotType.getStandardBallot() != null) {
			final StandardBallotResultType standardBallotType = ballotResultType.getStandardBallot();

			final BigInteger countOfAccountedBallotsTotal = standardBallotType.getCountOfAnswerYes().getTotal()
					.add(standardBallotType.getCountOfAnswerNo().getTotal());
			final BigInteger countOfUnaccountedBlankBallots = standardBallotType.getCountOfAnswerEmpty().getTotal();

			checkState(ballotResultType.getCountOfReceivedBallotsTotal().getTotal().subtract(countOfUnaccountedBlankBallots)
					.equals(countOfAccountedBallotsTotal));

			return new Counts(countOfAccountedBallotsTotal, countOfUnaccountedBlankBallots);

		} else if (ballotType.getVariantBallot() != null) {

			final List<List<String>> allVariantBallotsMapped = allBallotVoteTypes.stream()
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

	private static String mapAnswer(final String answer, final VariantBallotType variantBallotType) {
		for (final StandardQuestionType standardQuestionType : variantBallotType.getStandardQuestion()) {

			final StandardAnswerType yesAnswerType = standardQuestionType.getAnswer().stream()
					.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(YES_STR))
					.collect(MoreCollectors.onlyElement());

			final StandardAnswerType noAnswerType = standardQuestionType.getAnswer().stream()
					.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(NO_STR))
					.collect(MoreCollectors.onlyElement());

			final StandardAnswerType emptyAnswerType = standardQuestionType.getAnswer().stream()
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

		for (final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType tieBreakQuestionType : variantBallotType.getTieBreakQuestion()) {
			final TiebreakAnswerType emptyAnswerType = tieBreakQuestionType.getAnswer().stream()
					.filter(a -> Objects.nonNull(a.isHiddenAnswer()))
					.filter(TiebreakAnswerType::isHiddenAnswer)
					.collect(MoreCollectors.onlyElement());

			if (answer.equals(emptyAnswerType.getAnswerIdentification())) {
				return BLANK_STR;
			}

			final List<TiebreakAnswerType> nonBlankTieBreakAnswerTypes = tieBreakQuestionType.getAnswer().stream()
					.filter(a -> Objects.isNull(a.isHiddenAnswer()) || !a.isHiddenAnswer())
					.toList();

			for (final TiebreakAnswerType tiebreakAnswerType : nonBlankTieBreakAnswerTypes) {
				final String questionIdentification = tiebreakAnswerType.getStandardQuestionReference();
				final String answerIdentification = tiebreakAnswerType.getAnswerIdentification();

				checkState(questionIdentification != null, "The question identification cannot be null.");

				if (answer.equals(answerIdentification)) {
					return questionIdentification;
				}
			}
		}

		return null;
	}

	private StandardBallotResultType mapToStandardBallotResultType(final StandardBallotType standardBallotType,
			final List<String> answersIdentifications) {

		if (standardBallotType == null) {
			return null;
		}

		final StandardAnswerType yesAnswerType = standardBallotType.getAnswer().stream()
				.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(YES_STR))
				.collect(MoreCollectors.onlyElement());

		final StandardAnswerType noAnswerType = standardBallotType.getAnswer().stream()
				.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals(NO_STR))
				.collect(MoreCollectors.onlyElement());

		final StandardAnswerType emptyAnswerType = standardBallotType.getAnswer().stream()
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
				.withQuestionIdentification(standardBallotType.getQuestionIdentification())
				.withQuestion(mapToBallotQuestion(standardBallotType.getBallotQuestion()))
				.withCountOfAnswerYes(new ResultDetailType().withTotal(BigInteger.valueOf(totalYes)))
				.withCountOfAnswerNo(new ResultDetailType().withTotal(BigInteger.valueOf(totalNo)))
				.withCountOfAnswerInvalid(new ResultDetailType().withTotal(BigInteger.ZERO))
				.withCountOfAnswerEmpty(new ResultDetailType().withTotal(BigInteger.valueOf(totalEmpty)));
	}

	private VariantBallotResultType mapToVariantBallotResultType(final VariantBallotType variantBallotType,
			final List<String> answersIdentifications) {

		if (variantBallotType == null) {
			return null;
		}

		final List<StandardBallotResultType> questionInformationTypes = variantBallotType.getStandardQuestion().stream()
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

		final List<VariantBallotResultType.TieBreak> tieBreak = variantBallotType.getTieBreakQuestion().stream()
				.map(tieBreakQuestion -> {

					final TiebreakAnswerType emptyAnswerType = tieBreakQuestion.getAnswer().stream()
							.filter(a -> Objects.nonNull(a.isHiddenAnswer()))
							.filter(TiebreakAnswerType::isHiddenAnswer)
							.collect(MoreCollectors.onlyElement());

					final long totalEmpty = answersIdentifications.stream()
							.filter(answerIdentification -> answerIdentification.equals(emptyAnswerType.getAnswerIdentification()))
							.count();

					return new VariantBallotResultType.TieBreak()
							.withQuestionIdentification(tieBreakQuestion.getQuestionIdentification())
							.withTieBreakQuestion(mapToTieBreakQuestionType(tieBreakQuestion.getBallotQuestion()))
							.withCountOfAnswerInvalid(new ResultDetailType().withTotal(BigInteger.ZERO))
							.withCountOfAnswerEmpty(new ResultDetailType().withTotal(BigInteger.valueOf(totalEmpty)))
							.withCountInFavourOf(mapToCountInFavourOf(tieBreakQuestion, answersIdentifications));
				})
				.toList();

		return new VariantBallotResultType()
				.withQuestionInformation(questionInformationTypes)
				.withTieBreak(tieBreak);
	}

	private static List<VariantBallotResultType.TieBreak.CountInFavourOf> mapToCountInFavourOf(
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType tieBreakQuestionType,
			final List<String> answersIdentifications) {

		return tieBreakQuestionType.getAnswer().stream()
				.parallel()
				.filter(a -> Objects.isNull(a.isHiddenAnswer()) || !a.isHiddenAnswer())
				.map(tiebreakAnswerType -> {

					final String questionIdentification = tiebreakAnswerType.getStandardQuestionReference();
					final String answerIdentification = tiebreakAnswerType.getAnswerIdentification();

					final long countOfValidAnswers = answersIdentifications.stream()
							.parallel()
							.filter(answer -> answer.equals(answerIdentification))
							.count();

					return new VariantBallotResultType.TieBreak.CountInFavourOf()
							.withQuestionIdentification(questionIdentification)
							.withCountOfValidAnswers(new ResultDetailType().withTotal(BigInteger.valueOf(countOfValidAnswers)));

				})
				.toList();
	}

	private TieBreakQuestionType mapToTieBreakQuestionType(
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotQuestionType ballotQuestionType) {

		return new TieBreakQuestionType()
				.withTieBreakQuestionInfo(ballotQuestionType.getBallotQuestionInfo().stream()
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
