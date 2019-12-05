package ch.post.it.evoting.verifier.common.block.dto.revised;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.Base64;

public class PreImageProof {
    public final BigInteger q; // Check if necessary ?!? why is it repeated here?
    public final String base64hash;
    public final BigInteger z;

    public PreImageProof(@JsonProperty("q") BigInteger q, @JsonProperty("hash") String base64hash,
                         @JsonProperty("z") BigInteger z) {
        this.q = q;
        this.base64hash = base64hash;
        this.z = z;
    }

    public byte[] getH() {
        return Base64.getDecoder().decode(base64hash);
    }
}
