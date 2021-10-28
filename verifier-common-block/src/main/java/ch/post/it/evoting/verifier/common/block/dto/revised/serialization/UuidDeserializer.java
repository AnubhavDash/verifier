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
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class UuidDeserializer extends JsonDeserializer<UUID> {
	@Override
	public UUID deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		String value = jsonParser.getValueAsString();
		if (value.length() == 32) {
			value = String.format("%s-%s-%s-%s-%s", value.substring(0, 8), value.substring(8, 12),
					value.substring(12, 16), value.substring(16, 20), value.substring(20, 32));
		}

		if (value.length() != 36) {
			throw new InvalidFormatException(jsonParser, "UUIDs can be encoded as 32 or 36-char representations", value,
					UUID.class);
		}
		return UUID.fromString(value);
	}
}
