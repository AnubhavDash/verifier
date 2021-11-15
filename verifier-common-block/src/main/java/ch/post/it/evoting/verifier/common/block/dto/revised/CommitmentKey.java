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

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
@SuppressWarnings({ "java:S116", "java:S117" })
public class CommitmentKey {

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger h;
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private final List<BigInteger> g_vec;

	public CommitmentKey(
			@JsonProperty("h")
					BigInteger h,
			@JsonProperty("g_vec")
					List<BigInteger> g_vec) {
		this.h = h;
		this.g_vec = g_vec;
	}
}
