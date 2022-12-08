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

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0058._5.SendingApplicationType;
import ch.ech.xmlns.ech_0155._4.AnswerOptionIdentificationType;
import ch.ech.xmlns.ech_0222._1.Delivery;
import ch.ech.xmlns.ech_0222._1.ElectionRawDataType;
import ch.ech.xmlns.ech_0222._1.EventRawDataDelivery;
import ch.ech.xmlns.ech_0222._1.RawDataType;
import ch.ech.xmlns.ech_0222._1.ReportingBodyType;
import ch.ech.xmlns.ech_0222._1.VoteRawDataType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CandidatePositionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CandidateType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ListType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotElectionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType;

public class RawDataDeliveryMapper {

	private RawDataDeliveryMapper() {
		// Intentionally left blank.
	}

	/**
	 * @param configuration the configuration of the event.
	 * @param decrypt       the evoting-decrypt.
	 * @return the eCH-0222.
	 */
	public static Delivery createECH0222(final String electionEventId, final Configuration configuration, final Results decrypt) {
		final Delivery deliveryResult = new Delivery();

		deliveryResult.setDeliveryHeader(createDeliveryHeader(electionEventId));
		deliveryResult.setRawDataDelivery(createRawDataDelivery(electionEventId, configuration, decrypt));

		return deliveryResult;
	}

	private static HeaderType createDeliveryHeader(final String electionEventId) {
		return new HeaderType()
				.withSenderId("http://www.post.ch")
				.withMessageId(electionEventId)
				.withMessageType("http://www.post.ch")
				.withSendingApplication(createSendingApplication())
				.withMessageDate(currentXMLGregorianCalendar())
				.withAction("1")
				.withTestDeliveryFlag(false);
	}

	private static EventRawDataDelivery createRawDataDelivery(final String electionEventId, final Configuration configuration,
			final Results decrypt) {

		final EventRawDataDelivery eventRawDataDelivery = new EventRawDataDelivery();

		eventRawDataDelivery.setReportingBody(createReportingBody());
		eventRawDataDelivery.setRawData(createRawData(electionEventId, configuration, decrypt));

		return eventRawDataDelivery;
	}

	private static SendingApplicationType createSendingApplication() {
		return new SendingApplicationType()
				.withManufacturer("SwissPost")
				.withProduct("E-Voting")
				.withProductVersion("1");
	}

	private static ReportingBodyType createReportingBody() {
		final ReportingBodyType reportingBody = new ReportingBodyType();

		reportingBody.setReportingBodyIdentification("SwissPost");
		reportingBody.setCreationDateTime(currentXMLGregorianCalendar());

		return reportingBody;
	}

	private static RawDataType createRawData(final String electionEventId, final Configuration configuration, final Results decrypt) {
		final RawDataType rawDataType = new RawDataType();

		rawDataType.setContestIdentification(decrypt.getContestIdentification());
		rawDataType.setCountingCircleRawData(createCountingCircleRawData(electionEventId, configuration, decrypt));

		return rawDataType;
	}

	private static List<RawDataType.CountingCircleRawData> createCountingCircleRawData(final String electionEventId,
			final Configuration configuration, final Results decrypt) {

		final Map<String, List<VoteType>> mappingDecryptVoteToEch0222 = mappingDecryptVoteToEch0222(decrypt);
		final Map<String, List<ElectionType>> mappingDecryptElectionToEch0222 = mappingDecryptElectionToEch0222(decrypt);

		final List<String> allCountingCircles = new ArrayList<>(mappingDecryptVoteToEch0222.keySet());
		allCountingCircles.addAll(mappingDecryptElectionToEch0222.keySet());

		return allCountingCircles.stream()
				.distinct()
				.map(ccid -> {
					final RawDataType.CountingCircleRawData countingCircleRawData = new RawDataType.CountingCircleRawData();
					countingCircleRawData.setCountingCircleId(ccid);

					if (mappingDecryptVoteToEch0222.get(ccid) != null) {
						countingCircleRawData.setVoteRawData(createVoteRawData(configuration, mappingDecryptVoteToEch0222.get(ccid), ccid));
					}

					if (mappingDecryptElectionToEch0222.get(ccid) != null) {
						countingCircleRawData.setElectionGroupBallotRawData(
								createElectionGroupBallotRawData(electionEventId, mappingDecryptElectionToEch0222.get(ccid), configuration));
					}

					return countingCircleRawData;
				})
				.toList();
	}

	private static List<VoteRawDataType> createVoteRawData(final Configuration configuration, final List<VoteType> votes,
			final String countingCircleId) {

		return votes.stream()
				.map(vote -> {
					final Optional<ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType> voteConfig = configuration.getContest()
							.getVoteInformation().stream().parallel()
							.map(VoteInformationType::getVote)
							.filter(vt -> vt.getVoteIdentification().equals(vote.getVoteIdentification()))
							.findFirst();

					if (voteConfig.isEmpty()) {
						throw new IllegalStateException(String.format("voteConfig not found. [countingCircleId: %s]", countingCircleId));
					} else {
						final VoteRawDataType voteRawDataType = new VoteRawDataType();
						voteRawDataType.setVoteIdentification(vote.getVoteIdentification());
						voteRawDataType.setBallotRawData(createVoteBallotRawData(vote, voteConfig.get()));
						return voteRawDataType;
					}
				})
				.filter(voteRawDataType -> !voteRawDataType.getBallotRawData().isEmpty())
				.toList();
	}

	private static List<RawDataType.CountingCircleRawData.ElectionGroupBallotRawData> createElectionGroupBallotRawData(final String electionEventId,
			final List<ElectionType> elections, final Configuration configuration) {

		return elections.stream()
				.flatMap(election -> election.getBallot().stream()
						.map(ballotElection -> createElectionGroupBallot(electionEventId, election.getElectionIdentification(),
								ballotElection, configuration)))
				.map(electionGroupBallotRawData -> new AbstractMap.SimpleEntry<>(electionGroupBallotRawData.getElectionGroupIdentification(),
						electionGroupBallotRawData))
				.collect(Collectors.toMap(
						AbstractMap.SimpleEntry::getKey,
						AbstractMap.SimpleEntry::getValue,
						(g1, g2) -> {
							g1.getElectionRawData().addAll(g2.getElectionRawData());
							return g1;
						}))
				.values().stream()
				.filter(electionGroupBallotRawData -> !electionGroupBallotRawData.getElectionRawData().isEmpty())
				.toList();
	}

	private static RawDataType.CountingCircleRawData.ElectionGroupBallotRawData createElectionGroupBallot(final String electionGroupIdentification,
			final String electionIdentification, final BallotElectionType ballotElectionType, final Configuration configuration) {

		final RawDataType.CountingCircleRawData.ElectionGroupBallotRawData electionGroupBallotRawData = new RawDataType.CountingCircleRawData.ElectionGroupBallotRawData();
		electionGroupBallotRawData.setElectionGroupIdentification(electionGroupIdentification);

		final ArrayList<ElectionRawDataType> elections = new ArrayList<>();
		final ElectionRawDataType election = createElectionRawData(configuration, electionIdentification, ballotElectionType);

		if (!election.getBallotRawData().isEmpty()) {
			elections.add(election);
		}

		electionGroupBallotRawData.setElectionRawData(elections);

		return electionGroupBallotRawData;
	}

	private static ElectionRawDataType createElectionRawData(final Configuration configuration, final String electionIdentification,
			final BallotElectionType ballotElectionType) {

		final ElectionRawDataType election = new ElectionRawDataType();

		election.setElectionIdentification(electionIdentification);
		election.setBallotRawData(createElectionBallotRawData(ballotElectionType, configuration));

		return election;
	}

	private static List<VoteRawDataType.BallotRawData> createVoteBallotRawData(final VoteType voteType,
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType voteConfig) {

		return voteType.getBallot().stream()
				.map(ballotVote -> {
					final VoteRawDataType.BallotRawData ballotRawData = new VoteRawDataType.BallotRawData();

					final VoteRawDataType.BallotRawData.BallotCasted casted = new VoteRawDataType.BallotRawData.BallotCasted();

					casted.setQuestionRawData(ballotVote.getChosenAnswerIdentification().stream()
							.map(answerId -> {
								final String questionId = findQuestionIdentification(answerId, voteConfig);
								final BigInteger answerType = findAnswerType(answerId, voteConfig);

								if (StringUtils.isEmpty(ballotRawData.getBallotIdentification())) {
									final String ballotIdentification = findBallotIdentificationFromQuestionIdentification(questionId, voteConfig);
									ballotRawData.setBallotIdentification(ballotIdentification);
								}

								final VoteRawDataType.BallotRawData.BallotCasted.QuestionRawData questionRawData = new VoteRawDataType.BallotRawData.BallotCasted.QuestionRawData();
								questionRawData.setQuestionIdentification(questionId);

								final VoteRawDataType.BallotRawData.BallotCasted.QuestionRawData.Casted castedAnswer = new VoteRawDataType.BallotRawData.BallotCasted.QuestionRawData.Casted();
								castedAnswer.setCastedVote(answerType);
								castedAnswer.setAnswerOptionIdentification(createAnswerOptionIdentification(answerId, answerType, voteConfig));
								questionRawData.setCasted(castedAnswer);

								return questionRawData;
							})
							.toList());
					ballotRawData.setBallotCasted(casted);

					return ballotRawData;
				})
				.toList();
	}

	private static AnswerOptionIdentificationType createAnswerOptionIdentification(final String answerId, final BigInteger answerType,
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType voteConfig) {

		final AnswerOptionIdentificationType answerOptionIdentificationType = new AnswerOptionIdentificationType();

		answerOptionIdentificationType.setAnswerIdentification(answerId);
		answerOptionIdentificationType.setAnswerSequenceNumber(answerType);
		answerOptionIdentificationType.setAnswerTextInformation(createAnswerTextInformation(answerId, voteConfig));

		return answerOptionIdentificationType;
	}

	private static List<AnswerOptionIdentificationType.AnswerTextInformation> createAnswerTextInformation(final String answerId,
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType voteConfig) {

		final Optional<StandardAnswerType> answer = voteConfig.getBallot().stream().parallel()
				.map(BallotType::getStandardBallot)
				.filter(Objects::nonNull)
				.map(StandardBallotType::getAnswer)
				.flatMap(Collection::stream)
				.filter(standardAnswer -> standardAnswer.getAnswerIdentification().equals(answerId))
				.findFirst();

		if (answer.isPresent()) {
			return answer.get().getAnswerInfo().stream()
					.map(answerInformation -> {
						final AnswerOptionIdentificationType.AnswerTextInformation answerTextInformation = new AnswerOptionIdentificationType.AnswerTextInformation();
						answerTextInformation.setLanguage(answerInformation.getLanguage().value());
						answerTextInformation.setAnswerText(answerInformation.getAnswer());
						return answerTextInformation;
					})
					.toList();
		} else {
			final Optional<TiebreakAnswerType> tieAnswer = voteConfig.getBallot().stream().parallel()
					.map(BallotType::getVariantBallot)
					.filter(Objects::nonNull)
					.map(VariantBallotType::getTieBreakQuestion)
					.flatMap(Collection::stream)
					.map(TieBreakQuestionType::getAnswer)
					.flatMap(Collection::stream)
					.filter(a -> a.getAnswerIdentification().equals(answerId))
					.findFirst();

			if (tieAnswer.isPresent()) {
				return tieAnswer.get().getAnswerInfo().stream()
						.map(answerInformation -> {
							final AnswerOptionIdentificationType.AnswerTextInformation answerTextInformation = new AnswerOptionIdentificationType.AnswerTextInformation();
							answerTextInformation.setLanguage(answerInformation.getLanguage().value());
							answerTextInformation.setAnswerText(answerInformation.getAnswer());
							return answerTextInformation;
						})
						.toList();
			} else {
				throw new IllegalStateException(String.format("Unable to find answer. [answerId: %s]", answerId));
			}
		}
	}

	private static List<ElectionRawDataType.BallotRawData> createElectionBallotRawData(final BallotElectionType ballot,
			final Configuration configuration) {

		final List<ElectionRawDataType.BallotRawData> ballotRawDatas = new ArrayList<>();
		final ElectionRawDataType.BallotRawData result = new ElectionRawDataType.BallotRawData();

		result.setBallotPosition(createBallotPosition(configuration, ballot));
		result.setListRawData(createListRawData(configuration, ballot));

		ballotRawDatas.add(result);

		return ballotRawDatas;
	}

	private static List<ElectionRawDataType.BallotRawData.BallotPosition> createBallotPosition(final Configuration configuration,
			final BallotElectionType ballotElectionType) {

		final List<ElectionRawDataType.BallotRawData.BallotPosition> ballotPositions = new ArrayList<>();

		ballotElectionType.getChosenCandidateIdentification().forEach(chosenCandidateIdentification -> {
			final ElectionRawDataType.BallotRawData.BallotPosition ballotPosition = new ElectionRawDataType.BallotRawData.BallotPosition();

			final Optional<CandidateType> candidate = configuration.getContest().getElectionInformation().stream().parallel()
					.map(ElectionInformationType::getCandidate)
					.flatMap(Collection::stream)
					.filter(ca -> ca.getCandidateIdentification().equals(chosenCandidateIdentification))
					.findFirst();

			if (candidate.isEmpty()) {
				throw new IllegalStateException(
						String.format("Candidate not found. [chosenCandidateIdentification: %s", chosenCandidateIdentification));
			} else {
				ballotPosition.setCandidate(createCandidate(candidate.get()));
				ballotPositions.add(ballotPosition);
			}
		});

		ballotElectionType.getChosenCandidateListIdentification().forEach(chosenCandidateListIdentification -> {
			final ElectionRawDataType.BallotRawData.BallotPosition ballotPosition = new ElectionRawDataType.BallotRawData.BallotPosition();

			final Optional<CandidatePositionType> candidatePosition = configuration.getContest().getElectionInformation().stream().parallel()
					.map(ElectionInformationType::getList)
					.flatMap(Collection::stream)
					.map(ListType::getCandidatePosition)
					.flatMap(Collection::stream)
					.filter(ca -> ca.getCandidateListIdentification().equals(chosenCandidateListIdentification))
					.findFirst();

			if (candidatePosition.isEmpty()) {
				throw new IllegalStateException(String.format("No CandidateListIdentification found. [chosenCandidateListIdentification: %s]",
						chosenCandidateListIdentification));
			} else if (candidatePosition.get().getCandidateIdentification() == null) {
				ballotPosition.setIsEmpty(true);
				ballotPositions.add(ballotPosition);
			} else {
				ballotPosition.setCandidate(createCandidate(candidatePosition.get()));
				ballotPositions.add(ballotPosition);
			}
		});

		ballotElectionType.getChosenWriteInsCandidateValue().forEach(chosenWriteInsCandidateValue -> {
			final ElectionRawDataType.BallotRawData.BallotPosition ballotPosition = new ElectionRawDataType.BallotRawData.BallotPosition();
			ballotPosition.setCandidate(createCandidate(chosenWriteInsCandidateValue));
			ballotPositions.add(ballotPosition);
		});

		return ballotPositions;
	}

	private static ElectionRawDataType.BallotRawData.BallotPosition.Candidate createCandidate(final CandidatePositionType candidatePosition) {
		final ElectionRawDataType.BallotRawData.BallotPosition.Candidate candidate = new ElectionRawDataType.BallotRawData.BallotPosition.Candidate();
		candidate.setCandidateIdentification(candidatePosition.getCandidateIdentification());
		candidate.setCandidateReferenceOnPosition(candidatePosition.getCandidateReferenceOnPosition());
		return candidate;
	}

	private static ElectionRawDataType.BallotRawData.BallotPosition.Candidate createCandidate(final CandidateType configCandidate) {
		final ElectionRawDataType.BallotRawData.BallotPosition.Candidate candidate = new ElectionRawDataType.BallotRawData.BallotPosition.Candidate();
		candidate.setCandidateIdentification(configCandidate.getCandidateIdentification());
		candidate.setCandidateReferenceOnPosition(configCandidate.getReferenceOnPosition());
		return candidate;
	}

	private static ElectionRawDataType.BallotRawData.BallotPosition.Candidate createCandidate(final String writeIn) {
		final ElectionRawDataType.BallotRawData.BallotPosition.Candidate candidate = new ElectionRawDataType.BallotRawData.BallotPosition.Candidate();
		candidate.setWriteIn(writeIn);
		return candidate;
	}

	private static ElectionRawDataType.BallotRawData.ListRawData createListRawData(final Configuration configuration,
			final BallotElectionType ballotElectionType) {

		if (ballotElectionType.getChosenListIdentification() != null) {
			final Optional<ListType> list = configuration.getContest().getElectionInformation().stream().parallel()
					.map(ElectionInformationType::getList)
					.flatMap(Collection::stream)
					.filter(li -> li.getListIdentification().equals(ballotElectionType.getChosenListIdentification()))
					.findFirst();

			if (list.isPresent()) {
				final ElectionRawDataType.BallotRawData.ListRawData listRawData = new ElectionRawDataType.BallotRawData.ListRawData();
				listRawData.setListIdentification(list.get().getListIdentification());
				return listRawData;
			} else {
				throw new IllegalStateException(String.format("No list present in configuration. [chosenListIdentification: %s]",
						ballotElectionType.getChosenListIdentification()));
			}
		}
		return null;
	}

	private static Map<String, List<VoteType>> mappingDecryptVoteToEch0222(final Results decrypt) {
		return decrypt.getBallotsBox().stream()
				.flatMap(ballotBoxType -> ballotBoxType.getCountingCircle().stream())
				.map(countingCircleType -> new AbstractMap.SimpleEntry<>(
						countingCircleType.getCountingCircleIdentification(),
						countingCircleType.getDomainOfInfluence().stream()
								.flatMap(domainOfInfluenceType -> domainOfInfluenceType.getVote().stream())
								.toList()))
				.collect(Collectors.toMap(
						AbstractMap.SimpleEntry::getKey,
						AbstractMap.SimpleEntry::getValue,
						(List<VoteType> l1, List<VoteType> l2) -> {
							final List<VoteType> merged = new ArrayList<>(l1);
							merged.addAll(l2);
							return new ArrayList<>(
									merged.stream()
											.map(voteType -> new AbstractMap.SimpleEntry<>(voteType.getVoteIdentification(), voteType))
											.collect(Collectors.toMap(
													AbstractMap.SimpleEntry::getKey,
													AbstractMap.SimpleEntry::getValue,
													(VoteType v1, VoteType v2) -> {
														v1.getBallot().addAll(v2.getBallot());
														return v1;
													}))
											.values());
						}));
	}

	private static Map<String, List<ElectionType>> mappingDecryptElectionToEch0222(final Results decrypt) {
		return decrypt.getBallotsBox().stream()
				.flatMap(ballotBoxType -> ballotBoxType.getCountingCircle().stream())
				.map(countingCircleType -> new AbstractMap.SimpleEntry<>(
						countingCircleType.getCountingCircleIdentification(),
						countingCircleType.getDomainOfInfluence().stream()
								.flatMap(domainOfInfluenceType -> domainOfInfluenceType.getElection().stream())
								.toList()))
				.collect(Collectors.toMap(
						AbstractMap.SimpleEntry::getKey,
						AbstractMap.SimpleEntry::getValue,
						(List<ElectionType> l1, List<ElectionType> l2) -> {
							final List<ElectionType> merged = new ArrayList<>(l1);
							merged.addAll(l2);
							return merged;
						}));
	}

	private static BigInteger findAnswerType(final String answerId,
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType voteConfig) {
		final Optional<StandardAnswerType> answer = voteConfig.getBallot().stream().parallel()
				.map(BallotType::getStandardBallot)
				.filter(Objects::nonNull)
				.map(StandardBallotType::getAnswer)
				.flatMap(Collection::stream)
				.filter(standardAnswer -> standardAnswer.getAnswerIdentification().equals(answerId))
				.findFirst();

		if (answer.isPresent()) {
			return answer.get().getAnswerPosition();
		} else {
			final Optional<TiebreakAnswerType> answerTiebreak = voteConfig.getBallot().stream().parallel()
					.map(BallotType::getVariantBallot)
					.filter(Objects::nonNull)
					.map(VariantBallotType::getTieBreakQuestion)
					.flatMap(Collection::stream)
					.map(TieBreakQuestionType::getAnswer)
					.flatMap(Collection::stream)
					.filter(a -> a.getAnswerIdentification().equals(answerId))
					.findFirst();

			if (answerTiebreak.isPresent()) {
				return answerTiebreak.get().getAnswerPosition();
			} else {
				throw new IllegalStateException(String.format("Error mapping answerType. [answerId: %s]", answerId));
			}
		}
	}

	private static String findQuestionIdentification(final String answerId,
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType voteConfig) {
		final Optional<StandardBallotType> question = voteConfig.getBallot().stream().parallel()
				.map(BallotType::getStandardBallot)
				.filter(Objects::nonNull)
				.filter(standardBallot -> standardBallot.getAnswer().stream()
						.anyMatch(standardAnswer -> standardAnswer.getAnswerIdentification().equals(answerId)))
				.findFirst();

		if (question.isPresent()) {
			return question.get().getQuestionIdentification();
		} else {
			final Optional<StandardQuestionType> standQuestion = voteConfig.getBallot().stream().parallel()
					.map(BallotType::getVariantBallot)
					.filter(Objects::nonNull)
					.map(VariantBallotType::getStandardQuestion)
					.flatMap(Collection::stream)
					.filter(standardQuestion -> standardQuestion.getAnswer().stream()
							.anyMatch(standardAnswer -> standardAnswer.getAnswerIdentification().equals(answerId)))
					.findFirst();

			if (standQuestion.isPresent()) {
				return standQuestion.get().getQuestionIdentification();
			} else {
				final Optional<TieBreakQuestionType> tieQuestion = voteConfig.getBallot().stream().parallel()
						.map(BallotType::getVariantBallot)
						.filter(Objects::nonNull)
						.map(VariantBallotType::getTieBreakQuestion)
						.flatMap(Collection::stream)
						.filter(tieBreakQuestion -> tieBreakQuestion.getAnswer()
								.stream().anyMatch(tiebreakAnswer -> tiebreakAnswer.getAnswerIdentification().equals(answerId)))
						.findFirst();

				if (tieQuestion.isPresent()) {
					return tieQuestion.get().getQuestionIdentification();
				} else {
					throw new IllegalStateException(String.format("QuestionIdentification not found. [answerId: %s]", answerId));
				}
			}
		}
	}

	private static String findBallotIdentificationFromQuestionIdentification(final String questionId,
			final ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType voteConfig) {
		return voteConfig.getBallot().stream()
				.parallel()
				.filter(ballotType -> {
					if (ballotType.getStandardBallot() != null) {
						return questionId.equals(ballotType.getStandardBallot().getQuestionIdentification());
					} else {
						return Stream.concat(
										ballotType.getVariantBallot().getStandardQuestion().stream().map(StandardQuestionType::getQuestionIdentification),
										ballotType.getVariantBallot().getTieBreakQuestion().stream().map(TieBreakQuestionType::getQuestionIdentification))
								.anyMatch(questionId::equals);
					}
				}).findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("BallotIdentification not found. [questionId: %s]", questionId)))
				.getBallotIdentification();
	}

	private static XMLGregorianCalendar currentXMLGregorianCalendar() {
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDateTime.now().toString());
		} catch (final DatatypeConfigurationException e) {
			throw new IllegalStateException("Could not instantiate message date.", e);
		}
	}

}