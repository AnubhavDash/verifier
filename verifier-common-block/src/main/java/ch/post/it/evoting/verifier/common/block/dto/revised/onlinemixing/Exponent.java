package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class Exponent {

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private final BigInteger q;
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private final BigInteger value;

    public Exponent(@JsonProperty("q") BigInteger q,
                    @JsonProperty("value") BigInteger value) {
        this.q = q;
        this.value = value;
    }
}
