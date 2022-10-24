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

import java.util.ArrayList;
import java.util.List;

import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.verifier.protocol.algorithms.tally.QuadraticResidueToWriteInAlgorithm;

/**
 * Implements the DecodeWriteIns algorithm.
 */
public class DecodeWriteInsAlgorithm {
	private final IsWriteInOptionAlgorithm isWriteInOptionAlgorithm;
	private final QuadraticResidueToWriteInAlgorithm quadraticResidueToWriteInAlgorithm;

	public DecodeWriteInsAlgorithm(final IsWriteInOptionAlgorithm isWriteInOptionAlgorithm,
			final QuadraticResidueToWriteInAlgorithm quadraticResidueToWriteInAlgorithm) {
		this.isWriteInOptionAlgorithm = isWriteInOptionAlgorithm;
		this.quadraticResidueToWriteInAlgorithm = quadraticResidueToWriteInAlgorithm;
	}

	/**
	 * @param decodeWriteInsAlgorithmInput the inputs of the DecodeWriteIns as a {@link DecodeWriteInsAlgorithmInput}. Must be non-null.
	 * @return s&#770;, the vector of decoded write-ins.
	 * @throws NullPointerException if the input is null.
	 */
	@SuppressWarnings("java:S117")
	public List<String> decodeWriteIns(final DecodeWriteInsAlgorithmInput decodeWriteInsAlgorithmInput) {

		checkNotNull(decodeWriteInsAlgorithmInput);

		final GroupVector<PrimeGqElement, GqGroup> p_w_tilde = decodeWriteInsAlgorithmInput.getWriteInVotingOptions();
		final GroupVector<PrimeGqElement, GqGroup> p_hat = decodeWriteInsAlgorithmInput.getSelectedEncodedVotingOptions();
		final GroupVector<GqElement, GqGroup> w = decodeWriteInsAlgorithmInput.getEncodedWriteIns();
		final int psi = p_hat.size();
		final int delta_hat = p_w_tilde.size() + 1;

		// Require.

		checkArgument(1 <= delta_hat, "delta hat must be strictly positive. [delta_hat: %s]", delta_hat);
		checkArgument(delta_hat <= psi, "psi must be greater or equal to delta_hat. [psi: %s, delta_hat: %s]", psi, delta_hat);

		// Operation.

		final List<String> s_hat = new ArrayList<>();
		int k = 0;
		for (int i = 0; i < psi; i++) {
			final PrimeGqElement p_i_hat = p_hat.get(i);
			if (isWriteInOptionAlgorithm.isWriteInOption(p_w_tilde, p_i_hat)) {
				final GqElement w_k = GqElement.GqElementFactory.fromValue(w.get(k).getValue(), p_w_tilde.getGroup());
				final String s_k_hat = quadraticResidueToWriteInAlgorithm.quadraticResidueToWriteIn(w_k);
				s_hat.add(s_k_hat);
				k++;
			}
		}

		return s_hat;
	}
}