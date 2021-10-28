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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ListDeserializer extends JsonDeserializer<List<?>> {
	private final ObjectMapper mapper;

	public ListDeserializer() {
		mapper = new ObjectMapper();

		SimpleModule typesModule = new SimpleModule();
		typesModule.addDeserializer(UUID.class, new UuidDeserializer());
		mapper.registerModule(typesModule);
	}

	@Override
	public List<?> deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException {
		boolean isStructure = jsonParser.currentToken().isStructStart();

		if (!isStructure) {
			return parseAsString(jsonParser);
		} else {
			return mapper.readValue(jsonParser, List.class);
		}
	}

	private List<?> parseAsString(JsonParser jsonParser) throws IOException {
		String value = jsonParser.getValueAsString();
		if (value.matches("[0-9;]+")) {
			// Semi-column separated list of BigIntegers.
			String[] strings = value.split(";");
			return Arrays.stream(strings).map(BigInteger::new).collect(Collectors.toList());
		} else {
			// escaped json in property
			return mapper.readValue(value, List.class);
		}
	}

}
