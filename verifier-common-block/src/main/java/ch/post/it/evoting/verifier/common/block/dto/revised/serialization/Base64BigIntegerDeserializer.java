package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

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
