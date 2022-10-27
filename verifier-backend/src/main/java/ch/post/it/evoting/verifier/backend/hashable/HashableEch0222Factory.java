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

import static ch.post.it.evoting.cryptoprimitives.hashing.HashableList.toHashableList;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromDate;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullable;
import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableBigInteger;

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
				fromNullableBigInteger(delivery.getMinorVersion(), "minorVersion")
		);
	}

	private static Hashable fromEventRawDataDelivery(final EventRawDataDelivery rawDataDelivery) {
		return HashableList.of(
				fromReportingBody(rawDataDelivery.getReportingBody()),
				fromRawData(rawDataDelivery.getRawData())
		);
	}

	private static Hashable fromReportingBody(final ReportingBodyType reportingBody) {
		return HashableList.of(
				HashableEch0155Factory.fromIdentifier(reportingBody.getReportingBodyIdentification()),
				fromDate(reportingBody.getCreationDateTime())
		);
	}

	private static Hashable fromRawData(final RawDataType rawData) {
		return HashableList.of(
				HashableEch0155Factory.fromContestIdentification(rawData.getContestIdentification()),
				rawData.getCountingCircleRawData().stream()
						.map(HashableEch0222Factory::fromCountingCircleRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromCountingCircleRawData(final RawDataType.CountingCircleRawData countingCircleRawData) {
		return HashableList.of(
				HashableEch0155Factory.fromCountingCircleId(countingCircleRawData.getCountingCircleId()),
				fromNullable(countingCircleRawData.getVoteRawData(),
						"voteRawData",
						voteRawData -> voteRawData.stream()
								.map(HashableEch0222Factory::fromVoteRawData)
								.collect(toHashableList())),
				fromNullable(countingCircleRawData.getElectionGroupBallotRawData(),
						"electionGroupBallotRawData",
						electionGroupBallotRawData -> electionGroupBallotRawData.stream()
								.map(HashableEch0222Factory::fromElectionGroupBallotRawData)
								.collect(toHashableList()))
		);
	}

	// Vote.

	private static Hashable fromVoteRawData(final VoteRawDataType voteRawData) {
		return HashableList.of(
				HashableEch0155Factory.fromVoteIdentification(voteRawData.getVoteIdentification()),
				voteRawData.getBallotRawData().stream()
						.map(HashableEch0222Factory::fromVoteBallotRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromVoteBallotRawData(final VoteRawDataType.BallotRawData ballotRawData) {
		return HashableList.of(
				HashableEch0155Factory.fromBallotIdentification(ballotRawData.getBallotIdentification()),
				fromBallotCasted(ballotRawData.getBallotCasted())
		);
	}

	private static Hashable fromBallotCasted(final VoteRawDataType.BallotRawData.BallotCasted ballotCasted) {
		return HashableList.of(
				fromNullableBigInteger(ballotCasted.getBallotCastedNumber(), "ballotCastedNumber"),
				ballotCasted.getQuestionRawData().stream()
						.map(HashableEch0222Factory::fromQuestionRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromQuestionRawData(final VoteRawDataType.BallotRawData.BallotCasted.QuestionRawData questionRawData) {
		return HashableList.of(
				HashableEch0155Factory.fromQuestionId(questionRawData.getQuestionIdentification()),
				fromNullable(questionRawData.getCasted(),
						"casted",
						HashableEch0222Factory::fromCasted)
		);
	}

	private static Hashable fromCasted(final VoteRawDataType.BallotRawData.BallotCasted.QuestionRawData.Casted casted) {
		return HashableList.of(
				fromCastedVote(casted.getCastedVote()),
				fromNullable(casted.getAnswerOptionIdentification(),
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
				fromNullable(electionGroupBallotRawData.getElectionGroupIdentification(),
						"electionGroupIdentification",
						HashableEch0155Factory::fromElectionIdentification),
				electionGroupBallotRawData.getElectionRawData().stream()
						.map(HashableEch0222Factory::fromElectionRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromElectionRawData(final ElectionRawDataType electionRawDataType) {
		return HashableList.of(
				HashableEch0155Factory.fromElectionIdentification(electionRawDataType.getElectionIdentification()),
				electionRawDataType.getBallotRawData().stream()
						.map(HashableEch0222Factory::fromElectionBallotRawData)
						.collect(toHashableList())
		);
	}

	private static Hashable fromElectionBallotRawData(final ElectionRawDataType.BallotRawData ballotRawData) {
		return HashableList.of(
				fromNullable(ballotRawData.getListRawData(),
						"listRawData",
						HashableEch0222Factory::fromListRawData),
				ballotRawData.getBallotPosition().stream()
						.map(HashableEch0222Factory::fromBallotPosition)
						.collect(toHashableList()),
				fromNullable(ballotRawData.isIsUnchangedBallot(),
						"isUnchangedBallot",
						HashableEch0222Factory::fromIsUnchangedBallot)
		);
	}

	private static Hashable fromListRawData(final ElectionRawDataType.BallotRawData.ListRawData listRawData) {
		return HashableList.of(
				HashableEch0155Factory.fromListIdentification(listRawData.getListIdentification())
		);
	}

	private static Hashable fromBallotPosition(final ElectionRawDataType.BallotRawData.BallotPosition ballotPosition) {
		final boolean isEmpty = ballotPosition.isIsEmpty() != null;

		return isEmpty ?
				HashableString.from(Boolean.toString(ballotPosition.isIsEmpty())) :
				fromCandidate(ballotPosition.getCandidate());
	}

	private static Hashable fromCandidate(final ElectionRawDataType.BallotRawData.BallotPosition.Candidate candidate) {
		final boolean isWriteIn = candidate.getWriteIn() != null;

		return isWriteIn ?
				HashableEch0155Factory.fromWriteIn(candidate.getWriteIn()) :
				HashableList.of(
						HashableEch0155Factory.fromIdentifier(candidate.getCandidateIdentification()),
						HashableEch0155Factory.fromCandidateReferenceOnPosition(candidate.getCandidateReferenceOnPosition())
				);
	}

	private static Hashable fromIsUnchangedBallot(final Boolean isUnchangedBallot) {
		return HashableString.from(Boolean.toString(isUnchangedBallot));
	}

}