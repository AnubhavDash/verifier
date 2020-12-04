package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class Ciphertext {

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private final BigInteger gamma;
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private final List<BigInteger> phis;

    public Ciphertext(@JsonProperty("gamma") BigInteger gamma,
                      @JsonProperty("phis") BigInteger[] phis) {
        this.gamma = gamma;
        this.phis = ImmutableList.copyOf(phis);
    }
}
