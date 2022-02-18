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
package ch.post.it.evoting.verifier.core.internal.dto.revised.serialization;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.post.it.evoting.verifier.core.internal.dto.revised.EncryptionGroup;
import ch.post.it.evoting.verifier.core.internal.dto.revised.PublicKey;
import ch.post.it.evoting.verifier.core.internal.tools.TypeConverter;

public class PublicKeyDeserializer extends JsonDeserializer<PublicKey> {
	private final ObjectMapper mapper;

	public PublicKeyDeserializer() {
		mapper = new ObjectMapper();

		var simpleModule = new SimpleModule();
		simpleModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
		mapper.registerModule(simpleModule);
	}

	@Override
	public PublicKey deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException {
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
