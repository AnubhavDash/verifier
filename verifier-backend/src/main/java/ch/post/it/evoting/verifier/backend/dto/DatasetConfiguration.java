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
package ch.post.it.evoting.verifier.backend.dto;

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Objects;

public record DatasetConfiguration(String filename, String hash, String electionEventId, Map<String, String> aliasesToFingerprints,
								   String electionEventName, String electionEventDate, int numberOfElections, int numberOfVotes,
								   long numberOfNonTestBallotBoxes, long numberOfTestBallotBoxes, int totalNumberOfAuthorizedNonTestVoters,
								   int totalNumberOfTestVoters, Integer numberOfConfirmedNonTestVotes, Integer numberOfConfirmedTestVotes) {

	public DatasetConfiguration {
		checkNotNull(filename);
		checkNotNull(hash);
		validateUUID(electionEventId);
		checkNotNull(aliasesToFingerprints);
		checkArgument(!aliasesToFingerprints.isEmpty(), "The aliases to fingerprints map must not be empty.");
		checkNotNull(electionEventName);
		checkNotNull(electionEventDate);
		checkArgument(numberOfElections >= 0, "The number of elections must be positive.");
		checkArgument(numberOfVotes >= 0, "The number of votes must be positive.");
		checkArgument(numberOfNonTestBallotBoxes >= 0, "The number of non test ballot boxes must be positive.");
		checkArgument(numberOfTestBallotBoxes >= 0, "The number of test ballot boxes must be positive.");
		checkArgument(totalNumberOfAuthorizedNonTestVoters >= 0, "The total number of authorized non test voters must be positive.");
		checkArgument(totalNumberOfTestVoters >= 0, "The total number of test voters must be positive.");
		if (Objects.nonNull(numberOfConfirmedNonTestVotes)) {
			checkArgument(numberOfConfirmedNonTestVotes >= 0, "The number of confirmed non test votes must be positive.");
		}
		if (Objects.nonNull(numberOfConfirmedTestVotes)) {
			checkArgument(numberOfConfirmedTestVotes >= 0, "The number of confirmed non test votes must be positive.");
		}
	}
}
