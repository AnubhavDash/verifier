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
    private List<BigInteger> b_vec;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> e_vec;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger m;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger output;

    @JsonCreator
    public ModExpProductParameters(@JsonProperty("id") String id,
                            @JsonProperty("b_vec") List<BigInteger> b_vec,
                            @JsonProperty("e_vec") List<BigInteger> e_vec,
                            @JsonProperty("m") BigInteger m,
                            @JsonProperty("output") BigInteger output) {
        this.id = id;
        this.b_vec = b_vec;
        this.e_vec = e_vec;
        this.m = m;
        this.output = output;
    }

}
