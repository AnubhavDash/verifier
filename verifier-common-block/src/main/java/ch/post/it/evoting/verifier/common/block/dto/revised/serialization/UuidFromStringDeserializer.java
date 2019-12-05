package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.util.UUID;

public class UuidFromStringDeserializer extends JsonDeserializer<UUID> {
    @Override
    public UUID deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.readValueAs(String.class);
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
