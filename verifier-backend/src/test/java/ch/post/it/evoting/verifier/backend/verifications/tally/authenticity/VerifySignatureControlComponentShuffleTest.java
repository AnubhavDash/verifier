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
package ch.post.it.evoting.verifier.backend.verifications.tally.authenticity;

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
import ch.post.it.evoting.cryptoprimitives.mixnet.VerifiableShuffle;
import ch.post.it.evoting.cryptoprimitives.test.tools.serialization.JsonData;
import ch.post.it.evoting.cryptoprimitives.test.tools.serialization.TestParameters;
import ch.post.it.evoting.cryptoprimitives.zeroknowledgeproofs.VerifiableDecryptions;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ControlComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureControlComponentShuffleTest extends TallyVerificationTest {

	private Hash hash;
	private Base64 base64;

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureControlComponentShuffle(resultPublisherServiceMock, electionDataExtractionService,
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
		final ControlComponentShufflePayload controlComponentShufflePayload = electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
						datasetPath)
				.findFirst()
				.orElseThrow();

		assertTrue(((VerifySignatureControlComponentShuffle) verification).verifySignature(controlComponentShufflePayload));
	}

	@Test
	void testNOK() throws SignatureException {
		final ControlComponentShufflePayload controlComponentShufflePayload = electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
						datasetPath)
				.findFirst()
				.orElseThrow();

		final int nodeId = controlComponentShufflePayload.getNodeId();
		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(controlComponentShufflePayload,
				ChannelSecurityContextData.controlComponentShuffle(nodeId, controlComponentShufflePayload.getElectionEventId(),
						controlComponentShufflePayload.getBallotBoxId()),
				Alias.getControlComponentByNodeId(nodeId));
		controlComponentShufflePayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureControlComponentShuffle) verification).verifySignature(controlComponentShufflePayload));
	}

	@ParameterizedTest
	@MethodSource("jsonFileArgumentProvider")
	@DisplayName("specific values returns expected output")
	void getHashControlComponentShuffleWithSpecificValues(final ControlComponentShufflePayload controlComponentShufflePayload,
			final String hash, final String description) {
		assertEquals(hash, getHashControlComponentShuffleSpec(controlComponentShufflePayload),
				String.format("assertion failed for: %s", description));
	}

	@Test
	@DisplayName("implementation aligned to spec gives same result")
	void getHashControlComponentShuffleAlignment() {
		final ControlComponentShufflePayload controlComponentShufflePayload = electionDataExtractionService.getAllControlComponentShufflePayloadsOrderedByNodeId(
						datasetPath)
				.findFirst()
				.orElseThrow();
		final String expected = base64.base64Encode(hash.recursiveHash(controlComponentShufflePayload));
		assertEquals(expected, getHashControlComponentShuffleSpec(controlComponentShufflePayload));
	}

	private String getHashControlComponentShuffleSpec(final ControlComponentShufflePayload controlComponentShufflePayload) {

		final HashableList hShuffle = HashableList.of(
				controlComponentShufflePayload.getVerifiableShuffle().shuffledCiphertexts(),
				controlComponentShufflePayload.getVerifiableShuffle().shuffleArgument()
		);

		final HashableList hDecryption = HashableList.of(
				controlComponentShufflePayload.getVerifiableDecryptions().getCiphertexts(),
				controlComponentShufflePayload.getVerifiableDecryptions().getDecryptionProofs()
		);

		final GqGroup encryptionGroup = controlComponentShufflePayload.getEncryptionGroup();
		final HashableList p_q_g = HashableList.of(
				HashableBigInteger.from(encryptionGroup.getP()),
				HashableBigInteger.from(encryptionGroup.getQ()),
				HashableBigInteger.from(encryptionGroup.getGenerator().getValue()));

		final HashableString ee = HashableString.from(controlComponentShufflePayload.getElectionEventId());

		final HashableString bb = HashableString.from(controlComponentShufflePayload.getBallotBoxId());

		final HashableBigInteger j = HashableBigInteger.from(BigInteger.valueOf(controlComponentShufflePayload.getNodeId()));

		final HashableList h = HashableList.of(p_q_g, ee, bb, j, hShuffle, hDecryption);

		return base64.base64Encode(hash.recursiveHash(h));
	}

	static Stream<Arguments> jsonFileArgumentProvider() throws IOException {
		final URL url = VerifySignatureControlComponentShuffleTest.class.getResource(
				"/protocol-algorithms/json/verifySignatureControlComponentShuffle/verify-signature-control-component-shuffle.json");
		final ImmutableList<TestParameters> parametersList = ImmutableList.of(objectMapper.readValue(url, TestParameters[].class));

		return parametersList.stream().parallel().map(testParameters -> {
			try (final MockedStatic<SecurityLevelConfig> mockedSecurityLevel = Mockito.mockStatic(SecurityLevelConfig.class)) {
				mockedSecurityLevel.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(testParameters.getSecurityLevel());

				// Input.
				final JsonData input = testParameters.getInput();
				final GqGroup encryptionGroup = getEncryptionGroup(objectMapper, input.getJsonData("encryptionGroup").jsonNode());
				final String electionEventId = input.get("electionEventId", String.class);
				final String ballotBoxId = input.get("ballotBoxId", String.class);
				final int nodeId = input.get("nodeId", Integer.class);
				final VerifiableShuffle verifiableShuffle = objectMapper.reader()
						.withAttribute("group", encryptionGroup)
						.readValue(input.getJsonData("verifiableShuffle").jsonNode().toString(), VerifiableShuffle.class);
				final VerifiableDecryptions verifiableDecryptions = objectMapper.reader()
						.withAttribute("group", encryptionGroup)
						.readValue(input.getJsonData("verifiableDecryptions").jsonNode(), VerifiableDecryptions.class);
				final ControlComponentShufflePayload controlComponentShufflePayload = new ControlComponentShufflePayload(encryptionGroup,
						electionEventId,
						ballotBoxId, nodeId, verifiableShuffle, verifiableDecryptions);

				// Output.
				final JsonData output = testParameters.getOutput();
				final String hash = output.get("d", String.class);

				return Arguments.of(controlComponentShufflePayload, hash, testParameters.getDescription());
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}
}
