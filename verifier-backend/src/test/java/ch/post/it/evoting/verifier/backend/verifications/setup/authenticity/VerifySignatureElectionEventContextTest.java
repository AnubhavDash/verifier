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
import static ch.post.it.evoting.cryptoprimitives.hashing.HashableList.toHashableList;
import static ch.post.it.evoting.cryptoprimitives.math.BaseEncodingFactory.createBase64;
import static ch.post.it.evoting.cryptoprimitives.math.GroupVector.toGroupVector;
import static ch.post.it.evoting.evotinglibraries.domain.common.Constants.ISO8601_LOCAL_DATETIME_WITHOUT_TIMEZONE_FORMAT;
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
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.test.tools.serialization.JsonData;
import ch.post.it.evoting.cryptoprimitives.test.tools.serialization.TestParameters;
import ch.post.it.evoting.evotinglibraries.domain.common.ChannelSecurityContextData;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.evotinglibraries.domain.signature.Alias;
import ch.post.it.evoting.evotinglibraries.domain.signature.CryptoPrimitivesSignature;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifySignatureElectionEventContextTest extends SetupVerificationTest {

	private Hash hash;
	private Base64 base64;

	@BeforeEach
	void setUpAll() {
		verification = new VerifySignatureElectionEventContext(resultPublisherServiceMock, electionDataExtractionService,
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
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(datasetPath);

		assertTrue(((VerifySignatureElectionEventContext) verification).verifySignature(electionEventContextPayload));
	}

	@Test
	void testNOK() throws SignatureException {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(datasetPath);

		final CryptoPrimitivesSignature dummySignature = datasetSignatureFactory.getDummySignature(electionEventContextPayload,
				ChannelSecurityContextData.electionEventContext(electionEventContextPayload.getElectionEventContext().electionEventId()),
				Alias.SDM_CONFIG);
		electionEventContextPayload.setSignature(dummySignature);

		assertFalse(((VerifySignatureElectionEventContext) verification).verifySignature(electionEventContextPayload));
	}

	@ParameterizedTest
	@MethodSource("jsonFileArgumentProvider")
	@DisplayName("specific values returns expected output")
	void getHashElectionEventContextPayloadWithSpecificValues(final ElectionEventContextPayload electionEventContextPayload,
			final String hash, final String description) {
		assertEquals(hash, getHashElectionEventContextPayloadSpec(electionEventContextPayload),
				String.format("assertion failed for: %s", description));
	}

	@Test
	@DisplayName("implementation aligned to spec gives same result")
	void getHashElectionEventContextPayloadAlignment() {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(datasetPath);
		final String expected = base64.base64Encode(hash.recursiveHash(electionEventContextPayload));
		assertEquals(expected, getHashElectionEventContextPayloadSpec(electionEventContextPayload));
	}

	private String getHashElectionEventContextPayloadSpec(final ElectionEventContextPayload electionEventContextPayload) {

		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();
		final ImmutableList<HashableList> h_vcs = electionEventContext.verificationCardSetContexts().stream()
				.map(v -> {
					final PrimesMappingTable p = v.getPrimesMappingTable();
					final ImmutableList<HashableList> v_p_tau_sigma = p.pTable().stream()
							.map(pTable -> HashableList.of(
									HashableString.from(pTable.actualVotingOption()),
									HashableBigInteger.from(pTable.encodedVotingOption().getValue()),
									HashableString.from(pTable.semanticInformation()),
									HashableString.from(pTable.correctnessInformation())))
							.collect(toImmutableList());
					final HashableList h_pTable_j = HashableList.of(HashableList.from(v_p_tau_sigma));

					// h_vcs_j
					return HashableList.of(
							HashableString.from(v.getVerificationCardSetId()),
							HashableString.from(v.getVerificationCardSetAlias()),
							HashableString.from(v.getVerificationCardSetDescription()),
							HashableString.from(v.getBallotBoxId()),
							HashableString.from(v.getBallotBoxStartTime().format(ISO8601_LOCAL_DATETIME_WITHOUT_TIMEZONE_FORMAT)),
							HashableString.from(v.getBallotBoxFinishTime().format(ISO8601_LOCAL_DATETIME_WITHOUT_TIMEZONE_FORMAT)),
							HashableString.from(String.valueOf(v.isTestBallotBox())),
							HashableBigInteger.from(v.getNumberOfEligibleVoters()),
							HashableBigInteger.from(v.getGracePeriod()),
							h_pTable_j,
							v.getDomainsOfInfluence().stream().map(HashableString::from).collect(toHashableList())
					);
				}).collect(toImmutableList());

		final GqGroup encryptionGroup = electionEventContext.encryptionGroup();
		final HashableList p_q_g = HashableList.of(
				HashableBigInteger.from(encryptionGroup.getP()),
				HashableBigInteger.from(encryptionGroup.getQ()),
				HashableBigInteger.from(encryptionGroup.getGenerator().getValue()));
		final HashableList hContext = HashableList.of(
				p_q_g,
				HashableString.from(electionEventContext.electionEventId()),
				HashableString.from(electionEventContext.electionEventAlias()),
				HashableString.from(electionEventContext.electionEventDescription()),
				HashableList.from(h_vcs),
				HashableString.from(electionEventContext.startTime().format(ISO8601_LOCAL_DATETIME_WITHOUT_TIMEZONE_FORMAT)),
				HashableString.from(electionEventContext.finishTime().format(ISO8601_LOCAL_DATETIME_WITHOUT_TIMEZONE_FORMAT)),
				HashableBigInteger.from(electionEventContext.maximumNumberOfVotingOptions()),
				HashableBigInteger.from(electionEventContext.maximumNumberOfSelections()),
				HashableBigInteger.from(electionEventContext.maximumNumberOfWriteInsPlusOne())
		);

		final GqGroup encryptionGroupPayload = electionEventContextPayload.getEncryptionGroup();
		final HashableList p_q_g_payload = HashableList.of(
				HashableBigInteger.from(encryptionGroupPayload.getP()),
				HashableBigInteger.from(encryptionGroupPayload.getQ()),
				HashableBigInteger.from(encryptionGroupPayload.getGenerator().getValue()));

		final HashableString seed = HashableString.from(electionEventContextPayload.getSeed());

		final HashableList p = electionEventContextPayload.getSmallPrimes();

		final HashableString tenantId = HashableString.from(electionEventContextPayload.getTenantId());

		final HashableList h = HashableList.of(p_q_g_payload, seed, p, hContext, tenantId);

		return base64.base64Encode(hash.recursiveHash(h));
	}

	static Stream<Arguments> jsonFileArgumentProvider() throws IOException {
		final URL url = VerifySignatureElectionEventContextTest.class.getResource(
				"/protocol-algorithms/json/verifySignatureElectionEventContext/verify-signature-election-event-context.json");
		final ImmutableList<TestParameters> parametersList = ImmutableList.of(objectMapper.readValue(url, TestParameters[].class));

		return parametersList.stream().parallel().map(testParameters -> {
			try (final MockedStatic<SecurityLevelConfig> mockedSecurityLevel = Mockito.mockStatic(SecurityLevelConfig.class)) {
				mockedSecurityLevel.when(SecurityLevelConfig::getSystemSecurityLevel).thenReturn(testParameters.getSecurityLevel());

				// Input.
				final JsonData input = testParameters.getInput();
				final GqGroup encryptionGroup = getEncryptionGroup(objectMapper, input.getJsonData("encryptionGroup").jsonNode());
				final String seed = objectMapper.reader().readValue(input.getJsonData("seed").jsonNode(), String.class);
				final GroupVector<PrimeGqElement, GqGroup> smallPrimes = Arrays.stream(objectMapper.reader()
								.withAttribute("group", encryptionGroup)
								.readValue(input.getJsonData("smallPrimes").jsonNode(), PrimeGqElement[].class))
						.collect(toGroupVector());
				final ElectionEventContext electionEventContext = objectMapper.reader()
						.withAttribute("group", encryptionGroup)
						.readValue(input.getJsonData("electionEventContext").jsonNode(), ElectionEventContext.class);
				final String tenantId = objectMapper.reader().readValue(input.getJsonData("tenantId").jsonNode(), String.class);
				final ElectionEventContextPayload electionEventContextPayload = new ElectionEventContextPayload(encryptionGroup, seed, smallPrimes, electionEventContext, tenantId);

				// Output.
				final JsonData output = testParameters.getOutput();
				final String hash = output.get("d", String.class);

				return Arguments.of(electionEventContextPayload, hash, testParameters.getDescription());
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}
}