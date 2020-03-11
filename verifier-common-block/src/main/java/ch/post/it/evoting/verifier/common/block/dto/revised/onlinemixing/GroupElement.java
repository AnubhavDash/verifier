package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class GroupElement {

    private BigInteger value;
    private BigInteger p;
    private BigInteger q;

    public GroupElement(@JsonProperty("value") BigInteger value,
                        @JsonProperty("p") BigInteger p,
                        @JsonProperty("q") BigInteger q) {
        this.value = value;
        this.p = p;
        this.q = q;
    }
}
