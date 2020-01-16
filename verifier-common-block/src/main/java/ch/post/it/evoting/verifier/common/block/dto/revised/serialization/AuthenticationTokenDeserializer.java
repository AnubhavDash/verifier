package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import ch.post.it.evoting.verifier.common.block.dto.revised.AuthenticationToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

public class AuthenticationTokenDeserializer extends JsonDeserializer<AuthenticationToken> {
    private final ObjectMapper mapper;

    public AuthenticationTokenDeserializer() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        SimpleModule typesModule = new SimpleModule();
        typesModule.addDeserializer(UUID.class, new UuidDeserializer());
        typesModule.addDeserializer(BigInteger.class, new Base64BigIntegerDeserializer());
        typesModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(typesModule);
    }

    @Override
    public AuthenticationToken deserialize(JsonParser jsonParser,
                                           DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        if (jsonParser.currentToken().isStructStart()) {
            return mapper.readValue(jsonParser, AuthenticationToken.class);
        } else {
            String value = jsonParser.getValueAsString();
            return mapper.readValue(value, AuthenticationToken.class);
        }
    }
}
