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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.AnswerInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.AuthorizationObjectType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.AuthorizationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.AuthorizationsType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.BallotDescriptionInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.BallotQuestionType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.BallotType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.CandidatePositionType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.CandidateTextInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.CandidateType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.Configuration;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ContestDescriptionInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ContestType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.CountingCircleType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.DomainOfInfluenceType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.DwellingAddressType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ElectionDescriptionInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ElectionInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ElectionType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ElectoralAuthorityMembersType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ElectoralAuthorityType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ElectronicAddressType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ExtendedAuthenticationKeyType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ExtendedAuthenticationKeysDefinitionType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ExtendedAuthenticationKeysType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.HeaderType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.IncumbentTextInfoType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.IncumbentTextType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.IncumbentType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.LanguageType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ListDescriptionInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ListType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ListUnionDescriptionType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ListUnionType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.OccupationalTitleInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.PartyAffiliationformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.PersonType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.PhysicalAddressType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.PropertyType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.ReferencedElectionInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.RegisterType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.StandardAnswerType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.StandardBallotType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.StandardQuestionType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.TieBreakQuestionType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.TiebreakAnswerType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.UiPropertiesType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.VarListTextInfoType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.VarListTextType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.VariantBallotType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.VoteDescriptionInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.VoteInformationType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.VoteType;
import ch.post.it.evoting.verifier.backend.domain.xmlns.evotingconfig.VoterType;

public interface HashableContestConfigurationFactory {
	static Hashable fromConfiguration(final Configuration configuration) {
		return HashableList.of(
				fromHeader(configuration.getHeader()),
				fromContest(configuration.getContest()),
				fromAuthorizations(configuration.getAuthorizations()),
				fromRegister(configuration.getRegister())
		);
	}

	static Hashable fromConfigurationStreamedVoters(final Configuration configuration, final Hashable registerHash) {
		return HashableList.of(
				fromHeader(configuration.getHeader()),
				fromContest(configuration.getContest()),
				fromAuthorizations(configuration.getAuthorizations()),
				registerHash
		);
	}

	private static Hashable fromHeader(final HeaderType header) {
		return HashableList.of(
				HashableUtils.fromDate(header.getFileDate()),
				HashableBigInteger.from(header.getVoterTotal()),
				HashableUtils.fromNullable(header.getPartialDelivery(), "partialDelivery", HashableContestConfigurationFactory::fromPartialDelivery));
	}

	private static Hashable fromPartialDelivery(final HeaderType.PartialDelivery partialDelivery) {
		return HashableList.of(
				HashableBigInteger.from(partialDelivery.getVoterFrom()),
				HashableBigInteger.from(partialDelivery.getVoterTo())
		);
	}

	private static Hashable fromContest(final ContestType contest) {
		return HashableList.of(
				HashableString.from(contest.getContestIdentification()),
				fromLanguage(contest.getContestDefaultLanguage()),
				HashableUtils.fromDate(contest.getContestDate()),
				fromContestDescriptions(contest.getContestDescription()),
				HashableUtils.fromDate(contest.getEvotingFromDate()),
				HashableUtils.fromDate(contest.getEvotingToDate()),
				HashableUtils.fromNullable(contest.getElectoralAuthority(), "electoralAuthority",
						HashableContestConfigurationFactory::fromElectoralAuthority),
				HashableUtils.fromNullable(contest.getExtendedAuthenticationKeys(), "electoralAuthority",
						HashableContestConfigurationFactory::fromExtendedAuthenticationKeyReferences),
				HashableUtils.fromNullableCollection(contest.getElectionInformation(), "electionInformation",
						HashableContestConfigurationFactory::fromElectionInformations),
				HashableUtils.fromNullableCollection(contest.getVoteInformation(), "voteInformation",
						HashableContestConfigurationFactory::fromVoteInformations),
				HashableUtils.fromNullable(contest.getUiProperties(), "uiProperties", HashableContestConfigurationFactory::fromUiProperties)
		);
	}

	private static Hashable fromLanguage(final LanguageType language) {
		return HashableString.from(language.value());
	}

	private static Hashable fromContestDescriptions(final ContestDescriptionInformationType contestDescriptions) {
		return contestDescriptions.getContestDescriptionInfo().stream().map(HashableContestConfigurationFactory::fromContestDescription)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromContestDescription(final ContestDescriptionInformationType.ContestDescriptionInfo contestDescription) {
		return HashableList.of(
				fromLanguage(contestDescription.getLanguage()),
				HashableString.from(contestDescription.getContestDescription())
		);
	}

	private static Hashable fromElectoralAuthority(final ElectoralAuthorityType electoralAuthority) {
		return HashableList.of(
				HashableString.from(electoralAuthority.getElectoralAuthorityDescription()),
				HashableString.from(electoralAuthority.getElectoralAuthorityName()),
				HashableString.from(electoralAuthority.getElectoralAuthorityDescription()),
				HashableBigInteger.from(electoralAuthority.getElectoralAuthorityThresholdValue()),
				fromMembers(electoralAuthority.getElectoralAuthorityMembers())
		);
	}

	private static Hashable fromMembers(final ElectoralAuthorityMembersType members) {
		return members.getElectoralAuthorityMemberName().stream().collect(HashableUtils.stringsToHashableList());
	}

	private static Hashable fromExtendedAuthenticationKeyReferences(final ExtendedAuthenticationKeysDefinitionType extendedAuthenticationKeys) {
		return extendedAuthenticationKeys.getKeyName().stream().collect(HashableUtils.stringsToHashableList());
	}

	private static Hashable fromElectionInformations(final List<ElectionInformationType> electionInformations) {
		return electionInformations.stream().map(HashableContestConfigurationFactory::fromElectionInformation).collect(HashableList.toHashableList());
	}

	private static Hashable fromElectionInformation(final ElectionInformationType electionInformation) {
		return HashableList.of(
				fromElection(electionInformation.getElection()),
				HashableUtils.fromNullableCollection(electionInformation.getCandidate(), "candidate",
						HashableContestConfigurationFactory::fromCandidates),
				HashableUtils.fromNullableCollection(electionInformation.getList(), "list", HashableContestConfigurationFactory::fromLists),
				HashableUtils.fromNullableCollection(electionInformation.getListUnion(), "listUnion",
						HashableContestConfigurationFactory::fromListUnions)
		);
	}

	private static Hashable fromElection(final ElectionType election) {
		return HashableList.of(
				HashableString.from(election.getElectionIdentification()),
				HashableString.from(election.getDomainOfInfluence()),
				HashableBigInteger.from(election.getTypeOfElection()),
				fromElectionDescriptions(election.getElectionDescription()),
				HashableBigInteger.from(election.getNumberOfMandates()),
				HashableString.from(Boolean.toString(election.isWriteInsAllowed())),
				HashableBigInteger.from(election.getCandidateAccumulation()),
				HashableBigInteger.from(election.getMinimalCandidateSelectionInList()),
				HashableUtils.fromNullableCollection(election.getReferencedElection(), "referencedElection",
						HashableContestConfigurationFactory::fromReferencedElections),
				HashableUtils.fromNullable(election.getUiProperties(), "uiProperties", HashableContestConfigurationFactory::fromUiProperties)
		);
	}

	private static Hashable fromElectionDescriptions(final ElectionDescriptionInformationType electionDescriptions) {
		return electionDescriptions.getElectionDescriptionInfo().stream().map(HashableContestConfigurationFactory::fromElectionDescription)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromElectionDescription(final ElectionDescriptionInformationType.ElectionDescriptionInfo electionDescriptionInfo) {
		final List<Hashable> elements = new ArrayList<>();
		elements.add(fromLanguage(electionDescriptionInfo.getLanguage()));
		elements.add(HashableString.from(electionDescriptionInfo.getElectionDescription()));
		elements.add(HashableUtils.fromNullableString(electionDescriptionInfo.getElectionDescriptionShort(), "electionDescriptionShort"));

		return HashableList.from(elements);
	}

	private static Hashable fromReferencedElections(final List<ReferencedElectionInformationType> referencedElections) {
		return referencedElections.stream().map(HashableContestConfigurationFactory::fromReferencedElection).collect(HashableList.toHashableList());
	}

	private static Hashable fromReferencedElection(final ReferencedElectionInformationType referencedElection) {
		return HashableList.of(
				HashableString.from(referencedElection.getReferencedElection()),
				HashableBigInteger.from(referencedElection.getElectionRelation())
		);
	}

	private static Hashable fromUiProperties(final UiPropertiesType uiProperties) {
		return uiProperties.getProperty().stream().map(HashableContestConfigurationFactory::fromUiProperty)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromUiProperty(final PropertyType property) {
		return HashableList.of(
				HashableString.from(property.getKey()),
				HashableString.from(property.getValue())
		);
	}

	private static Hashable fromCandidates(final List<CandidateType> candidates) {
		return candidates.stream().map(HashableContestConfigurationFactory::fromCandidate).collect(HashableList.toHashableList());
	}

	private static Hashable fromCandidate(final CandidateType candidate) {
		final List<Hashable> elements = new ArrayList<>();
		elements.add(HashableString.from(candidate.getCandidateIdentification()));
		elements.add(HashableUtils.fromNullableString(candidate.getMrMrs(), "mrMrs"));
		elements.add(HashableUtils.fromNullableString(candidate.getTitle(), "title"));
		elements.add(HashableUtils.fromNullableString(candidate.getFamilyName(), "familyName"));
		elements.add(HashableUtils.fromNullableString(candidate.getFirstName(), "firstName"));
		elements.add(HashableString.from(candidate.getCallName()));
		elements.add(fromCandidateTextInformation(candidate.getCandidateText()));
		elements.add(HashableUtils.fromDate(candidate.getDateOfBirth()));
		elements.add(HashableString.from(candidate.getSex()));
		elements.add(fromIncumbent(candidate.getIncumbent()));
		elements.add(HashableUtils.fromNullable(candidate.getDwellingAddress(), "dwellingAddress",
				HashableContestConfigurationFactory::fromDwellingAddress));
		elements.add(fromSwiss(candidate.getSwiss()));
		elements.add(HashableUtils.fromNullable(candidate.getOccupationalTitle(), "occupationTitle",
				HashableContestConfigurationFactory::fromOccupationalTitle));
		elements.add(HashableUtils.fromNullableBigInteger(candidate.getPosition(), "position"));
		elements.add(HashableUtils.fromNullableString(candidate.getReferenceOnPosition(), "referenceOnPosition"));
		elements.add(HashableUtils.fromNullable(candidate.getPartyAffiliation(), "partyAffiliation",
				HashableContestConfigurationFactory::fromPartyAffiliationInformation));

		return HashableList.from(elements);
	}

	private static Hashable fromCandidateTextInformation(final CandidateTextInformationType candidateTexts) {
		return candidateTexts.getCandidateTextInfo().stream().map(HashableContestConfigurationFactory::fromCandidateText)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromCandidateText(final CandidateTextInformationType.CandidateTextInfo candidateText) {
		return HashableList.of(
				fromLanguage(candidateText.getLanguage()),
				HashableString.from(candidateText.getCandidateText())
		);
	}

	private static Hashable fromIncumbent(final IncumbentType incumbent) {
		return HashableList.of(HashableString.from(Boolean.toString(incumbent.isIncumbent())),
				fromIncumbentTexts(incumbent.getIncumbentText()));
	}

	private static Hashable fromIncumbentTexts(
			@Nullable
			final IncumbentTextType incumbentTexts) {
		return HashableUtils.fromNullable(incumbentTexts, "incumbentText",
				f -> f.getIncumbentTextInfo().stream().map(HashableContestConfigurationFactory::fromIncumbentText)
						.collect(HashableList.toHashableList()));
	}

	private static Hashable fromIncumbentText(final IncumbentTextInfoType incumbentText) {
		return HashableList.of(
				fromLanguage(incumbentText.getLanguage()),
				HashableString.from(incumbentText.getIncumbentText())
		);
	}

	private static Hashable fromDwellingAddress(final DwellingAddressType dwellingAddress) {
		final List<Hashable> elements = new ArrayList<>();
		elements.add(HashableUtils.fromNullableString(dwellingAddress.getStreet(), "street"));
		elements.add(HashableUtils.fromNullableString(dwellingAddress.getHouseNumber(), "houseNumber"));
		boolean hasSwissZipCode = dwellingAddress.getSwissZipCode() != null;
		if (hasSwissZipCode) {
			elements.add(HashableBigInteger.from(BigInteger.valueOf(dwellingAddress.getSwissZipCode())));
		} else {
			elements.add(HashableUtils.fromNullableString(dwellingAddress.getForeignZipCode(), "foreignZipCode"));
		}
		elements.add(HashableString.from(dwellingAddress.getTown()));
		return HashableList.from(elements);
	}

	private static Hashable fromSwiss(final CandidateType.Swiss swiss) {
		return HashableUtils.fromNullableCollection(swiss.getOrigin(), "origin", f -> f.stream().collect(HashableUtils.stringsToHashableList()));
	}

	private static Hashable fromOccupationalTitle(
			@Nullable
			final OccupationalTitleInformationType occupationalTitle) {
		return HashableUtils.fromNullable(occupationalTitle, "occupationalTitle",
				f -> f.getOccupationalTitleInfo().stream().map(HashableContestConfigurationFactory::fromOccupationalTitleInfo)
						.collect(HashableList.toHashableList()));
	}

	private static Hashable fromOccupationalTitleInfo(final OccupationalTitleInformationType.OccupationalTitleInfo occupationalTitle) {
		return HashableList.of(
				fromLanguage(occupationalTitle.getLanguage()),
				HashableString.from(occupationalTitle.getOccupationalTitle())
		);
	}

	private static Hashable fromPartyAffiliationInformation(final PartyAffiliationformationType partyAffiliationformation) {
		return partyAffiliationformation.getPartyAffiliationInfo().stream().map(HashableContestConfigurationFactory::fromPartyAffiliation)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromPartyAffiliation(final PartyAffiliationformationType.PartyAffiliationInfo partyAffiliation) {
		return HashableList.of(
				fromLanguage(partyAffiliation.getLanguage()),
				HashableString.from(partyAffiliation.getPartyAffiliationShort()),
				HashableUtils.fromNullableString(partyAffiliation.getPartyAffiliationLong(), "partyAffiliationLong")
		);
	}

	private static Hashable fromLists(final List<ListType> lists) {
		return lists.stream().map(HashableContestConfigurationFactory::fromList).collect(HashableList.toHashableList());
	}

	private static Hashable fromList(final ListType list) {
		final List<Hashable> elements = new ArrayList<>();
		elements.add(HashableString.from(list.getListIdentification()));
		elements.add(HashableString.from(list.getListIndentureNumber()));
		elements.add(fromListDescriptionInformation(list.getListDescription()));
		elements.add(HashableBigInteger.from(list.getListOrderOfPrecedence()));
		elements.add(HashableString.from(Boolean.toString(list.isListEmpty())));
		elements.add(HashableUtils.fromNullableCollection(list.getCandidatePosition(), "candidatePosition",
				HashableContestConfigurationFactory::fromCandidatePositions));
		elements.add(HashableUtils.fromNullable(list.getVarListText1(), "varListText1", HashableContestConfigurationFactory::fromVarListTexts));
		elements.add(HashableUtils.fromNullable(list.getVarListText2(), "varListText2", HashableContestConfigurationFactory::fromVarListTexts));

		return HashableList.from(elements);
	}

	private static Hashable fromListDescriptionInformation(final ListDescriptionInformationType listDescriptionInformation) {
		return listDescriptionInformation.getListDescriptionInfo().stream().map(HashableContestConfigurationFactory::fromListDescription)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromListDescription(final ListDescriptionInformationType.ListDescriptionInfo listDescription) {
		return HashableList.of(
				fromLanguage(listDescription.getLanguage()),
				HashableString.from(listDescription.getListDescription()),
				HashableString.from(listDescription.getListDescriptionShort())
		);
	}

	private static Hashable fromCandidatePositions(final List<CandidatePositionType> candidatePositions) {
		return candidatePositions.stream().map(HashableContestConfigurationFactory::fromCandidatePosition).collect(HashableList.toHashableList());
	}

	private static Hashable fromCandidatePosition(final CandidatePositionType candidatePosition) {
		final List<Hashable> elements = new ArrayList<>();
		elements.add(HashableString.from(candidatePosition.getCandidateListIdentification()));
		elements.add(HashableBigInteger.from(candidatePosition.getPositionOnList()));
		elements.add(HashableString.from(candidatePosition.getCandidateReferenceOnPosition()));
		elements.add(HashableUtils.fromNullableString(candidatePosition.getCandidateIdentification(), "candidateIdentification"));
		elements.add(fromCandidateTextInformation(candidatePosition.getCandidateTextOnPosition()));

		return HashableList.from(elements);
	}

	private static Hashable fromVarListTexts(final VarListTextType varListTexts) {
		return varListTexts.getVarListTextInfo().stream().map(HashableContestConfigurationFactory::fromVarListTextInfo)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromVarListTextInfo(final VarListTextInfoType varListText) {
		return HashableList.of(
				fromLanguage(varListText.getLanguage()),
				HashableString.from(varListText.getVarListText())
		);
	}

	private static Hashable fromListUnions(final List<ListUnionType> listUnions) {
		return listUnions.stream().map(HashableContestConfigurationFactory::fromListUnion).collect(HashableList.toHashableList());
	}

	private static Hashable fromListUnion(final ListUnionType listUnion) {
		return HashableList.of(
				HashableString.from(listUnion.getListUnionIdentification()),
				fromListUnionDescriptions(listUnion.getListUnionDescription()),
				HashableBigInteger.from(listUnion.getListUnionType()),
				listUnion.getReferencedList().stream().collect(HashableUtils.stringsToHashableList())
		);
	}

	private static Hashable fromListUnionDescriptions(final ListUnionDescriptionType listUnionDescriptions) {
		return listUnionDescriptions.getListUnionDescriptionInfo().stream().map(HashableContestConfigurationFactory::fromListUnionDescription)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromListUnionDescription(final ListUnionDescriptionType.ListUnionDescriptionInfo listUnionDescription) {
		return HashableList.of(
				fromLanguage(listUnionDescription.getLanguage()),
				HashableString.from(listUnionDescription.getListUnionDescription())
		);
	}

	private static Hashable fromVoteInformations(final List<VoteInformationType> voteInformations) {
		return voteInformations.stream()
				.map(VoteInformationType::getVote)
				.map(HashableContestConfigurationFactory::fromVote).collect(HashableList.toHashableList());
	}

	private static Hashable fromVote(final VoteType vote) {
		return HashableList.of(
				HashableString.from(vote.getVoteIdentification()),
				HashableString.from(vote.getDomainOfInfluence()),
				fromVoteDescriptionInformation(vote.getVoteDescription()),
				fromBallots(vote.getBallot())
		);
	}

	private static Hashable fromVoteDescriptionInformation(final VoteDescriptionInformationType voteDescriptionInformation) {
		return voteDescriptionInformation.getVoteDescriptionInfo().stream().map(HashableContestConfigurationFactory::fromVoteDescription)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromVoteDescription(final VoteDescriptionInformationType.VoteDescriptionInfo voteDescription) {
		return HashableList.of(
				fromLanguage(voteDescription.getLanguage()),
				HashableString.from(voteDescription.getVoteDescription())
		);
	}

	private static Hashable fromBallots(final List<BallotType> ballots) {
		return ballots.stream().map(HashableContestConfigurationFactory::fromBallot).collect(HashableList.toHashableList());
	}

	private static Hashable fromBallot(final BallotType ballot) {
		// As per the XSD, a ballot is either
		// - a standard ballot OR
		// - a variant ballot
		final boolean isStandard = ballot.getStandardBallot() != null;
		return HashableList.of(
				HashableString.from(ballot.getBallotIdentification()),
				HashableBigInteger.from(ballot.getBallotPosition()),
				HashableUtils.fromNullable(ballot.getBallotDescription(), "ballotDescription",
						HashableContestConfigurationFactory::fromBallotDescription),
				isStandard ? fromStandardBallot(ballot.getStandardBallot()) : fromVariantBallot(ballot.getVariantBallot())
		);
	}

	private static Hashable fromBallotDescription(final BallotDescriptionInformationType ballotDescriptionInformation) {
		return ballotDescriptionInformation.getBallotDescriptionInfo().stream().map(HashableContestConfigurationFactory::fromBallotDescriptionInfo)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromBallotDescriptionInfo(final BallotDescriptionInformationType.BallotDescriptionInfo ballotDescription) {
		final List<Hashable> elements = new ArrayList<>();
		elements.add(fromLanguage(ballotDescription.getLanguage()));
		elements.add(HashableUtils.fromNullableString(ballotDescription.getBallotDescriptionLong(), "ballotDescriptionLong"));
		elements.add(HashableUtils.fromNullableString(ballotDescription.getBallotDescriptionShort(), "ballotDescriptionShort"));

		return HashableList.from(elements);
	}

	private static Hashable fromStandardBallot(final StandardBallotType standardBallot) {
		return HashableList.of(
				HashableString.from(standardBallot.getQuestionIdentification()),
				HashableUtils.fromNullableBigInteger(standardBallot.getAnswerType(), "answerType"),
				fromBallotQuestions(standardBallot.getBallotQuestion()),
				fromAnswers(standardBallot.getAnswer())
		);
	}

	private static Hashable fromBallotQuestions(final BallotQuestionType ballotQuestions) {
		return ballotQuestions.getBallotQuestionInfo().stream().map(HashableContestConfigurationFactory::fromBallotQuestion)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromBallotQuestion(final BallotQuestionType.BallotQuestionInfo question) {
		final List<Hashable> elements = new ArrayList<>();
		elements.add(fromLanguage(question.getLanguage()));
		elements.add(HashableUtils.fromNullableString(question.getBallotQuestionTitle(), "ballotQuestionTitle"));
		elements.add(HashableString.from(question.getBallotQuestion()));

		return HashableList.from(elements);
	}

	private static Hashable fromAnswers(final List<StandardAnswerType> answers) {
		return answers.stream().map(HashableContestConfigurationFactory::fromAnswer).collect(HashableList.toHashableList());
	}

	private static Hashable fromAnswer(final StandardAnswerType answer) {
		final List<Hashable> elements = new ArrayList<>();
		elements.add(HashableString.from(answer.getAnswerIdentification()));
		elements.add(HashableBigInteger.from(answer.getAnswerPosition()));
		elements.add(HashableString.from(answer.getStandardAnswerType()));
		elements.add(HashableUtils.fromNullableBoolean(answer.isHiddenAnswer(), "hiddenAnswer"));
		elements.add(fromAnswerInfos(answer.getAnswerInfo()));
		return HashableList.from(elements);
	}

	private static Hashable fromAnswerInfos(final List<AnswerInformationType> answerInformations) {
		return answerInformations.stream().map(HashableContestConfigurationFactory::fromAnswerInfo).collect(HashableList.toHashableList());
	}

	private static Hashable fromAnswerInfo(final AnswerInformationType answerInformation) {
		return HashableList.of(
				fromLanguage(answerInformation.getLanguage()),
				HashableString.from(answerInformation.getAnswer())
		);
	}

	private static Hashable fromVariantBallot(final VariantBallotType variantBallot) {
		return HashableList.of(
				variantBallot.getStandardQuestion().stream().map(HashableContestConfigurationFactory::fromStandardQuestion)
						.collect(HashableList.toHashableList()),
				HashableUtils.fromNullableCollection(variantBallot.getTieBreakQuestion(), "tieBreakQuestion",
						f -> f.stream().map(HashableContestConfigurationFactory::fromTiebreakQuestion)
								.collect(HashableList.toHashableList()))
		);
	}

	private static Hashable fromStandardQuestion(final StandardQuestionType question) {
		return HashableList.of(
				HashableString.from(question.getQuestionIdentification()),
				HashableBigInteger.from(question.getQuestionPosition()),
				HashableUtils.fromNullableString(question.getQuestionNumber(), "questionNumber"),
				HashableUtils.fromNullableBigInteger(question.getAnswerType(), "answerType"),
				fromBallotQuestions(question.getBallotQuestion()),
				fromAnswers(question.getAnswer())
		);
	}

	private static Hashable fromTiebreakQuestion(final TieBreakQuestionType question) {
		return HashableList.of(
				HashableString.from(question.getQuestionIdentification()),
				HashableBigInteger.from(question.getQuestionPosition()),
				HashableUtils.fromNullableString(question.getQuestionNumber(), "questionNumber"),
				HashableUtils.fromNullableBigInteger(question.getAnswerType(), "answerType"),
				fromBallotQuestions(question.getBallotQuestion()),
				fromTiebreakAnswers(question.getAnswer())
		);
	}

	private static Hashable fromTiebreakAnswers(final List<TiebreakAnswerType> answers) {
		return answers.stream().map(HashableContestConfigurationFactory::fromTiebreakAnswer).collect(HashableList.toHashableList());
	}

	private static Hashable fromTiebreakAnswer(final TiebreakAnswerType answer) {
		return HashableList.of(
				HashableString.from(answer.getAnswerIdentification()),
				HashableBigInteger.from(answer.getAnswerPosition()),
				HashableUtils.fromNullableString(answer.getStandardQuestionReference(), "standardQuestionReference"),
				HashableUtils.fromNullableBoolean(answer.isHiddenAnswer(), "hiddenAnswer"),
				fromAnswerInfos(answer.getAnswerInfo())
		);
	}

	private static Hashable fromAuthorizations(final AuthorizationsType authorizations) {
		return authorizations.getAuthorization().stream().map(HashableContestConfigurationFactory::fromAuthorization)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromAuthorization(final AuthorizationType authorization) {
		return HashableList.of(
				HashableString.from(authorization.getAuthorizationIdentification()),
				HashableString.from(authorization.getAuthorizationName()),
				HashableString.from(authorization.getAuthorizationAlias()),
				HashableString.from(Boolean.toString(authorization.isAuthorizationTest())),
				HashableUtils.fromDate(authorization.getAuthorizationFromDate()),
				HashableUtils.fromDate(authorization.getAuthorizationToDate()),
				HashableBigInteger.from(authorization.getAuthorizationGracePeriod()),
				fromAuthorizationObjects(authorization.getAuthorizationObject())
		);
	}

	private static Hashable fromAuthorizationObjects(final List<AuthorizationObjectType> authorizationObjects) {
		return authorizationObjects.stream().map(HashableContestConfigurationFactory::fromAuthorizationObject).collect(HashableList.toHashableList());
	}

	private static Hashable fromAuthorizationObject(final AuthorizationObjectType authorizationObject) {
		return HashableList.of(
				fromDomainOfInfluence(authorizationObject.getDomainOfInfluence()),
				fromCountingCircle(authorizationObject.getCountingCircle())
		);
	}

	private static Hashable fromDomainOfInfluence(final DomainOfInfluenceType domainOfInfluence) {
		return HashableList.of(
				HashableString.from(domainOfInfluence.getId()),
				HashableString.from(domainOfInfluence.getCantonCode()),
				HashableString.from(domainOfInfluence.getType()),
				HashableString.from(domainOfInfluence.getLocalId())
		);
	}

	private static Hashable fromCountingCircle(final CountingCircleType countingCircle) {
		return HashableList.of(
				HashableString.from(countingCircle.getId()),
				HashableString.from(countingCircle.getName())
		);
	}

	private static Hashable fromRegister(final RegisterType register) {
		return register.getVoter().stream().map(HashableContestConfigurationFactory::fromVoter).collect(HashableList.toHashableList());
	}

	static Hashable fromVoter(final VoterType voter) {
		return HashableList.of(
				HashableString.from(voter.getVoterIdentification()),
				HashableString.from(voter.getAuthorization()),
				HashableUtils.fromNullable(voter.getExtendedAuthenticationKeys(), "extendedAuthenticationKeys",
						HashableContestConfigurationFactory::fromExtendedAuthenticationKeys),
				HashableString.from(voter.getSex().value()),
				HashableString.from(voter.getVoterType().value()),
				HashableUtils.fromNullable(voter.getPerson(), "person", HashableContestConfigurationFactory::fromPerson)
		);
	}

	private static Hashable fromExtendedAuthenticationKeys(final ExtendedAuthenticationKeysType extendedAuthenticationKeys) {
		return extendedAuthenticationKeys.getExtendedAuthenticationKey().stream()
				.map(HashableContestConfigurationFactory::fromExtendedAuthenticationKey)
				.collect(HashableList.toHashableList());
	}

	private static Hashable fromExtendedAuthenticationKey(final ExtendedAuthenticationKeyType extendedAuthenticationKey) {
		return HashableList.of(
				HashableString.from(extendedAuthenticationKey.getName()),
				HashableString.from(extendedAuthenticationKey.getValue())
		);
	}

	private static Hashable fromPerson(final PersonType person) {
		return HashableList.of(
				HashableString.from(person.getOfficialName()),
				HashableString.from(person.getFirstName()),
				HashableString.from(person.getSex()),
				HashableUtils.fromDate(person.getDateOfBirth()),
				fromLanguage(person.getLanguageOfCorrespondance()),
				HashableString.from(person.getResidenceCountryId()),
				fromMunicipality(person.getMunicipality()),
				HashableUtils.fromNullable(person.getPhysicalAddress(), "physicalAddress", HashableContestConfigurationFactory::fromPhysicalAddress),
				HashableUtils.fromNullableCollection(person.getElectronicAddress(), "electronicAddress",
						HashableContestConfigurationFactory::fromElectronicAddresses)
		);
	}

	private static Hashable fromMunicipality(final PersonType.Municipality municipality) {
		return HashableList.of(
				HashableBigInteger.from(BigInteger.valueOf(municipality.getMunicipalityId())),
				HashableString.from(municipality.getMunicipalityName())
		);
	}

	private static Hashable fromPhysicalAddress(final PhysicalAddressType physicalAddress) {
		return HashableList.of(
				HashableUtils.fromNullableString(physicalAddress.getMrMrs(), "mrMrs"),
				HashableUtils.fromNullableString(physicalAddress.getTitle(), "title"),
				HashableUtils.fromNullableString(physicalAddress.getFirstName(), "firstName"),
				HashableUtils.fromNullableString(physicalAddress.getLastName(), "lastName"),
				HashableUtils.fromNullableString(physicalAddress.getStreet(), "street"),
				HashableUtils.fromNullableString(physicalAddress.getHouseNumber(), "houseNumber"),
				HashableUtils.fromNullableString(physicalAddress.getDwellingNumber(), "dwellingNumber"),
				HashableUtils.fromNullableString(physicalAddress.getPostOfficeBoxText(), "postOfficeBoxText"),
				HashableUtils.fromNullableLong(physicalAddress.getPostOfficeBoxNumber(), "postOfficeBoxNumber"),
				HashableUtils.fromNullableString(physicalAddress.getZipCode(), "zipCode"),
				HashableUtils.fromNullableString(physicalAddress.getTown(), "town"),
				HashableUtils.fromNullableString(physicalAddress.getCountry(), "country"),
				HashableUtils.fromNullableCollection(physicalAddress.getBelowTitleLine(), "belowTownLine",
						f -> f.stream().collect(HashableUtils.stringsToHashableList())),
				HashableUtils.fromNullableCollection(physicalAddress.getBelowNameLine(), "belowNameLine",
						f -> f.stream().collect(HashableUtils.stringsToHashableList())),
				HashableUtils.fromNullableCollection(physicalAddress.getBelowStreetLine(), "belowStreetLine",
						f -> f.stream().collect(HashableUtils.stringsToHashableList())),
				HashableUtils.fromNullableCollection(physicalAddress.getBelowPostOfficeBoxLine(), "belowPostOfficeBoxLine",
						f -> f.stream().collect(HashableUtils.stringsToHashableList())),
				HashableUtils.fromNullableCollection(physicalAddress.getBelowTownLine(), "belowTownLine",
						f -> f.stream().collect(HashableUtils.stringsToHashableList())),
				HashableUtils.fromNullableCollection(physicalAddress.getBelowCountryLine(), "belowCountryLine",
						f -> f.stream().collect(HashableUtils.stringsToHashableList())),
				HashableUtils.fromNullable(physicalAddress.getFrankingArea(), "frankingArea", f -> HashableString.from(f.value()))
		);
	}

	private static Hashable fromElectronicAddresses(final List<ElectronicAddressType> electronicAddresses) {
		return electronicAddresses.stream().map(HashableContestConfigurationFactory::fromElectronicAddress).collect(HashableList.toHashableList());
	}

	private static Hashable fromElectronicAddress(final ElectronicAddressType electronicAddress) {
		return HashableList.of(
				HashableBigInteger.from(BigInteger.valueOf(electronicAddress.getElectronicAddressType())),
				HashableString.from(electronicAddress.getElectronicAddressValue())
		);
	}
}
