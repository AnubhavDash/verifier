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

import static ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants.MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS;
import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.validateUUID;
import static com.google.common.base.Preconditions.checkArgument;

import ch.post.it.evoting.cryptoprimitives.domain.validations.FailedValidationException;

/**
 * Regroups the context values needed by the VerifyVotingClientProofs algorithm.
 *
 * <ul>
 * <li>ee, the election event id. Not null.</li>
 * <li>psi, the number of selectable voting options. In range [1, 120].</li>
 * <li>delta_hat, the number of allowed write-ins + 1. Strictly positive.</li>
 * </ul>
 */
public class VerifyVotingClientProofsContext {

	private final String electionEventId;
	private final int numberOfSelectableVotingOptions;
	private final int numberOfAllowedWriteInsPlusOne;

	private VerifyVotingClientProofsContext(final String electionEventId, final int numberOfSelectableVotingOptions,
			final int numberOfAllowedWriteInsPlusOne) {
		this.electionEventId = electionEventId;
		this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
		this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;
	}

	public String getElectionEventId() {
		return electionEventId;
	}

	public int getNumberOfSelectableVotingOptions() {
		return numberOfSelectableVotingOptions;
	}

	public int getNumberOfAllowedWriteInsPlusOne() {
		return numberOfAllowedWriteInsPlusOne;
	}

	public static class Builder {

		private String electionEventId;
		private int numberOfSelectableVotingOptions;
		private int numberOfAllowedWriteInsPlusOne;

		public Builder setElectionEventId(final String electionEventId) {
			this.electionEventId = electionEventId;
			return this;
		}

		public Builder setNumberOfSelectableVotingOptions(final int numberOfSelectableVotingOptions) {
			this.numberOfSelectableVotingOptions = numberOfSelectableVotingOptions;
			return this;
		}

		public Builder setNumberOfAllowedWriteInsPlusOne(final int numberOfAllowedWriteInsPlusOne) {
			this.numberOfAllowedWriteInsPlusOne = numberOfAllowedWriteInsPlusOne;
			return this;
		}

		/**
		 * Creates a VerifyVotingClientProofsContext object.
		 *
		 * @throws NullPointerException      if the election event id is null.
		 * @throws FailedValidationException if the election event id is not a valid UUID.
		 * @throws IllegalArgumentException  if
		 *                                   <ul>
		 *                                       <li>the number of selectable voting options is not in the range [1, 120].</li>
		 *                                       <li>the number of allowed write-ins + 1 is smaller or equal to 0.</li>
		 *                                   </ul>
		 */
		public VerifyVotingClientProofsContext build() {
			validateUUID(electionEventId);

			final int psi = numberOfSelectableVotingOptions;
			final int delta_hat = numberOfAllowedWriteInsPlusOne;

			checkArgument(psi >= 1, "The number of selectable voting options should be greater or equal to one. [psi: %s]", psi);
			checkArgument(psi <= MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS,
					"The number of selectable voting options should be smaller or equal to %s. [psi: %s]",
					MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS, psi);
			checkArgument(0 < delta_hat, "The number of allowed write ins plus one must be a strictly positive number. [delta_hat: %s]", delta_hat);

			return new VerifyVotingClientProofsContext(electionEventId, numberOfSelectableVotingOptions, numberOfAllowedWriteInsPlusOne);
		}
	}

}
