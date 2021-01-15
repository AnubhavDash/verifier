/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public abstract class ProofDeserializer<T> extends JsonDeserializer<T> {
	private final ObjectMapper mapper;

	public ProofDeserializer() {
		mapper = new ObjectMapper();
	}

	@Override
	public T deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		String value = jsonParser.getValueAsString();
		JsonNode root = mapper.readTree(value);

		JsonNode zkProof = root.path("zkProof");

		Base64.Decoder decoder = Base64.getDecoder();
		BigInteger q = new BigInteger(decoder.decode(zkProof.path("q").asText()));
		String hash = zkProof.path("hash").asText();
		JsonNode values = zkProof.path("values");

		return instantiateProof(q, hash, values, jsonParser);
	}

	protected abstract T instantiateProof(BigInteger q, String hash, JsonNode values,
			JsonParser jsonParser) throws InvalidFormatException;
}
