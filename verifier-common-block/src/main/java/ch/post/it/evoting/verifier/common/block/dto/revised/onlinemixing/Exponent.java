package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class Exponent {

    private BigInteger q;
    private BigInteger value;

    public Exponent(@JsonProperty("q") BigInteger q,
                    @JsonProperty("value") BigInteger value) {
        this.q = q;
        this.value = value;
    }
}
