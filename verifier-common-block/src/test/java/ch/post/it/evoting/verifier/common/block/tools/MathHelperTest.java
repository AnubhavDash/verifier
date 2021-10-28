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
package ch.post.it.evoting.verifier.common.block.tools;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.post.it.evoting.verifier.common.block.dto.BilinearMappingParameters;
import ch.post.it.evoting.verifier.common.block.dto.ComputeCommitmentParameters;
import ch.post.it.evoting.verifier.common.block.dto.ComputePhiExponentiationParameters;
import ch.post.it.evoting.verifier.common.block.dto.ComputePhiSchnorrParameters;
import ch.post.it.evoting.verifier.common.block.dto.ComputePlaintextEqualityParameters;
import ch.post.it.evoting.verifier.common.block.dto.ModExpParameters;
import ch.post.it.evoting.verifier.common.block.dto.ModExpProductParameters;
import ch.post.it.evoting.verifier.common.block.dto.ModInvParameters;
import ch.post.it.evoting.verifier.common.block.dto.ProdIncPowParameters;
import ch.post.it.evoting.verifier.common.block.dto.RandomOracleHashParameters;
import ch.post.it.evoting.verifier.common.block.dto.VerifySVPArgumentParameters;
import ch.post.it.evoting.verifier.common.block.dto.VerifyZArgumentParameters;

class MathHelperTest {

	@Test
	void modExpWithSimpleValues() {
		modExpTest("modExpSimpleValues.json");
	}

	@Test
	void modExpWithRealSizeValues() {
		modExpTest("modExpRealSizeValues.json");
	}

	private void modExpTest(String jsonFileName) {
		List<ModExpParameters> modExpParametersList = readValues(jsonFileName, ModExpParameters[].class);
		modExpParametersList.forEach(modExpParameters -> {
			BigInteger output = MathHelper.modExp(modExpParameters.getB(), modExpParameters.getE(), modExpParameters.getM());
			assertEquals(modExpParameters.getId(), modExpParameters.getOutput(), output);
		});
	}

	@Test
	void modExpProductWithSimpleValues() {
		modExpProductTest("modExpProductSimpleValues.json");
	}

	@Test
	void modExpProductWithRealSizeValues() {
		modExpProductTest("modExpProductRealSizeValues.json");
	}

	private void modExpProductTest(String jsonFileName) {
		List<ModExpProductParameters> modExpProductParametersList =
				readValues(jsonFileName, ModExpProductParameters[].class);
		modExpProductParametersList.forEach(modExpProductParameters -> {
			BigInteger output = MathHelper.modExpProduct(
					modExpProductParameters.getB_vec(), modExpProductParameters.getE_vec(), modExpProductParameters.getM());

			assertEquals(modExpProductParameters.getId(), modExpProductParameters.getOutput(), output);
		});
	}

	@Test
	void modInvWithSimpleValues() {
		modInvTest("modInvSimpleValues.json");
	}

	@Test
	void modInvWithRealSizeValues() {
		modInvTest("modInvRealSizeValues.json");
	}

	private void modInvTest(String jsonFileName) {
		List<ModInvParameters> modInvParametersList = readValues(jsonFileName, ModInvParameters[].class);
		modInvParametersList.forEach(modInvParameters -> {
			BigInteger output = MathHelper.modInv(modInvParameters.getB(), modInvParameters.getM());
			assertEquals(modInvParameters.getId(), modInvParameters.getOutput(), output);
		});
	}

	@Test
	void procIncPowTest() {
		final ProdIncPowParameters prodIncPowParameters = readValue("prodIncPow.json", ProdIncPowParameters.class);

		final BigInteger result = MathHelper.prodIncPow(prodIncPowParameters.getEncryptionGroup(), prodIncPowParameters.getA_vec(),
				prodIncPowParameters.getX());

		Assertions.assertEquals(prodIncPowParameters.getOutput(), result);
	}

	@Test
	void computeCommitmentWithSimpleValues() {
		computeCommitmentTest("computeCommitmentSimpleValues.json");
	}

	@Test
	void computeCommitmentWithRealSizeValues() {
		computeCommitmentTest("computeCommitmentRealSizeValues.json");
	}

	private void computeCommitmentTest(String jsonFileName) {
		List<ComputeCommitmentParameters> computeCommitmentParametersList =
				readValues(jsonFileName, ComputeCommitmentParameters[].class);
		computeCommitmentParametersList.forEach(computeCommitmentParameters -> {
			BigInteger output = MathHelper.computeCommitment(
					computeCommitmentParameters.getEg(),
					computeCommitmentParameters.getR(),
					computeCommitmentParameters.getA_vec(),
					computeCommitmentParameters.getCk()
			);

			assertEquals(computeCommitmentParameters.getId(), computeCommitmentParameters.getOutput(), output);
		});
	}

	@Test
	void computePhiSchnorrWithSimpleValues() {
		computePhiSchnorrTest("computePhiSchnorrSimpleValues.json");
	}

	@Test
	void computePhiSchnorrWithRealSizeValues() {
		computePhiSchnorrTest("computePhiSchnorrRealSizeValues.json");
	}

	private void computePhiSchnorrTest(String jsonFileName) {
		List<ComputePhiSchnorrParameters> computePhiSchnorrParametersList =
				readValues(jsonFileName, ComputePhiSchnorrParameters[].class);
		computePhiSchnorrParametersList.forEach(computePhiSchnorrParameters -> {
			BigInteger output = MathHelper.computePhiSchnorr(
					computePhiSchnorrParameters.getEg(),
					computePhiSchnorrParameters.getX()
			);

			assertEquals(computePhiSchnorrParameters.getId(), computePhiSchnorrParameters.getOutput(), output);
		});
	}

	@Test
	void computePhiExponentiationSimpleValues() {
		computePhiExponentiationTest("computePhiExponentiationSimpleValues.json");
	}

	@Test
	void computePhiExponentiationRealSizeValues() {
		computePhiExponentiationTest("computePhiExponentiationRealSizeValues.json");
	}

	private void computePhiExponentiationTest(String jsonFileName) {
		List<ComputePhiExponentiationParameters> computePhiExponentiationParameters = readValues(jsonFileName,
				ComputePhiExponentiationParameters[].class);
		computePhiExponentiationParameters.forEach(cpep -> {
			List<BigInteger> output = MathHelper.computePhiExponentiation(
					cpep.getEg(),
					cpep.getG_vec(),
					cpep.getX()
			);
			assertEquals(cpep.getId(), cpep.getOutput_vec(), output);
		});
	}

	@Test
	void computePlaintextEqualityWithSimpleValues() {
		computePlaintextEqualityTest("computePlaintextEqualitySimpleValues.json");
	}

	@Test
	void computePlaintextEqualityWithRealSizeValues() {
		computePlaintextEqualityTest("computePlaintextEqualityRealSizeValues.json");
	}

	private void computePlaintextEqualityTest(String jsonFileName) {
		final List<ComputePlaintextEqualityParameters> computePlaintextEqualityParameters =
				readValues(jsonFileName, ComputePlaintextEqualityParameters[].class);
		computePlaintextEqualityParameters.forEach(cpep -> {
			final List<BigInteger> output = MathHelper.computePhiPlaintextEquality(
					cpep.getEg(),
					cpep.getH_vec(),
					cpep.getH_vec_bar(),
					cpep.getR(),
					cpep.getR_bar()
			);
			assertEquals(cpep.getId(), cpep.getOutput_vec(), output);
		});
	}

	@Test
	void bilinearMappingTest() {
		final BilinearMappingParameters bilinearMappingParameters = readValue("bilinearMap.json", BilinearMappingParameters.class);
		final BigInteger result = MathHelper.bilinearMapping(bilinearMappingParameters.getEg(), bilinearMappingParameters.getA_vec(),
				bilinearMappingParameters.getB_vec(), bilinearMappingParameters.getY());

		assertTrue(MathHelper.areEqual(result, bilinearMappingParameters.getOutput()));
	}

	@Test
	void reverseAndJoinTest() {
		Assertions.assertEquals("CBA", MathHelper.reverseAndJoin(Arrays.asList("A", "B", "C")));
	}

	@Test
	void randomOracleHash() throws Exception {
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		final List<RandomOracleHashParameters> randomOracleHashParameters = readValues("randomOracleHashValues.json",
				RandomOracleHashParameters[].class);
		for (RandomOracleHashParameters rohp : randomOracleHashParameters) {
			final BigInteger output = MathHelper.randomOracleHash(MessageDigest.getInstance("SHA-256", "BC"),
					rohp.getX().getBytes(StandardCharsets.UTF_8),
					BigInteger.ZERO, rohp.getQ());
			Assertions.assertEquals(rohp.getOutput(), output);
		}
	}

	@Test
	void verifySVPArgumentTest() {
		final List<VerifySVPArgumentParameters> verifySVPArgumentParameters = readValues("svp-argument.json",
				VerifySVPArgumentParameters[].class);

		for (VerifySVPArgumentParameters svpap : verifySVPArgumentParameters) {
			final boolean result = MathHelper.verifySVPArgument(svpap.getEg(), svpap.getCk(), svpap.getPk_mix(), svpap.getStatement(),
					svpap.getArgument());

			assertTrue(result, "Assertion failed for data id: " + svpap.getId());
		}
	}

	@Test
	void zeroArgumentTest() {
		final VerifyZArgumentParameters zArgumentParameters = readValue("z-argument.json", VerifyZArgumentParameters.class);

		final boolean result = MathHelper.verifyZArgument(zArgumentParameters.getEg(), zArgumentParameters.getCk(),
				zArgumentParameters.getPk_mix(),
				zArgumentParameters.getM(), zArgumentParameters.getN(), zArgumentParameters.getStatement(),
				zArgumentParameters.getArgument());

		assertTrue(result);
	}

	// =====================================================================================================================================
	// Utility methods.
	// =====================================================================================================================================

	private <T> List<T> readValues(String jsonFileName, Class<T[]> clazz) {
		return Arrays.asList(readValue(jsonFileName, clazz));
	}

	private <T> T readValue(String jsonFileName, Class<T> clazz) {
		try {
			Path jsonPath = Paths.get(getClass().getResource("/MathHelperTest/" + jsonFileName).toURI());
			InputStream is = Files.newInputStream(jsonPath);
			ObjectMapper jsonMapper = new ObjectMapper();
			return jsonMapper.readValue(is, clazz);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Read values failed for file " + jsonFileName + ". " + e.getMessage());
		}
	}

	private void assertEquals(String id, BigInteger expected, BigInteger actual) {
		assertTrue(MathHelper.areEqual(expected, actual), "Error in dataset for id : " + id);
	}

	private void assertEquals(String id, List<BigInteger> expected_vec, List<BigInteger> actual_vec) {
		int index = 0;
		for (BigInteger expected : expected_vec) {
			assertEquals(id, expected, actual_vec.get(index++));
		}
	}

}
