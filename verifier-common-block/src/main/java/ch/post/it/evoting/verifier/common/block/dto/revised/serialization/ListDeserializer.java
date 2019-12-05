package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        typesModule.addDeserializer(UUID.class, new UuidFromStringDeserializer());
        mapper.registerModule(typesModule);
    }

    @Override
    public List<?> deserialize(JsonParser jsonParser,
                               DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.readValueAs(String.class);
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
