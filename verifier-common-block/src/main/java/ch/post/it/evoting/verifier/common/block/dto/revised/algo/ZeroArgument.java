package ch.post.it.evoting.verifier.common.block.dto.revised.algo;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ZeroArgument implements Argument {

    @JsonProperty("c_a0")
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger c_a0;

    @JsonProperty("c_bm")
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger c_bm;

    @JsonProperty("c_d")
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> c_d_vec;

    @JsonProperty("a")
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> a_vec;

    @JsonProperty("b")
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> b_vec;

    @JsonProperty("r")
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger r;

    @JsonProperty("s")
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger s;

    @JsonProperty("t")
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger t;

}
