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
package ch.post.it.evoting.verifier.protocol.domain.configuration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.cryptoprimitives.hashing.Hashable;
import ch.post.it.evoting.cryptoprimitives.hashing.HashableList;
import ch.post.it.evoting.cryptoprimitives.math.GqGroup;
import ch.post.it.evoting.cryptoprimitives.math.GroupVector;
import ch.post.it.evoting.cryptoprimitives.math.PrimeGqElement;

/**
 * Represents the primes mapping table - pTable = ((v<sub>0</sub>, p&#771;<sub>0</sub>),...,(v<sub>n-1</sub>, p&#771;<sub>n-1</sub>)) - an ordered
 * table of {@link PrimesMappingTableEntry} elements with
 * <ul>
 *     <li>Actual voting options v&#771; = (v<sub>0</sub>,...,v<sub>n-1</sub>)</li>
 *     <li>Encoded voting options p&#771; = (p&#771;<sub>0</sub>,...,p&#771;<sub>n-1</sub>)</li>
 * </ul>
 * This class is immutable.
 */
@JsonPropertyOrder({ "pTable" })
public class PrimesMappingTable implements HashableList {

	@JsonDeserialize(using = PrimesMappingTableEntryGroupVectorDeserializer.class)
	@JsonProperty
	private GroupVector<PrimesMappingTableEntry, GqGroup> pTable;

	@JsonCreator
	public PrimesMappingTable(
			@JsonProperty("pTable")
			final GroupVector<PrimesMappingTableEntry, GqGroup> pTable) {
		this.pTable = checkNotNull(pTable);
	}

	/**
	 * Builds a primes mapping table based on the given primes mapping table entries.
	 *
	 * @param primesMappingTableEntries the primes mapping table entries to be built from. Must be non-null and its encoded voting options must not
	 *                                  contain any duplicate.
	 * @throws NullPointerException     if the primes mapping table entries is null.
	 * @throws IllegalArgumentException if the encoded voting options of the primes mapping table entries contain duplicates.
	 */
	public static PrimesMappingTable from(final List<PrimesMappingTableEntry> primesMappingTableEntries) {
		final List<PrimesMappingTableEntry> primesMappingTableEntriesCopy = List.copyOf(checkNotNull(primesMappingTableEntries));

		final Set<PrimeGqElement> encodedVotingOptions = primesMappingTableEntriesCopy.stream()
				.map(PrimesMappingTableEntry::encodedVotingOption)
				.collect(Collectors.toSet());

		checkArgument(encodedVotingOptions.size() == primesMappingTableEntriesCopy.size(),
				"The primes mapping table entries contain duplicated encoded voting options.");

		return new PrimesMappingTable(GroupVector.from(primesMappingTableEntriesCopy));
	}

	/**
	 * Returns the number of elements in this primes mapping table. If this primes mapping table contains more than {@code Integer.MAX_VALUE}
	 * elements, returns {@code Integer.MAX_VALUE}.
	 *
	 * @return the number of elements in this primes mapping table.
	 */
	public int size() {
		return pTable.size();
	}

	@JsonIgnore
	public GroupVector<PrimesMappingTableEntry, GqGroup> getPTable() {
		return pTable;
	}

	@Override
	public List<Hashable> toHashableForm() {
		return List.of(pTable);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final PrimesMappingTable that = (PrimesMappingTable) o;
		return pTable.equals(that.pTable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pTable);
	}
}
