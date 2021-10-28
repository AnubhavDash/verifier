/*
 * Copyright 2021 Post CH Ltd
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
package ch.post.it.evoting.verifier.common.block.dto.revised;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;

import lombok.Getter;

@Getter
public class CandidatePosition {

	private final String alias;
	private final UUID candidateListId;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private final List<BigInteger> primeNumbers;

	private final int initialAccumulation;
	private final boolean isBlank;

	public CandidatePosition(
			@JsonProperty("alias")
					String alias,
			@JsonProperty("candidateListId")
					UUID candidateListId,
			@JsonProperty("primeNumber")
					List<BigInteger> primeNumber,
			@JsonProperty("initialAccumulation")
					int initialAccumulation,
			@JsonProperty("isBlank")
					boolean isBlank) {
		this.alias = alias;
		this.candidateListId = candidateListId;
		this.primeNumbers = ImmutableList.copyOf(primeNumber);
		this.initialAccumulation = initialAccumulation;
		this.isBlank = isBlank;
	}
}
