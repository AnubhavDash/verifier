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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
