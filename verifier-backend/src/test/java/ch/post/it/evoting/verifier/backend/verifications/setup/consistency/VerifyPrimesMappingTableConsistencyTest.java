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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.collect.MoreCollectors;

import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.domain.mixnet.ElectionEventContextPayload;
import ch.post.it.evoting.verifier.backend.VerificationResult;
import ch.post.it.evoting.verifier.backend.tools.ElectionDataExtractionService;
import ch.post.it.evoting.verifier.backend.tools.TranslationHelper;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationSuite;
import ch.post.it.evoting.verifier.backend.verifications.setup.SetupVerificationTest;

class VerifyPrimesMappingTableConsistencyTest extends SetupVerificationTest {

	private static VerifyPrimesMappingTableConsistencyAlgorithm consistencyAlgorithm;

	@BeforeAll
	static void setupAll() {
		consistencyAlgorithm = spy(VerifyPrimesMappingTableConsistencyAlgorithm.class);

		verification = new VerifyPrimesMappingTableConsistency(electionDataExtractionService, consistencyAlgorithm, resultPublisherServiceMock);
	}

	@BeforeEach
	void setUp() {
		reset(consistencyAlgorithm);
	}

	@Test
	@DisplayName("valid input files is successful")
	void validInputFiles() {
		final VerificationResult verificationResult = verification.verify(datasetPath);

		final VerificationResult expectedResult = VerificationResult.success(verification.getVerificationDefinition());
		assertEquals(expectedResult, verificationResult);
	}

	@Test
	void verifyNokExchangedActualVotingOptions() {
		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(datasetPath);
		final List<VerificationCardSetContext> verificationCardSetContexts = electionEventContext.verificationCardSetContexts();
		final VerificationCardSetContext verificationCardSetContextWithPermutation = permuteActualVotingOptions(verificationCardSetContexts.get(0));
		final ArrayList<VerificationCardSetContext> verificationCardSetContextsModified = new ArrayList<>(verificationCardSetContexts.size());
		verificationCardSetContextsModified.add(verificationCardSetContextWithPermutation);
		verificationCardSetContextsModified.addAll(verificationCardSetContexts.subList(1, verificationCardSetContexts.size()));

		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		final ElectionEventContext electionEventContextMock = spy(electionEventContext);

		doReturn(verificationCardSetContextsModified).when(electionEventContextMock).verificationCardSetContexts();
		doReturn(electionEventContextMock).when(extractionServiceMock).getElectionEventContext(datasetPath);

		final VerifyPrimesMappingTableConsistency verifyPrimesMappingTableConsistency = new VerifyPrimesMappingTableConsistency(extractionServiceMock,
				consistencyAlgorithm, resultPublisherServiceMock);
		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.nok.message"));
		assertEquals(expectedResult, verifyPrimesMappingTableConsistency.verify(datasetPath));
	}

	@Test
	void verifyNokActualVotingOptionNotInConfig() {
		final ElectionEventContext electionEventContext = electionDataExtractionService.getElectionEventContext(datasetPath);
		final List<VerificationCardSetContext> verificationCardSetContexts = electionEventContext.verificationCardSetContexts();
		final List<VerificationCardSetContext> verificationCardSetContextsModified = addNewActualVotingOption(verificationCardSetContexts);

		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		final ElectionEventContext electionEventContextMock = spy(electionEventContext);

		doReturn(verificationCardSetContextsModified).when(electionEventContextMock).verificationCardSetContexts();
		doReturn(electionEventContextMock).when(extractionServiceMock).getElectionEventContext(datasetPath);

		final VerifyPrimesMappingTableConsistency verifyPrimesMappingTableConsistency = new VerifyPrimesMappingTableConsistency(extractionServiceMock,
				consistencyAlgorithm, resultPublisherServiceMock);
		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.nok.message"));
		assertEquals(expectedResult, verifyPrimesMappingTableConsistency.verify(datasetPath));
	}

	@Test
	void verifyNokActualVotingOptionMapsToTwoDifferentEncodedVotingOptions() {
		final ElectionEventContextPayload electionEventContextPayload = electionDataExtractionService.getElectionEventContextPayload(datasetPath);
		final ElectionEventContext electionEventContext = electionEventContextPayload.getElectionEventContext();
		final List<VerificationCardSetContext> verificationCardSetContexts = electionEventContext.verificationCardSetContexts();
		final List<VerificationCardSetContext> verificationCardSetContextsModified = mapToTwoDifferentEncodedVotingOptions(
				electionEventContextPayload.getSmallPrimes(), verificationCardSetContexts);

		final ElectionDataExtractionService extractionServiceMock = spy(electionDataExtractionService);
		final ElectionEventContext electionEventContextMock = spy(electionEventContext);

		doReturn(verificationCardSetContextsModified).when(electionEventContextMock).verificationCardSetContexts();
		doReturn(electionEventContextMock).when(extractionServiceMock).getElectionEventContext(datasetPath);

		final VerifyPrimesMappingTableConsistency verifyPrimesMappingTableConsistency = new VerifyPrimesMappingTableConsistency(extractionServiceMock,
				consistencyAlgorithm, resultPublisherServiceMock);
		final VerificationResult expectedResult = VerificationResult.failure(verification.getVerificationDefinition(),
				TranslationHelper.getFromResourceBundle(SetupVerificationSuite.RESOURCE_BUNDLE_NAME, "setup.verification308.nok.message"));
		assertEquals(expectedResult, verifyPrimesMappingTableConsistency.verify(datasetPath));
	}

	private VerificationCardSetContext permuteActualVotingOptions(final VerificationCardSetContext verificationCardSetContext) {
		final PrimesMappingTable primesMappingTable = verificationCardSetContext.getPrimesMappingTable();
		final GroupVector<PrimesMappingTableEntry, GqGroup> pTable = primesMappingTable.getPTable();
		final PrimesMappingTableEntry entry0 = pTable.get(0);
		PrimesMappingTableEntry entry1;
		int i = 0;
		do {
			i++;
			entry1 = pTable.get(i);
		} while (entry1.actualVotingOption().equals(entry0.actualVotingOption()));

		final PrimesMappingTableEntry permutedEntry0 = new PrimesMappingTableEntry(entry0.actualVotingOption(),
				entry1.encodedVotingOption(), entry0.semanticInformation(), entry0.correctnessInformation());
		final PrimesMappingTableEntry permutedEntry1 = new PrimesMappingTableEntry(entry1.actualVotingOption(),
				entry0.encodedVotingOption(), entry1.semanticInformation(), entry1.correctnessInformation());

		final ArrayList<PrimesMappingTableEntry> permutedPTableList = new ArrayList<>(pTable.size());
		permutedPTableList.add(permutedEntry0);
		for (int j = 1; j < pTable.size(); j++) {
			if (j != i) {
				permutedPTableList.add(pTable.get(j));
			} else {
				permutedPTableList.add(permutedEntry1);
			}
		}
		final PrimesMappingTable permutedPrimesMappingTable = PrimesMappingTable.from(permutedPTableList);

		return new VerificationCardSetContext.Builder()
				.setVerificationCardSetId(verificationCardSetContext.getVerificationCardSetId())
				.setVerificationCardSetAlias(verificationCardSetContext.getVerificationCardSetAlias())
				.setVerificationCardSetDescription(verificationCardSetContext.getVerificationCardSetDescription())
				.setBallotBoxId(verificationCardSetContext.getBallotBoxId())
				.setBallotBoxStartTime(verificationCardSetContext.getBallotBoxStartTime())
				.setBallotBoxFinishTime(verificationCardSetContext.getBallotBoxFinishTime())
				.setTestBallotBox(verificationCardSetContext.isTestBallotBox())
				.setNumberOfVotingCards(verificationCardSetContext.getNumberOfVotingCards())
				.setGracePeriod(verificationCardSetContext.getGracePeriod())
				.setPrimesMappingTable(permutedPrimesMappingTable)
				.setDomainsOfInfluence(verificationCardSetContext.getDomainsOfInfluence())
				.build();
	}

	private List<VerificationCardSetContext> addNewActualVotingOption(final List<VerificationCardSetContext> verificationCardSetContexts) {
		final PrimeGqElement encodedVotingOption = verificationCardSetContexts.get(0).getPrimesMappingTable().getPTable().get(0)
				.encodedVotingOption();
		return verificationCardSetContexts.stream()
				.map(verificationCardSetContext -> {
					final PrimesMappingTable primesMappingTable = verificationCardSetContext.getPrimesMappingTable();
					if (primesMappingTable.getPTable().stream().anyMatch(entry -> entry.encodedVotingOption().equals(encodedVotingOption))) {
						final Map<Boolean, List<PrimesMappingTableEntry>> toModify = primesMappingTable.getPTable().stream()
								.collect(Collectors.partitioningBy(entry -> entry.encodedVotingOption().equals(encodedVotingOption)));
						final PrimesMappingTableEntry pTable = toModify.get(true).stream().collect(MoreCollectors.onlyElement());
						final PrimesMappingTableEntry newEntry = new PrimesMappingTableEntry("newActualVotingOption|NotInConfig",
								pTable.encodedVotingOption(), pTable.semanticInformation(), pTable.correctnessInformation());

						final ArrayList<PrimesMappingTableEntry> pTableWithNewEntry = new ArrayList<>(pTable.size());
						pTableWithNewEntry.add(newEntry);
						pTableWithNewEntry.addAll(toModify.get(false));
						final PrimesMappingTable notInConfigPrimesMappingTable = PrimesMappingTable.from(pTableWithNewEntry);

						return new VerificationCardSetContext.Builder()
								.setVerificationCardSetId(verificationCardSetContext.getVerificationCardSetId())
								.setVerificationCardSetAlias(verificationCardSetContext.getVerificationCardSetAlias())
								.setVerificationCardSetDescription(verificationCardSetContext.getVerificationCardSetDescription())
								.setBallotBoxId(verificationCardSetContext.getBallotBoxId())
								.setBallotBoxStartTime(verificationCardSetContext.getBallotBoxStartTime())
								.setBallotBoxFinishTime(verificationCardSetContext.getBallotBoxFinishTime())
								.setTestBallotBox(verificationCardSetContext.isTestBallotBox())
								.setNumberOfVotingCards(verificationCardSetContext.getNumberOfVotingCards())
								.setGracePeriod(verificationCardSetContext.getGracePeriod())
								.setPrimesMappingTable(notInConfigPrimesMappingTable)
								.setDomainsOfInfluence(verificationCardSetContext.getDomainsOfInfluence())
								.build();
					}
					return verificationCardSetContext;
				}).toList();
	}

	private List<VerificationCardSetContext> mapToTwoDifferentEncodedVotingOptions(final GroupVector<PrimeGqElement, GqGroup> smallPrimes,
			final List<VerificationCardSetContext> verificationCardSetContexts) {
		// Choose a new prime that's not in any pTable
		final PrimeGqElement primeNotInPTables = smallPrimes.get(smallPrimes.size() - 1);

		// Choose any pTable to modify
		final int chosenVerificationCardSetContextIndex = 0;
		final VerificationCardSetContext verificationCardSetContext = verificationCardSetContexts.get(chosenVerificationCardSetContextIndex);
		final GroupVector<PrimesMappingTableEntry, GqGroup> pTable = verificationCardSetContext.getPrimesMappingTable().getPTable();

		// Choose a voting option that does not represent a blank voting option
		final int chosenPTableIndex = 2;
		final PrimesMappingTableEntry pTableEntry = pTable.get(chosenPTableIndex);
		final List<PrimesMappingTableEntry> pTableModified = IntStream.range(0, pTable.size())
				.mapToObj(i -> (i == chosenPTableIndex)
						? new PrimesMappingTableEntry(pTableEntry.actualVotingOption(), primeNotInPTables, pTableEntry.semanticInformation(),
						pTableEntry.correctnessInformation())
						: pTable.get(i)
				).toList();

		return IntStream.range(0, verificationCardSetContexts.size())
				.mapToObj(i -> (i == chosenVerificationCardSetContextIndex)
						? new VerificationCardSetContext.Builder()
						.setVerificationCardSetId(verificationCardSetContext.getVerificationCardSetId())
						.setVerificationCardSetAlias(verificationCardSetContext.getVerificationCardSetAlias())
						.setVerificationCardSetDescription(verificationCardSetContext.getVerificationCardSetDescription())
						.setBallotBoxId(verificationCardSetContext.getBallotBoxId())
						.setBallotBoxStartTime(verificationCardSetContext.getBallotBoxStartTime())
						.setBallotBoxFinishTime(verificationCardSetContext.getBallotBoxFinishTime())
						.setTestBallotBox(verificationCardSetContext.isTestBallotBox())
						.setNumberOfVotingCards(verificationCardSetContext.getNumberOfVotingCards())
						.setGracePeriod(verificationCardSetContext.getGracePeriod())
						.setPrimesMappingTable(PrimesMappingTable.from(pTableModified))
						.setDomainsOfInfluence(verificationCardSetContext.getDomainsOfInfluence())
						.build()
						: verificationCardSetContexts.get(i))
				.toList();
	}
}
