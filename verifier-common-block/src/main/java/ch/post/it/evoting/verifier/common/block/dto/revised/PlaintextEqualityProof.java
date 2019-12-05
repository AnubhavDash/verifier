package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.Arrays;

public class PlaintextEqualityProof {
    public final BigInteger q; // Does it really have to be repeated?
    public final byte[] h;
    public final BigInteger c_0;
    public final BigInteger c_1;

    @JsonCreator
    public PlaintextEqualityProof(@JsonProperty("q") BigInteger q, @JsonProperty("h") byte[] h,
                                  @JsonProperty("c_0") BigInteger c_0, @JsonProperty("c_1") BigInteger c_1) {
        this.q = q;
        this.h = Arrays.copyOf(h, h.length);
        this.c_0 = c_0;
        this.c_1 = c_1;
    }
}
