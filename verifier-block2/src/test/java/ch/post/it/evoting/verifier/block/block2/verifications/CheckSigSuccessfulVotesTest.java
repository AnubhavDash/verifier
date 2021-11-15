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
package ch.post.it.evoting.verifier.block.block2.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.base.Throwables;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.block.exceptions.VerificationPreconditionException;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.event.PreDecryptionEvent;
import ch.post.it.evoting.verifier.common.event.VerificationResultEvent;

class CheckSigSuccessfulVotesTest extends Block2VerificationTest {

	@BeforeAll
	static void setUpAll() {
		verification = new CheckSigSuccessfulVotes(pathService, applicationEventPublisherMock);
	}

	@Test
	void executeTestOK() throws Exception {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/OK").toURI()).toString();
		final VerificationResultEvent resultEvent = verification.verify(new PreDecryptionEvent(this, inputDirectory));

		final var expectedResultEvent = VerificationResultEvent.success(this, verification.getVerificationDefinition());
		assertEquals(expectedResultEvent, resultEvent);
	}

	@Test
	void executeTestNOKInvalidSignature() throws URISyntaxException {
		final String inputDirectory = Paths.get(getClass().getResource("/CheckSigSuccessfulVotesTest/NOK").toURI()).toString();
		final var event = new PreDecryptionEvent(this, inputDirectory);
		final VerificationResultEvent resultEvent = verification.verify(event);

		final var expectedResultEvent = VerificationResultEvent.failure(this, verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "verification71.nok.message"));
		assertEquals(expectedResultEvent, resultEvent);
	}

	private static Stream<Arguments> nokArgumentProvider() throws URISyntaxException {
		return Stream.of(
				Arguments.of(getInputDirectory("/CheckSigSuccessfulVotesTest/NOK-NOFILE"), UncheckedIOException.class, "successfulVotes.csv"),
				Arguments.of(getInputDirectory("/CheckSigSuccessfulVotesTest/NOK-NOFILE2"), UncheckedIOException.class, "ballotBox.json"),
				Arguments.of(getInputDirectory("/CheckSigSuccessfulVotesTest/NOK-NOFILE3"), UncheckedIOException.class,
						"[eE]lectionInformationContents.json"),
				Arguments.of(getInputDirectory("/CheckSigSuccessfulVotesTest/NOK-NOCERT"), VerificationPreconditionException.class,
						formatMessage(CheckSigSuccessfulVotes.BALLOT_BOX_CERT)),
				Arguments.of(getInputDirectory("/CheckSigSuccessfulVotesTest/NOK-NOCERT2"), VerificationPreconditionException.class,
						formatMessage(CheckSigSuccessfulVotes.SERVICES_CA)),
				Arguments.of(getInputDirectory("/CheckSigSuccessfulVotesTest/NOK-NOCERT3"), VerificationPreconditionException.class,
						formatMessage(CheckSigSuccessfulVotes.ELECTION_ROOT_CA))
		);
	}

	private static String getInputDirectory(final String directory) throws URISyntaxException {
		return Paths.get(CheckSigSuccessfulVotesTest.class.getResource(directory).toURI()).toString();
	}

	private static String formatMessage(final String ballotBoxCert) {
		return String.format("%s certificate is missing!", ballotBoxCert);
	}

	@ParameterizedTest(name = "{2}")
	@MethodSource("nokArgumentProvider")
	void executeTestNOKFileNotFound(final String inputDirectory, final Class<RuntimeException> expectedException,
			final String expectedExceptionMessage) {

		final var event = new PreDecryptionEvent(this, inputDirectory);

		final var exception = assertThrows(expectedException, () -> verification.verify(event));
		assertTrue(Throwables.getRootCause(exception).getMessage().contains(expectedExceptionMessage));
	}
}
