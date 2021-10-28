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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import ch.post.it.evoting.verifier.common.block.dto.revised.AuthenticationToken;

public class AuthenticationTokenDeserializer extends JsonDeserializer<AuthenticationToken> {
	private final ObjectMapper mapper;

	public AuthenticationTokenDeserializer() {
		mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		SimpleModule typesModule = new SimpleModule();
		typesModule.addDeserializer(UUID.class, new UuidDeserializer());
		typesModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
		typesModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
		mapper.registerModule(typesModule);
	}

	@Override
	public AuthenticationToken deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		if (jsonParser.currentToken().isStructStart()) {
			return mapper.readValue(jsonParser, AuthenticationToken.class);
		} else {
			String value = jsonParser.getValueAsString();
			return mapper.readValue(value, AuthenticationToken.class);
		}
	}
}
