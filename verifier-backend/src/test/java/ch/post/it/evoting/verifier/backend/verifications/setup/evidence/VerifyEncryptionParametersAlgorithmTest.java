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
import static org.mockito.Mockito.mockStatic;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamal;
import ch.post.it.evoting.cryptoprimitives.elgamal.ElGamalFactory;
import ch.post.it.evoting.cryptoprimitives.internal.securitylevel.SecurityLevelConfig;
import ch.post.it.evoting.cryptoprimitives.internal.securitylevel.SecurityLevelInternal;
import ch.post.it.evoting.cryptoprimitives.math.GqElement;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;

class VerifyEncryptionParametersAlgorithmTest {

	private final ElGamal elGamal = ElGamalFactory.createElGamal();
	private final VerifyEncryptionParametersAlgorithm verifyEncryptionParametersAlgorithm = new VerifyEncryptionParametersAlgorithm(elGamal);

	private GqGroup encryptionGroup;
	private BigInteger p;
	private BigInteger q;
	private GqElement g;
	private String seed;

	@BeforeEach
	void setup() {

		p = new BigInteger(
				"ac2feb59d9c6d336e70e4f93932631f70116b079e6601592bc95a47551a7e395d45492186e516d36b9040910e9e7cef9c691f9db0d6371f3dac38fcf605d060a4446658db2383a1ddf3e4fd9bd88e2674a21d5e3af62ba88da688dd10a55ee9bf59f16bc9567dc2491d3997d31bfc77cb2d876a192708c8ead6ea72fb166159062afae92357f641500681a5c9187314c3b92103bd0f7ba909246f7d81d4f0c9d49929e5704cf069b8cfa3e1378a7c7a11acd1bdaf400f895216ae3cae06e8ac058d6b75ea635cd047d29fcd273cad1cb9d2716b148e4809366733a15904053ef28d169827ad6ef313a64a96cbf4841e84012893632dc9dcc82c3d9077f6d7faf5e82ea3f00bd71512795276be9bec16f0270ac61432c3bc9202f20dd382945d0b3547230e84d2a9889969c695e1f0a5143a17665be5e9658c969d903fa35d98e91502d30335161b4177abd3462800a592ef8ffb1ec59d1e1199b044f52c3cf49f4d279e1562d933fdb1842d691abcb4711f438b5d03e966aa027e16eab253ed7",
				16);
		q = new BigInteger(
				"5617f5acece3699b738727c9c99318fb808b583cf3300ac95e4ad23aa8d3f1caea2a490c3728b69b5c82048874f3e77ce348fced86b1b8f9ed61c7e7b02e8305222332c6d91c1d0eef9f27ecdec47133a510eaf1d7b15d446d3446e8852af74dfacf8b5e4ab3ee1248e9ccbe98dfe3be596c3b50c938464756b75397d8b30ac83157d7491abfb20a80340d2e48c398a61dc9081de87bdd4849237bec0ea7864ea4c94f2b8267834dc67d1f09bc53e3d08d668ded7a007c4a90b571e5703745602c6b5baf531ae6823e94fe6939e568e5ce938b58a4724049b3399d0ac82029f79468b4c13d6b77989d3254b65fa420f42009449b196e4ee64161ec83bfb6bfd7af41751f805eb8a893ca93b5f4df60b781385630a1961de49017906e9c14a2e859aa39187426954c44cb4e34af0f8528a1d0bb32df2f4b2c64b4ec81fd1aecc748a8169819a8b0da0bbd5e9a3140052c977c7fd8f62ce8f08ccd8227a961e7a4fa693cf0ab16c99fed8c216b48d5e5a388fa1c5ae81f4b355013f0b755929f6b",
				16);
		encryptionGroup = new GqGroup(p, q, BigInteger.valueOf(2));
		g = encryptionGroup.getGenerator();
		seed = "11111111";
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
	@DisplayName("wrong seed returns false")
	void wrongSeedFails() {
		final String badSeed = "11111112";
		try (final MockedStatic<SecurityLevelConfig> mocked = mockStatic(SecurityLevelConfig.class)) {
			mocked.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(SecurityLevelInternal.STANDARD);
			assertFalse(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, badSeed));
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
