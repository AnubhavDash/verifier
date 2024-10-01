/*
 * (c) Copyright 2024 Swiss Post Ltd.
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
package ch.post.it.evoting.verifier.backend.dto;

import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;

public class DatasetConfigurationContext {

	private final String filename;
	private final String hash;
	private final String electionEventId;
	private final ImmutableMap<String, String> aliasesToFingerprints;
	private final String electionEventName;
	private final String electionEventSeed;
	private final String electionEventDate;
	private final int numberOfElections;
	private final int numberOfVotes;
	private final int numberOfBallots;
	private final int numberOfNonTestBallotBoxes;
	private final int numberOfTestBallotBoxes;
	private final int totalNumberOfAuthorizedNonTestVoters;
	private final int totalNumberOfTestVoters;

	private DatasetConfigurationContext(final String filename, final String hash, final String electionEventId,
			final ImmutableMap<String, String> aliasesToFingerprints, final String electionEventName, final String electionEventSeed,
			final String electionEventDate, final int numberOfElections, final int numberOfVotes, final int numberOfBallots,
			final int numberOfNonTestBallotBoxes, final int numberOfTestBallotBoxes, final int totalNumberOfAuthorizedNonTestVoters,
			final int totalNumberOfTestVoters) {
		this.filename = checkNotNull(filename);
		this.hash = checkNotNull(hash);
		this.electionEventId = validateUUID(electionEventId);
		this.aliasesToFingerprints = checkNotNull(aliasesToFingerprints);
		this.electionEventName = checkNotNull(electionEventName);
		this.electionEventSeed = checkNotNull(electionEventSeed);
		this.electionEventDate = checkNotNull(electionEventDate);
		this.numberOfElections = numberOfElections;
		this.numberOfVotes = numberOfVotes;
		this.numberOfBallots = numberOfBallots;
		this.numberOfNonTestBallotBoxes = numberOfNonTestBallotBoxes;
		this.numberOfTestBallotBoxes = numberOfTestBallotBoxes;
		this.totalNumberOfAuthorizedNonTestVoters = totalNumberOfAuthorizedNonTestVoters;
		this.totalNumberOfTestVoters = totalNumberOfTestVoters;

		checkArgument(!aliasesToFingerprints.isEmpty(), "The aliases to fingerprints map must not be empty.");
		checkArgument(numberOfElections >= 0, "The number of elections must be positive.");
		checkArgument(numberOfVotes >= 0, "The number of votes must be positive.");
		checkArgument(numberOfBallots >= 0, "The number of ballots must be positive.");
		checkArgument(numberOfNonTestBallotBoxes >= 0, "The number of non test ballot boxes must be positive.");
		checkArgument(numberOfTestBallotBoxes >= 0, "The number of test ballot boxes must be positive.");
		checkArgument(totalNumberOfAuthorizedNonTestVoters >= 0, "The total number of authorized non test voters must be positive.");
		checkArgument(totalNumberOfTestVoters >= 0, "The total number of test voters must be positive.");
	}

	public String getFilename() {
		return filename;
	}

	public String getHash() {
		return hash;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public ImmutableMap<String, String> getAliasesToFingerprints() {
		return aliasesToFingerprints;
	}

	public String getElectionEventName() {
		return electionEventName;
	}

	public String getElectionEventSeed() {
		return electionEventSeed;
	}

	public String getElectionEventDate() {
		return electionEventDate;
	}

	public int getNumberOfElections() {
		return numberOfElections;
	}

	public int getNumberOfVotes() {
		return numberOfVotes;
	}

	public int getNumberOfBallots() {
		return numberOfBallots;
	}

	public int getNumberOfNonTestBallotBoxes() {
		return numberOfNonTestBallotBoxes;
	}

	public int getNumberOfTestBallotBoxes() {
		return numberOfTestBallotBoxes;
	}

	public int getTotalNumberOfAuthorizedNonTestVoters() {
		return totalNumberOfAuthorizedNonTestVoters;
	}

	public int getTotalNumberOfTestVoters() {
		return totalNumberOfTestVoters;
	}

	public static class Builder {
		private String filename;
		private String hash;
		private String electionEventId;
		private ImmutableMap<String, String> aliasesToFingerprints;
		private String electionEventName;
		private String electionEventSeed;
		private String electionEventDate;
		private int numberOfElections;
		private int numberOfVotes;
		private int numberOfBallots;
		private int numberOfNonTestBallotBoxes;
		private int numberOfTestBallotBoxes;
		private int totalNumberOfAuthorizedNonTestVoters;
		private int totalNumberOfTestVoters;

		public Builder setFilename(final String filename) {
			this.filename = filename;
			return this;
		}

		public Builder setHash(final String hash) {
			this.hash = hash;
			return this;
		}

		public Builder setElectionEventId(final String electionEventId) {
			this.electionEventId = electionEventId;
			return this;
		}

		public Builder setAliasesToFingerprints(final ImmutableMap<String, String> aliasesToFingerprints) {
			this.aliasesToFingerprints = aliasesToFingerprints;
			return this;
		}

		public Builder setElectionEventName(final String electionEventName) {
			this.electionEventName = electionEventName;
			return this;
		}

		public Builder setElectionEventSeed(final String electionEventSeed) {
			this.electionEventSeed = electionEventSeed;
			return this;
		}

		public Builder setElectionEventDate(final String electionEventDate) {
			this.electionEventDate = electionEventDate;
			return this;
		}

		public Builder setNumberOfElections(final int numberOfElections) {
			this.numberOfElections = numberOfElections;
			return this;
		}

		public Builder setNumberOfVotes(final int numberOfVotes) {
			this.numberOfVotes = numberOfVotes;
			return this;
		}

		public Builder setNumberOfBallots(final int numberOfBallots) {
			this.numberOfBallots = numberOfBallots;
			return this;
		}

		public Builder setNumberOfNonTestBallotBoxes(final int numberOfNonTestBallotBoxes) {
			this.numberOfNonTestBallotBoxes = numberOfNonTestBallotBoxes;
			return this;
		}

		public Builder setNumberOfTestBallotBoxes(final int numberOfTestBallotBoxes) {
			this.numberOfTestBallotBoxes = numberOfTestBallotBoxes;
			return this;
		}

		public Builder setTotalNumberOfAuthorizedNonTestVoters(final int totalNumberOfAuthorizedNonTestVoters) {
			this.totalNumberOfAuthorizedNonTestVoters = totalNumberOfAuthorizedNonTestVoters;
			return this;
		}

		public Builder setTotalNumberOfTestVoters(final int totalNumberOfTestVoters) {
			this.totalNumberOfTestVoters = totalNumberOfTestVoters;
			return this;
		}

		public DatasetConfigurationContext build() {
			return new DatasetConfigurationContext(filename, hash, electionEventId, aliasesToFingerprints, electionEventName, electionEventSeed,
					electionEventDate, numberOfElections, numberOfVotes, numberOfBallots, numberOfNonTestBallotBoxes, numberOfTestBallotBoxes,
					totalNumberOfAuthorizedNonTestVoters, totalNumberOfTestVoters);
		}
	}
}
