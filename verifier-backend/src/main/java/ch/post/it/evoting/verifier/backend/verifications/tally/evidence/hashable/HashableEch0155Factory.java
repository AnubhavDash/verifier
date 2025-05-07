/*
 * (c) Copyright 2025 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.verifications.tally.evidence.hashable;

import static ch.post.it.evoting.cryptoprimitives.hashing.HashableList.toHashableList;
import static com.google.common.base.Preconditions.checkState;

import java.math.BigInteger;

import ch.ech.xmlns.ech_0155._4.AnswerInformationType;
import ch.ech.xmlns.ech_0155._4.AnswerOptionIdentificationType;
import ch.ech.xmlns.ech_0155._4.BallotDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.BallotQuestionType;
import ch.ech.xmlns.ech_0155._4.CandidateTextInformationType;
import ch.ech.xmlns.ech_0155._4.CandidateType;
import ch.ech.xmlns.ech_0155._4.ContestDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.ContestType;
import ch.ech.xmlns.ech_0155._4.CountingCircleType;
import ch.ech.xmlns.ech_0155._4.DomainOfInfluenceType;
import ch.ech.xmlns.ech_0155._4.EVotingPeriodType;
import ch.ech.xmlns.ech_0155._4.ElectionDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.ElectionGroupDescriptionType;
import ch.ech.xmlns.ech_0155._4.ElectionType;
import ch.ech.xmlns.ech_0155._4.ListDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.OccupationalTitleInformationType;
import ch.ech.xmlns.ech_0155._4.PartyAffiliationformationType;
import ch.ech.xmlns.ech_0155._4.PoliticalAddressInfoType;
import ch.ech.xmlns.ech_0155._4.ReferencedElectionInformationType;
import ch.ech.xmlns.ech_0155._4.RoleInformationType;
import ch.ech.xmlns.ech_0155._4.TieBreakQuestionType;
import ch.ech.xmlns.ech_0155._4.VoteDescriptionInformationType;
import ch.ech.xmlns.ech_0155._4.VoteType;
import ch.ech.xmlns.ech_0155._4.VotingCardType;
import ch.ech.xmlns.ech_0155._4.VotingPersonIdentificationType;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

interface HashableEch0155Factory {

	static Hashable fromAnswerInformation(final AnswerInformationType answerInformation) {
		return HashableList.of(
				HashableUtils.fromNullableBigInteger(answerInformation.getAnswerType(), "answer"),
				HashableUtils.fromNullableCollection(answerInformation.getAnswerOptionIdentification(), "answerOptionIdentification",
						answerOptionIdentification -> answerOptionIdentification.stream().map(HashableEch0155Factory::fromAnswerOptionIdentification)
								.collect(toHashableList()))
				);
	}

	static Hashable fromAnswerOptionIdentification(final AnswerOptionIdentificationType answerOptionIdentification) {
		return HashableList.of(
				HashableString.from(answerOptionIdentification.getAnswerIdentification()),
				HashableBigInteger.from(answerOptionIdentification.getAnswerSequenceNumber()),
				answerOptionIdentification.getAnswerTextInformation().stream()
						.map(HashableEch0155Factory::fromAnswerTextInformation)
						.collect(toHashableList())
		);
	}

	private static Hashable fromAnswerTextInformation(final AnswerOptionIdentificationType.AnswerTextInformation answerTextInformation) {
		return HashableList.of(
				HashableString.from(answerTextInformation.getLanguage()),
				HashableUtils.fromNullableString(answerTextInformation.getAnswerTextShort(), "answerTextShort"),
				HashableString.from(answerTextInformation.getAnswerText())
		);
	}

	static Hashable fromBallotDescription(final BallotDescriptionInformationType ballotDescription) {
		return ballotDescription.getBallotDescriptionInfo().stream()
				.map(HashableEch0155Factory::fromBallotDescriptionInfo)
				.collect(toHashableList());
	}

	private static Hashable fromBallotDescriptionInfo(final BallotDescriptionInformationType.BallotDescriptionInfo ballotDescriptionInfo) {
		return HashableList.of(
				HashableString.from(ballotDescriptionInfo.getLanguage()),
				HashableUtils.fromNullableString(ballotDescriptionInfo.getBallotDescriptionLong(), "ballotDescriptionLong"),
				HashableUtils.fromNullableString(ballotDescriptionInfo.getBallotDescriptionShort(), "ballotDescriptionShort")
		);
	}

	static Hashable fromBallotQuestion(final BallotQuestionType ballotQuestion) {
		return ballotQuestion.getBallotQuestionInfo().stream().map(HashableEch0155Factory::fromBallotQuestionInfo).collect(toHashableList());
	}

	private static Hashable fromBallotQuestionInfo(final BallotQuestionType.BallotQuestionInfo ballotQuestionInfo) {
		return HashableList.of(
				HashableString.from(ballotQuestionInfo.getLanguage()),
				HashableUtils.fromNullableString(ballotQuestionInfo.getBallotQuestionTitle(), "ballotQuestionTitle"),
				HashableString.from(ballotQuestionInfo.getBallotQuestion())
		);
	}

	private static Hashable fromCandidateTextInfo(final CandidateTextInformationType.CandidateTextInfo candidateTextInfo) {
		return HashableList.of(
				HashableString.from(candidateTextInfo.getLanguage()),
				HashableString.from(candidateTextInfo.getCandidateText())
		);
	}

	static Hashable fromCandidate(final CandidateType candidate) {
		final boolean isSwiss = candidate.getSwiss() != null;
		final boolean isForeigner = candidate.getForeigner() != null;

		checkState(isSwiss ^ isForeigner, "Candidate must be either Swiss or Foreigner [candidateIdentification=%s]",
				candidate.getCandidateIdentification());

		return HashableList.of(
				HashableUtils.fromNullable(candidate.getVn(), "vn", vn -> HashableBigInteger.from(BigInteger.valueOf(vn))),
				HashableString.from(candidate.getCandidateIdentification()),
				HashableUtils.fromNullable(candidate.getBfSNumberCanton(),
						"bfsNumberCanton",
						bfsNumberCanton -> HashableBigInteger.from(BigInteger.valueOf(bfsNumberCanton))),
				HashableString.from(candidate.getFamilyName()),
				HashableUtils.fromNullableString(candidate.getFirstName(), "firstName"),
				HashableString.from(candidate.getCallName()),
				HashableUtils.fromNullable(candidate.getCandidateText(), "candidateText", HashableEch0155Factory::fromCandidateTextInformation),
				HashableUtils.fromDate(candidate.getDateOfBirth()),
				HashableString.from(candidate.getSex()),
				HashableUtils.fromNullable(candidate.getOccupationalTitle(),
						"occupationalTitle",
						occupationalTitle -> occupationalTitle.getOccupationalTitleInfo().stream()
								.map(HashableEch0155Factory::fromOccupationalTitleInfo)
								.collect(toHashableList())),
				HashableUtils.fromNullable(candidate.getContactAddress(), "contactAddress", HashableEch0010Factory::fromPersonMailAddress),
				HashableUtils.fromNullable(candidate.getPoliticalAddress(), "politicalAddress", HashableEch0155Factory::fromPoliticalAddressInfo),
				HashableUtils.fromNullable(candidate.getDwellingAddress(), "dwellingAddress", HashableEch0010Factory::fromAddressInformation),
				isSwiss ?
						fromCandidateSwiss(candidate.getSwiss()) :
						fromCandidateForeigner(candidate.getForeigner()),
				HashableUtils.fromNullableString(candidate.getMrMrs(), "mrMrs"),
				HashableUtils.fromNullableString(candidate.getTitle(), "title"),
				HashableUtils.fromNullableString(candidate.getLanguageOfCorrespondence(), "languageOfCorrespondance"),
				HashableUtils.fromNullableBoolean(candidate.isIncumbentYesNo(), "incumbentYesNo"),
				HashableUtils.fromNullableString(candidate.getCandidateReference(), "candidateReference"),
				HashableUtils.fromNullable(candidate.getRole(), "role", HashableEch0155Factory::fromRoleInformation),
				HashableUtils.fromNullable(candidate.getPartyAffiliation(), "partyAffiliation", HashableEch0155Factory::fromPartyAffiliation)
		);
	}

	private static Hashable fromOccupationalTitleInfo(final OccupationalTitleInformationType.OccupationalTitleInfo occupationalTitleInfo) {
		return HashableList.of(
				HashableString.from(occupationalTitleInfo.getLanguage()),
				HashableString.from(occupationalTitleInfo.getOccupationalTitle())
		);
	}

	private static Hashable fromPoliticalAddressInfo(final PoliticalAddressInfoType politicalAddressInfo) {
		return HashableList.of(
				HashableEch0010Factory.fromSwissAddressInformation(politicalAddressInfo.getPoliticalAddress()),
				HashableBigInteger.from(BigInteger.valueOf(politicalAddressInfo.getMunicipalityId()))
		);
	}

	private static Hashable fromCandidateSwiss(final CandidateType.Swiss swissCandidate) {
		return HashableUtils.fromNullableCollection(swissCandidate.getOrigin(), "origin",
				origin -> origin.stream().collect(HashableUtils.stringsToHashableList()));
	}

	private static Hashable fromCandidateForeigner(final CandidateType.Foreigner foreignCandidate) {
		return HashableList.of(
				HashableString.from(foreignCandidate.getResidencePermit()),
				HashableEch0010Factory.fromSwissAddressInformation(foreignCandidate.getDwellingAddress()),
				HashableUtils.fromDate(foreignCandidate.getInCantonSince()),
				HashableEch0008Factory.fromCountry(foreignCandidate.getNationality())
		);
	}

	private static Hashable fromRoleInformation(final RoleInformationType roleInformation) {
		return roleInformation.getRoleInfo().stream()
				.map(HashableEch0155Factory::fromRoleInfo)
				.collect(toHashableList());
	}

	private static Hashable fromRoleInfo(final RoleInformationType.RoleInfo roleInfo) {
		return HashableList.of(
				HashableString.from(roleInfo.getLanguage()),
				HashableString.from(roleInfo.getRole())
		);
	}

	private static Hashable fromPartyAffiliation(final PartyAffiliationformationType partyAffiliationInformation) {
		return partyAffiliationInformation.getPartyAffiliationInfo().stream()
				.map(HashableEch0155Factory::fromPartyAffiliationInfo)
				.collect(toHashableList());
	}

	private static Hashable fromPartyAffiliationInfo(final PartyAffiliationformationType.PartyAffiliationInfo partyAffiliationInfo) {
		return HashableList.of(
				HashableString.from(partyAffiliationInfo.getLanguage()),
				HashableString.from(partyAffiliationInfo.getPartyAffiliationShort()),
				HashableUtils.fromNullableString(partyAffiliationInfo.getPartyAffiliationLong(), "partyAffiliationLong")
		);
	}

	static Hashable fromContestInformation(final ContestType contestInformation) {
		return HashableList.of(
				HashableString.from(contestInformation.getContestIdentification()),
				HashableUtils.fromDate(contestInformation.getContestDate()),
				HashableUtils.fromNullable(contestInformation.getContestDescription(), "contestDescription", HashableEch0155Factory::fromContestDescription),
				HashableUtils.fromNullable(contestInformation.getEVotingPeriod(), "eVotingPeriod", HashableEch0155Factory::fromEvotingPeriod)
		);
	}

	private static Hashable fromContestDescription(final ContestDescriptionInformationType contestDescription) {
		return contestDescription.getContestDescriptionInfo().stream()
				.map(HashableEch0155Factory::fromContestDescriptionInformation)
				.collect(toHashableList());
	}

	private static Hashable fromContestDescriptionInformation(final ContestDescriptionInformationType.ContestDescriptionInfo contestDescriptionInformation) {
		return HashableList.of(
				HashableString.from(contestDescriptionInformation.getLanguage()),
				HashableString.from(contestDescriptionInformation.getContestDescription())
		);
	}

	private static Hashable fromEvotingPeriod(final EVotingPeriodType eVotingPeriod) {
		return HashableList.of(
				HashableUtils.fromDate(eVotingPeriod.getEVotingPeriodFrom()),
				HashableUtils.fromDate(eVotingPeriod.getEVotingPeriodTill())
		);
	}

	static Hashable fromCandidateTextInformation(final CandidateTextInformationType candidateTextInformation) {
		return candidateTextInformation.getCandidateTextInfo().stream().map(HashableEch0155Factory::fromCandidateTextInfo).collect(toHashableList());
	}

	static Hashable fromDomainOfInfluence(final DomainOfInfluenceType domainOfInfluence) {
		return HashableList.of(
				HashableString.from(domainOfInfluence.getDomainOfInfluenceType().value()),
				HashableString.from(domainOfInfluence.getLocalDomainOfInfluenceIdentification()),
				HashableString.from(domainOfInfluence.getDomainOfInfluenceName()),
				HashableUtils.fromNullableString(domainOfInfluence.getDomainOfInfluenceShortname(), "domainOfInfluenceShortname")
		);
	}

	static Hashable fromCountingCircle(final CountingCircleType countingCircle) {
		return HashableList.of(
				HashableUtils.fromNullableString(countingCircle.getCountingCircleId(), "countingCircleId"),
				HashableUtils.fromNullableString(countingCircle.getCountingCircleName(), "countingCircleName")
		);
	}

	static Hashable fromVote(final VoteType vote) {
		return HashableList.of(
				HashableString.from(vote.getVoteIdentification()),
				HashableString.from(vote.getDomainOfInfluenceIdentification()),
				HashableUtils.fromNullable(vote.getVoteDescription(), "voteDescription", HashableEch0155Factory::fromVoteDescription)
		);
	}

	private static Hashable fromVoteDescription(final VoteDescriptionInformationType voteDescription) {
		return voteDescription.getVoteDescriptionInfo().stream()
				.map(HashableEch0155Factory::fromVoteDescriptionInfo)
				.collect(toHashableList());
	}

	private static Hashable fromVoteDescriptionInfo(final VoteDescriptionInformationType.VoteDescriptionInfo voteDescriptionInfo) {
		return HashableList.of(
				HashableString.from(voteDescriptionInfo.getLanguage()),
				HashableString.from(voteDescriptionInfo.getVoteDescription())
		);
	}

	private static Hashable fromElectionDescriptionInfo(final ElectionDescriptionInformationType.ElectionDescriptionInfo electionDescriptionInfo) {
		return HashableList.of(
				HashableString.from(electionDescriptionInfo.getLanguage()),
				HashableUtils.fromNullableString(electionDescriptionInfo.getElectionDescriptionShort(), "electionDescriptionShort"),
				HashableString.from(electionDescriptionInfo.getElectionDescription())
		);
	}

	static Hashable fromElection(final ElectionType electionType) {
		return HashableList.of(
				HashableString.from(electionType.getElectionIdentification()),
				HashableBigInteger.from(electionType.getTypeOfElection()),
				HashableUtils.fromNullableBigInteger(electionType.getElectionPosition(), "electionPosition"),
				HashableUtils.fromNullable(electionType.getElectionDescription(), "electionDescription",
						HashableEch0155Factory::fromElectionDescription),
				HashableBigInteger.from(electionType.getNumberOfMandates()),
				HashableUtils.fromNullableCollection(electionType.getReferencedElection(), "referencedElection",
						referencedElection -> referencedElection.stream().map(HashableEch0155Factory::fromReferencedElectionInformation)
								.collect(toHashableList()))
		);
	}

	private static Hashable fromElectionDescription(final ElectionDescriptionInformationType electionDescriptionInformation) {
		return electionDescriptionInformation.getElectionDescriptionInfo().stream()
				.map(HashableEch0155Factory::fromElectionDescriptionInfo)
				.collect(toHashableList());
	}

	private static Hashable fromReferencedElectionInformation(final ReferencedElectionInformationType referencedElectionInformation) {
		return HashableList.of(
				HashableString.from(referencedElectionInformation.getReferencedElection()),
				HashableBigInteger.from(referencedElectionInformation.getElectionRelation())
		);
	}

	static Hashable fromVotingCard(final VotingCardType votingCard) {
		return HashableList.of(
				HashableUtils.fromNullableString(votingCard.getVotingCardNumber(), "votingCardNumber"),
				HashableUtils.fromNullable(votingCard.getVotingPersonIdentification(),
						"votingPersonIdentification",
						HashableEch0155Factory::fromVotingPersonIdentification),
				HashableUtils.fromNullableCollection(votingCard.getDomainOfInfluence(), "domainOfInfluence",
						domainOfInfluence -> domainOfInfluence.stream().map(HashableEch0155Factory::fromDomainOfInfluence).collect(toHashableList())),
				HashableUtils.fromNullableBigInteger(votingCard.getVoterType(), "voterType"),
				HashableUtils.fromNullableBigInteger(votingCard.getVotingChannel(), "votingChannel"),
				HashableUtils.fromNullableDate(votingCard.getDateOfVoting(), "dateOfVoting"),
				HashableUtils.fromNullableDate(votingCard.getTimeOfVoting(), "timeOfVoting"),
				HashableUtils.fromNullableString(votingCard.getPlaceOfVoting(), "placeOfVoting"),
				HashableUtils.fromNullableBoolean(votingCard.isElectronicVotingCardYesNo(), "electronicVotingCardYesNo")
		);
	}

	private static Hashable fromVotingPersonIdentification(final VotingPersonIdentificationType votingPersonIdentification) {
		return HashableList.of(
				HashableUtils.fromNullable(votingPersonIdentification.getVn(), "vn", vn -> HashableBigInteger.from(BigInteger.valueOf(vn))),
				HashableEch0044Factory.fromNamedPersonId(votingPersonIdentification.getLocalPersonId()),
				HashableUtils.fromNullableCollection(votingPersonIdentification.getOtherPersonId(), "otherPersonId",
						otherPersonId -> otherPersonId.stream().map(HashableEch0044Factory::fromNamedPersonId).collect(toHashableList())),
				HashableUtils.fromNullableString(votingPersonIdentification.getOfficialName(), "officialName"),
				HashableUtils.fromNullableString(votingPersonIdentification.getFirstName(), "firstName"),
				HashableUtils.fromNullableString(votingPersonIdentification.getSex(), "sex"),
				HashableUtils.fromNullable(votingPersonIdentification.getDateOfBirth(), "dateOfBirth", HashableEch0044Factory::fromDatePartiallyKnown)
		);
	}

	static Hashable fromElectionGroupDescription(final ElectionGroupDescriptionType electionGroupDescription) {
		return electionGroupDescription.getElectionDescriptionInfo().stream().map(HashableEch0155Factory::fromElectionDescriptionInfo)
				.collect(toHashableList());
	}

	static Hashable fromListDescriptionInfo(final ListDescriptionInformationType.ListDescriptionInfo listDescriptionInfo) {
		return HashableList.of(
				HashableString.from(listDescriptionInfo.getLanguage()),
				HashableUtils.fromNullableString(listDescriptionInfo.getListDescriptionShort(), "listDescriptionShort"),
				HashableString.from(listDescriptionInfo.getListDescription())
		);
	}

	static Hashable fromTieBreakQuestion(final TieBreakQuestionType tieBreakQuestion) {
		return tieBreakQuestion.getTieBreakQuestionInfo().stream().map(HashableEch0155Factory::fromTieBreakQuestionInfo).collect(toHashableList());
	}

	private static Hashable fromTieBreakQuestionInfo(final TieBreakQuestionType.TieBreakQuestionInfo tieBreakQuestionInfo) {
		return HashableList.of(
				HashableString.from(tieBreakQuestionInfo.getLanguage()),
				HashableUtils.fromNullableString(tieBreakQuestionInfo.getTieBreakQuestionTitle(), "tieBreakQuestionTitle"),
				HashableString.from(tieBreakQuestionInfo.getTieBreakQuestion()),
				HashableUtils.fromNullableString(tieBreakQuestionInfo.getTieBreakQuestion2(), "tieBreakQuestion2")
		);
	}

}