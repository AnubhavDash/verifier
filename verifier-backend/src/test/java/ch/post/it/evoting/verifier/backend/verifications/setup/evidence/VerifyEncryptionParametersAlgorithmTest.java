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
		environmentVariables.set("SECURITY_LEVEL", "EXTENDED");
	}

	@BeforeEach
	void setup() {

		p = new BigInteger(
				"BFF67CCCAE0F61B38BA70AD736CFA8EA284B5D6CAEBF2FED2FC88D0ADFF9E2B220BFD9CCDA59BD3BD52B12CDFCCF41AA3D9BF81F95A7D59452690BF45F7993BE760ABBCA3E29705D473A66638DCD6EA78663C0DB91E3E0AB1DFE1AFF25181D4D2C3BA059F9131D95D37F431233EA2276E052C960DCB130F9DFFDC0BE977C9947E7AE05EA516AA81B2528FEF03625ACFCF495C3AB5D5F176E06F1382AE96A470321092C0C1C02A196AB4DA20D3605B4E72A5CFD16CF9381C83513EBD18A8A4A21BF95B864EDA4C0214583E99A3180F7A561F19D451BC4354E7A284DC7EB0C5A05DC58856C6DC8CF3A57B42D866D85F453D1BD8CC61117FB606A40AF0A0EF76D603C7A307C0B8854355D5836774C6BB12238E09806782A487BB9888AE1DB54DECA3FEC374D30CC9A722D3052585069D212B62FD6758710337CA17411E82FF7E7E7B754F4C9F3A1C49AA15E0D0A0E9B05A2EA880216D052B780E68168CA336309D3C1802A278AFCF1C0F8FA3381C145DA0864892221B960ECD6D46165E057B55EEB",
				16);
		q = new BigInteger(
				"5FFB3E665707B0D9C5D3856B9B67D4751425AEB6575F97F697E446856FFCF159105FECE66D2CDE9DEA958966FE67A0D51ECDFC0FCAD3EACA293485FA2FBCC9DF3B055DE51F14B82EA39D3331C6E6B753C331E06DC8F1F0558EFF0D7F928C0EA6961DD02CFC898ECAE9BFA18919F5113B702964B06E58987CEFFEE05F4BBE4CA3F3D702F528B5540D92947F781B12D67E7A4AE1D5AEAF8BB703789C1574B52381908496060E0150CB55A6D1069B02DA73952E7E8B67C9C0E41A89F5E8C5452510DFCADC3276D26010A2C1F4CD18C07BD2B0F8CEA28DE21AA73D1426E3F5862D02EE2C42B636E4679D2BDA16C336C2FA29E8DEC663088BFDB035205785077BB6B01E3D183E05C42A1AAEAC1B3BA635D8911C704C033C15243DDCC44570EDAA6F651FF61BA698664D391698292C2834E9095B17EB3AC38819BE50BA08F417FBF3F3DBAA7A64F9D0E24D50AF0685074D82D17544010B68295BC07340B46519B184E9E0C01513C57E78E07C7D19C0E0A2ED0432449110DCB0766B6A30B2F02BDAAF75",
				16);
		encryptionGroup = new GqGroup(p, q, BigInteger.valueOf(3));
		g = encryptionGroup.getGenerator();
		seed = "31";
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
		final String badSeed = "35";
		assertFalse(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, badSeed));
	}

	@Test
	@DisplayName("valid input returns true")
	void validInput() {
		assertTrue(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, seed));
	}
}
