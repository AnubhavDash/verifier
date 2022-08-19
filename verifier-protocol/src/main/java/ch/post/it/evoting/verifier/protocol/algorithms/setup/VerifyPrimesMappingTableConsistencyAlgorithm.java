package ch.post.it.evoting.verifier.protocol.algorithms.setup;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;
import ch.post.it.evoting.verifier.protocol.domain.configuration.PrimesMappingTable;
import ch.post.it.evoting.verifier.protocol.domain.configuration.PrimesMappingTableEntry;

public class VerifyPrimesMappingTableConsistencyAlgorithm {

	/**
	 * Verifies that all PrimesMappingTables are consistent.
	 * <ul>
	 *     <li>A PrimesMappingTable must not contain duplicate encoded voting options</li>
	 *     <li>The same encoded voting option must have the same actual voting option in each table</li>
	 * </ul>
	 *
	 * @param primesMappingTables the list of PrimesMappingTables, one per verification card set. Must be non-null and not empty.
	 * @return {@code true} if the PrimesMappingTables are consistent, {@code false} otherwise}
	 */
	public boolean verifyPrimesMappingTableConsistency(final List<PrimesMappingTable> primesMappingTables) {
		checkNotNull(primesMappingTables);
		checkArgument(!primesMappingTables.isEmpty());
		primesMappingTables.forEach(Preconditions::checkNotNull);

		// 1. Check that neither of the PrimesMappingTables contains duplicates
		final Boolean uniquenessVerif = primesMappingTables.stream()
				.map(PrimesMappingTable::getPTable)
				.map(pTable -> pTable.size() == new HashSet<>(pTable).size())
				.reduce(Boolean::logicalAnd).orElse(false);

		// 2. Check that the same encoded voting option has the same actual voting option in all PrimesMappingTables
		final Set<PrimesMappingTableEntry> primesMappingTableEntries = primesMappingTables.stream()
				.map(PrimesMappingTable::getPTable)
				.flatMap(GroupVector::stream)
				.collect(Collectors.toSet());
		final Set<PrimeGqElement> encodedVotingOptions = primesMappingTableEntries.stream()
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.collect(Collectors.toSet());
		final boolean mappingVerif = primesMappingTableEntries.size() == encodedVotingOptions.size();

		return uniquenessVerif && mappingVerif;
	}

}
