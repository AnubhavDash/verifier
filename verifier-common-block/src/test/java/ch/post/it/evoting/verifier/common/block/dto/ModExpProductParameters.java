package ch.post.it.evoting.verifier.common.block.dto;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class ModExpProductParameters {

    private String id;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> b;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> e;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger m;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger output;

    @JsonCreator
    public ModExpProductParameters(@JsonProperty("id") String id,
                            @JsonProperty("b") List<BigInteger> b,
                            @JsonProperty("e") List<BigInteger> e,
                            @JsonProperty("m") BigInteger m,
                            @JsonProperty("output") BigInteger output) {
        this.id = id;
        this.b = b;
        this.e = e;
        this.m = m;
        this.output = output;
    }

}
