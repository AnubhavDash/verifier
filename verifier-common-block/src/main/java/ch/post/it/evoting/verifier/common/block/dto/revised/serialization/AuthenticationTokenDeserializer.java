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
