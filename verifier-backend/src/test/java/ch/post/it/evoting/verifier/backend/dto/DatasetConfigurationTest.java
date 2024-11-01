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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableMap;
import ch.post.it.evoting.cryptoprimitives.math.Base16Alphabet;
import ch.post.it.evoting.cryptoprimitives.math.Random;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.evotinglibraries.domain.common.Constants;

@DisplayName("DatasetConfiguration with")
class DatasetConfigurationTest {

	private DatasetConfigurationContext context;
	private DatasetConfigurationTally tally;

	@BeforeEach
	void setup() {
		final Random random = RandomFactory.createRandom();
		final String electionEventId = random.genRandomString(Constants.ID_LENGTH, Base16Alphabet.getInstance());
		final String contextFilename = String.format("dataset-context-%s.zip", electionEventId);
		final String hash = "DC:D5:9D:15:4C:AB:F3:09:17:25:A1:55:F8:07:E6:DD:10:F5:F6:70:4D:28:5F:77:A9:79:BB:E1:0A:DD:D6:9C";
		final ImmutableMap<String, String> aliasesToFingerprints = ImmutableMap.of(
				"Canton", "F0:C1:0E:F1:AD:67:95:7D:C1:80:4B:F8:81:51:12:70:F1:2A:98:9C:01:61:34:F3:3D:8A:C3:CF:C9:01:6B:0A",
				"Setup Component", "28:8B:B6:3A:1E:17:05:0D:51:03:C8:47:F4:53:E5:04:6D:3F:CC:4E:09:BE:7D:12:B0:D9:26:6A:F5:59:DE:25",
				"Tally Control Component", "E7:0E:D2:94:B5:72:5A:4E:31:6A:12:7C:A5:3E:00:64:54:6B:19:6E:BF:C0:23:B6:0D:8B:C3:31:AD:03:47:B0"
		);
		context = new DatasetConfigurationContext.Builder()
				.setFilename(contextFilename)
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
				.setTotalNumberOfTestVoters(43)
				.build();

		final String tallyFilename = String.format("dataset-tally-%s.zip", electionEventId);
		final int numberOfConfirmedNonTestVotes = 0;
		final int numberOfConfirmedTestVotes = 10;
		tally = new DatasetConfigurationTally(tallyFilename, hash, numberOfConfirmedNonTestVotes, numberOfConfirmedTestVotes);
	}

	@Test
	@DisplayName("null context throws NullPointerException")
	void nullContextThrows() {
		assertThrows(NullPointerException.class, () -> new DatasetConfiguration(null, tally));
	}

	@Test
	@DisplayName("valid parameters does not throw")
	void validParametersDoesNotThrow() {
		assertDoesNotThrow(() -> new DatasetConfiguration(context, tally));
		assertDoesNotThrow(() -> new DatasetConfiguration(context, null));
	}
}
