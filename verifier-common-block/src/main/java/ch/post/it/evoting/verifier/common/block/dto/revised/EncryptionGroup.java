package ch.post.it.evoting.verifier.common.block.dto.revised;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class EncryptionGroup {
    public final BigInteger p;
    public final BigInteger q;
    public final BigInteger g;

    @JsonCreator
    public EncryptionGroup(@JsonProperty("p") BigInteger p, @JsonProperty("q") BigInteger q,
                           @JsonProperty("g") BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }
}
