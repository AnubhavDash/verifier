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
package ch.post.it.evoting.verifier.protocol.algorithms.tally.mixoffline;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTable;
import ch.post.it.evoting.cryptoprimitives.domain.election.PrimesMappingTableEntry;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Implements the DecodeVotingOptions algorithm.
 */
public class DecodeVotingOptionsAlgorithm {

	/**
	 * Returns the list of decoded voting options.
	 *
	 * @param encodedVotingOptions (p&#771;<sub>0</sub>, ..., p&#771;<sub>m-1</sub>)</li>, the encoded voting options. Must be non-null and all
	 *                             encoded voting options must be distinct.
	 * @param primesMappingTable   pTable, the primes mapping table of size n. Must be non-null.
	 * @return the list of decoded voting options, (v<sub>0</sub>, ..., v<sub>m-1</sub>).
	 * @throws NullPointerException     if any input is null.
	 * @throws IllegalArgumentException if
	 *                                  <ul>
	 *                                      <li>the inputs don't have the same group.</li>
	 *                                      <li>m is bigger or equal to n.</li>
	 *                                  </ul>
	 */
	public List<String> decodeVotingOptions(final GroupVector<PrimeGqElement, GqGroup> encodedVotingOptions,
			final PrimesMappingTable primesMappingTable) {
		checkNotNull(encodedVotingOptions);
		checkNotNull(primesMappingTable);

		checkArgument(primesMappingTable.getPTable().getGroup().equals(encodedVotingOptions.getGroup()),
				"The groups of the primes mapping table and the encoded voting options must be equal.");

		final int m = encodedVotingOptions.size();
		final int n = primesMappingTable.size(); // Guaranteed to be strictly positive by the Object itself.

		// Require
		checkArgument(m < n, "The size of the encoded voting options must be smaller than the size of the primes mapping table. [m: %s, n: %s]", m,
				n);
		checkArgument(encodedVotingOptions.stream().distinct().count() == encodedVotingOptions.size(),
				"The encoded voting options must all be distinct.");

		// Operation & Output
		return encodedVotingOptions.stream()
				.map(primesMappingTable::getPrimesMappingTableEntry)
				.map(PrimesMappingTableEntry::actualVotingOption)
				.toList();
	}

}
