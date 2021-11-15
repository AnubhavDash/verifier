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

import static ch.post.it.evoting.cryptoprimitives.domain.VotingOptionsConstants.MAXIMUM_NUMBER_OF_VOTING_OPTIONS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Paths;
import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.cryptoprimitives.domain.mapper.DomainObjectMapper;
import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.block.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.event.Block1Event;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class VerifySmallPrimeGroupMembersTest extends Block1VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new VerifySmallPrimeGroupMembers(
				new ElectionDataExtractionService(pathService, DomainObjectMapper.getNewInstance()),
				applicationEventPublisherMock);
	}

	@Test
	void executeTestOk() throws Exception {
		final var inputDirectory = Paths.get(getClass().getResource("/VerifySmallPrimeGroupMembersTest/OK").toURI()).toString();
		VerificationResultEvent resultEvent = verification.verify(new Block1Event(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNokWrongPrimes() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/VerifySmallPrimeGroupMembersTest/NOK_WrongPrimes").toURI()).toString();
		VerificationResultEvent resultEvent =
				verification.verify(new Block1Event(this, inputDirectory));

		final VerificationResultEvent expectedResult = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification02.nok.message"));
		assertEquals(expectedResult, resultEvent);
	}

	@Test
	void executeTestNokTooManyPrimes() throws Exception {
		final var inputDirectory = Paths.get(
				Objects.requireNonNull(getClass().getResource("/VerifySmallPrimeGroupMembersTest/NOK_TooManyPrimes")).toURI()).toString();
		final var block1Event = new Block1Event(this, inputDirectory);
		final var exception = assertThrows(
				IllegalArgumentException.class, () -> verification.verify(block1Event));
		assertEquals(String.format("The list of small prime group members must contain %d elements", MAXIMUM_NUMBER_OF_VOTING_OPTIONS),
				Throwables.getRootCause(exception).getMessage());
	}

	@Test
	void executeTestNokOrder() throws Exception {
		final var inputDirectory = Paths.get(Objects.requireNonNull(getClass().getResource("/VerifySmallPrimeGroupMembersTest/NOK_Order")).toURI())
				.toString();
		final var block1Event = new Block1Event(this, inputDirectory);
		final var exception = assertThrows(IllegalArgumentException.class, () -> verification.verify(block1Event));
		assertEquals("The list of small prime group members must be sorted in ascending order",
				Throwables.getRootCause(exception).getMessage());
	}
}