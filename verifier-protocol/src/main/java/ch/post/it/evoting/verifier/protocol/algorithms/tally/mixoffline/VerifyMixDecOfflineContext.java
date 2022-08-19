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
package ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline;

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;

import ch.post.it.evoting.cryptoprimitives.domain.validations.FailedValidationException;

/**
 * Represents the context of the verification of a mix
 */
public record VerifyMixDecOfflineContext(String electionEventId, String ballotBoxId, int numberOfAllowedWriteInsPlusOne) {
	/**
	 * Constructs the context of the VerifyMixDecOffline algorithm.
	 *
	 * @param electionEventId                ee, an election event ID. Must be non-null and a valid UUID.
	 * @param ballotBoxId                    bb, a ballot box ID. Must be non-null and a valid UUID.
	 * @param numberOfAllowedWriteInsPlusOne delta_hat, the number of allowed write-ins + 1 for the given ballot box. Must be strictly positive.
	 * @throws NullPointerException      if any of the fields {@code electionEventId} or {@code ballotBoxId} is null.
	 * @throws FailedValidationException if any of the fields {@code electionEventId} or {@code ballotBoxId} is invalid.
	 * @throws IllegalArgumentException  if {@code numberOfAllowedWriteInsPlusOne} is not strictly positive.
	 */
	public VerifyMixDecOfflineContext {
		validateUUID(electionEventId);
		validateUUID(ballotBoxId);
		checkArgument(numberOfAllowedWriteInsPlusOne > 0, "The number of allowed write-ins + 1 must be strictly positive.");
	}

}
