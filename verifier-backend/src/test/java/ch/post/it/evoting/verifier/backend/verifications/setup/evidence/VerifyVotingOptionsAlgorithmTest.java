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

import static ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement.PrimeGqElementFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupElement;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.domain.VotingOptionsConstants;

@DisplayName("VerifyVotingOptionsAlgorithm calling verifyVotingOptions with")
class VerifyVotingOptionsAlgorithmTest {

	private final int n_sup = VotingOptionsConstants.MAXIMUM_SUPPORTED_NUMBER_OF_VOTING_OPTIONS;
	private final VerifyVotingOptionsAlgorithm verifyVotingOptionsAlgorithm = new VerifyVotingOptionsAlgorithm();

	private GroupVector<PrimeGqElement, GqGroup> primes;
	private GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions;

	@BeforeEach
	void setup() {
		final GqGroup gqGroup = new GqGroup(BigInteger.valueOf(59), BigInteger.valueOf(29), BigInteger.valueOf(3));

		final GroupVector<PrimeGqElement, GqGroup> testPrimes = PrimeGqElementFactory.getSmallPrimeGroupMembers(gqGroup, 3);
		primes = spy(testPrimes);

		final GroupVector<PrimeGqElement, GqGroup> testEncodedVotingOptions = PrimeGqElementFactory.getSmallPrimeGroupMembers(gqGroup, 3);
		encodedVotingOptions = spy(testEncodedVotingOptions);
	}

	@Test
	@DisplayName("any null argument throws NullPointerException")
	void nullArgumentsThrows() {
		assertThrows(NullPointerException.class, () -> verifyVotingOptionsAlgorithm.verifyVotingOptions(null, encodedVotingOptions));
		assertThrows(NullPointerException.class, () -> verifyVotingOptionsAlgorithm.verifyVotingOptions(primes, null));
	}

	@Test
	@DisplayName("encoded voting options not in ascending order throws IllegalArgumentException")
	void encodedVotingOptionsNotInAscendingOrder() {
		final GroupVector<PrimeGqElement, GqGroup> reversedEncodedVotingOptions = encodedVotingOptions.stream()
				.sorted(Collections.reverseOrder(Comparator.comparing(GroupElement::getValue)))
				.collect(GroupVector.toGroupVector());

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> verifyVotingOptionsAlgorithm.verifyVotingOptions(primes, reversedEncodedVotingOptions));
		assertEquals("The encoded voting options must be in strict ascending order.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("encoded voting options with duplicates throws IllegalArgumentException")
	void encodedVotingOptionsWithDuplicates() {
		final GroupVector<PrimeGqElement, GqGroup> duplicateEncodedVotingOptions = encodedVotingOptions.append(
				encodedVotingOptions.get(encodedVotingOptions.size() - 1));

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> verifyVotingOptionsAlgorithm.verifyVotingOptions(primes, duplicateEncodedVotingOptions));
		assertEquals("The encoded voting options must be in strict ascending order.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("encoded voting options not strictly greater than 3 throws IllegalArgumentException")
	void encodedVotingOptionsNotStrictlyGreaterThan3() {
		final GqGroup otherGqGroup = new GqGroup(BigInteger.valueOf(47), BigInteger.valueOf(23), BigInteger.valueOf(2));

		final GroupVector<PrimeGqElement, GqGroup> otherSmallPrimeGroupMembers = PrimeGqElementFactory.getSmallPrimeGroupMembers(otherGqGroup, 1);

		final GroupVector<PrimeGqElement, GqGroup> otherEncodedVotingOptions = GroupVector.of(
				PrimeGqElementFactory.fromValue(3, otherGqGroup));

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> verifyVotingOptionsAlgorithm.verifyVotingOptions(otherSmallPrimeGroupMembers, otherEncodedVotingOptions));
		assertEquals("The encoded voting options must be strictly greater than 3.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("primes and options with different group throws IllegalArgumentException")
	void primesAndOptionsWithDifferentGroup() {
		final GqGroup otherGqGroup = new GqGroup(BigInteger.valueOf(23), BigInteger.valueOf(11), BigInteger.valueOf(3));
		final GroupVector<PrimeGqElement, GqGroup> otherSmallPrimeGroupMembers = PrimeGqElementFactory.getSmallPrimeGroupMembers(otherGqGroup, 1);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> verifyVotingOptionsAlgorithm.verifyVotingOptions(otherSmallPrimeGroupMembers, encodedVotingOptions));
		assertEquals("The small primes and encoded voting options must have the same group.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("list of primes of different size than n_sup throws IllegalArgumentException")
	void listOfPrimesSizeDifferentNSupThrows() {
		when(primes.size()).thenReturn(n_sup - 1);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> verifyVotingOptionsAlgorithm.verifyVotingOptions(primes, encodedVotingOptions));
		assertEquals(String.format("The list of small prime group members must be of size n_sup. [n_sup: %s, size: %s]", n_sup, n_sup - 1),
				Throwables.getRootCause(exception).getMessage());

		when(primes.size()).thenReturn(n_sup + 1);

		exception = assertThrows(IllegalArgumentException.class,
				() -> verifyVotingOptionsAlgorithm.verifyVotingOptions(primes, encodedVotingOptions));
		assertEquals(String.format("The list of small prime group members must be of size n_sup. [n_sup: %s, size: %s]", n_sup, n_sup + 1),
				Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("encoded options of size zero throws IllegalArgumentException")
	void encodedVotingOfOptionsSizeZeroThrows() {
		when(encodedVotingOptions.size()).thenReturn(0);
		when(primes.size()).thenReturn(n_sup);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> verifyVotingOptionsAlgorithm.verifyVotingOptions(primes, encodedVotingOptions));
		assertEquals("The number of encoded voting options must be strictly greater than 0.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("encoded voting options of size greater n_sup throws IllegalArgumentException")
	void encodedVotingOptionsOfSizeGreaterNSupThrows() {
		final int originalSize = encodedVotingOptions.size();
		when(encodedVotingOptions.size()).thenReturn(originalSize, n_sup + 1);
		when(primes.size()).thenReturn(n_sup);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> verifyVotingOptionsAlgorithm.verifyVotingOptions(primes, encodedVotingOptions));
		assertEquals("The number of encoded voting options must not be greater than the maximum supported number of voting options.",
				Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("valid input returns true")
	void validInput() {
		when(primes.size()).thenReturn(n_sup);

		assertTrue(verifyVotingOptionsAlgorithm.verifyVotingOptions(primes, encodedVotingOptions));
	}

}