package ch.post.it.evoting.verifier.common.block.dto;

import ch.post.it.evoting.verifier.common.block.converter.HexStringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.converter.HexStringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

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
    @JsonDeserialize(converter = HexStringArrayToBigIntegerListConverter.class)
    private List<BigInteger> b;

    @JsonProperty("e")
    @JsonDeserialize(converter = HexStringArrayToBigIntegerListConverter.class)
    private List<BigInteger> e;

    @JsonProperty("m")
    @JsonDeserialize(converter = HexStringToBigIntegerConverter.class)
    private BigInteger m;

    @JsonProperty("output")
    @JsonDeserialize(converter = HexStringToBigIntegerConverter.class)
    private BigInteger output;

}
