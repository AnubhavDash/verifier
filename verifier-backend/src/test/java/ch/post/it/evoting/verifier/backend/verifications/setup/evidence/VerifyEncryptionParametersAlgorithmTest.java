/*
 * Copyright 2022 Post CH Ltd
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
package ch.post.it.evoting.verifier.backend.verifications.setup.evidence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;

import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
class VerifyEncryptionParametersAlgorithmTest {

	@SystemStub
	private static EnvironmentVariables environmentVariables;

	private final ElGamal elGamal = ElGamalFactory.createElGamal();
	private final VerifyEncryptionParametersAlgorithm verifyEncryptionParametersAlgorithm = new VerifyEncryptionParametersAlgorithm(elGamal);

	private GqGroup encryptionGroup;
	private BigInteger p;
	private BigInteger q;
	private GqElement g;
	private String seed;

	@BeforeAll
	static void setupAll() {
		environmentVariables.set("SECURITY_LEVEL", "TESTING_ONLY");
	}

	@BeforeEach
	void setup() {
		encryptionGroup = new GqGroup(BigInteger.valueOf(181358268525299L), BigInteger.valueOf(90679134262649L), BigInteger.valueOf(3));
		p = encryptionGroup.getP();
		q = encryptionGroup.getQ();
		g = encryptionGroup.getGenerator();
		seed = "3";
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
		assertFalse(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, encryptionGroup.getIdentity(), seed));
	}

	@Test
	@DisplayName("wrong seed returns false")
	void wrongSeedFails() {
		assertFalse(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, "2"));
	}

	@Test
	@DisplayName("valid input returns true")
	void validInput() {
		assertTrue(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, seed));
	}
}
