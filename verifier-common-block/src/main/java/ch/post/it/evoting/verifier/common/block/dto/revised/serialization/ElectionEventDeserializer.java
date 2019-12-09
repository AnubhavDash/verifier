package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

public class ElectionEventDeserializer extends JsonDeserializer<ElectionEvent> {
    private final ObjectMapper mapper;

    public ElectionEventDeserializer() {
        mapper = new ObjectMapper();

        SimpleModule typesModule = new SimpleModule();
        typesModule.addDeserializer(UUID.class, new UuidDeserializer());
        typesModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
        typesModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(typesModule);
    }

    @Override
    public ElectionEvent deserialize(JsonParser jsonParser,
                                     DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode root = mapper.readTree(jsonParser);

        return mapper.readValue(root.path("electionEvent").traverse(), ElectionEvent.class);
    }
}
