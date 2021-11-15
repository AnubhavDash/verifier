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
package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public abstract class ProofDeserializer<T> extends JsonDeserializer<T> {
	private final ObjectMapper mapper;

	protected ProofDeserializer() {
		mapper = new ObjectMapper();
	}

	@Override
	public T deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException {
		var value = jsonParser.getValueAsString();
		JsonNode root = mapper.readTree(value);

		JsonNode zkProof = root.path("zkProof");

		var decoder = Base64.getDecoder();
		var q = new BigInteger(decoder.decode(zkProof.path("q").asText()));
		String hash = zkProof.path("hash").asText();
		JsonNode values = zkProof.path("values");

		return instantiateProof(q, hash, values, jsonParser);
	}

	protected abstract T instantiateProof(BigInteger q, String hash, JsonNode values,
			JsonParser jsonParser) throws InvalidFormatException;
}
