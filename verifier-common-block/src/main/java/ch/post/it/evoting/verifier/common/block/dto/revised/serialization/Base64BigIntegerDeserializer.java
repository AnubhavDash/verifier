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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class Base64BigIntegerDeserializer extends JsonDeserializer<BigInteger> {
	@Override
	public BigInteger deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		String value = jsonParser.getValueAsString();

		if (isBase64Encoded(value)) {
			byte[] bytes = Base64.getDecoder().decode(value);
			return new BigInteger(bytes);
		} else {
			return new BigInteger(value);
		}
	}

	private boolean isBase64Encoded(String value) {
		// BigInteger are serialized as hexadecimal strings, decimal strings or base64 encoded, depending on the location.
		// If not decimal or hexadecimal, assume Base64 encoding.
		return !value.matches("^[0-9a-fA-F]+$");
	}
}
