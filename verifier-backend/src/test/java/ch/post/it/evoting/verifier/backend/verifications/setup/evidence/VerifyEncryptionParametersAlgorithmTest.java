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

import static ch.post.it.evoting.cryptoprimitives.utils.Conversions.byteArrayToInteger;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableByteArray;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.internal.securitylevel.SecurityLevelConfig;
import ch.post.it.evoting.cryptoprimitives.internal.securitylevel.SecurityLevelInternal;
import ch.post.it.evoting.cryptoprimitives.math.Base64;
import ch.post.it.evoting.cryptoprimitives.math.BaseEncodingFactory;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.evotinglibraries.domain.validations.FailedValidationException;

class VerifyEncryptionParametersAlgorithmTest {

	private final ElGamal elGamal = ElGamalFactory.createElGamal();
	private final VerifyEncryptionParametersAlgorithm verifyEncryptionParametersAlgorithm = new VerifyEncryptionParametersAlgorithm(elGamal);
	private final Base64 base64 = BaseEncodingFactory.createBase64();

	private GqGroup encryptionGroup;
	private BigInteger p;
	private BigInteger q;
	private GqElement g;
	private String seed;

	@BeforeEach
	void setup() {
		final ImmutableByteArray bytesP = base64.base64Decode(
				"rnSjVsUQCZqi5wU7hgXvjCwUJ7SBKlx2FGb2/H572foRw1sffLdYhB1aS9pUDmlo5XgOAruXqnKublYuO7v33imQ75x4CH+9jDxtRtpsnv31t2FNxPVT0h5giq25tyy42N79bX1hsbwl/1FFczgBHQ6NQkYckljNnVaqGnJVnYavHdrGb4nVrg1bxOsP02IL1jqdyNPprrO2CkO8mmy1M9lXnGwDBYpBHL5sioHSi0qtudvoM3Me9W5YFdrFWKJ1f2kmSViguvv+2AEcQ2wXEcsRxPSmLKl5S/JrBcBW9uwC78HnnfTsMrwxNZlWqZ9dnGbmZ0LE6PTRS+JSIeYPtaWOcWG4DLpSMJpJLSZhvYPgsCuAHwfZ6KNeshrBbFKBJwU1CvuYgRbHgAAo2TtnmfwBASxtooay4vvkoJiNA66PP6EZoE3NHnlFFdybiy8GH39Pyi+ZUauXSWaZ4k6776TFpGNyGR/12wR9lVgHHzvKZsDVr5ASZ3Z7ZwZ4iWdn");
		p = byteArrayToInteger(bytesP);

		final ImmutableByteArray bytesQ = base64.base64Decode(
				"VzpRq2KIBM1Rc4KdwwL3xhYKE9pAlS47CjN7fj897P0I4a2PvlusQg6tJe0qBzS0crwHAV3L1TlXNysXHd377xTId848BD/exh42o202T37627Cm4nqp6Q8wRVbc25ZcbG9+tr6w2N4S/6iiuZwAjodGoSMOSSxmzqtVDTkqzsNXju1jN8Tq1wat4nWH6bEF6x1O5Gn011nbBSHeTTZameyrzjYBgsUgjl82RUDpRaVW3O30GbmPercsCu1irFE6v7STJKxQXX3/bACOIbYLiOWI4npTFlS8pfk1guAre3YBd+Dzzvp2GV4YmsyrVM+uzjNzM6FidHpopfEpEPMH2tLHOLDcBl0pGE0klpMw3sHwWBXAD4Ps9FGvWQ1gtilAk4KahX3MQItjwAAUbJ2zzP4AgJY20UNZcX3yUExGgddHn9CM0Cbmjzyiiu5NxZeDD7+n5RfMqNXLpLNM8Sdd99Ji0jG5DI/67YI+yqwDj53lM2Bq18gJM7s9s4M8RLOz");
		q = byteArrayToInteger(bytesQ);

		encryptionGroup = new GqGroup(p, q, byteArrayToInteger(base64.base64Decode("Ag==")));
		g = encryptionGroup.getGenerator();
		seed = "NE_20231117_TT01";
	}

	@Test
	@DisplayName("any null arguments throws NullPointerException")
	void nullArgumentsThrows() {
		assertThrows(NullPointerException.class, () -> verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(null, q, g, seed));
		assertThrows(NullPointerException.class, () -> verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, null, g, seed));
		assertThrows(NullPointerException.class, () -> verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, null, seed));
		assertThrows(NullPointerException.class, () -> verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, null));
	}

	@Test
	@DisplayName("invalid p throws IllegalArgumentException")
	void invalidPThrows() {
		assertThrows(IllegalArgumentException.class,
				() -> verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(BigInteger.ONE, q, g, seed));
	}

	@Test
	@DisplayName("invalid q throws IllegalArgumentException")
	void invalidQThrows() {
		assertThrows(IllegalArgumentException.class,
				() -> verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, BigInteger.ONE, g, seed));
	}

	@Test
	@DisplayName("wrong g returns false")
	void wrongGFails() {
		try (final MockedStatic<SecurityLevelConfig> mocked = mockStatic(SecurityLevelConfig.class)) {
			mocked.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevelInternal.STANDARD);
			assertFalse(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, encryptionGroup.getIdentity(), seed));
		}
	}

	@Test
	@DisplayName("wrong seed throws FailedValidationException ")
	void wrongSeedFails() {
		final String badSeed = "NE_20231117_XX01";
		try (final MockedStatic<SecurityLevelConfig> mocked = mockStatic(SecurityLevelConfig.class)) {
			mocked.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevelInternal.STANDARD);
			assertThrows(FailedValidationException.class, () -> verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, badSeed));
		}
	}

	@Test
	@DisplayName("valid input returns true")
	void validInput() {
		try (final MockedStatic<SecurityLevelConfig> mocked = mockStatic(SecurityLevelConfig.class)) {
			mocked.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevelInternal.STANDARD);
			assertTrue(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, seed));
		}
	}
}
