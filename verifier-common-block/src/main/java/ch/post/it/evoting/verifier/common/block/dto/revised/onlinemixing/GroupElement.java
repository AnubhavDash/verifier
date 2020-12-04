package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class GroupElement {

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private final BigInteger value;
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private final BigInteger p;
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private final BigInteger q;

    public GroupElement(@JsonProperty("value") BigInteger value,
                        @JsonProperty("p") BigInteger p,
                        @JsonProperty("q") BigInteger q) {
        this.value = value;
        this.p = p;
        this.q = q;
    }
}
