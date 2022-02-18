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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.core.internal.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class EncryptionParameters {

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger p;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger q;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger g;

	private final String seed;
	private final int pCounter;
	private final int qCounter;

	public EncryptionParameters(
			@JsonProperty("p")
					BigInteger p,
			@JsonProperty("q")
					BigInteger q,
			@JsonProperty("g")
					BigInteger g,
			@JsonProperty("seed")
					String seed,
			@JsonProperty("pCounter")
					int pCounter,
			@JsonProperty("qCounter")
					int qCounter) {
		this.p = p;
		this.q = q;
		this.g = g;
		this.seed = seed;
		this.pCounter = pCounter;
		this.qCounter = qCounter;
	}
}
