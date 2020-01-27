package ch.post.it.evoting.verifier.common.block.dto;


import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.CommitmentKey;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class ComputeCommitmentParameters {

    private String id;

    private EncryptionGroup eg;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger r;

    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> a_vec;

    private CommitmentKey ck;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger output;

    @JsonCreator
    public ComputeCommitmentParameters(@JsonProperty("id") String id,
                                       @JsonProperty("eg") EncryptionGroup eg,
                                       @JsonProperty("r") BigInteger r,
                                       @JsonProperty("a_vec") List<BigInteger> a_vec,
                                       @JsonProperty("ck") CommitmentKey ck,
                                       @JsonProperty("output") BigInteger output) {
        this.id = id;
        this.eg = eg;
        this.r = r;
        this.a_vec = a_vec;
        this.ck = ck;
        this.output = output;
    }

}
