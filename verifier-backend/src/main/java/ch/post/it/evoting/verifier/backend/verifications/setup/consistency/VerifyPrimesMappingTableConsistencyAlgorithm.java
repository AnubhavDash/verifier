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
package ch.post.it.evoting.verifier.backend.verifications.setup.consistency;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTable;
import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

public class VerifyPrimesMappingTableConsistencyAlgorithm {

	/**
	 * Verifies that all PrimesMappingTables are consistent.
	 * <ul>
	 *     <li>A PrimesMappingTable must not contain duplicate encoded voting options. This is ensured by {@link PrimesMappingTable#from(List)}</li>
	 *     <li>The same encoded voting option must have the same actual voting option in each table</li>
	 * </ul>
	 *
	 * @param primesMappingTables the list of PrimesMappingTables, one per verification card set. Must be non-null and not empty.
	 * @return {@code true} if the PrimesMappingTables are consistent, {@code false} otherwise}
	 */
	public boolean verifyPrimesMappingTableConsistency(final List<PrimesMappingTable> primesMappingTables) {
		checkNotNull(primesMappingTables);
		checkArgument(!primesMappingTables.isEmpty());
		primesMappingTables.stream().parallel().forEach(Preconditions::checkNotNull);

		// Check that the same encoded voting option has the same actual voting option in all PrimesMappingTables
		final Set<PrimesMappingTableEntry> primesMappingTableEntries = primesMappingTables.stream()
				.parallel()
				.map(PrimesMappingTable::getPTable)
				.flatMap(GroupVector::stream)
				.collect(Collectors.toSet());
		final Set<PrimeGqElement> encodedVotingOptions = primesMappingTableEntries.stream()
				.parallel()
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.collect(Collectors.toSet());

		return primesMappingTableEntries.size() == encodedVotingOptions.size();
	}

}
