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
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
@SuppressWarnings({ "java:S116", "java:S117" })
public class PlaintextEqualityProof {

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger q; // Does it really have to be repeated?
	private final String base64hash;
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger c_0;
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger c_1;

	public PlaintextEqualityProof(
			@JsonProperty("q")
					BigInteger q,
			@JsonProperty("h")
					String base64hash,
			@JsonProperty("c_0")
					BigInteger c_0,
			@JsonProperty("c_1")
					BigInteger c_1) {
		this.q = q;
		this.base64hash = base64hash;
		this.c_0 = c_0;
		this.c_1 = c_1;
	}

	public byte[] getH() {
		return Base64.getDecoder().decode(base64hash);
	}
}
