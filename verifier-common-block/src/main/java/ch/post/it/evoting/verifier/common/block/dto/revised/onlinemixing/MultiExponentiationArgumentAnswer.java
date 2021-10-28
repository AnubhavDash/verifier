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
package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import lombok.Getter;

@Getter
public class MultiExponentiationArgumentAnswer {

	private final List<Exponent> exponentsA;
	private final Exponent exponentR;
	private final Exponent exponentsB;
	private final Exponent exponentS;
	private final RandomnessTau randomnessTau;

	public MultiExponentiationArgumentAnswer(
			@JsonProperty("exponentsA")
					Exponent[] exponentsA,
			@JsonProperty("exponentR")
					Exponent exponentR,
			@JsonProperty("exponentsB")
					Exponent exponentsB,
			@JsonProperty("exponentS")
					Exponent exponentS,
			@JsonProperty("randomnessTau")
					RandomnessTau randomnessTau) {
		this.exponentsA = ImmutableList.copyOf(exponentsA);
		this.exponentR = exponentR;
		this.exponentsB = exponentsB;
		this.exponentS = exponentS;
		this.randomnessTau = randomnessTau;
	}
}
