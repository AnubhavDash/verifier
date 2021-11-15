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
package ch.post.it.evoting.verifier.common.block.dto.revised.algo;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("java:S116")
public class ZeroArgument implements Argument {

	@JsonProperty("c_a0")
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger c_a0;

	@JsonProperty("c_bm")
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger c_bm;

	@JsonProperty("c_d")
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> c_d_vec;

	@JsonProperty("a")
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> a_vec;

	@JsonProperty("b")
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> b_vec;

	@JsonProperty("r")
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger r;

	@JsonProperty("s")
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger s;

	@JsonProperty("t")
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger t;

}
