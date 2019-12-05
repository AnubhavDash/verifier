package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import ch.post.it.evoting.verifier.common.block.dto.revised.PreImageProof;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.math.BigInteger;
import java.util.Base64;

public class PreImageProofDeserializer extends ProofDeserializer<PreImageProof> {

    @Override
    protected PreImageProof instantiateProof(BigInteger q, String hash, JsonNode values,
                                             JsonParser jsonParser) throws InvalidFormatException {
        Base64.Decoder decoder = Base64.getDecoder();
        if (values.size() != 1) {
            throw new InvalidFormatException(jsonParser, "wrong number of values (expects 1)", values.asText(),
                                             BigInteger.class);
        }
        BigInteger z = new BigInteger( decoder.decode(values.get(0).asText()));
        return new PreImageProof(q, hash, z);
    }
}

