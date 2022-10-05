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

import static ch.post.it.evoting.verifier.backend.hashable.HashableUtils.fromNullableString;

import java.util.List;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotBoxType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotElectionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.BallotVoteType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.CountingCircleType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.DomainOfInfluenceType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.ElectionType;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.Results;
import ch.post.it.verifier.backend.domain.xmlns.evotingdecrypt.VoteType;

public interface HashableResultsFactory {
	static Hashable fromResults(final Results results) {
		return HashableList.of(
				HashableString.from(results.getContestIdentification()),
				HashableBigInteger.from(results.getCastBallots()),
				HashableResultsFactory.fromBallotBoxes(results.getBallotsBox())
		);
	}

	private static Hashable fromBallotBoxes(final List<BallotBoxType> ballotBoxes) {
		return ballotBoxes.stream().map(HashableResultsFactory::fromBallotBox).collect(HashableList.toHashableList());
	}

	private static Hashable fromBallotBox(final BallotBoxType ballotBox) {
		return HashableList.of(
				HashableString.from(ballotBox.getBallotBoxIdentification()),
				HashableResultsFactory.fromCountingCircles(ballotBox.getCountingCircle())
		);
	}

	private static Hashable fromCountingCircles(final List<CountingCircleType> countingCircles) {
		return countingCircles.stream().map(HashableResultsFactory::fromCountingCircle).collect(HashableList.toHashableList());
	}

	private static Hashable fromCountingCircle(final CountingCircleType countingCircle) {
		return HashableList.of(
				HashableString.from(countingCircle.getCountingCircleIdentification()),
				HashableResultsFactory.fromDomainsOfInfluence(countingCircle.getDomainOfInfluence())
		);
	}

	private static Hashable fromDomainsOfInfluence(final List<DomainOfInfluenceType> domainsOfInfluence) {
		return domainsOfInfluence.stream().map(HashableResultsFactory::fromDomainOfInfluence).collect(HashableList.toHashableList());
	}

	private static Hashable fromDomainOfInfluence(final DomainOfInfluenceType domainOfInfluence) {
		return HashableList.of(
				HashableString.from(domainOfInfluence.getDomainOfInfluenceIdentification()),
				HashableResultsFactory.fromVotes(domainOfInfluence.getVote()),
				HashableResultsFactory.fromElections(domainOfInfluence.getElection())
		);
	}

	private static Hashable fromVotes(final List<VoteType> votes) {
		return votes.stream().map(HashableResultsFactory::fromVote).collect(HashableList.toHashableList());
	}

	private static Hashable fromVote(final VoteType vote) {
		return HashableList.of(
				HashableString.from(vote.getVoteIdentification()),
				HashableResultsFactory.fromBallotVotes(vote.getBallot())
		);
	}

	private static Hashable fromBallotVotes(final List<BallotVoteType> ballots) {
		return ballots.stream().map(HashableResultsFactory::fromBallotVote).collect(HashableList.toHashableList());
	}

	private static Hashable fromBallotVote(final BallotVoteType ballot) {
		return ballot.getChosenAnswerIdentification().stream().collect(HashableUtils.stringsToHashableList());
	}

	private static Hashable fromElections(final List<ElectionType> elections) {
		return elections.stream().map(HashableResultsFactory::fromElection).collect(HashableList.toHashableList());
	}

	private static Hashable fromElection(final ElectionType election) {
		return HashableList.of(
				HashableString.from(election.getElectionIdentification()),
				HashableResultsFactory.fromBallotElections(election.getBallot())
		);
	}

	private static Hashable fromBallotElections(final List<BallotElectionType> ballots) {
		return ballots.stream().map(HashableResultsFactory::fromBallotElection).collect(HashableList.toHashableList());
	}

	private static Hashable fromBallotElection(final BallotElectionType electionBallot) {
		return HashableList.of(
				fromNullableString(electionBallot.getChosenListIdentification(), "chosenListIdentification"),
				electionBallot.getChosenCandidateListIdentification().stream().collect(HashableUtils.stringsToHashableList()),
				electionBallot.getChosenCandidateIdentification().stream().collect(HashableUtils.stringsToHashableList()),
				electionBallot.getChosenWriteInsCandidateValue().stream().collect(HashableUtils.stringsToHashableList())
		);
	}

}

