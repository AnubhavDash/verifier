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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence.algorithms;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.MultiplicativeGroupElement;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Implements the VerifyVotingOptions algorithm.
 */
@Service
public class VerifyVotingOptionsAlgorithm {

	/**
	 * Verifies the correctness of the voting options.
	 * <p>
	 * The voting options must correspond to the smallest prime group members and the product of the &phi; biggest voting options must be smaller than
	 * p.
	 *
	 * @param smallPrimeGroupMembers <b>p</b>, a list of the &omega; small prime group members strictly greater than 3.
	 * @param encodedVotingOptions   <b>p</b>Tilde, a list of the voting options encoded as primes.
	 * @return {@code true} if the verification is successful, {@code false} otherwise
	 * @throws NullPointerException     if any input is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>&omega; or &phi; are not strictly positive.</li>
	 *                                      <li>The small primes and encoded voting options do not have the same group.</li>
	 *                                      <li>There is not &omega; small primes.</li>
	 *                                      <li>&omega; is strictly smaller than &phi;.</li>
	 *                                      <li>There is not n encoded voting options with <code> 0 < n < &omega;</code>.</li>
	 *                                  </ul>
	 */
	@SuppressWarnings("java:S117")
	public boolean verifyVotingOptions(final GroupVector<PrimeGqElement, GqGroup> smallPrimeGroupMembers,
			final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions) {

		checkNotNull(smallPrimeGroupMembers);
		checkNotNull(encodedVotingOptions);

		final long omega = VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS;
		final long phi = VotingOptionsConstants.MAXIMUM_NUMBER_OF_SELECTABLE_VOTING_OPTIONS;
		checkArgument(omega > 0, "omega must be strictly positive.");
		checkArgument(phi > 0, "phi must be strictly positive.");

		final boolean isStrictlyAscending = IntStream.range(0, encodedVotingOptions.size() - 1)
				.allMatch(i -> encodedVotingOptions.get(i).getValue().compareTo(encodedVotingOptions.get(i + 1).getValue()) < 0);
		checkArgument(isStrictlyAscending, "The encoded voting options must be in strict ascending order.");

		checkArgument(
				encodedVotingOptions.get(0).getValue().compareTo(BigInteger.valueOf(3)) > 0,
				"The encoded voting options must be strictly greater than 3.");

		checkArgument(smallPrimeGroupMembers.getGroup().equals(encodedVotingOptions.getGroup()),
				"The small primes and encoded voting options must have the same group.");

		final GroupVector<PrimeGqElement, GqGroup> p_vector = smallPrimeGroupMembers;
		final GroupVector<PrimeGqElement, GqGroup> p_tilde = encodedVotingOptions;
		final int n_total = p_tilde.size();

		checkArgument(p_vector.size() == omega, "The list of small prime group members must be of size omega. [omega: %s, size: %s]", omega,
				p_vector.size());

		// Require.
		checkArgument(phi <= omega, "The supported number of selections must not be greater than the supported number of voting options.");
		checkArgument(0 < n_total, "The number of encoded voting options must be strictly greater than 0.");
		checkArgument(n_total <= omega, "The number of encoded voting options must not be greater than the maximum number of voting options.");

		// Operation.
		final GroupVector<PrimeGqElement, GqGroup> p_prime = p_vector.subVector(0, n_total);

		final boolean verifA = p_prime.equals(p_tilde);

		final BigInteger p = p_vector.getGroup().getP();
		final boolean verifB = p_vector.stream()
				.skip(omega - phi)
				.reduce(p_vector.getGroup().getIdentity(), MultiplicativeGroupElement::multiply, MultiplicativeGroupElement::multiply)
				.getValue()
				.compareTo(p) < 0;

		return verifA && verifB;
	}

}