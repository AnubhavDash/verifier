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

public record DatasetConfiguration(String filename, String hash, String electionEventId, int numberOfAuthorizedVoters, int numberOfTestVoters,
								   Map<String, String> aliasesToFingerprints) {

	public DatasetConfiguration {
		checkNotNull(filename);
		checkNotNull(hash);
		validateUUID(electionEventId);
		checkArgument(numberOfAuthorizedVoters >= 0, "The number of authorized voters must be positive.");
		checkArgument(numberOfTestVoters >= 0, "The number of test voters must be positive.");
		checkNotNull(aliasesToFingerprints);
		checkArgument(!aliasesToFingerprints.isEmpty(), "The aliases to fingerprints map must not be empty.");
	}
}
