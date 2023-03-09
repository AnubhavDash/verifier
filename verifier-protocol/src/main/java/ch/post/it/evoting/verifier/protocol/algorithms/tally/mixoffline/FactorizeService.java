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

import static ch.post.it.evoting.cryptoprimitives.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;

import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

public class FactorizeService {

	private FactorizeService() {
		// Do not instantiate
	}

	/**
	 * Factorizes a group element {@code x} into its prime factors. Factorizing the element is efficient since the primes are small and the set of
	 * primes is known (as {@code encodingPrimes}).
	 * <p>
	 * The element and encoding primes must be part of the same group.
	 *
	 * @param x              the element to factorize. Non-null.
	 * @param encodingPrimes the {@code GroupVector} of primes encoding the voting options. Non-empty, group vector of distinct primes from the same
	 *                       group without its generator.
	 * @param psi            the expected number of factors of {@code x}. In the range [1, 120].
	 * @return a group vector of size {@code psi} containing the prime factors, picked from {@code encodingPrimes}, of message {@code x}.
	 */
	public static GroupVector<PrimeGqElement, GqGroup> factorize(final GqElement x, final GroupVector<PrimeGqElement, GqGroup> encodingPrimes,
			final int psi) {
		checkNotNull(x);
		checkNotNull(encodingPrimes);

		checkArgument(!encodingPrimes.isEmpty(), "The encoding primes must not be empty.");
		checkArgument(1 <= psi && psi <= 120, "Psi must be within the bounds [1, 120].");

		// Encoding primes validity checking.
		final GqGroup gqGroup = encodingPrimes.get(0).getGroup();

		checkArgument(hasNoDuplicates(encodingPrimes), "The encoding primes must not contain duplicates.");

		// Cross group checking.
		checkArgument(x.getGroup().equals(gqGroup), "The element x and the encoding primes must be part of the same group.");

		// Algorithm operations.
		final GroupVector<PrimeGqElement, GqGroup> factors = encodingPrimes.stream()
				.filter(pk -> x.getValue().remainder(pk.getValue()).equals(BigInteger.ZERO))
				.collect(GroupVector.toGroupVector());

		final GqElement product = factors.stream().reduce(gqGroup.getIdentity(), GqElement::multiply, GqElement::multiply);
		if (!x.equals(product)) {
			throw new IllegalArgumentException(
					String.format("The message x could not be factorized using the provided encoding primes. [x: %s, encoding primes: %s]", x,
							encodingPrimes));
		}

		if (factors.size() != psi) {
			throw new IllegalArgumentException(
					String.format("The actual number of prime factors does not match the expected number of factors psi. Expected: %d, found: %d",
							psi, factors.size()));
		}

		return factors;
	}

}
