package ch.post.it.evoting.verifier.common.block.dto.revised.serialization;

import ch.post.it.evoting.verifier.common.block.dto.revised.PlaintextEqualityProof;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.math.BigInteger;
import java.util.Base64;

public class PlaintextEqualityProofDeserializer extends ProofDeserializer<PlaintextEqualityProof> {

    @Override
    protected PlaintextEqualityProof instantiateProof(BigInteger q, byte[] h, JsonNode values,
                                                      JsonParser jsonParser) throws InvalidFormatException {
        Base64.Decoder decoder = Base64.getDecoder();
        if (values.size() != 2) {
            throw new InvalidFormatException(jsonParser, "wrong number of values (expects 1)", values.asText(),
                                             BigInteger.class);
        }
        BigInteger c_0 = new BigInteger( decoder.decode(values.get(0).asText()));
        BigInteger c_1 = new BigInteger( decoder.decode(values.get(1).asText()));
        return new PlaintextEqualityProof(q, h, c_0, c_1);
    }
}
