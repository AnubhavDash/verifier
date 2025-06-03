/*
 * (c) Copyright 2025 Swiss Post Ltd.
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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.cryptoprimitives.hashing.HashFactory.createHash;
import static ch.post.it.evoting.cryptoprimitives.hashing.HashableList.toHashableList;
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
import java.util.Arrays;
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
import ch.post.it.evoting.evotinglibraries.domain.common.EncryptedVerifiableVote;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.evotinglibraries.domain.tally.ControlComponentBallotBoxPayload;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureControlComponentBallotBoxTest extends TallyVerificationTest {

	private Hash hash;
	private Base64 base64;

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureControlComponentBallotBox(resultPublisherServiceMock, electionDataExtractionService,
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
		final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						datasetPath)
				.collect(toImmutableList())
				.get(0);

		assertTrue(((VerifySignatureControlComponentBallotBox) verification).verifySignature(controlComponentBallotBoxPayload));
	}

	@Test
	void testNOK() throws SignatureException {
		final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						datasetPath)
				.collect(toImmutableList())
				.get(0);

		final int nodeId = controlComponentBallotBoxPayload.getNodeId();
		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(controlComponentBallotBoxPayload,
				ChannelSecurityContextData.controlComponentBallotBox(nodeId, controlComponentBallotBoxPayload.getElectionEventId(),
						controlComponentBallotBoxPayload.getBallotBoxId()),
				Alias.getControlComponentByNodeId(nodeId));
		controlComponentBallotBoxPayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureControlComponentBallotBox) verification).verifySignature(controlComponentBallotBoxPayload));
	}

	@ParameterizedTest
	@MethodSource("jsonFileArgumentProvider")
	@DisplayName("specific values returns expected output")
	void getHashControlComponentBallotBoxWithSpecificValues(final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload,
			final String hash, final String description) {
		assertEquals(hash, getHashControlComponentBallotBoxSpec(controlComponentBallotBoxPayload),
				String.format("assertion failed for: %s", description));
	}

	@Test
	@DisplayName("implementation aligned to spec gives same result")
	void getHashControlComponentBallotBoxAlignment() {
		final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload = electionDataExtractionService.getAllControlComponentBallotBoxPayloadsOrderedByNodeId(
						datasetPath)
				.findFirst()
				.orElseThrow();
		final String expected = base64.base64Encode(hash.recursiveHash(controlComponentBallotBoxPayload));
		assertEquals(expected, getHashControlComponentBallotBoxSpec(controlComponentBallotBoxPayload));
	}

	private String getHashControlComponentBallotBoxSpec(final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload) {

		final HashableList hVotes = controlComponentBallotBoxPayload.getConfirmedEncryptedVotes().stream()
				.map(confirmedEncryptedVote -> {
					final HashableString ee = HashableString.from(confirmedEncryptedVote.contextIds().electionEventId());
					final HashableString vcs_i = HashableString.from(confirmedEncryptedVote.contextIds().verificationCardSetId());
					final HashableString vc = HashableString.from(confirmedEncryptedVote.contextIds().verificationCardId());
					final HashableList E1_j_i = confirmedEncryptedVote.encryptedVote();
					final HashableList E1_j_i_tilde = confirmedEncryptedVote.exponentiatedEncryptedVote();
					final HashableList E2_j_i = confirmedEncryptedVote.encryptedPartialChoiceReturnCodes();
					final HashableList pi_Exp_j_i = confirmedEncryptedVote.exponentiationProof();
					final HashableList pi_EqEnc_j_i = confirmedEncryptedVote.plaintextEqualityProof();

					return HashableList.of(HashableList.of(ee, vcs_i, vc), E1_j_i, E1_j_i_tilde, E2_j_i, pi_Exp_j_i, pi_EqEnc_j_i);
				}).collect(toHashableList());

		final GqGroup encryptionGroup = controlComponentBallotBoxPayload.getEncryptionGroup();
		final HashableList p_q_g = HashableList.of(
				HashableBigInteger.from(encryptionGroup.getP()),
				HashableBigInteger.from(encryptionGroup.getQ()),
				HashableBigInteger.from(encryptionGroup.getGenerator().getValue()));

		final HashableString ee = HashableString.from(controlComponentBallotBoxPayload.getElectionEventId());

		final HashableString bb = HashableString.from(controlComponentBallotBoxPayload.getBallotBoxId());

		final HashableBigInteger j = HashableBigInteger.from(BigInteger.valueOf(controlComponentBallotBoxPayload.getNodeId()));

		final HashableList h = HashableList.of(p_q_g, ee, bb, j, hVotes);

		return base64.base64Encode(hash.recursiveHash(h));
	}

	static Stream<Arguments> jsonFileArgumentProvider() throws IOException {
		final URL url = VerifySignatureControlComponentBallotBoxTest.class.getResource(
				"/protocol-algorithms/json/verifySignatureControlComponentBallotBox/verify-signature-control-component-ballot-box.json");
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
				final ImmutableList<EncryptedVerifiableVote> confirmedEncryptedVotes = Arrays.stream(objectMapper.reader()
								.withAttribute("group", encryptionGroup)
								.readValue(input.getJsonData("confirmedEncryptedVotes").jsonNode(), EncryptedVerifiableVote[].class))
						.collect(toImmutableList());
				final ControlComponentBallotBoxPayload controlComponentBallotBoxPayload = new ControlComponentBallotBoxPayload(encryptionGroup,
						electionEventId, ballotBoxId, nodeId, confirmedEncryptedVotes);

				// Output.
				final JsonData output = testParameters.getOutput();
				final String hash = output.get("d", String.class);

				return Arguments.of(controlComponentBallotBoxPayload, hash, testParameters.getDescription());
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}
}
