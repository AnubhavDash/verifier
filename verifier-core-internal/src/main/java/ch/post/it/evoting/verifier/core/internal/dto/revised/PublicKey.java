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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.core.internal.dto.converter.StringArrayToBigIntegerListConverter;

import lombok.Getter;

@Getter
public class PublicKey {

	private final EncryptionGroup group;
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private final List<BigInteger> keys;

	public PublicKey(
			@JsonProperty("group")
					EncryptionGroup group,
			@JsonProperty("key")
					List<BigInteger> keys) {
		this.group = group;
		this.keys = keys;
	}
}
