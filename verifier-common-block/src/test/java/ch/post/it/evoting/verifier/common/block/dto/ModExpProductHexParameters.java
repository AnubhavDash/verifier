package ch.post.it.evoting.verifier.common.block.dto;

import ch.post.it.evoting.verifier.common.block.converter.HexStringArrayToBigIntegerVectorConverter;
import ch.post.it.evoting.verifier.common.block.converter.HexStringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Vector;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "b",
        "e",
        "m",
        "output"
})
public class ModExpProductHexParameters {

    @JsonProperty("id")
    private String id;

    @JsonProperty("b")
    @JsonDeserialize(converter = HexStringArrayToBigIntegerVectorConverter.class)
    private Vector<BigInteger> b;

    @JsonProperty("e")
    @JsonDeserialize(converter = HexStringArrayToBigIntegerVectorConverter.class)
    private Vector<BigInteger> e;

    @JsonProperty("m")
    @JsonDeserialize(converter = HexStringToBigIntegerConverter.class)
    private BigInteger m;

    @JsonProperty("output")
    @JsonDeserialize(converter = HexStringToBigIntegerConverter.class)
    private BigInteger output;

}
