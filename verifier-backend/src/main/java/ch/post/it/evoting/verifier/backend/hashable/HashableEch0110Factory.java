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
package ch.post.it.evoting.verifier.backend.hashable;

import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromDate;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullable;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableBigInteger;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableBoolean;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableString;
import static com.google.common.base.Preconditions.checkArgument;

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
import ch.ech.xmlns.ech_0110._4.VotingCardsResultDetailType;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

public interface HashableEch0110Factory {
	static Hashable fromDelivery(final Delivery delivery) {
		return HashableList.of(
				HashableEch0058Factory.fromHeader(delivery.getDeliveryHeader()),
				fromEventResultDelivery(delivery.getResultDelivery()),
				fromNullableBigInteger(delivery.getMinorVersion(), "minorVersion")
		);
	}

	private static Hashable fromEventResultDelivery(final EventResultDelivery resultDelivery) {
		checkArgument(resultDelivery.getRawData() == null, "Hashing of raw data is not supported");

		return HashableList.of(
				fromReportingBody(resultDelivery.getReportingBody()),
				HashableEch0155Factory.fromContestInformation(resultDelivery.getContestInformation()),
				resultDelivery.getCountingCircleResults().stream().map(HashableEch0110Factory::fromCountingCircleResults)
						.collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromReportingBody(final ReportingBodyType reportingBody) {
		return HashableList.of(
				HashableString.from(reportingBody.getReportingBodyIdentification()),
				fromNullable(reportingBody.getDomainOfInfluence(), "domainOfInfluence", HashableEch0155Factory::fromDomainOfInfluence),
				fromDate(reportingBody.getCreationDateTime())
		);
	}

	private static Hashable fromCountingCircleResults(final CountingCircleResultsType countingCircleResults) {
		return HashableList.of(
				HashableEch0155Factory.fromCountingCircle(countingCircleResults.getCountingCircle()),
				fromVotingCardsInformation(countingCircleResults.getVotingCardsInformation()),
				countingCircleResults.getVoteResults().stream().map(HashableEch0110Factory::fromVoteResults).collect(HashableList.toHashableList()),
				countingCircleResults.getElectionGroupResults().stream().map(HashableEch0110Factory::fromElectionGroupResults)
						.collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromVotingCardsInformation(final VotingCardsInformationType votingCardsInformation) {
		return HashableList.of(
				votingCardsInformation.getReceivedValidVotingCards().stream().map(HashableEch0155Factory::fromVotingCard)
						.collect(HashableList.toHashableList()),
				votingCardsInformation.getReceivedInvalidVotingCards().stream().map(HashableEch0155Factory::fromVotingCard)
						.collect(HashableList.toHashableList()),
				HashableBigInteger.from(votingCardsInformation.getCountOfReceivedValidVotingCardsTotal()),
				HashableBigInteger.from(votingCardsInformation.getCountOfReceivedInvalidVotingCardsTotal()),
				votingCardsInformation.getSubTotalInfo().stream().map(HashableEch0110Factory::fromVotingCardResultsDetail)
						.collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromVotingCardResultsDetail(final VotingCardsResultDetailType votingCardsResultDetail) {
		return HashableList.of(
				HashableBigInteger.from(votingCardsResultDetail.getCountOfReceivedVotingCards()),
				fromNullableBigInteger(votingCardsResultDetail.getVoterType(), "voterType"),
				fromNullableBoolean(votingCardsResultDetail.isAllowsEvoting(), "allowsEvoting"),
				fromNullableBoolean(votingCardsResultDetail.isValid(), "valid"),
				fromNullableBigInteger(votingCardsResultDetail.getChannel(), "channel")
		);
	}

	private static Hashable fromVoteResults(final VoteResultType voteResult) {
		return HashableList.of(
				HashableEch0155Factory.fromVote(voteResult.getVote()),
				fromCountOfVotersInformation(voteResult.getCountOfVotersInformation()),
				voteResult.getBallotResult().stream().map(HashableEch0110Factory::fromBallotResult).collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromCountOfVotersInformation(final CountOfVotersInformationType countOfVotersInformation) {
		return HashableList.of(
				HashableBigInteger.from(countOfVotersInformation.getCountOfVotersTotal()),
				countOfVotersInformation.getSubtotalInfo().stream().map(HashableEch0110Factory::fromCountOfVotersInformationSubtotalInfo)
						.collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromCountOfVotersInformationSubtotalInfo(final CountOfVotersInformationType.SubtotalInfo subtotalInfo) {
		return HashableList.of(
				HashableBigInteger.from(subtotalInfo.getCountOfVoters()),
				fromNullableBigInteger(subtotalInfo.getVoterType(), "voterType"),
				fromNullableString(subtotalInfo.getSex(), "sex"),
				fromNullableBoolean(subtotalInfo.isAllowsEvoting(), "allowsEvoting")
		);
	}

	private static Hashable fromBallotResult(final BallotResultType ballotResult) {
		final boolean isStandard = ballotResult.getStandardBallot() != null;
		return HashableList.of(
				HashableString.from(ballotResult.getBallotIdentification()),
				fromNullableString(ballotResult.getBallotGroup(), "ballotGroup"),
				HashableBigInteger.from(ballotResult.getBallotPosition()),
				fromNullable(ballotResult.getBallotDescription(), "ballotDescription", HashableEch0155Factory::fromBallotDescription),
				fromResultDetail(ballotResult.getCountOfReceivedBallotsTotal()),
				fromResultDetail(ballotResult.getCountOfAccountedBallotsTotal()),
				fromResultDetail(ballotResult.getCountOfUnaccountedBallotsTotal()),
				fromResultDetail(ballotResult.getCountOfUnaccountedBlankBallots()),
				fromResultDetail(ballotResult.getCountOfUnaccountedInvalidBallots()),
				isStandard ?
						fromStandardBallotResult(ballotResult.getStandardBallot()) :
						fromVariantBallotResult(ballotResult.getVariantBallot())
				// caveat: extensions are not supported
		);
	}

	private static Hashable fromResultDetail(final ResultDetailType resultDetail) {
		return HashableList.of(
				HashableBigInteger.from(resultDetail.getTotal()),
				resultDetail.getSubTotalInfo().stream().map(HashableEch0110Factory::fromResultDetailSubtotalInfo)
						.collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromResultDetailSubtotalInfo(final ResultDetailType.SubTotalInfo subTotalInfo) {
		return HashableList.of(
				HashableBigInteger.from(subTotalInfo.getSubTotal()),
				HashableBigInteger.from(subTotalInfo.getChannel())
		);
	}

	private static Hashable fromStandardBallotResult(final StandardBallotResultType standardBallot) {
		return HashableList.of(
				HashableString.from(standardBallot.getQuestionIdentification()),
				HashableEch0155Factory.fromAnswerInformation(standardBallot.getAnswerType()),
				HashableEch0155Factory.fromBallotQuestion(standardBallot.getQuestion()),
				fromResultDetail(standardBallot.getCountOfAnswerYes()),
				fromResultDetail(standardBallot.getCountOfAnswerNo()),
				fromResultDetail(standardBallot.getCountOfAnswerInvalid()),
				fromResultDetail(standardBallot.getCountOfAnswerEmpty())
		);
	}

	private static Hashable fromVariantBallotResult(final VariantBallotResultType variantBallotResult) {
		return HashableList.of(
				variantBallotResult.getQuestionInformation().stream().map(HashableEch0110Factory::fromStandardBallotResult)
						.collect(HashableList.toHashableList()),
				variantBallotResult.getTieBreak().stream().map(HashableEch0110Factory::fromTieBreak).collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromTieBreak(final VariantBallotResultType.TieBreak tieBreak) {
		return HashableList.of(
				HashableString.from(tieBreak.getQuestionIdentification()),
				HashableEch0155Factory.fromAnswerInformation(tieBreak.getAnswerType()),
				HashableEch0155Factory.fromTieBreakQuestion(tieBreak.getTieBreakQuestion()),
				fromResultDetail(tieBreak.getCountOfAnswerInvalid()),
				fromResultDetail(tieBreak.getCountOfAnswerEmpty()),
				tieBreak.getCountInFavourOf().stream().map(HashableEch0110Factory::fromCountInFavourOf).collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromCountInFavourOf(final VariantBallotResultType.TieBreak.CountInFavourOf countInFavourOf) {
		return HashableList.of(
				HashableString.from(countInFavourOf.getQuestionIdentification()),
				fromResultDetail(countInFavourOf.getCountOfValidAnswers())
		);
	}

	private static Hashable fromElectionGroupResults(final ElectionGroupResultsType electionGroupResults) {
		return HashableList.of(
				fromNullableString(electionGroupResults.getElectionGroupIdentification(), "electionGroupIdentification"),
				HashableString.from(electionGroupResults.getDomainOfInfluenceIdentification()),
				HashableEch0155Factory.fromElectionGroupDescription(electionGroupResults.getElectionGroupDescription()),
				fromNullableBigInteger(electionGroupResults.getElectionGroupPosition(), "electionGroupPosition"),
				fromCountOfVotersInformation(electionGroupResults.getCountOfVotersInformation()),
				fromResultDetail(electionGroupResults.getCountOfReceivedBallotsTotal()),
				fromResultDetail(electionGroupResults.getCountOfAccountedBallots()),
				fromResultDetail(electionGroupResults.getCountOfUnaccountedBallots()),
				fromResultDetail(electionGroupResults.getCountOfUnaccountedBlankBallots()),
				fromResultDetail(electionGroupResults.getCountOfUnaccountedInvalidBallots()),
				electionGroupResults.getElectionResults().stream().map(HashableEch0110Factory::fromElectionResult)
						.collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromElectionResult(final ElectionResultType electionResult) {
		final boolean isMajoral = electionResult.getMajoralElection() != null;
		return HashableList.of(
				HashableEch0155Factory.fromElection(electionResult.getElection()),
				isMajoral ?
						fromMajoralElection(electionResult.getMajoralElection()) :
						fromProportionalElection(electionResult.getProportionalElection()),
				electionResult.getElectedCandidate().stream().map(HashableEch0110Factory::fromElectedCandidate)
						.collect(HashableList.toHashableList())
				// caveat: extensions not supported

		);
	}

	private static Hashable fromMajoralElection(final ElectionResultType.MajoralElection majoralElection) {
		return HashableList.of(
				majoralElection.getCandidate().stream().map(HashableEch0110Factory::fromCandidateResult).collect(HashableList.toHashableList()),
				fromResultDetail(majoralElection.getCountOfIndividualVotesTotal()),
				fromResultDetail(majoralElection.getCountOfBlankVotesTotal()),
				fromResultDetail(majoralElection.getCountOfInvalidVotesTotal())
		);
	}

	private static Hashable fromCandidateResult(final CandidateResultType candidateResult) {
		final boolean isWriteIn = candidateResult.getWriteIn() != null;
		return HashableList.of(
				!isWriteIn ?
						fromCandidateInformation(candidateResult.getCandidateInformation()) :
						HashableString.from(candidateResult.getWriteIn()),
				candidateResult.getListResults().stream().map(HashableEch0110Factory::fromCandidateListResults)
						.collect(HashableList.toHashableList()),
				HashableBigInteger.from(candidateResult.getCountOfVotesTotal())
		);
	}

	private static Hashable fromCandidateInformation(final CandidateInformationType candidateInformation) {
		return HashableList.of(
				HashableString.from(candidateInformation.getCandidateIdentification()),
				HashableString.from(candidateInformation.getCandidateReference()),
				HashableString.from(candidateInformation.getFamilyName()),
				fromNullableString(candidateInformation.getFirstName(), "firstName"),
				HashableString.from(candidateInformation.getCallName()),
				HashableEch0155Factory.fromCandidateTextInformation(candidateInformation.getCandidateText()),
				HashableString.from(Boolean.toString(candidateInformation.isOfficialCandidateYesNo()))
		);
	}

	private static Hashable fromCandidateListResults(final CandidateListResultType listResults) {
		return HashableList.of(
				HashableString.from(listResults.getListIdentification()),
				fromResultDetail(listResults.getCountOfvotesFromUnchangedBallots()),
				fromResultDetail(listResults.getCountOfvotesFromChangedBallots())
		);
	}

	private static Hashable fromProportionalElection(final ElectionResultType.ProportionalElection proportionalElection) {
		return HashableList.of(
				fromResultDetail(proportionalElection.getCountOfChangedBallotsWithPartyAffiliation()),
				fromResultDetail(proportionalElection.getCountOfChangedBallotsWithoutPartyAffiliation()),
				fromResultDetail(proportionalElection.getCountOfEmptyVotesOfChangedBallotsWithoutPartyAffiliation()),
				proportionalElection.getList().stream().map(HashableEch0110Factory::fromListResults).collect(HashableList.toHashableList()),
				proportionalElection.getCandidate().stream().map(HashableEch0110Factory::fromCandidateResult).collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromListResults(final ListResultsType listResults) {
		return HashableList.of(
				fromListInformation(listResults.getListInformation()),
				fromResultDetail(listResults.getCountOfChangedBallots()),
				fromResultDetail(listResults.getCountOfUnchangedBallots()),
				fromResultDetail(listResults.getCountOfCandidateVotes()),
				fromResultDetail(listResults.getCountOfAdditionalVotes()),
				fromResultDetail(listResults.getCountOfPartyVotes())
		);
	}

	private static Hashable fromListInformation(final ListInformationType listInformation) {
		return HashableList.of(
				HashableString.from(listInformation.getListIdentification()),
				HashableString.from(listInformation.getListIndentureNumber()),
				listInformation.getListDescription().getListDescriptionInfo().stream().map(HashableEch0155Factory::fromListDescriptionInfo)
						.collect(HashableList.toHashableList())
		);
	}

	private static Hashable fromElectedCandidate(final ElectionResultType.ElectedCandidate electedCandidate) {
		return electedCandidate.getWriteIn() == null ?
				HashableEch0155Factory.fromCandidate(electedCandidate.getCandidate()) :
				HashableString.from(electedCandidate.getWriteIn());
	}

}
