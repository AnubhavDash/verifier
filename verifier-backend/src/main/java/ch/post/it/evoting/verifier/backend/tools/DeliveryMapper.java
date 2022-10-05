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
import java.util.Collection;
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
import ch.ech.xmlns.ech_0110._4.CountOfVotersInformationType;
import ch.ech.xmlns.ech_0110._4.CountingCircleResultsType;
import ch.ech.xmlns.ech_0110._4.Delivery;
import ch.ech.xmlns.ech_0110._4.EventResultDelivery;
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
import ch.ech.xmlns.ech_0155._4.TieBreakQuestionType;
import ch.ech.xmlns.ech_0155._4.TieBreakQuestionType.TieBreakQuestionInfo;
import ch.ech.xmlns.ech_0155._4.VoteDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.VoteType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.AuthorizationObjectType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.RegisterType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotVoteType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.DomainOfInfluenceType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

/**
 * Maps to {@link Delivery}.
 */
@Mapper
public interface DeliveryMapper {

	DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);
	String BLANK = "BLANK";

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

	default ReportingBodyType mapToReportingBodyType() {

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

	default List<CountingCircleResultsType> mapToCountingCircleResultsTypes(final Configuration configuration, final Results results) {

		return configuration.getAuthorizations().getAuthorization().stream()
				.map(AuthorizationType::getAuthorizationObject)
				.flatMap(Collection::stream)
				.map(AuthorizationObjectType::getCountingCircle)
				.distinct()
				.map(countingCircleType -> new CountingCircleType()
						.withCountingCircleId(countingCircleType.getId())
						.withCountingCircleName(countingCircleType.getName()))
				.map(countingCircleType -> new CountingCircleResultsType()
						.withCountingCircle(countingCircleType)
						.withVotingCardsInformation(mapToVotingCardsInformation(countingCircleType.getCountingCircleId(), results))
						.withVoteResults(mapToVoteResults(configuration, results, countingCircleType.getCountingCircleId())))
				.toList();
	}

	default VotingCardsInformationType mapToVotingCardsInformation(final String countingCircleIdentification, final Results results) {

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

	default List<VoteResultType> mapToVoteResults(final Configuration configuration, final Results results,
			final String countingCircleIdentification) {

		final List<AuthorizationType> authorizations = configuration.getAuthorizations().getAuthorization();
		final List<VoteInformationType> voteInformationTypes = configuration.getContest().getVoteInformation();
		final RegisterType register = configuration.getRegister();

		return voteInformationTypes.stream()
				.map(VoteInformationType::getVote)
				.filter(voteType ->
						isDomainOfInfluenceInCountingCircle(voteType.getDomainOfInfluence(), countingCircleIdentification, authorizations))
				.map(voteType -> new VoteResultType()
						.withVote(new VoteType()
								.withVoteIdentification(voteType.getVoteIdentification())
								.withDomainOfInfluenceIdentification(voteType.getDomainOfInfluence())
								.withVoteDescription(mapToVoteDescription(voteType.getVoteDescription())))
						.withCountOfVotersInformation(new CountOfVotersInformationType()
								.withCountOfVotersTotal(
										getCountOfVotersTotal(register, countingCircleIdentification, voteType.getDomainOfInfluence(),
												authorizations)))
						.withBallotResult(mapToBallotResultTypes(voteType, results, countingCircleIdentification)))
				.toList();
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
		final String authorizationIdentification = getAuthorizationIdentification(countingCircleIdentification, domainOfInfluenceIdentification,
				authorizations);

		return BigInteger.valueOf(registerType.getVoter().stream()
				.filter(voterType -> voterType.getAuthorization().equals(authorizationIdentification))
				.count());
	}

	private static String getAuthorizationIdentification(final String countingCircleIdentification, final String domainOfInfluenceIdentification,
			final List<AuthorizationType> authorizations) {

		return authorizations.stream()
				.filter(authorizationType -> authorizationType.getAuthorizationObject().stream()
						.anyMatch(authorizationObjectType ->
								authorizationObjectType.getDomainOfInfluence().getId().equals(domainOfInfluenceIdentification) &&
										authorizationObjectType.getCountingCircle().getId().equals(countingCircleIdentification)))
				.collect(MoreCollectors.onlyElement())
				.getAuthorizationIdentification();
	}

	default List<BallotResultType> mapToBallotResultTypes(final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType voteType,
			final Results results,
			final String countingCircleIdentification) {

		return voteType.getBallot().stream()
				.map(ballotType -> mapToBallotResult(ballotType, results, countingCircleIdentification, voteType.getDomainOfInfluence()))
				.toList();
	}

	default BallotResultType mapToBallotResult(final BallotType ballotType, final Results results,
			final String countingCircleIdentification, final String domainOfInfluenceIdentification) {

		final List<BallotVoteType> allBallotVotes = from(results)
				.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType.class)
				.filter(countingCircleType -> countingCircleType.getCountingCircleIdentification().equals(countingCircleIdentification))
				.find(DomainOfInfluenceType.class)
				.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification().equals(domainOfInfluenceIdentification))
				.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType.class)
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
					.filter(listOfMappedAnswersPerVoter -> listOfMappedAnswersPerVoter.stream().anyMatch(answer -> !answer.equals(BLANK)))
					.count());

			final BigInteger countOfUnaccountedBlankBallots = BigInteger.valueOf(allVariantBallotsMapped.stream()
					.filter(listOfMappedAnswersPerVoter -> listOfMappedAnswersPerVoter.stream().allMatch(answer -> answer.equals(BLANK)))
					.count());

			return new Counts(countOfAccountedBallotsTotal, countOfUnaccountedBlankBallots);
		} else {
			throw new IllegalStateException("Ballot must either be standard or variant.");
		}
	}

	private static String mapAnswer(final String answer, final VariantBallotType variantBallot) {
		for (final StandardQuestionType standardQuestion : variantBallot.getStandardQuestion()) {

			final StandardAnswerType yesAnswerType = standardQuestion.getAnswer().stream()
					.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("YES"))
					.collect(MoreCollectors.onlyElement());

			final StandardAnswerType noAnswerType = standardQuestion.getAnswer().stream()
					.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("NO"))
					.collect(MoreCollectors.onlyElement());

			final StandardAnswerType emptyAnswerType = standardQuestion.getAnswer().stream()
					.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("EMPTY"))
					.collect(MoreCollectors.onlyElement());

			if (answer.equals(yesAnswerType.getAnswerIdentification())) {
				return yesAnswerType.getStandardAnswerType();
			} else if (answer.equals(noAnswerType.getAnswerIdentification())) {
				return noAnswerType.getStandardAnswerType();
			} else if (answer.equals(emptyAnswerType.getAnswerIdentification())) {
				return BLANK;
			}
		}

		for (final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType tieBreakQuestion : variantBallot.getTieBreakQuestion()) {
			final TiebreakAnswerType emptyAnswerType = tieBreakQuestion.getAnswer().stream()
					.filter(TiebreakAnswerType::isHiddenAnswer)
					.collect(MoreCollectors.onlyElement());

			if (answer.equals(emptyAnswerType.getAnswerIdentification())) {
				return BLANK;
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

	default StandardBallotResultType mapToStandardBallotResult(final StandardBallotType standardBallot, final List<String> answersIdentifications) {

		if (standardBallot == null) {
			return null;
		}

		final StandardAnswerType yesAnswerType = standardBallot.getAnswer().stream()
				.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("YES"))
				.collect(MoreCollectors.onlyElement());

		final StandardAnswerType noAnswerType = standardBallot.getAnswer().stream()
				.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("NO"))
				.collect(MoreCollectors.onlyElement());

		final StandardAnswerType emptyAnswerType = standardBallot.getAnswer().stream()
				.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("EMPTY"))
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

	default VariantBallotResultType mapToVariantBallotResult(final VariantBallotType variantBallot, final List<String> answersIdentifications) {

		if (variantBallot == null) {
			return null;
		}

		final List<StandardBallotResultType> questionInformation = variantBallot.getStandardQuestion().stream()
				.map(standardQuestion -> {

					final StandardAnswerType yesAnswerType = standardQuestion.getAnswer().stream()
							.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("YES"))
							.collect(MoreCollectors.onlyElement());

					final StandardAnswerType noAnswerType = standardQuestion.getAnswer().stream()
							.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("NO"))
							.collect(MoreCollectors.onlyElement());

					final StandardAnswerType emptyAnswerType = standardQuestion.getAnswer().stream()
							.filter(standardAnswerType -> standardAnswerType.getStandardAnswerType().equals("EMPTY"))
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
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType tieBreakQuestion,
			final List<String> answersIdentifications) {

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

	default TieBreakQuestionType mapToTieBreakQuestion(
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotQuestionType ballotQuestion) {

		return new TieBreakQuestionType()
				.withTieBreakQuestionInfo(ballotQuestion.getBallotQuestionInfo().stream()
						.map(this::mapToTieBreakQuestionInfo)
						.toList());
	}

	record Counts(BigInteger countOfAccountedBallotsTotal, BigInteger countOfUnaccountedBlankBallots) {
	}

}
