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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.domain.VotingOptionsConstants;

@Service
public class VerifySmallPrimeGroupMembersAlgorithm {

	/**
	 * Verifies that the given prime numbers correspond to the small prime group members.
	 *
	 * @param smallPrimeGroupMembers <b>p</b>, a list of the &omega; smallest prime group members strictly greater than 3, in ascending order.
	 * @return true if the given prime numbers are indeed the smallest &omega; prime group members, false otherwise
	 */
	@SuppressWarnings("java:S117")
	public boolean verifySmallPrimeGroupMembers(final GroupVector<PrimeGqElement, GqGroup> smallPrimeGroupMembers) {
		checkNotNull(smallPrimeGroupMembers);

		final int omega = VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS;

		final GqGroup group = smallPrimeGroupMembers.getGroup();
		checkArgument(smallPrimeGroupMembers.size() == omega,
				String.format("The list of small prime group members must contain omega elements. [omega: %d]", omega));
		checkArgument(
				smallPrimeGroupMembers.stream()
						.parallel()
						.map(PrimeGqElement::getValue)
						.allMatch(primeValue -> BigInteger.valueOf(3).compareTo(primeValue) < 0),
				"The small prime group members must be strictly greater than 3.");
		checkArgument(IntStream.range(1, omega)
				.parallel()
				.mapToObj(i -> smallPrimeGroupMembers.get(i - 1).getValue().compareTo(smallPrimeGroupMembers.get(i).getValue()) < 0)
				.allMatch(Boolean::booleanValue), "The list of small prime group members must be sorted in ascending order");
		final GroupVector<PrimeGqElement, GqGroup> p = smallPrimeGroupMembers;

		// Operation
		final GroupVector<PrimeGqElement, GqGroup> p_prime = PrimeGqElement.PrimeGqElementFactory.getSmallPrimeGroupMembers(group, omega);
		return p_prime.equals(p);
	}
}
