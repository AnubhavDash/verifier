package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Base64;

@Getter
public class PlaintextEqualityProof {
    public final BigInteger q; // Does it really have to be repeated?
    public final String base64hash;
    public final BigInteger c_0;
    public final BigInteger c_1;

    @JsonCreator
    public PlaintextEqualityProof(@JsonProperty("q") BigInteger q, @JsonProperty("h") String base64hash,
                                  @JsonProperty("c_0") BigInteger c_0, @JsonProperty("c_1") BigInteger c_1) {
        this.q = q;
        this.base64hash = base64hash;
        this.c_0 = c_0;
        this.c_1 = c_1;
    }

    public byte[] getH() {
        return Base64.getDecoder().decode(base64hash);
    }
}
