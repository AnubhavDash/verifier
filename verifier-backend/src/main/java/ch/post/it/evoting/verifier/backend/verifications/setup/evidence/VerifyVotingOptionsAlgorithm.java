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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static ch.post.it.evoting.evotinglibraries.domain.VotingOptionsConstants.MAXIMUM_SUPPORTED_NUMBER_OF_SELECTIONS;
import static ch.post.it.evoting.evotinglibraries.domain.VotingOptionsConstants.MAXIMUM_SUPPORTED_NUMBER_OF_VOTING_OPTIONS;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Implements the VerifyVotingOptions algorithm.
 */
@Service
public class VerifyVotingOptionsAlgorithm {

	/**
	 * Verifies the correctness of the voting options.
	 * <p>
	 * The voting options must correspond to the smallest prime group members and the product of the &psi;<sub>sup</sub> biggest voting options must
	 * be smaller than p.
	 *
	 * @param encryptionGroup        (p, q, g), the encryption group.
	 * @param smallPrimeGroupMembers <b>p</b>, a list of the n<sub>sup</sub> small prime group members strictly greater than 3.
	 * @param encodedVotingOptions   <b>p&#771;</b>, a list of the voting options encoded as primes.
	 * @return {@code true} if the verification is successful, {@code false} otherwise
	 * @throws NullPointerException     if any input is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>The encoded voting options are not in strictly ascending order.</li>
	 *                                      <li>At least one of the encoded voting options is smaller than or equal to 3.</li>
	 *                                      <li>n<sub>sup</sub> or &psi;<sub>sup</sub> are not strictly positive.</li>
	 *                                      <li>The small primes and encoded voting options do not have the same group.</li>
	 *                                      <li>There are not n<sub>sup</sub> small primes.</li>
	 *                                      <li>n<sub>sup</sub> is strictly smaller than &psi;<sub>sup</sub>.</li>
	 *                                      <li>There are not n<sub>total</sub> encoded voting options with <code> 0 < n<sub>total</sub> < n<sub>sup</sub></code>.</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	boolean verifyVotingOptions(final GqGroup encryptionGroup, final GroupVector<PrimeGqElement, GqGroup> smallPrimeGroupMembers,
			final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions) {
		checkNotNull(encryptionGroup);
		checkNotNull(smallPrimeGroupMembers);
		checkNotNull(encodedVotingOptions);
		final boolean isStrictlyAscending = IntStream.range(0, encodedVotingOptions.size() - 1)
				.parallel()
				.allMatch(i -> encodedVotingOptions.get(i).getValue().compareTo(encodedVotingOptions.get(i + 1).getValue()) < 0);
		checkArgument(isStrictlyAscending, "The encoded voting options must be in strict ascending order.");
		checkArgument(!encodedVotingOptions.isEmpty(), "The number of encoded voting options must be strictly greater than 0.");
		checkArgument(
				encodedVotingOptions.get(0).getValue().compareTo(BigInteger.valueOf(3)) > 0,
				"The encoded voting options must be strictly greater than 3.");

		// Context.
		final GqGroup p_q_g = encryptionGroup;
		final long n_sup = MAXIMUM_SUPPORTED_NUMBER_OF_VOTING_OPTIONS;
		final long psi_sup = MAXIMUM_SUPPORTED_NUMBER_OF_SELECTIONS;
		checkArgument(n_sup > 0, "The maximum supported number of voting options must be strictly positive. [n_sup: %s]", n_sup);
		checkArgument(psi_sup > 0, "The maximum supported number of selections must be strictly positive. [psi_sup]", psi_sup);

		// Input.
		final GroupVector<PrimeGqElement, GqGroup> p_vector = smallPrimeGroupMembers;
		final GroupVector<PrimeGqElement, GqGroup> p_tilde = encodedVotingOptions;
		final int n_total = p_tilde.size();

		// Cross-checks.
		checkArgument(smallPrimeGroupMembers.getGroup().equals(encodedVotingOptions.getGroup()),
				"The small primes and encoded voting options must have the same group.");
		checkArgument(p_vector.size() == n_sup, "The list of small prime group members must be of size n_sup. [n_sup: %s, size: %s]", n_sup,
				p_vector.size());

		// Require.
		checkArgument(psi_sup <= n_sup,
				"The maximum supported number of selections must not be greater than the maximum supported number of voting options.");
		checkArgument(0 < n_total, "The number of encoded voting options must be strictly greater than 0.");
		checkArgument(n_total <= n_sup,
				"The number of encoded voting options must not be greater than the maximum supported number of voting options.");

		// Operation.
		final GroupVector<PrimeGqElement, GqGroup> p_prime = p_vector.subVector(0, n_total);

		final boolean verifA = p_prime.equals(p_tilde);

		final BigInteger p = p_q_g.getP();
		final boolean verifB = p_vector.stream()
				.skip(n_sup - psi_sup)
				.parallel()
				.reduce(p_q_g.getIdentity(), GqElement::multiply, GqElement::multiply)
				.getValue()
				.compareTo(p) < 0;

		return verifA && verifB;
	}

}