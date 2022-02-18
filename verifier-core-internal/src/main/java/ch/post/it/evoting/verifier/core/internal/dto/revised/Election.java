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
package ch.post.it.evoting.verifier.core.internal.dto.revised;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

import ch.post.it.evoting.verifier.core.internal.dto.converter.StringArrayToBigIntegerListConverter;

import lombok.Getter;

@Getter
public class Election {

	private final UUID id;
	private final String alias;
	private final int numberOfSeats;
	private final List<CandidateList> lists;
	private final List<Candidate> candidates;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private final List<BigInteger> writeIns;

	private final int changedBallotsWithPartyAffiliation;
	private final int changedBallotsWithoutPartyAffiliation;
	private final int emptyVotesOfChangedBallotsWithoutPartyAffiliation;

	public Election(
			@JsonProperty("id")
					UUID id,
			@JsonProperty("alias")
					String alias,
			@JsonProperty("numberOfSeats")
					int numberOfSeats,
			@JsonProperty("lists")
					CandidateList[] lists,
			@JsonProperty("candidates")
					Candidate[] candidates,
			@JsonProperty("writeIns")
					List<BigInteger> writeIns,
			@JsonProperty("changedBallotsWithPartyAffiliation")
					int changedBallotsWithPartyAffiliation,
			@JsonProperty("changedBallotsWithoutPartyAffiliation")
					int changedBallotsWithoutPartyAffiliation,
			@JsonProperty("emptyVotesOfChangedBallotsWithoutPartyAffiliation")
					int emptyVotesOfChangedBallotsWithoutPartyAffiliation) {
		this.id = id;
		this.alias = alias;
		this.numberOfSeats = numberOfSeats;
		this.lists = lists != null ? ImmutableList.copyOf(lists) : List.of();
		this.candidates = candidates != null ? ImmutableList.copyOf(candidates) : List.of();
		this.writeIns = writeIns != null ? ImmutableList.copyOf(writeIns) : List.of();
		this.changedBallotsWithPartyAffiliation = changedBallotsWithPartyAffiliation;
		this.changedBallotsWithoutPartyAffiliation = changedBallotsWithoutPartyAffiliation;
		this.emptyVotesOfChangedBallotsWithoutPartyAffiliation = emptyVotesOfChangedBallotsWithoutPartyAffiliation;
	}
}
