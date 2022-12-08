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

import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Contains the inputs of the DecodeWriteIns algorithm. The inputs are:
 * <ul>
 *     <li>p&#771;<sub>w</sub>, the write-in voting options</li>
 *     <li>p&#770;, the selected encoded voting options</li>
 *     <li>w, the list of encoded write-ins</li>
 * </ul>
 */
public class DecodeWriteInsAlgorithmInput {

	private final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions;
	private final GroupVector<PrimeGqElement, GqGroup> selectedEncodedVotingOptions;
	private final GroupVector<GqElement, GqGroup> encodedWriteIns;

	private DecodeWriteInsAlgorithmInput(final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions,
			final GroupVector<PrimeGqElement, GqGroup> selectedEncodedVotingOptions, final GroupVector<GqElement, GqGroup> encodedWriteIns) {

		this.writeInVotingOptions = writeInVotingOptions;
		this.selectedEncodedVotingOptions = selectedEncodedVotingOptions;
		this.encodedWriteIns = encodedWriteIns;
	}

	/**
	 * @return p&#771;<sub>w</sub>, the write-in voting options.
	 */
	public GroupVector<PrimeGqElement, GqGroup> getWriteInVotingOptions() {
		return writeInVotingOptions;
	}

	/**
	 * @return p&#770;, the selected encoded voting options.
	 */
	public GroupVector<PrimeGqElement, GqGroup> getSelectedEncodedVotingOptions() {
		return selectedEncodedVotingOptions;
	}

	/**
	 * @return w, the list of encoded write-ins.
	 */
	public GroupVector<GqElement, GqGroup> getEncodedWriteIns() {
		return encodedWriteIns;
	}

	/**
	 * Builder performing input validations before constructing a {@link DecodeWriteInsAlgorithmInput}.
	 */
	public static class Builder {

		private GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions;
		private GroupVector<PrimeGqElement, GqGroup> selectedEncodedVotingOptions;
		private GroupVector<GqElement, GqGroup> encodedWriteIns;

		/**
		 * @param writeInVotingOptions p&#771;<sub>w</sub>, the write-in voting options. Must be non-null.
		 * @throws NullPointerException if the input is null.
		 */
		public DecodeWriteInsAlgorithmInput.Builder setWriteInVotingOptions(final GroupVector<PrimeGqElement, GqGroup> writeInVotingOptions) {
			this.writeInVotingOptions = checkNotNull(writeInVotingOptions);
			return this;
		}

		/**
		 * @param selectedEncodedVotingOptions p&#770;, the selected encoded voting options. Must be non-null.
		 * @throws NullPointerException if the input is null.
		 */
		public DecodeWriteInsAlgorithmInput.Builder setSelectedEncodedVotingOptions(
				final GroupVector<PrimeGqElement, GqGroup> selectedEncodedVotingOptions) {
			this.selectedEncodedVotingOptions = checkNotNull(selectedEncodedVotingOptions);
			return this;
		}

		/**
		 * @param encodedWriteIns w, the list of encoded write-ins. Must be non-null.
		 * @throws NullPointerException if the input is null.
		 */
		public DecodeWriteInsAlgorithmInput.Builder setEncodedWriteIns(final GroupVector<GqElement, GqGroup> encodedWriteIns) {
			this.encodedWriteIns = checkNotNull(encodedWriteIns);
			return this;
		}

		/**
		 * @return a validated DecodedWriteInsAlgorithmInput.
		 */
		public DecodeWriteInsAlgorithmInput build() {

			final GqGroup group = selectedEncodedVotingOptions.getGroup();

			checkArgument(encodedWriteIns.isEmpty() || encodedWriteIns.getGroup().equals(group),
					"The group of the encoded write-in options must be equal to the group of selected encoded voting options.");
			checkArgument(writeInVotingOptions.isEmpty() || writeInVotingOptions.getGroup().equals(group),
					"The group of the write-in voting options must be equal to the group of selected encoded voting options.");
			checkArgument(writeInVotingOptions.size() == encodedWriteIns.size(),
					"The must be the same number of write-in voting options as the number of encoded write-ins.");

			final int psi = selectedEncodedVotingOptions.size();
			final int delta_hat = writeInVotingOptions.size() + 1;

			checkArgument(1 <= delta_hat, "Delta_hat must be strictly positive. [delta_hat: %s]", delta_hat);
			checkArgument(delta_hat <= psi + 1, "Psi + 1 must be greater or equal to delta_hat. [psi: %s, delta_hat: %s]", psi, delta_hat);

			return new DecodeWriteInsAlgorithmInput(writeInVotingOptions, selectedEncodedVotingOptions, encodedWriteIns);
		}
	}
}