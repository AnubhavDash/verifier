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
import static org.swisspush.supermachine.BeanScanner.from;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.MoreCollectors;
import com.google.common.collect.Streams;

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
public class ContestResultsMapper {

	private ContestResultsMapper() {
		// static usage only.
	}

	/**
	 * Returns the contest results tally output.
	 *
	 * @param configuration                                       the configuration of the event.
	 * @param authorizationAliasToSelectedDecodedVotingOptionsMap a map with the authorization alias as key, and the related selected decoded voting
	 *                                                            options.
	 * @return the results built with the inputs.
	 * @throws NullPointerException     if any input is null.
	 * @throws IllegalArgumentException if the input map does not contain exactly one entry per authorization.
	 */
	public static Results toResults(final Configuration configuration,
			final Map<String, List<List<String>>> authorizationAliasToSelectedDecodedVotingOptionsMap) {

		checkNotNull(configuration);
		checkNotNull(authorizationAliasToSelectedDecodedVotingOptionsMap);

		final List<AuthorizationType> authorizations = configuration.getAuthorizations().getAuthorization();

		checkArgument(authorizations.size() == authorizationAliasToSelectedDecodedVotingOptionsMap.size(),
				"There must be exactly the same number of authorizations as the number of selected decoded voting options lists.");

		checkArgument(authorizations.stream()
				.allMatch(authorizationType -> {
					final String authorizationAlias = authorizationType.getAuthorizationAlias();

					return authorizationAliasToSelectedDecodedVotingOptionsMap.get(authorizationAlias) != null;
				}), "There must be an existing list of selected decoded voting options mapping for each authorization.");

		final ContestType contest = configuration.getContest();
		final String contestIdentification = contest.getContestIdentification();
		final int castBallots = authorizationAliasToSelectedDecodedVotingOptionsMap.values().stream()
				.mapToInt(List::size)
				.sum();

		final List<BallotBoxType> ballotBoxes = authorizations.stream()
				.map(authorizationType -> toBallotBoxType(authorizationType, contest,
						authorizationAliasToSelectedDecodedVotingOptionsMap.get(authorizationType.getAuthorizationAlias())))
				.toList();

		final Results results = new Results();
		results.setContestIdentification(contestIdentification);
		results.getBallotsBox().addAll(ballotBoxes);
		results.setCastBallots(BigInteger.valueOf(castBallots));

		return results;
	}

	private static BallotBoxType toBallotBoxType(final AuthorizationType authorizationType, final ContestType contestType,
			final List<List<String>> allSelectedDecodedVotingOptions) {

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

		allSelectedDecodedVotingOptions.forEach(listOfSelectedVotingOptionsPerVoter ->
				updateBallotBoxTypeWithSelectedVotingOptions(contestType, ballotBoxType, listOfSelectedVotingOptionsPerVoter));

		return ballotBoxType;
	}

	private static List<ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType> getVoteTypes(final String domainOfInfluenceId,
			final ContestType contestType) {
		return from(contestType)
				.find(VoteType.class)
				.filter(voteType -> voteType.getDomainOfInfluence().equals(domainOfInfluenceId))
				.extract(VoteType::getVoteIdentification).stream()
				.map(voteId -> {
					final ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType voteType = new ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType();
					voteType.setVoteIdentification(voteId);
					return voteType;
				}).toList();
	}

	private static List<ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType> getElectionTypes(final String domainOfInfluenceId,
			final ContestType contestType) {
		return from(contestType)
				.find(ElectionType.class)
				.filter(electionType -> electionType.getDomainOfInfluence().equals(domainOfInfluenceId))
				.extract(ElectionType::getElectionIdentification).stream()
				.map(electionId -> {
					final ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType electionType = new ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType();
					electionType.setElectionIdentification(electionId);
					return electionType;
				}).toList();
	}

	private static void updateBallotBoxTypeWithSelectedVotingOptions(final ContestType contestType, final BallotBoxType ballotBoxType,
			final List<String> listOfSelectedVotingOptionsPerVoter) {
		final Map<IdentificationIds, BallotVoteType> ballotVoteTypes = new HashMap<>();
		final Map<IdentificationIds, BallotElectionType> ballotElectionTypes = new HashMap<>();

		for (final String answer : listOfSelectedVotingOptionsPerVoter) {

			final AnswerAdditionalInformation answerAdditionalInformation = getAnswerAdditionalInformation(contestType, answer);

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
			default -> throw new IllegalStateException(String.format("No matching found for answer. [answer: %s]", answer));
			}
		}

		ballotVoteTypes.forEach((ids, ballotVoteType) -> {
			final ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType vote =
					from(ballotBoxType)
							.find(DomainOfInfluenceType.class)
							.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification()
									.equals(ids.domainOfInfluenceIdentification()))
							.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType.class)
							.filter(voteType -> voteType.getVoteIdentification().equals(ids.typeIdentification())).stream()
							.collect(MoreCollectors.onlyElement());

			vote.getBallot().add(ballotVoteType);
		});

		ballotElectionTypes.forEach((ids, ballotElectionType) -> {
			final ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType election =
					from(ballotBoxType)
							.find(DomainOfInfluenceType.class)
							.filter(domainOfInfluenceType -> domainOfInfluenceType.getDomainOfInfluenceIdentification()
									.equals(ids.domainOfInfluenceIdentification()))
							.find(ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType.class)
							.filter(electionType -> electionType.getElectionIdentification().equals(ids.typeIdentification())).stream()
							.collect(MoreCollectors.onlyElement());

			election.getBallot().add(ballotElectionType);
		});

	}

	private static AnswerAdditionalInformation getAnswerAdditionalInformation(final ContestType contestType,
			final String actualSelectedVotingOption) {
		final Optional<AnswerAdditionalInformation> optionalVoteAnswerAdditionalInformation = from(contestType)
				.find(VoteType.class).stream()
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

			final Optional<CandidateType> optionalCandidateMatch = electionInformation.getCandidate().stream()
					.filter(candidateType -> actualSelectedVotingOption.equals(candidateType.getCandidateIdentification()))
					.findAny();

			if (optionalCandidateMatch.isPresent()) {
				return new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.CANDIDATE_IDENTIFICATION,
						new IdentificationIds(domainOfInfluence, electionInformation.getElection().getElectionIdentification()));
			}

			final Optional<ListType> optionalCandidatePositionMatch = electionInformation.getList().stream()
					.filter(list -> list.getCandidatePosition().stream()
							.anyMatch(candidatePositionType -> candidatePositionType.getCandidateListIdentification()
									.equals(actualSelectedVotingOption)))
					.findAny();

			if (optionalCandidatePositionMatch.isPresent()) {
				return new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.CANDIDATE_LIST_IDENTIFICATION,
						new IdentificationIds(domainOfInfluence, electionInformation.getElection().getElectionIdentification()));
			}

			final Optional<ListType> optionalListIdentificationMatch = electionInformation.getList().stream()
					.filter(list -> list.getListIdentification().equals(actualSelectedVotingOption))
					.findAny();

			if (optionalListIdentificationMatch.isPresent()) {
				return new AnswerAdditionalInformation(AnswerAdditionalInformationEnum.LIST_IDENTIFICATION,
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
		UNKNOWN
	}

	record IdentificationIds(String domainOfInfluenceIdentification, String typeIdentification) {
	}

	record AnswerAdditionalInformation(AnswerAdditionalInformationEnum type, IdentificationIds identificationIds) {
	}

}
