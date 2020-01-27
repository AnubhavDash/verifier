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
public class ComputePhiExponentiationParameters {

    private String id;

    private EncryptionGroup eg;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> g_vec;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger x;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> output_vec;

    @JsonCreator
    public ComputePhiExponentiationParameters(@JsonProperty("id") String id,
                                              @JsonProperty("eg") EncryptionGroup eg,
                                              @JsonProperty("a_vec") List<BigInteger> g_vec,
                                              @JsonProperty("output_vec") List<BigInteger> output_vec) {
        this.id = id;
        this.eg = eg;
        this.g_vec = g_vec;
        this.output_vec = output_vec;
    }
}
