/*
 * (c) Copyright 2025 Swiss Post Ltd.
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

import static ch.post.it.evoting.evotinglibraries.domain.VotingOptionsConstants.MAXIMUM_SUPPORTED_NUMBER_OF_VOTING_OPTIONS;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

@Service
public class VerifySmallPrimeGroupMembersAlgorithm {

	/**
	 * Verifies that the given prime numbers correspond to the small prime group members.
	 *
	 * @param encryptionGroup        (p, q, g), the encryption group.
	 * @param smallPrimeGroupMembers <b>p</b>, a list of the n<sub>sup</sub> smallest prime group members strictly greater than 3, in ascending
	 *                               order.
	 * @return true if the given prime numbers are indeed the smallest n<sub>sup</sub> prime group members, false otherwise
	 */
	@SuppressWarnings("java:S117")
	boolean verifySmallPrimeGroupMembers(final GqGroup encryptionGroup, final GroupVector<PrimeGqElement, GqGroup> smallPrimeGroupMembers) {
		checkNotNull(encryptionGroup);
		checkNotNull(smallPrimeGroupMembers);

		// Context.
		final GqGroup p_q_g = encryptionGroup;
		final int n_sup = MAXIMUM_SUPPORTED_NUMBER_OF_VOTING_OPTIONS;

		// Input.
		final GroupVector<PrimeGqElement, GqGroup> p_vector = smallPrimeGroupMembers;

		// Cross-checks.
		checkArgument(p_vector.size() == n_sup,
				String.format("The list of small prime group members must contain n_sup elements. [n_sup: %d]", n_sup));
		checkArgument(IntStream.range(1, n_sup)
				.parallel()
				.mapToObj(i -> p_vector.get(i - 1).getValue().compareTo(p_vector.get(i).getValue()) < 0)
				.allMatch(Boolean::booleanValue), "The list of small prime group members must be sorted in ascending order");
		checkArgument(p_vector.get(0).getValue().compareTo(BigInteger.valueOf(3)) > 0,
				"The small prime group members must be strictly greater than 3.");

		// Operation.
		final GroupVector<PrimeGqElement, GqGroup> p_prime = PrimeGqElement.PrimeGqElementFactory.getSmallPrimeGroupMembers(p_q_g, n_sup);

		return p_prime.equals(p_vector);
	}
}
