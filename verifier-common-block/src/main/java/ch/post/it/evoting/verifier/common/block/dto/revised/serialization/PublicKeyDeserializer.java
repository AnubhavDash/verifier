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

import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import ch.post.it.evoting.verifier.common.block.dto.revised.PublicKey;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

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

        EncryptionGroup group = mapper.readValue(publicKeyNode.path("zpSubgroup").traverse(), EncryptionGroup.class);
        JsonNode elements = publicKeyNode.path("elements");
        if (elements.size() != 1) {
            throw new InvalidFormatException(jsonParser, "wrong number of elements (expects 1)", elements.asText(),
                                             BigInteger.class);
        }
        BigInteger key = new BigInteger( Base64.getDecoder().decode(elements.get(0).asText()));

        return new PublicKey(group, key);
    }
}
