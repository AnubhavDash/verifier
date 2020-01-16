package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

public abstract class ProofDeserializer<T> extends JsonDeserializer<T> {
    private final ObjectMapper mapper;

    public ProofDeserializer() {
        mapper = new ObjectMapper();
    }


    @Override
    public T deserialize(JsonParser jsonParser,
                         DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String value = jsonParser.getValueAsString();
        JsonNode root = mapper.readTree(value);

        JsonNode zkProof = root.path("zkProof");

        Base64.Decoder decoder = Base64.getDecoder();
        BigInteger q = new BigInteger(decoder.decode(zkProof.path("q").asText()));
        String hash = zkProof.path("hash").asText();
        JsonNode values = zkProof.path("values");

        return instantiateProof(q, hash, values, jsonParser);
    }

    protected abstract T instantiateProof(BigInteger q, String hash, JsonNode values,
                                          JsonParser jsonParser) throws InvalidFormatException;
}
