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
package ch.post.it.evoting.verifier.backend.verifications.tally.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Streams;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.cryptoprimitives.math.Random;
import ch.post.it.evoting.cryptoprimitives.math.RandomFactory;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.tally.TallyVerificationTest;

class VerifyPlaintextsConsistencyTest extends TallyVerificationTest {

	@BeforeAll
	static void setupAll() {
		verification = new VerifyPlaintextsConsistency(resultPublisherServiceMock, electionDataExtractionService);
	}

	@Test
	void verifyOk() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNok() {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(datasetPath);
		final GqGroup encryptionGroup = electionEventContextPayload.getEncryptionGroup();
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();
		final List<VerificationCardSetContext> vcsContexts = electionEventContext.verificationCardSetContexts();
		final VerificationCardSetContext firstContext = vcsContexts.get(0);
		final int numberOfWriteInsPlusOne = firstContext.numberOfWriteInFields() + 1;
		final Random random = RandomFactory.createRandom();
		final PrimeGqElement encodedVotingOption = PrimeGqElement.PrimeGqElementFactory.getSmallPrimeGroupMembers(encryptionGroup, 1).get(0);
		final List<PrimesMappingTableEntry> pTable = List.of(new PrimesMappingTableEntry(random.genRandomBase16String(4),
				encodedVotingOption, "semantic information", "correctnessInformation"));
		final VerificationCardSetContext modifiedFirstContext = new VerificationCardSetContext(firstContext.verificationCardSetId(),
				firstContext.verificationCardSetAlias(), firstContext.verificationCardSetDescription(), firstContext.ballotBoxId(),
				firstContext.ballotBoxStartTime(), firstContext.ballotBoxFinishTime(), firstContext.testBallotBox(), numberOfWriteInsPlusOne,
				firstContext.numberOfVotingCards(), firstContext.gracePeriod(), PrimesMappingTable.from(pTable));
		final List<VerificationCardSetContext> modifiedVcsContexts = Streams.concat(Stream.of(modifiedFirstContext), vcsContexts.stream().skip(1))
				.toList();
		final ElectionEventContext modifiedElectionEventContext = spy(electionEventContext);
		doReturn(modifiedVcsContexts).when(modifiedElectionEventContext).verificationCardSetContexts();
		final ElectionEventContextPayload modifiedElectionEventContextPayload = new ElectionEventContextPayload(
				electionEventContextPayload.getEncryptionGroup(), modifiedElectionEventContext);
		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		doReturn(modifiedElectionEventContextPayload).when(extractionServiceMock).getElectionEventContextPayload(datasetPath);

		final VerifyPlaintextsConsistency verificationWithMock = new VerifyPlaintextsConsistency(resultPublisherServiceMock,
				extractionServiceMock);
		final VerificationResult verificationResult = verificationWithMock.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.failure(verificationWithMock.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(TallyVerificationSuite.RESOURCE_BUNDLE_NAME, "tally.verification302.nok.message"));
		assertEquals(expectedResult, verificationResult);
	}
}
