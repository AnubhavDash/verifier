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
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.TallyComponentShufflePayload;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.VerifiablePlaintextDecryption;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureTallyComponentShuffleTest extends TallyVerificationTest {

	private Hash hash;
	private Base64 base64;

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureTallyComponentShuffle(resultPublisherServiceMock, electionDataExtractionService,
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
		final TallyComponentShufflePayload tallyComponentShufflePayload = electionDataExtractionService.getTallyComponentShufflePayloads(datasetPath)
				.findFirst()
				.orElseThrow();

		assertTrue(((VerifySignatureTallyComponentShuffle) verification).verifySignature(tallyComponentShufflePayload));
	}

	@Test
	void testNOK() throws SignatureException {
		final TallyComponentShufflePayload tallyComponentShufflePayload = electionDataExtractionService.getTallyComponentShufflePayloads(datasetPath)
				.findFirst()
				.orElseThrow();

		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(tallyComponentShufflePayload,
				ChannelSecurityContextData.tallyComponentShuffle(tallyComponentShufflePayload.getElectionEventId(),
						tallyComponentShufflePayload.getBallotBoxId()),
				Alias.SDM_TALLY);
		tallyComponentShufflePayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureTallyComponentShuffle) verification).verifySignature(tallyComponentShufflePayload));
	}

	@ParameterizedTest
	@MethodSource("jsonFileArgumentProvider")
	@DisplayName("specific values returns expected output")
	void getHashTallyComponentShuffleWithSpecificValues(final TallyComponentShufflePayload tallyComponentShufflePayload,
			final String hash, final String description) {
		assertEquals(hash, getHashTallyComponentShuffleSpec(tallyComponentShufflePayload),
				String.format("assertion failed for: %s", description));
	}

	@Test
	@DisplayName("implementation aligned to spec gives same result")
	void getHashTallyComponentShuffleAlignment() {
		final TallyComponentShufflePayload tallyComponentShufflePayload = electionDataExtractionService.getTallyComponentShufflePayloads(
				datasetPath)
				.findFirst()
				.orElseThrow();
		final String expected = base64.base64Encode(hash.recursiveHash(tallyComponentShufflePayload));
		assertEquals(expected, getHashTallyComponentShuffleSpec(tallyComponentShufflePayload));
	}

	private String getHashTallyComponentShuffleSpec(final TallyComponentShufflePayload tallyComponentShufflePayload) {

		final HashableList hShuffle = HashableList.of(
				tallyComponentShufflePayload.getVerifiableShuffle().shuffledCiphertexts(),
				tallyComponentShufflePayload.getVerifiableShuffle().shuffleArgument()
		);

		final HashableList hDecryption = HashableList.of(
				tallyComponentShufflePayload.getVerifiablePlaintextDecryption().getDecryptedVotes(),
				tallyComponentShufflePayload.getVerifiablePlaintextDecryption().getDecryptionProofs()
		);

		final GqGroup encryptionGroup = tallyComponentShufflePayload.getEncryptionGroup();
		final HashableList p_q_g = HashableList.of(
				HashableBigInteger.from(encryptionGroup.getP()),
				HashableBigInteger.from(encryptionGroup.getQ()),
				HashableBigInteger.from(encryptionGroup.getGenerator().getValue()));

		final HashableString ee = HashableString.from(tallyComponentShufflePayload.getElectionEventId());

		final HashableString bb = HashableString.from(tallyComponentShufflePayload.getBallotBoxId());


		final HashableList h = HashableList.of(p_q_g, ee, bb, hShuffle, hDecryption);

		return base64.base64Encode(hash.recursiveHash(h));
	}

	static Stream<Arguments> jsonFileArgumentProvider() throws IOException {
		final URL url = VerifySignatureTallyComponentShuffleTest.class.getResource(
				"/protocol-algorithms/json/verifySignatureTallyComponentShuffle/verify-signature-tally-component-shuffle.json");
		final ImmutableList<TestParameters> parametersList = ImmutableList.of(objectMapper.readValue(url, TestParameters[].class));

		return parametersList.stream().parallel().map(testParameters -> {
			try (final MockedStatic<SecurityLevelConfig> mockedSecurityLevel = Mockito.mockStatic(SecurityLevelConfig.class)) {
				mockedSecurityLevel.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(testParameters.getSecurityLevel());

				// Input.
				final JsonData input = testParameters.getInput();
				final GqGroup encryptionGroup = getEncryptionGroup(objectMapper, input.getJsonData("encryptionGroup").jsonNode());
				final String electionEventId = input.get("electionEventId", String.class);
				final String ballotBoxId = input.get("ballotBoxId", String.class);
				final VerifiableShuffle verifiableShuffle = objectMapper.reader()
						.withAttribute("group", encryptionGroup)
						.readValue(input.getJsonData("verifiableShuffle").jsonNode().toString(), VerifiableShuffle.class);
				final VerifiablePlaintextDecryption verifiablePlaintextDecryption = objectMapper.reader()
						.withAttribute("group", encryptionGroup)
						.readValue(input.getJsonData("verifiablePlaintextDecryption").jsonNode(), VerifiablePlaintextDecryption.class);
				final TallyComponentShufflePayload tallyComponentShufflePayload = new TallyComponentShufflePayload(encryptionGroup, electionEventId,
						ballotBoxId, verifiableShuffle, verifiablePlaintextDecryption);

				// Output.
				final JsonData output = testParameters.getOutput();
				final String hash = output.get("d", String.class);

				return Arguments.of(tallyComponentShufflePayload, hash, testParameters.getDescription());
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

}