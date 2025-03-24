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
package ch.post.it.evoting.verifier.backend.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.evotinglibraries.domain.UUIDGenerator;

@DisplayName("DatasetConfigurationTally with")
class DatasetConfigurationTallyTest {

	private final UUIDGenerator uuidGenerator = UUIDGenerator.getInstance();
	private final String electionEventId = uuidGenerator.generate();
	private final String filename = String.format("dataset-tally-%s.zip", electionEventId);
	private final String hash = "DC:D5:9D:15:4C:AB:F3:09:17:25:A1:55:F8:07:E6:DD:10:F5:F6:70:4D:28:5F:77:A9:79:BB:E1:0A:DD:D6:9C";
	private final int numberOfConfirmedNonTestVotes = 0;
	private final int numberOfConfirmedTestVotes = 10;

	@Test
	@DisplayName("null parameters throws NullPointerException")
	void nullParametersThrows() {
		assertAll(
				() -> assertThrows(NullPointerException.class,
						() -> new DatasetConfigurationTally(null, hash, numberOfConfirmedNonTestVotes, numberOfConfirmedTestVotes)),
				() -> assertThrows(NullPointerException.class,
						() -> new DatasetConfigurationTally(filename, null, numberOfConfirmedNonTestVotes, numberOfConfirmedNonTestVotes))
		);
	}

	@Test
	@DisplayName("negative number of confirmed non test votes throws IllegalArgumentException")
	void negativeNumberOfConfirmedNonTestVotesThrows() {
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new DatasetConfigurationTally(filename, hash, -2, numberOfConfirmedTestVotes));

		assertEquals("The number of confirmed non test votes must be positive.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("negative number of confirmed test votes throws IllegalArgumentException")
	void negativeNumberOfConfirmedTestVotesThrows() {
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> new DatasetConfigurationTally(filename, hash, numberOfConfirmedNonTestVotes, -2));

		assertEquals("The number of confirmed non test votes must be positive.", Throwables.getRootCause(exception).getMessage());
	}
}