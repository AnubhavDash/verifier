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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.MoreCollectors;
import com.google.common.collect.Streams;

import ch.post.it.evoting.verifier.protocol.domain.tally.TallyComponentVotesPayload;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.BallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.CandidateType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ContestType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ElectionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.ListType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.verifier.backend.domain.xmlns.evotingconfig.VoteType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotBoxType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotElectionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotVoteType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.DomainOfInfluenceType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;

/**
 * Maps to {@link Results}.
 */
public class ResultDeliveryMapper {

	private ResultDeliveryMapper() {
		// static usage only.
	}

	/**
	 * Returns the tally component decrypt output.
	 *
	 * @param configuration                                    the configuration of the event.
	 * @param authorizationNameToTallyComponentVotesPayloadMap a map with the authorization name as key, and the related tally component votes payload
	 *                                                         as value.
	 * @return the results built with the inputs.
	 * @throws NullPointerException     if any input is null.
	 * @throws IllegalArgumentException if the input map does not contain exactly one entry per authorization.
	 */
	public static Results toResults(final Configuration configuration,
			final Map<String, TallyComponentVotesPayload> authorizationNameToTallyComponentVotesPayloadMap) {

		checkNotNull(configuration);
		checkNotNull(authorizationNameToTallyComponentVotesPayloadMap);

		final List<AuthorizationType> authorizations = configuration.getAuthorizations().getAuthorization();

		checkArgument(authorizations.size() == authorizationNameToTallyComponentVotesPayloadMap.size(),
				"There must be exactly the same number of authorizations as tally component votes payload mappings.");

		checkArgument(authorizations.stream()
						.parallel()
						.map(AuthorizationType::getAuthorizationName)
						.allMatch(authorizationNameToTallyComponentVotesPayloadMap::containsKey),
				"There must be an existing tally component votes payload mapping for each authorization.");

		final ContestType contest = configuration.getContest();
		final String contestIdentification = contest.getContestIdentification();
		final int castBallots = authorizationNameToTallyComponentVotesPayloadMap.values().stream()
				.parallel()
				.map(TallyComponentVotesPayload::getVotes)
				.mapToInt(List::size)
				.reduce(0, Math::addExact);

		final List<BallotBoxType> ballotBoxes = authorizations.stream()
				.parallel()
				.sorted(Comparator.comparing(AuthorizationType::getAuthorizationName))
				.map(authorizationType -> toBallotBoxType(authorizationType, contest,
						authorizationNameToTallyComponentVotesPayloadMap.get(authorizationType.getAuthorizationName())))
				.toList();

		final Results results = new Results();
		results.setContestIdentification(contestIdentification);
		results.getBallotsBox().addAll(ballotBoxes);
		results.setCastBallots(BigInteger.valueOf(castBallots));

		return results;
	}

	private static BallotBoxType toBallotBoxType(final AuthorizationType authorizationType, final ContestType contestType,
			final TallyComponentVotesPayload tallyComponentVotesPayload) {

		final String ballotBoxIdentification = authorizationType.getAuthorizationIdentification();

		final Map<String, List<String>> countingCircleIdToDomainOfInfluenceIdsMap = authorizationType.getAuthorizationObject().stream()
				.collect(Collectors.groupingBy(
						authorizationObjectType -> authorizationObjectType.getCountingCircle().getId(),
						Collectors.mapping(authorizationObjectType -> authorizationObjectType.getDomainOfInfluence().getId(), Collectors.toList())));

		final List<CountingCircleType> countingCircles = countingCircleIdToDomainOfInfluenceIdsMap.entrySet().stream()
				.map(entry -> {

					final String countingCircleIdentification = entry.getKey();
					final List<DomainOfInfluenceType> domainsOfInfluence = entry.getValue().stream()
							.map(domainOfInfluenceId -> {
								final DomainOfInfluenceType domainOfInfluenceType = new DomainOfInfluenceType();
								domainOfInfluenceType.setDomainOfInfluenceIdentification(domainOfInfluenceId);
								domainOfInfluenceType.getVote().addAll(getVoteTypes(domainOfInfluenceId, contestType));
								domainOfInfluenceType.getElection().addAll(getElectionTypes(domainOfInfluenceId, contestType));
								return domainOfInfluenceType;
							}).toList();

					final CountingCircleType countingCircleType = new CountingCircleType();
					countingCircleType.setCountingCircleIdentification(countingCircleIdentification);
					countingCircleType.getDomainOfInfluence().addAll(domainsOfInfluence);
					return countingCircleType;
				}).toList();

		final BallotBoxType ballotBoxType = new BallotBoxType();
		ballotBoxType.setBallotBoxIdentification(ballotBoxIdentification);
		ballotBoxType.getCountingCircle().addAll(countingCircles);

		IntStream.range(0, tallyComponentVotesPayload.getActualSelectedVotingOptions().size())
				.forEach(i -> updateBallotTypeWithSelectedVotingOptions(
						contestType,
						ballotBoxType,
						tallyComponentVotesPayload.getActualSelectedVotingOptions().get(i),
						tallyComponentVotesPayload.getDecodedWriteInVotes().get(i)));

		return ballotBoxType;
	}

	private static List<ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType> getVoteTypes(final String domainOfInfluenceId,
			final ContestType contestType) {
		return contestType.getVoteInformation().stream().parallel()
				.map(VoteInformationType::getVote)
				.filter(voteType -> voteType.getDomainOfInfluence().equals(domainOfInfluenceId))
				.map(VoteType::getVoteIdentification)
				.map(voteId -> {
					final ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType voteType = new ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType();
					voteType.setVoteIdentification(voteId);
					return voteType;
				}).toList();
	}

	private static List<ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType> getElectionTypes(final String domainOfInfluenceId,
			final ContestType contestType) {
		return contestType.getElectionInformation().stream().parallel()
				.map(ElectionInformationType::getElection)
				.filter(electionType -> electionType.getDomainOfInfluence().equals(domainOfInfluenceId))
				.map(ElectionType::getElectionIdentification)
				.map(electionId -> {
					final ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType electionType = new ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType();
					electionType.setElectionIdentification(electionId);
					return electionType;
				}).toList();
	}

	private static void updateBallotTypeWithSelectedVotingOptions(final ContestType contestType, final BallotBoxType ballotBoxType,
			final List<String> listOfActualSelectedVotingOptionsPerVoter, final List<String> listOfDecodedWriteInsPerVoter) {

		final Map<IdentificationIds, BallotVoteType> ballotVoteTypes = new HashMap<>();
		final Map<IdentificationIds, BallotElectionType> ballotElectionTypes = new HashMap<>();

		final List<String> domainOfInfluencesOfBallotBox = ballotBoxType.getCountingCircle().stream().parallel()
				.map(CountingCircleType::getDomainOfInfluence)
				.flatMap(Collection::stream)
				.map(DomainOfInfluenceType::getDomainOfInfluenceIdentification)
				.toList();

		int currentWriteInIndex = 0;
		for (final String answer : listOfActualSelectedVotingOptionsPerVoter) {
			final AnswerAdditionalInformation answerAdditionalInformation = getAnswerAdditionalInformation(contestType, domainOfInfluencesOfBallotBox,
					answer);

			switch (answerAdditionalInformation.type) {
			case VOTE_IDENTIFICATION -> {
				BallotVoteType ballotVoteType = ballotVoteTypes.get(answerAdditionalInformation.identificationIds());
				if (ballotVoteType == null) {
					ballotVoteType = new BallotVoteType();
					ballotVoteTypes.put(answerAdditionalInformation.identificationIds(), ballotVoteType);
				}
				ballotVoteType.getChosenAnswerIdentification().add(answer);
			}
			case CANDIDATE_IDENTIFICATION -> {
				BallotElectionType ballotElectionType = ballotElectionTypes.get(answerAdditionalInformation.identificationIds());
				if (ballotElectionType == null) {
					ballotElectionType = new BallotElectionType();
					ballotElectionTypes.put(answerAdditionalInformation.identificationIds(), ballotElectionType);
				}
				ballotElectionType.getChosenCandidateIdentification().add(answer);
			}
			case CANDIDATE_LIST_IDENTIFICATION -> {
				BallotElectionType ballotElectionType = ballotElectionTypes.get(answerAdditionalInformation.identificationIds());
				if (ballotElectionType == null) {
					ballotElectionType = new BallotElectionType();
					ballotElectionTypes.put(answerAdditionalInformation.identificationIds(), ballotElectionType);
				}
				ballotElectionType.getChosenCandidateListIdentification().add(answer);
			}
			case LIST_IDENTIFICATION -> {
				BallotElectionType ballotElectionType = ballotElectionTypes.get(answerAdditionalInformation.identificationIds());
				if (ballotElectionType == null) {
					ballotElectionType = new BallotElectionType();
					ballotElectionTypes.put(answerAdditionalInformation.identificationIds(), ballotElectionType);
				}
				ballotElectionType.setChosenListIdentification(answer);
			}
			case WRITE_INS_CANDIDATE_VALUE -> {
				BallotElectionType ballotElectionType = ballotElectionTypes.get(answerAdditionalInformation.identificationIds());
				if (ballotElectionType == null) {
					ballotElectionType = new BallotElectionType();
					ballotElectionTypes.put(answerAdditionalInformation.identificationIds(), ballotElectionType);
				}
				// It is assumed all dummy values across elections are at the end.
				final String decodedWriteIn = listOfDecodedWriteInsPerVoter.get(currentWriteInIndex++);
				ballotElectionType.getChosenWriteInsCandidateValue().add(decodedWriteIn.substring(decodedWriteIn.indexOf("#") + 1));
			}
			default -> throw new IllegalStateException(String.format("No matching found for answer. [answer: %s]", answer));
			}
		}

		ballotVoteTypes.forEach((ids, ballotVoteType) -> {
			final ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType vote =
					ballotBoxType.getCountingCircle().stream()
							.map(CountingCircleType::getDomainOfInfluence)
							.flatMap(Collection::stream)
							.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification()
									.equals(ids.domainOfInfluenceIdentification()))
							.map(DomainOfInfluenceType::getVote)
							.flatMap(Collection::stream)
							.filter(voteType -> voteType.getVoteIdentification().equals(ids.typeIdentification()))
							.collect(MoreCollectors.onlyElement());

			vote.getBallot().add(ballotVoteType);
		});

		ballotElectionTypes.forEach((ids, ballotElectionType) -> {
			final ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType election =
					ballotBoxType.getCountingCircle().stream()
							.map(CountingCircleType::getDomainOfInfluence)
							.flatMap(Collection::stream)
							.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification()
									.equals(ids.domainOfInfluenceIdentification()))
							.map(DomainOfInfluenceType::getElection)
							.flatMap(Collection::stream)
							.filter(electionType -> electionType.getElectionIdentification().equals(ids.typeIdentification()))
							.collect(MoreCollectors.onlyElement());

			election.getBallot().add(ballotElectionType);
		});

	}

	private static AnswerAdditionalInformation getAnswerAdditionalInformation(final ContestType contestType,
			final List<String> domainOfInfluencesOfBallotBox, final String actualSelectedVotingOption) {

		final Optional<AnswerAdditionalInformation> optionalVoteAnswerAdditionalInformation =
				contestType.getVoteInformation().stream().parallel()
						.map(VoteInformationType::getVote)
						// Keep votes if any of their answer (either standard, variant or tie-break) matches the voter selection.
						.filter(vote -> vote.getBallot().stream()
								.anyMatch(ballotType -> isVotingOptionPresentAsAnswer(actualSelectedVotingOption, ballotType)))
						.map(vote -> new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.VOTE_IDENTIFICATION,
								new IdentificationIds(vote.getDomainOfInfluence(), vote.getVoteIdentification())))
						.findAny();

		if (optionalVoteAnswerAdditionalInformation.isPresent()) {
			return optionalVoteAnswerAdditionalInformation.get();
		}

		for (final ElectionInformationType electionInformation : contestType.getElectionInformation()) {
			final String domainOfInfluence = electionInformation.getElection().getDomainOfInfluence();

			// Candidate identification.
			final Optional<CandidateType> optionalCandidateMatch = electionInformation.getCandidate().stream()
					.filter(candidateType -> actualSelectedVotingOption.equals(candidateType.getCandidateIdentification()))
					.findAny();

			if (optionalCandidateMatch.isPresent()) {
				return new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.CANDIDATE_IDENTIFICATION,
						new IdentificationIds(domainOfInfluence, electionInformation.getElection().getElectionIdentification()));
			}

			// Candidate list identification.
			final Optional<ListType> optionalCandidatePositionMatch = electionInformation.getList().stream()
					.filter(list -> list.getCandidatePosition().stream()
							.anyMatch(candidatePositionType -> candidatePositionType.getCandidateListIdentification()
									.equals(actualSelectedVotingOption)))
					.findAny();

			if (optionalCandidatePositionMatch.isPresent()) {
				return new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.CANDIDATE_LIST_IDENTIFICATION,
						new IdentificationIds(domainOfInfluence, electionInformation.getElection().getElectionIdentification()));
			}

			// List identification.
			final Optional<ListType> optionalListIdentificationMatch = electionInformation.getList().stream()
					.filter(list -> list.getListIdentification().equals(actualSelectedVotingOption))
					.findAny();

			if (optionalListIdentificationMatch.isPresent()) {
				return new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.LIST_IDENTIFICATION,
						new IdentificationIds(domainOfInfluence, electionInformation.getElection().getElectionIdentification()));
			}

			// Write ins candidate value.
			if (actualSelectedVotingOption.startsWith("WRITE_IN_") && domainOfInfluencesOfBallotBox.contains(domainOfInfluence)) {
				return new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.WRITE_INS_CANDIDATE_VALUE,
						new IdentificationIds(domainOfInfluence, electionInformation.getElection().getElectionIdentification()));
			}
		}

		return new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.UNKNOWN, new IdentificationIds(null, null));
	}

	private static boolean isVotingOptionPresentAsAnswer(final String actualSelectedVotingOption, final BallotType ballotType) {
		final StandardBallotType standardBallot = ballotType.getStandardBallot();
		final VariantBallotType variantBallot = ballotType.getVariantBallot();

		final Optional<StandardAnswerType> optionalStandardAnswerMatch = Streams.concat(
						standardBallot != null ? standardBallot.getAnswer().stream() : Stream.empty(),
						variantBallot != null ? variantBallot.getStandardQuestion().stream()
								.map(StandardQuestionType::getAnswer)
								.flatMap(Collection::stream) : Stream.empty())
				.filter(answer -> actualSelectedVotingOption.equals(answer.getAnswerIdentification()))
				.findAny();

		final Optional<TiebreakAnswerType> optionalTiebreakAnswerMatch =
				variantBallot != null ? variantBallot.getTieBreakQuestion().stream()
						.map(TieBreakQuestionType::getAnswer)
						.flatMap(Collection::stream)
						.filter(answer -> actualSelectedVotingOption.equals(answer.getAnswerIdentification()))
						.findAny() : Optional.empty();

		return optionalStandardAnswerMatch.isPresent() || optionalTiebreakAnswerMatch.isPresent();
	}

	enum AnswerAdditionalInformationEnum {
		VOTE_IDENTIFICATION,
		CANDIDATE_IDENTIFICATION,
		CANDIDATE_LIST_IDENTIFICATION,
		LIST_IDENTIFICATION,
		WRITE_INS_CANDIDATE_VALUE,
		UNKNOWN
	}

	record IdentificationIds(String domainOfInfluenceIdentification, String typeIdentification) {
	}

	record AnswerAdditionalInformation(AnswerAdditionalInformationEnum type, IdentificationIds identificationIds) {
	}

}
