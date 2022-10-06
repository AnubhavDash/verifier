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
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableDate;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableString;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.stringsToHashableList;

import java.math.BigInteger;

import javax.annotation.Nullable;

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
	static Hashable fromAnswerInformation(
			@Nullable
			final AnswerInformationType answerInformation) {
		return fromNullable(answerInformation, "answerInformation", ait ->
				HashableList.of(
						fromNullableBigInteger(ait.getAnswerType(), "answer"),
						ait.getAnswerOptionIdentification().stream()
								.map(HashableEch0155Factory::fromAnswerOptionIdentification)
								.collect(HashableList.toHashableList())
				));
	}

	static Hashable fromAnswerOptionIdentification(final AnswerOptionIdentificationType answerOptionIdentification) {
		return HashableList.of(
				HashableString.from(answerOptionIdentification.getAnswerIdentification()),
				HashableBigInteger.from(answerOptionIdentification.getAnswerSequenceNumber()),
				answerOptionIdentification.getAnswerTextInformation().stream().map(HashableEch0155Factory::fromAnswerTextInformation)
						.collect(HashableList.toHashableList())
		);
	}

	static Hashable fromAnswerTextInformation(final AnswerOptionIdentificationType.AnswerTextInformation answerTextInformation) {
		return HashableList.of(
				HashableString.from(answerTextInformation.getLanguage()),
				fromNullableString(answerTextInformation.getAnswerTextShort(), "answerTextShort"),
				HashableString.from(answerTextInformation.getAnswerText())
		);
	}

	static Hashable fromBallotDescription(final BallotDescriptionInformationType ballotDescription) {
		return ballotDescription.getBallotDescriptionInfo().stream().map(HashableEch0155Factory::fromBallotDescriptionInfo)
				.collect(HashableList.toHashableList());
	}

	static Hashable fromBallotDescriptionInfo(final BallotDescriptionInformationType.BallotDescriptionInfo ballotDescriptionInfo) {
		return HashableList.of(
				HashableString.from(ballotDescriptionInfo.getLanguage()),
				fromNullableString(ballotDescriptionInfo.getBallotDescriptionLong(), "ballotDescriptionLong"),
				fromNullableString(ballotDescriptionInfo.getBallotDescriptionShort(), "ballotDescriptionShort")
		);
	}

	static Hashable fromBallotQuestion(
			@Nullable
			final BallotQuestionType ballotQuestion) {

		return fromNullable(ballotQuestion, "ballotQuestion",
				bq -> bq.getBallotQuestionInfo().stream()
						.map(HashableEch0155Factory::fromBallotQuestionInfo)
						.collect(HashableList.toHashableList()));
	}

	static Hashable fromBallotQuestionInfo(final BallotQuestionType.BallotQuestionInfo ballotQuestionInfo) {
		return HashableList.of(
				HashableString.from(ballotQuestionInfo.getLanguage()),
				fromNullableString(ballotQuestionInfo.getBallotQuestionTitle(), "ballotQuestionTitle"),
				HashableString.from(ballotQuestionInfo.getBallotQuestion())
		);
	}

	static Hashable fromCandidateTextInfo(final CandidateTextInformationType.CandidateTextInfo candidateTextInfo) {
		return HashableList.of(
				HashableString.from(candidateTextInfo.getLanguage()),
				HashableString.from(candidateTextInfo.getCandidateText())
		);
	}

	static Hashable fromCandidate(final CandidateType candidate) {
		final boolean isSwiss = candidate.getSwiss() != null;
		return HashableList.of(
				fromNullable(candidate.getVn(), "vn", vn -> HashableBigInteger.from(BigInteger.valueOf(vn))),
				HashableString.from(candidate.getCandidateIdentification()),
				fromNullable(candidate.getBfSNumberCanton(), "bfsNumberCanton",
						bfsNumberCanton -> HashableBigInteger.from(BigInteger.valueOf(bfsNumberCanton))),
				HashableString.from(candidate.getFamilyName()),
				fromNullableString(candidate.getFirstName(), "firstName"),
				HashableString.from(candidate.getCallName()),
				candidate.getCandidateText().getCandidateTextInfo().stream().map(HashableEch0155Factory::fromCandidateTextInfo)
						.collect(HashableList.toHashableList()),
				fromDate(candidate.getDateOfBirth()),
				HashableString.from(candidate.getSex()),
				fromNullable(candidate.getOccupationalTitle(), "occupationalTitle",
						occupationalTitle -> occupationalTitle.getOccupationalTitleInfo().stream()
								.map(HashableEch0155Factory::fromOccupationalTitleInfo)
								.collect(HashableList.toHashableList())),
				HashableEch0010Factory.fromPersonMailAddress(candidate.getContactAddress()),
				fromPoliticalAddressInfo(candidate.getPoliticalAddress()),
				HashableEch0010Factory.fromAddressInformation(candidate.getDwellingAddress()),
				isSwiss ?
						fromCandidateSwiss(candidate.getSwiss()) :
						fromCandidateForeigner(candidate.getForeigner()),
				fromNullableString(candidate.getMrMrs(), "mrMrs"),
				fromNullableString(candidate.getTitle(), "title"),
				fromNullableString(candidate.getLanguageOfCorrespondence(), "languageOfCorrespondance"),
				fromNullableBoolean(candidate.isIncumbentYesNo(), "incumbentYesNo"),
				fromNullableString(candidate.getCandidateReference(), "candidateReference"),
				fromRoleInformation(candidate.getRole()),
				fromPartyAffiliation(candidate.getPartyAffiliation())
		);
	}

	static Hashable fromOccupationalTitleInfo(final OccupationalTitleInformationType.OccupationalTitleInfo occupationalTitleInfo) {
		return HashableList.of(
				HashableString.from(occupationalTitleInfo.getLanguage()),
				HashableString.from(occupationalTitleInfo.getOccupationalTitle())
		);
	}

	static Hashable fromPoliticalAddressInfo(final PoliticalAddressInfoType politicalAddressInfo) {
		return HashableList.of(
				HashableEch0010Factory.fromSwissAddressInformation(politicalAddressInfo.getPoliticalAddress()),
				HashableBigInteger.from(BigInteger.valueOf(politicalAddressInfo.getMunicipalityId()))
		);
	}

	static Hashable fromCandidateSwiss(final CandidateType.Swiss swissCandidate) {
		return swissCandidate.getOrigin().stream().collect(stringsToHashableList());
	}

	static Hashable fromCandidateForeigner(final CandidateType.Foreigner foreignCandidate) {
		return HashableList.of(
				fromNullableString(foreignCandidate.getResidencePermit(), "residencePermit"),
				HashableEch0010Factory.fromSwissAddressInformation(foreignCandidate.getDwellingAddress()),
				fromDate(foreignCandidate.getInCantonSince()),
				HashableEch0008Factory.fromCountry(foreignCandidate.getNationality())
		);
	}

	static Hashable fromRoleInformation(final RoleInformationType roleInformation) {
		return roleInformation.getRoleInfo().stream().map(HashableEch0155Factory::fromRoleInfo).collect(HashableList.toHashableList());
	}

	static Hashable fromRoleInfo(final RoleInformationType.RoleInfo roleInfo) {
		return HashableList.of(
				HashableString.from(roleInfo.getLanguage()),
				HashableString.from(roleInfo.getRole())
		);
	}

	static Hashable fromPartyAffiliation(final PartyAffiliationformationType partyAffiliationformation) {
		return partyAffiliationformation.getPartyAffiliationInfo().stream().map(HashableEch0155Factory::fromPartyAffiliationInfo)
				.collect(HashableList.toHashableList());
	}

	static Hashable fromPartyAffiliationInfo(final PartyAffiliationformationType.PartyAffiliationInfo partyAffiliationInfo) {
		return HashableList.of(
				HashableString.from(partyAffiliationInfo.getLanguage()),
				HashableString.from(partyAffiliationInfo.getPartyAffiliationShort()),
				fromNullableString(partyAffiliationInfo.getPartyAffiliationLong(), "partyAffiliationLong")
		);
	}

	static Hashable fromContestInformation(final ContestType contestInformation) {
		return HashableList.of(
				HashableString.from(contestInformation.getContestIdentification()),
				fromDate(contestInformation.getContestDate()),
				fromNullable(contestInformation.getContestDescription(), "contestDescription", HashableEch0155Factory::fromContestDescription),
				fromNullable(contestInformation.getEVotingPeriod(), "eVotingPeriod", HashableEch0155Factory::fromEvotingPeriod)
		);
	}

	static Hashable fromContestDescription(final ContestDescriptionInformationType contestDescription) {
		return contestDescription.getContestDescriptionInfo().stream()
				.map(HashableEch0155Factory::fromContestDescriptionInformation)
				.collect(HashableList.toHashableList());
	}

	static Hashable fromContestDescriptionInformation(
			final ContestDescriptionInformationType.ContestDescriptionInfo contestDescriptionInformation) {
		return HashableList.of(
				HashableString.from(contestDescriptionInformation.getLanguage()),
				HashableString.from(contestDescriptionInformation.getContestDescription())
		);
	}

	static Hashable fromEvotingPeriod(final EVotingPeriodType eVotingPeriod) {
		return HashableList.of(
				fromDate(eVotingPeriod.getEVotingPeriodFrom()),
				fromDate(eVotingPeriod.getEVotingPeriodTill())
		);
	}

	static Hashable fromCandidateTextInformation(
			@Nullable
			final CandidateTextInformationType candidateTextInformation) {
		return fromNullable(candidateTextInformation, "candidateTextInformation",
				cti -> cti.getCandidateTextInfo().stream()
						.map(HashableEch0155Factory::fromCandidateTextInfo)
						.collect(HashableList.toHashableList()));
	}

	static Hashable fromDomainOfInfluence(final DomainOfInfluenceType domainOfInfluence) {
		return HashableList.of(
				HashableString.from(domainOfInfluence.getDomainOfInfluenceType().value()),
				HashableString.from(domainOfInfluence.getLocalDomainOfInfluenceIdentification()),
				HashableString.from(domainOfInfluence.getDomainOfInfluenceName()),
				fromNullableString(domainOfInfluence.getDomainOfInfluenceShortname(), "domainOfInfluenceShortname")
		);
	}

	static Hashable fromCountingCircle(final CountingCircleType countingCircle) {
		return HashableList.of(
				fromNullableString(countingCircle.getCountingCircleId(), "countingCircleId"),
				fromNullableString(countingCircle.getCountingCircleName(), "countingCircleName")
		);
	}

	static Hashable fromVote(final VoteType vote) {
		return HashableList.of(
				HashableString.from(vote.getVoteIdentification()),
				HashableString.from(vote.getDomainOfInfluenceIdentification()),
				fromNullable(vote.getVoteDescription(), "voteDescription", HashableEch0155Factory::fromVoteDescription)
		);
	}

	static Hashable fromVoteDescription(final VoteDescriptionInformationType voteDescription) {
		return voteDescription.getVoteDescriptionInfo().stream().map(HashableEch0155Factory::fromVoteDescriptionInfo)
				.collect(HashableList.toHashableList());
	}

	static Hashable fromVoteDescriptionInfo(final VoteDescriptionInformationType.VoteDescriptionInfo voteDescriptionInfo) {
		return HashableList.of(
				HashableString.from(voteDescriptionInfo.getLanguage()),
				HashableString.from(voteDescriptionInfo.getVoteDescription())
		);
	}

	static Hashable fromElectionDescriptionInfo(final ElectionDescriptionInformationType.ElectionDescriptionInfo electionDescriptionInfo) {
		return HashableList.of(
				HashableString.from(electionDescriptionInfo.getLanguage()),
				fromNullableString(electionDescriptionInfo.getElectionDescriptionShort(), "electionDescriptionShort"),
				HashableString.from(electionDescriptionInfo.getElectionDescription())
		);
	}

	static Hashable fromElection(final ElectionType electionType) {
		return HashableList.of(
				HashableString.from(electionType.getElectionIdentification()),
				HashableBigInteger.from(electionType.getTypeOfElection()),
				fromNullableBigInteger(electionType.getElectionPosition(), "electionPosition"),
				electionType.getElectionDescription().getElectionDescriptionInfo().stream().map(HashableEch0155Factory::fromElectionDescriptionInfo)
						.collect(HashableList.toHashableList()),
				HashableBigInteger.from(electionType.getNumberOfMandates()),
				electionType.getReferencedElection().stream().map(HashableEch0155Factory::fromReferencedElectionInformation)
						.collect(HashableList.toHashableList())
		);
	}

	static Hashable fromReferencedElectionInformation(final ReferencedElectionInformationType referencedElectionInformation) {
		return HashableList.of(
				HashableString.from(referencedElectionInformation.getReferencedElection()),
				HashableBigInteger.from(referencedElectionInformation.getElectionRelation())
		);
	}

	static Hashable fromVotingCard(final VotingCardType votingCard) {
		return HashableList.of(
				fromNullableString(votingCard.getVotingCardNumber(), "votingCardNumber"),
				fromNullable(votingCard.getVotingPersonIdentification(), "votingPersonIdentification",
						HashableEch0155Factory::fromVotingPersonIdentification),
				votingCard.getDomainOfInfluence().stream().map(HashableEch0155Factory::fromDomainOfInfluence).collect(HashableList.toHashableList()),
				fromNullableBigInteger(votingCard.getVoterType(), "voterType"),
				fromNullableBigInteger(votingCard.getVotingChannel(), "votingChannel"),
				fromNullableDate(votingCard.getDateOfVoting(), "dateOfVoting"),
				fromNullableDate(votingCard.getTimeOfVoting(), "timeOfVoting"),
				fromNullableString(votingCard.getPlaceOfVoting(), "placeOfVoting"),
				fromNullableBoolean(votingCard.isElectronicVotingCardYesNo(), "electronicVotingCardYesNo")
		);
	}

	static Hashable fromVotingPersonIdentification(final VotingPersonIdentificationType votingPersonIdentification) {
		return HashableList.of(
				fromNullable(votingPersonIdentification.getVn(), "vn", vn -> HashableBigInteger.from(BigInteger.valueOf(vn))),
				HashableEch0044Factory.fromNamedPersonId(votingPersonIdentification.getLocalPersonId()),
				votingPersonIdentification.getOtherPersonId().stream()
						.map(HashableEch0044Factory::fromNamedPersonId)
						.collect(HashableList.toHashableList()),
				fromNullableString(votingPersonIdentification.getOfficialName(), "officialName"),
				fromNullableString(votingPersonIdentification.getFirstName(), "firstName"),
				fromNullableString(votingPersonIdentification.getSex(), "sex"),
				fromNullable(votingPersonIdentification.getDateOfBirth(), "dateOfBirth", HashableEch0044Factory::fromDatePartiallyKnown)
		);
	}

	static Hashable fromElectionGroupDescription(
			@Nullable
			final ElectionGroupDescriptionType electionGroupDescription) {
		return fromNullable(electionGroupDescription, "electionGroupDescription",
				egd -> egd.getElectionDescriptionInfo().stream()
						.map(HashableEch0155Factory::fromElectionDescriptionInfo)
						.collect(HashableList.toHashableList()));
	}

	static Hashable fromListDescriptionInfo(final ListDescriptionInformationType.ListDescriptionInfo listDescriptionInfo) {
		return HashableList.of(
				HashableString.from(listDescriptionInfo.getLanguage()),
				fromNullableString(listDescriptionInfo.getListDescriptionShort(), "listDescriptionShort"),
				HashableString.from(listDescriptionInfo.getListDescription())
		);
	}

	static Hashable fromTieBreakQuestion(
			@Nullable
			final TieBreakQuestionType tieBreakQuestion) {
		return fromNullable(tieBreakQuestion, "tieBreakQuestion", tbq -> tbq.getTieBreakQuestionInfo().stream()
				.map(HashableEch0155Factory::fromTieBreakQuestionInfo)
				.collect(HashableList.toHashableList()));
	}

	static Hashable fromTieBreakQuestionInfo(final TieBreakQuestionType.TieBreakQuestionInfo tieBreakQuestionInfo) {
		return HashableList.of(
				HashableString.from(tieBreakQuestionInfo.getLanguage()),
				fromNullableString(tieBreakQuestionInfo.getTieBreakQuestionTitle(), "tieBreakQuestionTitle"),
				HashableString.from(tieBreakQuestionInfo.getTieBreakQuestion()),
				fromNullableString(tieBreakQuestionInfo.getTieBreakQuestion2(), "tieBreakQuestion2")
		);
	}
}
