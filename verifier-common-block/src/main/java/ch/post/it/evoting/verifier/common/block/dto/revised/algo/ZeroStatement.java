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
public class ZeroStatement implements Statement {

    @JsonProperty("c_a")
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> c_a_vec;

    @JsonProperty("c_b")
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> c_b_vec;

    @JsonProperty("y")
    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger y;

}
