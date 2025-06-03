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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableList.toImmutableList;
import static ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet.toImmutableSet;
import static ch.post.it.evoting.evotinglibraries.domain.validations.Validations.hasNoDuplicates;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.it.evoting.cryptoprimitives.collection.ImmutableList;
import ch.post.it.evoting.cryptoprimitives.collection.ImmutableSet;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.evotinglibraries.domain.election.ElectionEventContext;
import ch.post.it.evoting.evotinglibraries.domain.election.PartialPrimesMappingTableEntry;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTable;
import ch.post.it.evoting.evotinglibraries.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.evotinglibraries.domain.election.VerificationCardSetContext;
import ch.post.it.evoting.evotinglibraries.xml.primesmappingtable.PartialPrimesMappingTableEntryBuilder;
import ch.post.it.evoting.evotinglibraries.xml.xmlns.evotingconfig.Configuration;

@Service
public class VerifyPrimesMappingTableConsistencyAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyPrimesMappingTableConsistencyAlgorithm.class);

	/**
	 * Verifies that all PrimesMappingTables of the given election event context are consistent.
	 * <ul>
	 *     <li>A PrimesMappingTable must not contain duplicate encoded voting options.</li>
	 *     <li>The same actual voting option must have the same encoded voting option in each table.</li>
	 *     <li>The same actual voting option must have the same semantic information in each table.</li>
	 *     <li>The same actual voting option must have the same correctness information in each table.</li>
	 *     <li>The actual voting options, semantic information and correctness information in the pTable correspond to the configuration XML.</li>
	 *     <li>The number of tuples in the pTable correspond to the configuration XML taking into account possible accumulation of candidates.</li>
	 * </ul>
	 *
	 * @param electionEventContext the election event context, containing a list of PrimesMappingTables, one per verification card set. Must be
	 *                             non-null.
	 * @param configuration        the configuration XML. Must be non-null.
	 * @return {@code true} if the PrimesMappingTables are consistent, {@code false} otherwise
	 */
	public boolean verifyPrimesMappingTableConsistency(final ElectionEventContext electionEventContext, final Configuration configuration) {
		checkNotNull(electionEventContext);
		checkNotNull(configuration);

		final ImmutableList<PrimesMappingTable> primesMappingTables = electionEventContext.verificationCardSetContexts().stream()
				.map(VerificationCardSetContext::getPrimesMappingTable)
				.collect(toImmutableList());
		checkArgument(!primesMappingTables.isEmpty());

		primesMappingTables.forEach(primesMappingTable -> checkArgument(hasNoDuplicates(primesMappingTable.pTable().stream()
						.map(PrimesMappingTableEntry::encodedVotingOption)
						.collect(GroupVector.toGroupVector())),
				"The primes mapping table entries contain duplicated encoded voting options."));

		// Join the PrimesMappingTables of all verification card sets, deleting duplicates.
		final ImmutableSet<PrimesMappingTableEntry> primesMappingTableEntries = primesMappingTables.stream()
				.map(PrimesMappingTable::pTable)
				.flatMap(GroupVector::stream)
				.collect(toImmutableSet());

		// Create the actual voting option, semantic information and correctness information mapping using the configuration XML.
		final ImmutableSet<PartialPrimesMappingTableEntry> configurationPartialPrimesMappingTableEntries = electionEventContext.verificationCardSetContexts()
				.stream()
				.map(VerificationCardSetContext::getDomainsOfInfluence)
				.map(domainOfInfluence -> PartialPrimesMappingTableEntryBuilder.create(configuration, domainOfInfluence))
				.flatMap(ImmutableList::stream)
				.collect(toImmutableSet());

		final ImmutableList<BiFunction<ImmutableSet<PrimesMappingTableEntry>, ImmutableSet<PartialPrimesMappingTableEntry>, Boolean>> consistencyVerifications = ImmutableList.of(
				this::verifyCorrectMappingInAllVerificationCardSets,
				this::verifyInformationCorrespondsToConfiguration,
				this::verifyNumberOfTuplesCorrespondsToConfiguration);

		return consistencyVerifications
				.stream()
				.parallel()
				.map(f -> f.apply(primesMappingTableEntries, configurationPartialPrimesMappingTableEntries))
				.reduce(Boolean::logicalAnd)
				.orElse(Boolean.FALSE);
	}

	/**
	 * Verifies that the same actual voting option has the same encoded voting option, that the same actual voting option has the same semantic
	 * information and that the same actual voting option has the same correctness information in all PrimesMappingTables.
	 *
	 * @param configurationPartialPrimesMappingTableEntries ignored, needed for consistency in the signature of the verification methods.
	 */
	@SuppressWarnings("java:S1172")
	private boolean verifyCorrectMappingInAllVerificationCardSets(final ImmutableSet<PrimesMappingTableEntry> primesMappingTableEntries,
			final ImmutableSet<PartialPrimesMappingTableEntry> configurationPartialPrimesMappingTableEntries) {
		final ImmutableSet<PrimeGqElement> encodedVotingOptions = primesMappingTableEntries.stream()
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.collect(toImmutableSet());

		final ImmutableSet<String> actualVotingOptions = primesMappingTableEntries.stream()
				.map(PrimesMappingTableEntry::actualVotingOption)
				.collect(toImmutableSet());

		final boolean correctMapping = primesMappingTableEntries.size() == encodedVotingOptions.size() &&
				primesMappingTableEntries.size() == actualVotingOptions.size();
		if (!correctMapping) {
			LOGGER.error(
					"The encoded voting options, actual voting options, semantic information and correctness information mapping is not the same in all verification card sets.");
		}
		return correctMapping;
	}

	/**
	 * Verifies that the actual voting options, semantic information and correctness information in the PrimesMappingTable correspond to the
	 * configuration XML.
	 */
	private boolean verifyInformationCorrespondsToConfiguration(final ImmutableSet<PrimesMappingTableEntry> primesMappingTableEntries,
			final ImmutableSet<PartialPrimesMappingTableEntry> configurationPartialPrimesMappingTableEntries) {
		final ImmutableSet<PartialPrimesMappingTableEntry> partialPrimesMappingTableEntries = primesMappingTableEntries.stream()
				.parallel()
				.map(entry -> new PartialPrimesMappingTableEntry(entry.actualVotingOption(), entry.semanticInformation(),
						entry.correctnessInformation()))
				.collect(toImmutableSet());

		final boolean informationCorrespondsToConfiguration = partialPrimesMappingTableEntries.equals(configurationPartialPrimesMappingTableEntries);
		if (!informationCorrespondsToConfiguration) {
			LOGGER.error("The actual voting options, semantic information and correctness information do not correspond to the configuration XML.");
		}
		return informationCorrespondsToConfiguration;
	}

	/**
	 * Verifies that the number of tuples in the pTable corresponds to the configuration XML.
	 */
	private boolean verifyNumberOfTuplesCorrespondsToConfiguration(final ImmutableSet<PrimesMappingTableEntry> primesMappingTableEntries,
			final ImmutableSet<PartialPrimesMappingTableEntry> configurationPartialPrimesMappingTableEntries) {
		final int expectedPrimesMappingTableEntriesSize = configurationPartialPrimesMappingTableEntries.size();

		final boolean numberOfTuplesCorrespondsToConfiguration = primesMappingTableEntries.size() == expectedPrimesMappingTableEntriesSize;
		if (!numberOfTuplesCorrespondsToConfiguration) {
			LOGGER.error(
					"The number of tuples in the pTable does not correspond to the configuration XML.");
		}

		return numberOfTuplesCorrespondsToConfiguration;
	}
}
