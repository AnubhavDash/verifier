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

import ch.ech.xmlns.ech_0222._1.Delivery;
import ch.ech.xmlns.ech_0222._1.ElectionRawDataType;
import ch.ech.xmlns.ech_0222._1.EventRawDataDelivery;
import ch.ech.xmlns.ech_0222._1.RawDataType;
import ch.ech.xmlns.ech_0222._1.ReportingBodyType;
import ch.ech.xmlns.ech_0222._1.VoteRawDataType;
import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;

public interface HashableEch0222Factory {

	static Hashable fromDelivery(final Delivery delivery) {
		return HashableList.of(
				HashableEch0058Factory.fromHeader(delivery.getDeliveryHeader()),
				fromEventRawDataDelivery(delivery.getRawDataDelivery()),
				HashableUtils.fromNullableBigInteger(delivery.getMinorVersion(), "minorVersion")
		);
	}

	private static Hashable fromEventRawDataDelivery(final EventRawDataDelivery rawDataDelivery) {
		return HashableList.of(
				fromReportingBody(rawDataDelivery.getReportingBody()),
				fromRawData(rawDataDelivery.getRawData())
				// extensions are not supported
		);
	}

	private static Hashable fromReportingBody(final ReportingBodyType reportingBody) {
		return HashableList.of(
				HashableString.from(reportingBody.getReportingBodyIdentification()),
				HashableUtils.fromDate(reportingBody.getCreationDateTime())
		);
	}

	private static Hashable fromRawData(final RawDataType rawData) {
		return HashableList.of(
				HashableString.from(rawData.getContestIdentification()),
				rawData.getCountingCircleRawData().stream()
						.map(HashableEch0222Factory::fromCountingCircleRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromCountingCircleRawData(final RawDataType.CountingCircleRawData countingCircleRawData) {
		return HashableList.of(
				HashableString.from(countingCircleRawData.getCountingCircleId()),
				HashableUtils.fromNullableCollection(countingCircleRawData.getVoteRawData(),
						"voteRawData",
						voteRawData -> voteRawData.stream()
								.map(HashableEch0222Factory::fromVoteRawData)
								.collect(toHashableList())),
				HashableUtils.fromNullable(countingCircleRawData.getElectionGroupBallotRawData(),
						"electionGroupBallotRawData",
						electionGroupBallotRawData -> electionGroupBallotRawData.stream()
								.map(HashableEch0222Factory::fromElectionGroupBallotRawData)
								.collect(toHashableList()))
		);
	}

	// Vote.

	private static Hashable fromVoteRawData(final VoteRawDataType voteRawData) {
		return HashableList.of(
				HashableString.from(voteRawData.getVoteIdentification()),
				voteRawData.getBallotRawData().stream()
						.map(HashableEch0222Factory::fromVoteBallotRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromVoteBallotRawData(final VoteRawDataType.BallotRawData ballotRawData) {
		return HashableList.of(
				HashableString.from(ballotRawData.getBallotIdentification()),
				fromBallotCasted(ballotRawData.getBallotCasted())
		);
	}

	private static Hashable fromBallotCasted(final VoteRawDataType.BallotRawData.BallotCasted ballotCasted) {
		return HashableList.of(
				HashableUtils.fromNullableBigInteger(ballotCasted.getBallotCastedNumber(), "ballotCastedNumber"),
				ballotCasted.getQuestionRawData().stream()
						.map(HashableEch0222Factory::fromQuestionRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromQuestionRawData(final VoteRawDataType.BallotRawData.BallotCasted.QuestionRawData questionRawData) {
		return HashableList.of(
				HashableString.from(questionRawData.getQuestionIdentification()),
				HashableUtils.fromNullable(questionRawData.getCasted(),
						"casted",
						HashableEch0222Factory::fromCasted)
		);
	}

	private static Hashable fromCasted(final VoteRawDataType.BallotRawData.BallotCasted.QuestionRawData.Casted casted) {
		return HashableList.of(
				fromCastedVote(casted.getCastedVote()),
				HashableUtils.fromNullable(casted.getAnswerOptionIdentification(),
						"answerOptionIdentification",
						HashableEch0155Factory::fromAnswerOptionIdentification)
		);
	}

	private static Hashable fromCastedVote(final BigInteger castedVote) {
		return HashableBigInteger.from(castedVote);
	}

	// Election.

	private static Hashable fromElectionGroupBallotRawData(
			final RawDataType.CountingCircleRawData.ElectionGroupBallotRawData electionGroupBallotRawData) {

		return HashableList.of(
				HashableUtils.fromNullableString(electionGroupBallotRawData.getElectionGroupIdentification(), "electionGroupIdentification"),
				electionGroupBallotRawData.getElectionRawData().stream()
						.map(HashableEch0222Factory::fromElectionRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromElectionRawData(final ElectionRawDataType electionRawDataType) {
		return HashableList.of(
				HashableString.from(electionRawDataType.getElectionIdentification()),
				electionRawDataType.getBallotRawData().stream()
						.map(HashableEch0222Factory::fromElectionBallotRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromElectionBallotRawData(final ElectionRawDataType.BallotRawData ballotRawData) {
		return HashableList.of(
				HashableUtils.fromNullable(ballotRawData.getListRawData(),
						"listRawData",
						HashableEch0222Factory::fromListRawData),
				ballotRawData.getBallotPosition().stream()
						.map(HashableEch0222Factory::fromBallotPosition)
						.collect(toHashableList()),
				HashableUtils.fromNullable(ballotRawData.isIsUnchangedBallot(),
						"isUnchangedBallot",
						HashableEch0222Factory::fromIsUnchangedBallot)
		);
	}

	private static Hashable fromListRawData(final ElectionRawDataType.BallotRawData.ListRawData listRawData) {
		return HashableString.from(listRawData.getListIdentification());
	}

	private static Hashable fromBallotPosition(final ElectionRawDataType.BallotRawData.BallotPosition ballotPosition) {
		final boolean hasCandidate = ballotPosition.getCandidate() != null;
		final boolean isEmpty = ballotPosition.isIsEmpty() != null;

		checkState(hasCandidate ^ isEmpty, "Either candidate or isEmpty must be present for ballot position.");

		return hasCandidate ?
				fromCandidate(ballotPosition.getCandidate()) :
				HashableString.from(Boolean.toString(ballotPosition.isIsEmpty()));
	}

	private static Hashable fromCandidate(final ElectionRawDataType.BallotRawData.BallotPosition.Candidate candidate) {
		final boolean hasCandidateInformation = candidate.getCandidateIdentification() != null && candidate.getCandidateReferenceOnPosition() != null;
		final boolean isWriteIn = candidate.getWriteIn() != null;

		checkState(hasCandidateInformation ^ isWriteIn, "Either candidate information or write-in must be present for candidate.");

		return hasCandidateInformation ?
				HashableList.of(
						HashableString.from(candidate.getCandidateIdentification()),
						HashableString.from(candidate.getCandidateReferenceOnPosition())) :
				HashableString.from(candidate.getWriteIn());
	}

	private static Hashable fromIsUnchangedBallot(final Boolean isUnchangedBallot) {
		return HashableString.from(Boolean.toString(isUnchangedBallot));
	}

}
