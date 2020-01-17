package ch.post.it.evoting.verifier.common.block.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;
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
public class ModExpProductParameters {

    @JsonProperty("id")
    private String id;

    @JsonProperty("b")
    private List<BigInteger> b;

    @JsonProperty("e")
    private List<BigInteger> e;

    @JsonProperty("m")
    private BigInteger m;

    @JsonProperty("output")
    private BigInteger output;

}
