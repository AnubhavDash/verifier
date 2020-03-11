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

import ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing.Ciphertext;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class CiphertextDeserializer extends JsonDeserializer<Ciphertext> {
    private final ObjectMapper mapper;

    public CiphertextDeserializer() {
        mapper = new ObjectMapper();

        SimpleModule typesModule = new SimpleModule();
        typesModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
        mapper.registerModule(typesModule);
    }

    @Override
    public Ciphertext deserialize(JsonParser jsonParser,
                                  DeserializationContext deserializationContext) throws IOException {

        JsonNode root = mapper.readTree(jsonParser);

        String gammaString = root.path("gamma").asText();
        BigInteger gamma;
        if (gammaString.indexOf(";") > 0) {
            gamma = TypeConverter.stringToBigInteger(gammaString.split(";")[0]);
        } else {
            gamma = TypeConverter.stringToBigInteger(gammaString);
        }

        BigInteger[] phis;
        if (root.path("phis").isArray()) {
            ArrayNode arrayNode = (ArrayNode) root.path("phis");
            phis = new BigInteger[arrayNode.size()];
            for (int i = 0; i < arrayNode.size(); i++) {
                phis[i] = TypeConverter.stringToBigInteger(arrayNode.get(i).asText());
            }
        } else {
            String phisString = root.path("phis").asText();
            if (phisString.indexOf(";") > 0) {
                phis = Arrays.stream(phisString.split(";"))
                        .map(TypeConverter::stringToBigInteger)
                        .toArray(BigInteger[]::new);
            } else {
                phis = new BigInteger[] {TypeConverter.stringToBigInteger(phisString)};
            }
        }

        return new Ciphertext(gamma, phis);
    }
}
