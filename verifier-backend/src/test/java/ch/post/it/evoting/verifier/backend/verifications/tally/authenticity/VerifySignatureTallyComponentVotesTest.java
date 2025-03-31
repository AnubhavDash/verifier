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

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.cryptoprimitives.hashing.HashFactory.createHash;
import static ch.post.it.evoting.cryptoprimitives.hashing.HashableList.toHashableList;
import static ch.post.it.evoting.cryptoprimitives.math.BaseEncodingFactory.createBase64;
import static ch.post.it.evoting.cryptoprimitives.math.GroupVector.toGroupVector;
import static ch.post.it.evoting.evotinglibraries.domain.mapper.EncryptionGroupUtils.getEncryptionGroup;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
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

import com.fasterxml.jackson.core.type.TypeReference;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.hashing.Hash;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableBigInteger;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableString;
import ch.post.it.evoting.cryptoprimitives.internal.securitylevel.SecurityLevelConfig;
import ch.post.it.evoting.cryptoprimitives.math.Base64;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.test.tools.serialization.JsonData;
import ch.post.it.evoting.cryptoprimitives.test.tools.serialization.TestParameters;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.evotinglibraries.domain.tally.TallyComponentVotesPayload;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifySignatureTallyComponentVotesTest extends TallyVerificationTest {

	private Hash hash;
	private Base64 base64;

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureTallyComponentVotes(resultPublisherServiceMock, electionDataExtractionService,
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
		final TallyComponentVotesPayload tallyComponentVotesPayload = electionDataExtractionService.getTallyComponentVotesPayloads(datasetPath)
				.findFirst()
				.orElseThrow();

		assertTrue(((VerifySignatureTallyComponentVotes) verification).verifySignature(tallyComponentVotesPayload));
	}

	@Test
	void testNOK() throws SignatureException {
		final TallyComponentVotesPayload tallyComponentVotesPayload = electionDataExtractionService.getTallyComponentVotesPayloads(datasetPath)
				.findFirst()
				.orElseThrow();

		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(tallyComponentVotesPayload,
				ChannelSecurityContextData.tallyComponentVotes(tallyComponentVotesPayload.getElectionEventId(),
						tallyComponentVotesPayload.getBallotBoxId()),
				Alias.SDM_TALLY);
		tallyComponentVotesPayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureTallyComponentVotes) verification).verifySignature(tallyComponentVotesPayload));
	}

	@ParameterizedTest
	@MethodSource("jsonFileArgumentProvider")
	@DisplayName("specific values returns expected output")
	void getHashTallyComponentVotesWithSpecificValues(final TallyComponentVotesPayload tallyComponentVotesPayload,
			final String hash, final String description) {
		assertEquals(hash, getHashTallyComponentVotesSpec(tallyComponentVotesPayload),
				String.format("assertion failed for: %s", description));
	}

	@Test
	@DisplayName("implementation aligned to spec gives same result")
	void getHashTallyComponentVotesAlignment() {
		final TallyComponentVotesPayload tallyComponentVotesPayload = electionDataExtractionService.getTallyComponentVotesPayloads(
				datasetPath).collect(toImmutableList()).getLast();
		final String expected = base64.base64Encode(hash.recursiveHash(tallyComponentVotesPayload));
		assertEquals(expected, getHashTallyComponentVotesSpec(tallyComponentVotesPayload));
	}

	private String getHashTallyComponentVotesSpec(final TallyComponentVotesPayload tallyComponentVotesPayload) {
		final HashableString ee = HashableString.from(tallyComponentVotesPayload.getElectionEventId());

		final HashableString bb = HashableString.from(tallyComponentVotesPayload.getBallotBoxId());

		final GqGroup encryptionGroup = tallyComponentVotesPayload.getEncryptionGroup();
		final HashableList p_q_g = HashableList.of(
				HashableBigInteger.from(encryptionGroup.getP()),
				HashableBigInteger.from(encryptionGroup.getQ()),
				HashableBigInteger.from(encryptionGroup.getGenerator().getValue()));

		final HashableList hVotes = tallyComponentVotesPayload.getDecryptedVotes().stream()
				.map(votes -> votes.stream()
						.map(PrimeGqElement::getValue)
						.map(HashableBigInteger::from)
						.collect(toHashableList()))
				.collect(toHashableList());

		final HashableList hDecodedVotes = tallyComponentVotesPayload.getDecodedVotes().stream()
				.map(decodedVotes -> decodedVotes.stream()
						.map(HashableString::from)
						.collect(toHashableList()))
				.collect(toHashableList());

		final HashableList hWriteIns = tallyComponentVotesPayload.getDecodedWriteIns().stream()
				.map(writeIns -> writeIns.stream()
						.map(HashableString::from)
						.collect(toHashableList()))
				.collect(toHashableList());

		final HashableList h = HashableList.of(p_q_g, ee, bb, hVotes, hDecodedVotes, hWriteIns);

		return base64.base64Encode(hash.recursiveHash(h));
	}

	static Stream<Arguments> jsonFileArgumentProvider() throws IOException {
		final URL url = VerifySignatureTallyComponentVotesTest.class.getResource(
				"/protocol-algorithms/json/verifySignatureTallyComponentVotes/verify-signature-tally-component-votes.json");
		final ImmutableList<TestParameters> parametersList = ImmutableList.of(objectMapper.readValue(url, TestParameters[].class));

		return parametersList.stream().parallel().map(testParameters -> {
			try (final MockedStatic<SecurityLevelConfig> mockedSecurityLevel = Mockito.mockStatic(SecurityLevelConfig.class)) {
				mockedSecurityLevel.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(testParameters.getSecurityLevel());

				// Input.
				final JsonData input = testParameters.getInput();
				final GqGroup encryptionGroup = getEncryptionGroup(objectMapper, input.getJsonData("encryptionGroup").jsonNode());
				final GroupVector<GroupVector<PrimeGqElement, GqGroup>, GqGroup> votes = Arrays.stream(objectMapper.reader()
								.withAttribute("group", encryptionGroup)
								.readValue(input.getJsonData("votes").jsonNode(), PrimeGqElement[][].class))
						.map(GroupVector::of)
						.collect(toGroupVector());
				final String electionEventId = input.get("electionEventId", String.class);
				final String ballotBoxId = input.get("ballotBoxId", String.class);
				final ImmutableList<ImmutableList<String>> actualSelectedVotingOptions = ImmutableList.from(objectMapper.reader()
						.readValue(input.getJsonData("actualSelectedVotingOptions").jsonNode().traverse(), new TypeReference<>() {
						}));
				final ImmutableList<ImmutableList<String>> decodedWriteInVotes = ImmutableList.from(objectMapper.reader()
						.readValue(input.getJsonData("decodedWriteInVotes").jsonNode().traverse(), new TypeReference<>() {
						}));
				final TallyComponentVotesPayload tallyComponentVotesPayload = new TallyComponentVotesPayload(encryptionGroup, electionEventId,
						ballotBoxId, votes, actualSelectedVotingOptions, decodedWriteInVotes);

				// Output.
				final JsonData output = testParameters.getOutput();
				final String hash = output.get("d", String.class);

				return Arguments.of(tallyComponentVotesPayload, hash, testParameters.getDescription());
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}