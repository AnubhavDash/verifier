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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency.verifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.math.Random;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;
import ch.post.it.evoting.verifier.protocol.algorithms.setup.VerifyPrimesMappingTableConsistencyAlgorithm;
import ch.post.it.evoting.verifier.protocol.domain.configuration.PrimesMappingTable;
import ch.post.it.evoting.verifier.protocol.domain.configuration.PrimesMappingTableEntry;
import ch.post.it.evoting.verifier.protocol.domain.configuration.SetupComponentTallyDataPayload;

class VerifyPrimesMappingTableConsistencyTest extends SetupVerificationTest {

	@BeforeAll
	static void setupAll() {
		final ElectionDataExtractionService extractionService = new ElectionDataExtractionService(pathService, objectMapper);
		final VerifyPrimesMappingTableConsistencyAlgorithm consistencyAlgorithm = new VerifyPrimesMappingTableConsistencyAlgorithm();
		verification = new VerifyPrimesMappingTableConsistency(extractionService, consistencyAlgorithm, applicationEventPublisherMock);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNok() {
		final GqGroup encryptionGroup = new ElectionDataExtractionService(pathService, objectMapper).getEncryptionParametersPayload(datasetPath)
				.getEncryptionGroup();
		final Random random = RandomFactory.createRandom();
		final PrimeGqElement encodedVotingOption = PrimeGqElement.PrimeGqElementFactory.getSmallPrimeGroupMembers(encryptionGroup, 1).get(0);

		final GroupVector<PrimesMappingTableEntry, GqGroup> pTable1 = GroupVector.of(new PrimesMappingTableEntry(random.genRandomBase16String(4),
				encodedVotingOption));
		final SetupComponentTallyDataPayload tallyDataPayloadMock1 = mock(SetupComponentTallyDataPayload.class);
		when(tallyDataPayloadMock1.getPrimesMappingTable()).thenReturn(new PrimesMappingTable(pTable1));

		final GroupVector<PrimesMappingTableEntry, GqGroup> pTable2 = GroupVector.of(new PrimesMappingTableEntry(random.genRandomBase16String(4),
				encodedVotingOption));
		final SetupComponentTallyDataPayload tallyDataPayloadMock2 = mock(SetupComponentTallyDataPayload.class);
		when(tallyDataPayloadMock2.getPrimesMappingTable()).thenReturn(new PrimesMappingTable(pTable2));

		final ElectionDataExtractionService extractionServiceMock = mock(ElectionDataExtractionService.class);
		when(extractionServiceMock.getSetupComponentTallyDataPayloads(datasetPath)).thenReturn(
				List.of(tallyDataPayloadMock1, tallyDataPayloadMock2));

		final VerifyPrimesMappingTableConsistency verificationWithMock = new VerifyPrimesMappingTableConsistency(extractionServiceMock,
				new VerifyPrimesMappingTableConsistencyAlgorithm(), applicationEventPublisherMock);

		final VerificationResult result = verificationWithMock.verify(datasetPath);
		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification36.nok.message"));
		assertEquals(expectedResult, result);
	}
}