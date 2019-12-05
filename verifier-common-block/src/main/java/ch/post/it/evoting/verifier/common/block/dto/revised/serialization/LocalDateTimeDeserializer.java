package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser,
                                     DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        long timestamp = jsonParser.getValueAsLong();
        Instant instant = Instant.ofEpochMilli(timestamp);
        // TODO: The timezone below should ideally be loaded from data provided along with the verification data.
        return LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Zurich"));
    }
}
