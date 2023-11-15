/*
 * Copyright 2023 Post CH Ltd
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
package ch.post.it.evoting.verifier.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.base.Throwables;

import ch.post.it.evoting.cryptoprimitives.math.Random;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.evotinglibraries.domain.common.Constants;
import ch.post.it.evoting.evotinglibraries.domain.validations.FailedValidationException;

@DisplayName("DatasetConfigurationContext with")
class DatasetConfigurationContextTest {

	private DatasetConfigurationContext.Builder builder;

	@BeforeEach
	void setUp() {
		final Random random = RandomFactory.createRandom();
		final String electionEventId = random.genRandomBase16String(Constants.ID_LENGTH);
		final String filename = String.format("dataset-context-%s.zip", electionEventId);
		final String hash = "DC:D5:9D:15:4C:AB:F3:09:17:25:A1:55:F8:07:E6:DD:10:F5:F6:70:4D:28:5F:77:A9:79:BB:E1:0A:DD:D6:9C";
		final Map<String, String> aliasesToFingerprints = Map.of(
				"Canton", "F0:C1:0E:F1:AD:67:95:7D:C1:80:4B:F8:81:51:12:70:F1:2A:98:9C:01:61:34:F3:3D:8A:C3:CF:C9:01:6B:0A",
				"Setup Component", "28:8B:B6:3A:1E:17:05:0D:51:03:C8:47:F4:53:E5:04:6D:3F:CC:4E:09:BE:7D:12:B0:D9:26:6A:F5:59:DE:25",
				"Tally Control Component", "E7:0E:D2:94:B5:72:5A:4E:31:6A:12:7C:A5:3E:00:64:54:6B:19:6E:BF:C0:23:B6:0D:8B:C3:31:AD:03:47:B0"
		);

		builder = new DatasetConfigurationContext.Builder()
				.setFilename(filename)
				.setHash(hash)
				.setElectionEventId(electionEventId)
				.setAliasesToFingerprints(aliasesToFingerprints)
				.setElectionEventName("ElectionEvent")
				.setElectionEventSeed("seed")
				.setElectionEventDate("25.11.2022")
				.setNumberOfElections(2)
				.setNumberOfVotes(1)
				.setNumberOfBallots(2)
				.setNumberOfNonTestBallotBoxes(0)
				.setNumberOfTestBallotBoxes(4)
				.setTotalNumberOfAuthorizedNonTestVoters(0)
				.setTotalNumberOfTestVoters(43);
	}

	@Test
	@DisplayName("null filename throws NullPointerException")
	void nullFilenameThrows() {
		builder.setFilename(null);

		assertThrows(NullPointerException.class, builder::build);
	}

	@Test
	@DisplayName("null hash throws NullPointerException")
	void nullHashThrows() {
		builder.setHash(null);

		assertThrows(NullPointerException.class, builder::build);
	}

	@Test
	@DisplayName("null election event id throws NullPointerException")
	void nullElectionEventIdThrows() {
		builder.setElectionEventId(null);

		assertThrows(NullPointerException.class, builder::build);
	}

	@Test
	@DisplayName("null aliases to fingerprints throws NullPointerException")
	void nullAliasesToFingerprintsThrows() {
		builder.setAliasesToFingerprints(null);

		assertThrows(NullPointerException.class, builder::build);
	}

	@Test
	@DisplayName("null election event name throws NullPointerException")
	void nullElectionEventNameThrows() {
		builder.setElectionEventName(null);

		assertThrows(NullPointerException.class, builder::build);
	}

	@Test
	@DisplayName("null election event seed throws NullPointerException")
	void nullElectionEventSeedThrows() {
		builder.setElectionEventSeed(null);

		assertThrows(NullPointerException.class, builder::build);
	}

	@Test
	@DisplayName("null election event date throws NullPointerException")
	void nullElectionEventDateThrows() {
		builder.setElectionEventDate(null);

		assertThrows(NullPointerException.class, builder::build);
	}

	@Test
	@DisplayName("invalid election event id throws FailedValidationException")
	void invalidElectionEventIdThrows() {
		builder.setElectionEventId("invalid id");

		assertThrows(FailedValidationException.class, builder::build);
	}

	@Test
	@DisplayName("empty aliases to fingerprints throws IllegalArgumentException")
	void emptyAliasesToFingerprintsThrows() {
		final Map<String, String> emptyAliasesToFingerprints = Map.of();
		builder.setAliasesToFingerprints(emptyAliasesToFingerprints);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

		assertEquals("The aliases to fingerprints map must not be empty.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("negative number of elections throws IllegalArgumentException")
	void negativeNumberOfElectionsThrows() {
		builder.setNumberOfElections(-2);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

		assertEquals("The number of elections must be positive.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("negative number of votes throws IllegalArgumentException")
	void negativeNumberOfVotesThrows() {
		builder.setNumberOfVotes(-2);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

		assertEquals("The number of votes must be positive.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("negative number of ballots throws IllegalArgumentException")
	void negativeNumberOfBallotsThrows() {
		builder.setNumberOfBallots(-2);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

		assertEquals("The number of ballots must be positive.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("negative number of non test ballot boxes throws IllegalArgumentException")
	void negativeNumberOfNonTestBallotBoxesThrows() {
		builder.setNumberOfNonTestBallotBoxes(-2);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

		assertEquals("The number of non test ballot boxes must be positive.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("negative number of test ballot boxes throws IllegalArgumentException")
	void negativeNumberOfTestBallotBoxesThrows() {
		builder.setNumberOfTestBallotBoxes(-2);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

		assertEquals("The number of test ballot boxes must be positive.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("negative total number of authorized non test voters throws IllegalArgumentException")
	void negativeTotalNumberOfAuthorizedNonTestVotersThrows() {
		builder.setTotalNumberOfAuthorizedNonTestVoters(-2);

		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

		assertEquals("The total number of authorized non test voters must be positive.", Throwables.getRootCause(exception).getMessage());
	}

	@Test
	@DisplayName("negative total number of authorized test voters throws IllegalArgumentException")
	void negativeTotalNumberOfAuthorizedTestVotersThrows() {
		builder.setTotalNumberOfTestVoters(-2);
		
		final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

		assertEquals("The total number of test voters must be positive.", Throwables.getRootCause(exception).getMessage());
	}
}