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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import ch.post.it.evoting.verifier.common.block.dto.revised.PublicKey;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;

public class PublicKeyDeserializer extends JsonDeserializer<PublicKey> {
	private final ObjectMapper mapper;

	public PublicKeyDeserializer() {
		mapper = new ObjectMapper();

		SimpleModule typesModule = new SimpleModule();
		typesModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
		mapper.registerModule(typesModule);
	}

	@Override
	public PublicKey deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		boolean isStructure = jsonParser.currentToken().isStructStart();

		JsonNode root;
		if (!isStructure) {
			String value = jsonParser.readValueAs(String.class);

			byte[] bytes = TypeConverter.base64ToByte(value);

			root = mapper.readTree(bytes);
		} else {
			root = mapper.readTree(jsonParser);
		}

		JsonNode publicKeyNode = root.path("publicKey");

		// Encryption group
		EncryptionGroup group = mapper.readValue(publicKeyNode.path("zpSubgroup").traverse(), EncryptionGroup.class);

		// Elements
		JsonNode elements = publicKeyNode.path("elements");
		List<BigInteger> keys = new ArrayList<>();
		for (JsonNode element : elements) {
			keys.add(new BigInteger(Base64.getDecoder().decode(element.asText())));
		}

		return new PublicKey(group, keys);
	}
}
