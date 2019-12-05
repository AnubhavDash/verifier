package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class PreImageProof {
    public final BigInteger q; // Check if necessary ?!? why is it repeated here?
    public final byte[] h;
    public final BigInteger z;

    public PreImageProof(@JsonProperty("q") BigInteger q, @JsonProperty("hash") byte[] h,
                         @JsonProperty("z") BigInteger z) {
        this.q = q;
        this.h = h;
        this.z = z;
    }
}
