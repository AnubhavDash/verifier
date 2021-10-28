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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class CandidateList {

	private final boolean isBlank;
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger primeNumber;
	private final List<CandidatePosition> candidatePositions;
	private final String alias;

	public CandidateList(
			@JsonProperty("isBlank")
					boolean isBlank,
			@JsonProperty("primeNumber")
					BigInteger primeNumber,
			@JsonProperty("candidatePositions")
					CandidatePosition[] candidatePositions,
			@JsonProperty("alias")
					String alias) {
		this.isBlank = isBlank;
		this.primeNumber = primeNumber;
		this.candidatePositions = ImmutableList.copyOf(candidatePositions);
		this.alias = alias;
	}
}
