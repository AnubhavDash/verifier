package ch.post.it.evoting.verifier.common.block.dto;


import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class ComputePlaintextEqualityParameters {

    private String id;

    private EncryptionGroup eg;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> h_vec;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> h_vec_bar;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger r;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger r_bar;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> output_vec;

    @JsonCreator
    public ComputePlaintextEqualityParameters(@JsonProperty("id") String id,
                                              @JsonProperty("eg") EncryptionGroup eg,
                                              @JsonProperty("h_vec") List<BigInteger> h_vec,
                                              @JsonProperty("h_vec_bar") List<BigInteger> h_vec_bar,
                                              @JsonProperty("r") BigInteger r,
                                              @JsonProperty("r_bar") BigInteger r_bar,
                                              @JsonProperty("output_vec") List<BigInteger> output_vec) {
        this.id = id;
        this.eg = eg;
        this.h_vec = h_vec;
        this.h_vec_bar = h_vec_bar;
        this.r = r;
        this.r_bar = r_bar;
        this.output_vec = output_vec;
    }

}
