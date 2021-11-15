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
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;

public class ElectionEventDeserializer extends JsonDeserializer<ElectionEvent> {
	private final ObjectMapper mapper;

	public ElectionEventDeserializer() {
		mapper = new ObjectMapper();

		var simpleModule = new SimpleModule();
		simpleModule.addDeserializer(UUID.class, new UuidDeserializer());
		simpleModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
		simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
		mapper.registerModule(simpleModule);
	}

	@Override
	public ElectionEvent deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException {
		JsonNode root = mapper.readTree(jsonParser);

		return mapper.readValue(root.path("electionEvent").traverse(), ElectionEvent.class);
	}
}
