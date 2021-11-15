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
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.Ciphertext;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;

public class CiphertextDeserializer extends JsonDeserializer<Ciphertext> {
	private final ObjectMapper mapper;

	public CiphertextDeserializer() {
		mapper = new ObjectMapper();

		var simpleModule = new SimpleModule();
		simpleModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
		mapper.registerModule(simpleModule);
	}

	@Override
	public Ciphertext deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException {

		JsonNode root = mapper.readTree(jsonParser);

		var gammaString = root.path("gamma").asText();
		BigInteger gamma;
		if (gammaString.indexOf(";") > 0) {
			gamma = TypeConverter.stringToBigInteger(gammaString.split(";")[0]);
		} else {
			gamma = TypeConverter.stringToBigInteger(gammaString);
		}

		BigInteger[] phis;
		if (root.path("phis").isArray()) {
			var arrayNode = (ArrayNode) root.path("phis");
			phis = new BigInteger[arrayNode.size()];
			for (var i = 0; i < arrayNode.size(); i++) {
				phis[i] = TypeConverter.stringToBigInteger(arrayNode.get(i).asText());
			}
		} else {
			var phisString = root.path("phis").asText();
			if (phisString.indexOf(",") > 0 || phisString.indexOf(";") > 0) {
				phis = Arrays.stream(phisString.split(","))
						.map(p -> TypeConverter.stringToBigInteger(p.split(";")[0]))
						.toArray(BigInteger[]::new);
			} else {
				phis = new BigInteger[] { TypeConverter.stringToBigInteger(phisString) };
			}
		}

		return new Ciphertext(gamma, phis);
	}
}
