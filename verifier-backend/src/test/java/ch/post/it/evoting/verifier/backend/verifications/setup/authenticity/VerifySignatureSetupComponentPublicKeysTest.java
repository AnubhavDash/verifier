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

package ch.post.it.evoting.verifier.backend.verifications.setup.authenticity;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.cryptoprimitives.hashing.HashFactory.createHash;
import static ch.post.it.evoting.cryptoprimitives.math.BaseEncodingFactory.createBase64;
import static ch.post.it.evoting.evotinglibraries.domain.mapper.EncryptionGroupUtils.getEncryptionGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.security.SignatureException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.hashing.Hash;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.evoting.cryptoprimitives.internal.securitylevel.SecurityLevelConfig;
import ch.post.it.evoting.cryptoprimitives.math.Base64;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.test.tools.serialization.JsonData;
import ch.post.it.evoting.cryptoprimitives.test.tools.serialization.TestParameters;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.election.SetupComponentPublicKeys;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.SetupComponentPublicKeysPayload;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifySignatureSetupComponentPublicKeysTest extends SetupVerificationTest {

	private Hash hash;
	private Base64 base64;

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureSetupComponentPublicKeys(resultPublisherServiceMock, electionDataExtractionService,
				datasetSignatureVerification);

		hash = createHash();
		base64 = createBase64();
	}

	@Test
	void testAbsentFileDoesNotVerify() {
		assertThrows(Exception.class, () -> verification.verify(Path.of("")));
	}

	@Test
	void testOK() {
		final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload = electionDataExtractionService.getSetupComponentPublicKeysPayload(
				datasetPath);

		assertTrue(((VerifySignatureSetupComponentPublicKeys) verification).verifySignature(setupComponentPublicKeysPayload));
	}

	@Test
	void testNOK() throws SignatureException {
		final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload = electionDataExtractionService.getSetupComponentPublicKeysPayload(
				datasetPath);

		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(setupComponentPublicKeysPayload,
				ChannelSecurityContextData.setupComponentPublicKeys(setupComponentPublicKeysPayload.getElectionEventId()),
				Alias.SDM_CONFIG);
		setupComponentPublicKeysPayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureSetupComponentPublicKeys) verification).verifySignature(setupComponentPublicKeysPayload));
	}

	@ParameterizedTest
	@MethodSource("jsonFileArgumentProvider")
	@DisplayName("specific values returns expected output")
	void getHashSetupComponentPublicKeysWithSpecificValues(final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload,
			final String hash, final String description) {
		assertEquals(hash, getHashSetupComponentPublicKeysSpec(setupComponentPublicKeysPayload),
				String.format("assertion failed for: %s", description));
	}

	@Test
	@DisplayName("implementation aligned to spec gives same result")
	void getHashSetupComponentPublicKeysAlignment() {
		final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload = electionDataExtractionService.getSetupComponentPublicKeysPayload(
				datasetPath);
		final String expected = base64.base64Encode(hash.recursiveHash(setupComponentPublicKeysPayload));
		assertEquals(expected, getHashSetupComponentPublicKeysSpec(setupComponentPublicKeysPayload));
	}

	private String getHashSetupComponentPublicKeysSpec(final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload) {
		final ImmutableList<HashableList> h_pk = setupComponentPublicKeysPayload.getSetupComponentPublicKeys()
				.combinedControlComponentPublicKeys().stream()
				.map(controlComponentPublicKeys -> {

					final HashableList hashableCcrjPublicKey = HashableList.from(
							controlComponentPublicKeys.ccrjChoiceReturnCodesEncryptionPublicKey().stream()
									.map(pkCCRj_i -> HashableBigInteger.from(pkCCRj_i.getValue()))
									.collect(toImmutableList()));

					final HashableList hashableCcrjSchnorrProofs = HashableList.from(
							controlComponentPublicKeys.ccrjSchnorrProofs().stream()
									.map(h_pi_pkCCR_j_i -> HashableList.of(h_pi_pkCCR_j_i.get_e(), h_pi_pkCCR_j_i.get_z()))
									.collect(toImmutableList()));

					final HashableList hashableCcmjPublicKey = HashableList.from(
							controlComponentPublicKeys.ccmjElectionPublicKey().stream()
									.map(h_ELpk_j_i -> HashableBigInteger.from(h_ELpk_j_i.getValue()))
									.collect(toImmutableList()));

					final HashableList hashableCcmjSchnorrProofs = HashableList.from(
							controlComponentPublicKeys.ccmjSchnorrProofs().stream()
									.map(h_pi_ELpk_j_i -> HashableList.of(h_pi_ELpk_j_i.get_e(), h_pi_ELpk_j_i.get_z()))
									.collect(toImmutableList()));

					// h_pk_j
					return HashableList.of(
							HashableBigInteger.from(BigInteger.valueOf(controlComponentPublicKeys.nodeId())),
							hashableCcrjPublicKey,
							hashableCcrjSchnorrProofs,
							hashableCcmjPublicKey,
							hashableCcmjSchnorrProofs
					);
				}).collect(toImmutableList());

		final HashableList hashableElectoralBoardPublicKey = HashableList.from(
				setupComponentPublicKeysPayload.getSetupComponentPublicKeys().electoralBoardPublicKey().stream()
						.map(EBpk_i -> HashableBigInteger.from(EBpk_i.getValue()))
						.collect(toImmutableList()));

		final HashableList hashableElectoralBoardSchnorrProofs = HashableList.from(
				setupComponentPublicKeysPayload.getSetupComponentPublicKeys().electoralBoardSchnorrProofs().stream()
						.map(pi_EBpk_i -> HashableList.of(pi_EBpk_i.get_e(), pi_EBpk_i.get_z()))
						.collect(toImmutableList()));

		final HashableList hashableElectionPublicKey = HashableList.from(
				setupComponentPublicKeysPayload.getSetupComponentPublicKeys().electionPublicKey().stream()
						.map(ELpk_i -> HashableBigInteger.from(ELpk_i.getValue()))
						.collect(toImmutableList()));

		final HashableList hashableChoiceReturnCodesPublicKey = HashableList.from(
				setupComponentPublicKeysPayload.getSetupComponentPublicKeys().choiceReturnCodesEncryptionPublicKey().stream()
						.map(pkCCR_i -> HashableBigInteger.from(pkCCR_i.getValue()))
						.collect(toImmutableList()));

		final HashableList hPublicKeys = HashableList.of(HashableList.from(h_pk), hashableElectoralBoardPublicKey,
				hashableElectoralBoardSchnorrProofs, hashableElectionPublicKey, hashableChoiceReturnCodesPublicKey);

		final GqGroup encryptionGroup = setupComponentPublicKeysPayload.getEncryptionGroup();
		final HashableList p_q_g = HashableList.of(
				HashableBigInteger.from(encryptionGroup.getP()),
				HashableBigInteger.from(encryptionGroup.getQ()),
				HashableBigInteger.from(encryptionGroup.getGenerator().getValue()));

		final HashableList h = HashableList.of(p_q_g, HashableString.from(setupComponentPublicKeysPayload.getElectionEventId()), hPublicKeys);

		return base64.base64Encode(hash.recursiveHash(h));
	}

	static Stream<Arguments> jsonFileArgumentProvider() throws IOException {
		final URL url = VerifySignatureSetupComponentPublicKeysTest.class.getResource(
				"/protocol-algorithms/json/verifySignatureSetupComponentPublicKeys/verify-signature-setup-component-public-keys.json");
		final ImmutableList<TestParameters> parametersList = ImmutableList.of(objectMapper.readValue(url, TestParameters[].class));

		return parametersList.stream().parallel().map(testParameters -> {
			try (final MockedStatic<SecurityLevelConfig> mockedSecurityLevel = Mockito.mockStatic(SecurityLevelConfig.class)) {
				mockedSecurityLevel.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(testParameters.getSecurityLevel());

				// Input.
				final JsonData input = testParameters.getInput();
				final GqGroup encryptionGroup = getEncryptionGroup(objectMapper, input.getJsonData("encryptionGroup").jsonNode());
				final String electionEventId = objectMapper.reader().readValue(input.getJsonData("electionEventId").jsonNode(), String.class);
				final SetupComponentPublicKeys setupComponentPublicKeys = objectMapper.reader()
						.withAttribute("group", encryptionGroup)
						.readValue(input.getJsonData("setupComponentPublicKeys").jsonNode(), SetupComponentPublicKeys.class);
				final SetupComponentPublicKeysPayload setupComponentPublicKeysPayload = new SetupComponentPublicKeysPayload(encryptionGroup,
						electionEventId, setupComponentPublicKeys);

				// Output.
				final JsonData output = testParameters.getOutput();
				final String hash = output.get("d", String.class);

				return Arguments.of(setupComponentPublicKeysPayload, hash, testParameters.getDescription());
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}
}