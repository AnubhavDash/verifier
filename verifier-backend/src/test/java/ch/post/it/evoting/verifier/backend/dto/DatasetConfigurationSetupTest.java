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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.math.Base16Alphabet;
import ch.post.it.evoting.cryptoprimitives.math.Random;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.evotinglibraries.domain.common.Constants;

@DisplayName("DatasetConfigurationSetup with")
class DatasetConfigurationSetupTest {

	private final Random random = RandomFactory.createRandom();
	private final String electionEventId = random.genRandomString(Constants.ID_LENGTH, Base16Alphabet.getInstance());
	private final String filename = String.format("dataset-setup-%s.zip", electionEventId);
	private final String hash = "DC:D5:9D:15:4C:AB:F3:09:17:25:A1:55:F8:07:E6:DD:10:F5:F6:70:4D:28:5F:77:A9:79:BB:E1:0A:DD:D6:9C";

	@Test
	@DisplayName("null parameters throws NullPointerException")
	void nullParametersThrows() {
		assertAll(
				() -> assertThrows(NullPointerException.class, () -> new DatasetConfigurationSetup(null, hash)),
				() -> assertThrows(NullPointerException.class, () -> new DatasetConfigurationSetup(filename, null))
		);
	}
}