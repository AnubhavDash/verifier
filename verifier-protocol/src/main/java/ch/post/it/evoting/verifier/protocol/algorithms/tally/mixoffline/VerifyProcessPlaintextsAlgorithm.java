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

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalMultiRecipientMessage;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

public final class VerifyProcessPlaintextsAlgorithm {

	private final ElGamal elGamal;

	public VerifyProcessPlaintextsAlgorithm(final ElGamal elGamal) {
		this.elGamal = elGamal;
	}

	/**
	 * Factorizes each plaintext vote.
	 *
	 * @param encodedVotingOptions            p_tile, the encoded voting options. Must be non-null.
	 * @param plaintextVotes                  m, the list of plaintext votes. Must be non-null.
	 * @param numberOfSelectableVotingOptions psi, the number of selectable voting options. Must be between 1 and 120 inclusive.
	 * @param numberOfAllowedWriteInsPlusOne  delta_hat, the number of write-ins + 1. Must be strictly positive.
	 * @return the list of all selected encoded voting options L_votes.
	 * @throws NullPointerException     if any of the inputs is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>the number of selectable voting options is strictly smaller than 1.</li>
	 *                                      <li>the number of selectable voting options is strictly greater than 120.</li>
	 *                                      <li>the number of allowed write-ins + 1 is smaller or equal to 0.</li>
	 *                                      <li>the number of allowed write-ins + 1 is strictly greater to the number of elements in the decrypted votes.</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyProcessPlaintexts(
			final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions, final GroupVector<ElGamalMultiRecipientMessage, GqGroup> plaintextVotes,
			final int numberOfSelectableVotingOptions, final int numberOfAllowedWriteInsPlusOne,
			final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> selectedEncodedVotingOptions) {
		checkNotNull(plaintextVotes);
		checkNotNull(encodedVotingOptions);

		// Context
		final GqGroup group = plaintextVotes.getGroup();
		final GroupVector<PrimeGqElement, GqGroup> p_tilde = encodedVotingOptions;

		// Input
		final GroupVector<ElGamalMultiRecipientMessage, GqGroup> m = plaintextVotes;
		final int psi = numberOfSelectableVotingOptions;
		final int delta_hat = numberOfAllowedWriteInsPlusOne;
		checkArgument(psi >= 1, "The number of selectable voting options must be greater or equal to 1. [psi: %s]", psi);
		checkArgument(psi <= 120, "The number of selectable voting options must be smaller or equal to 120. [psi: %s]", psi);
		checkArgument(delta_hat > 0, "The number of allowed write-ins + 1 must be strictly greater than 0. [delta_hat: %s]", delta_hat);

		// Requires
		final int l = m.getElementSize();
		checkArgument(delta_hat <= l,
				"The number of allowed write-ins + 1 must be smaller or equal to the number of elements in the decrypted votes. [delta_hat: %s, l: %s]",
				delta_hat, l);

		// Operation
		final ElGamalMultiRecipientMessage one_vector = elGamal.ones(group, delta_hat);

		final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> calculatedSelectedEncodedVotingOptions = m.stream()
				.filter(m_i -> !m_i.equals(one_vector))
				.map(m_i -> FactorizeService.factorize(m_i.get(0), p_tilde, psi))
				.collect(GroupVector.toGroupVector());
		return calculatedSelectedEncodedVotingOptions.equals(selectedEncodedVotingOptions);
	}

}

