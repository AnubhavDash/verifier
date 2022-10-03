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
				"CE9E0307D2AE75BDBEEC3E0A6E71A279417B56C955C602FFFD067586BACFDAC3BCC49A49EB4D126F5E9255E57C14F3E09492B6496EC8AC1366FC4BB7F678573FA2767E6547FA727FC0E631AA6F155195C035AF7273F31DFAE1166D1805C8522E95F9AF9CE33239BF3B68111141C20026673A6C8B9AD5FA8372ED716799FE05C0BB6EAF9FCA1590BD9644DBEFAA77BA01FD1C0D4F2D53BAAE965B1786EC55961A8E2D3E4FE8505914A408D50E6B99B71CDA78D8F9AF1A662512F8C4C3A9E72AC72D40AE5D4A0E6571135CBBAAE08C7A2AA0892F664549FA7EEC81BA912743F3E584AC2B2092243C4A17EC98DF079D8EECB8B885E6BBAFA452AAFA8CB8C08024EFF28DE4AF4AC710DCD3D66FD88212101BCB412BCA775F94A2DCE18B1A6452D4CF818B6D099D4505E0040C57AE1F3E84F2F8E07A69C0024C05ACE05666A6B63B0695904478487E78CD0704C14461F24636D7A3F267A654EEDCF8789C7F627C72B4CBD54EED6531C0E54E325D6F09CB648AE9185A7BDA6553E40B125C78E5EAA867",
				16);
		q = new BigInteger(
				"674F0183E9573ADEDF761F053738D13CA0BDAB64AAE3017FFE833AC35D67ED61DE624D24F5A68937AF492AF2BE0A79F04A495B24B7645609B37E25DBFB3C2B9FD13B3F32A3FD393FE07318D5378AA8CAE01AD7B939F98EFD708B368C02E429174AFCD7CE71991CDF9DB40888A0E10013339D3645CD6AFD41B976B8B3CCFF02E05DB757CFE50AC85ECB226DF7D53BDD00FE8E06A796A9DD574B2D8BC3762ACB0D47169F27F4282C8A52046A8735CCDB8E6D3C6C7CD78D3312897C6261D4F3956396A0572EA50732B889AE5DD570463D15504497B322A4FD3F7640DD4893A1F9F2C256159049121E250BF64C6F83CEC7765C5C42F35DD7D229557D465C60401277F946F257A563886E69EB37EC4109080DE5A095E53BAFCA516E70C58D32296A67C0C5B684CEA282F002062BD70F9F42797C703D34E0012602D6702B33535B1D834AC8223C243F3C66838260A230F9231B6BD1F933D32A776E7C3C4E3FB13E395A65EAA776B298E072A7192EB784E5B245748C2D3DED32A9F205892E3C72F55433",
				16);
		encryptionGroup = new GqGroup(p, q, BigInteger.valueOf(2));
		g = encryptionGroup.getGenerator();
		seed = "22800";
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
		String badSeed = "65684";
		assertFalse(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, badSeed));
	}

	@Test
	@DisplayName("valid input returns true")
	void validInput() {
		assertTrue(verifyEncryptionParametersAlgorithm.verifyEncryptionParameters(p, q, g, seed));
	}
}
