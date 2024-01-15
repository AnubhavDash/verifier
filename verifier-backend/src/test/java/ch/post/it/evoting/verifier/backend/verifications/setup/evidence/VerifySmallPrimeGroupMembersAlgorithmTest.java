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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement.PrimeGqElementFactory;
import ch.post.it.evoting.evotinglibraries.domain.VotingOptionsConstants;

class VerifySmallPrimeGroupMembersAlgorithmTest {

	private final VerifySmallPrimeGroupMembersAlgorithm verifySmallPrimeGroupMembersAlgorithm = new VerifySmallPrimeGroupMembersAlgorithm();

	private GroupVector<PrimeGqElement, GqGroup> primes;

	@BeforeEach
	void setup() {
		final GqGroup gqGroup = new GqGroup(BigInteger.valueOf(181358268525299L), BigInteger.valueOf(90679134262649L), BigInteger.valueOf(3));

		primes = PrimeGqElementFactory.getSmallPrimeGroupMembers(gqGroup, VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS + 1);
	}

	@Test
	@DisplayName("null argument throws NullPointerException")
	void nullArgumentThrows() {
		assertThrows(NullPointerException.class, () -> verifySmallPrimeGroupMembersAlgorithm.verifySmallPrimeGroupMembers(null));
	}

	@Test
	@DisplayName("wrong primes fails")
	void wrongPrimesFails() {
		final GroupVector<PrimeGqElement, GqGroup> wrongPrimes = primes.subVector(1, VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS + 1);
		assertFalse(verifySmallPrimeGroupMembersAlgorithm.verifySmallPrimeGroupMembers(wrongPrimes));
	}

	@Test
	@DisplayName("too many primes fails")
	void tooManyPrimesFails() {
		final GroupVector<PrimeGqElement, GqGroup> tooManyPrimes = primes.append(primes.get(0));
		assertThrows(IllegalArgumentException.class,
				() -> verifySmallPrimeGroupMembersAlgorithm.verifySmallPrimeGroupMembers(tooManyPrimes));
	}

	@Test
	@DisplayName("wrong order primes fails")
	void wrongOrderPrimesFails() {
		final GroupVector<PrimeGqElement, GqGroup> wrongOrderPrimes = primes.prepend(primes.get(0))
				.subVector(0, VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS);
		assertThrows(IllegalArgumentException.class, () -> verifySmallPrimeGroupMembersAlgorithm.verifySmallPrimeGroupMembers(wrongOrderPrimes));
	}

	@Test
	@DisplayName("valid input returns true")
	void validInput() {
		assertTrue(verifySmallPrimeGroupMembersAlgorithm.verifySmallPrimeGroupMembers(
				primes.subVector(0, VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS)));
	}

}
