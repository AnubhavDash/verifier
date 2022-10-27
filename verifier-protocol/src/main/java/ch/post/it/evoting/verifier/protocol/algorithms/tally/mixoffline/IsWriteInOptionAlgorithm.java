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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Implements the isWriteInOption algorithm.
 */
public class IsWriteInOptionAlgorithm {

	/**
	 * @param writeInVotingOptions p&#771;<sub>w</sub>, the write-in voting options. Must be non-null.
	 * @param votingOption         p&#771;<sub>i</sub>, the voting option. Must be non-null.
	 * @return true if the voting option is a write-in, false otherwise.
	 * @throws NullPointerException     if any input is null.
	 * @throws IllegalArgumentException if the group of the voting option is not equal to the group of the write-in voting options.
	 */
	@SuppressWarnings("java:S117")
	boolean isWriteInOption(final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions, final PrimeGqElement votingOption) {
		final GroupVector<PrimeGqElement, GqGroup> p_w_tilde = checkNotNull(writeInVotingOptions);
		final PrimeGqElement p_i_tilde = checkNotNull(votingOption);
		final GqGroup group = p_i_tilde.getGroup();

		checkArgument(p_w_tilde.isEmpty() || group.equals(p_w_tilde.getGroup()),
				"The group of the write-in voting options and of the voting option must be the same.");

		// Operation.

		return p_w_tilde.contains(p_i_tilde);
	}
}