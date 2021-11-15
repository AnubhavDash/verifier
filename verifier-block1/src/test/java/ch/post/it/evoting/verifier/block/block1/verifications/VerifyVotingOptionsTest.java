/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.block.block1.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.ImmutableList;

import ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants;
import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class VerifyVotingOptionsTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new VerifyVotingOptions(new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance()),
				applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifyVotingOptionsTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new Block1Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	static Stream<Arguments> getResourcePaths() {
		return Stream.of(
				Arguments.of("/VerifyVotingOptionsTest/NOK-G"),
				Arguments.of("/VerifyVotingOptionsTest/NOK-DUPLICATES"),
				Arguments.of("/VerifyVotingOptionsTest/NOK-SMALLEST"),
				Arguments.of("/VerifyVotingOptionsTest/NOK-PRODUCT")
		);
	}

	@ParameterizedTest
	@MethodSource("getResourcePaths")
	void executeTestNOK(final String resourcePath) throws Exception {
		final String inputDirectory = Paths.get(Objects.requireNonNull(getClass().getResource(resourcePath)).toURI()).toString();
		final var event = new Block1Event(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		assertNotNull(resultEvent);
		assertEquals(Status.NOK, resultEvent.getStatus());
		assertEquals(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification03.nok.message"),
				resultEvent.getMessage());
	}

	@Nested
	class VerifyVotingOptionsAlgorithmTest {

		private final int omega = VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS;
		private BigInteger p;
		private ImmutableList<BigInteger> primes;
		private ImmutableList<BigInteger> encodedVotingOptions;

		@BeforeEach
		void setup() {
			p = BigInteger.valueOf(59);
			final ImmutableList<BigInteger> testPrimes = ImmutableList.of(BigInteger.valueOf(7), BigInteger.valueOf(17), BigInteger.valueOf(37));
			primes = spy(testPrimes);
			final ImmutableList<BigInteger> testEncodedVotingOptions = ImmutableList.of(BigInteger.valueOf(7), BigInteger.valueOf(17));
			encodedVotingOptions = spy(testEncodedVotingOptions);
		}

		@Test
		void verifyVotingOptionsWithNullArgumentsThrows() {
			when(primes.size()).thenReturn(1200);
			when(encodedVotingOptions.size()).thenReturn(25);
			assertThrows(NullPointerException.class,
					() -> ((VerifyVotingOptions) verification).verifyVotingOptions(null, primes, encodedVotingOptions));
			assertThrows(NullPointerException.class, () -> ((VerifyVotingOptions) verification).verifyVotingOptions(p, null, encodedVotingOptions));
			assertThrows(NullPointerException.class, () -> ((VerifyVotingOptions) verification).verifyVotingOptions(p, primes, null));
		}

		@Test
		void verifyVotingOptionsWithListOfPrimesSizeDifferentOmegaThrows() {
			when(encodedVotingOptions.size()).thenReturn(25);
			when(primes.size()).thenReturn(omega - 1);
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
					() -> ((VerifyVotingOptions) verification).verifyVotingOptions(p, primes, encodedVotingOptions));
			assertEquals(String.format("The list of smallest prime group members must contain %d elements", omega), exception.getMessage());

			when(primes.size()).thenReturn(omega + 1);
			exception = assertThrows(IllegalArgumentException.class,
					() -> ((VerifyVotingOptions) verification).verifyVotingOptions(p, primes, encodedVotingOptions));
			assertEquals(String.format("The list of smallest prime group members must contain %d elements", omega), exception.getMessage());
		}

		@Test
		void verifyVotingOptionsOfSizeZeroThrows() {
			when(encodedVotingOptions.size()).thenReturn(0);
			when(primes.size()).thenReturn(1200);
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
					() -> ((VerifyVotingOptions) verification).verifyVotingOptions(p, primes, encodedVotingOptions));
			assertEquals("The number of voting options must be strictly greater than 0", exception.getMessage());
		}

		@Test
		void verifyVotingOptionsOfSizeGreaterOmegaThrows() {
			when(encodedVotingOptions.size()).thenReturn(omega + 1);
			when(primes.size()).thenReturn(omega);
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
					() -> ((VerifyVotingOptions) verification).verifyVotingOptions(p, primes, encodedVotingOptions));
			assertEquals("The number of voting options must not be greater than the supported number of voting options", exception.getMessage());
		}

		@Test
		void verifyVotingOptionsWithValidInput() {
			when(primes.size()).thenReturn(omega);
			assertTrue(((VerifyVotingOptions) verification).verifyVotingOptions(p, primes, encodedVotingOptions));
		}
	}
}
