package ch.post.it.evoting.verifier.common.block.dto;


import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ComputePhiSchnorrParameters {

    private String id;

    private EncryptionGroup eg;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger x;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger output;

    @JsonCreator
    public ComputePhiSchnorrParameters(@JsonProperty("id") String id,
                                       @JsonProperty("eg") EncryptionGroup eg,
                                       @JsonProperty("x") BigInteger x,
                                       @JsonProperty("output") BigInteger output) {
        this.id = id;
        this.eg = eg;
        this.x = x;
        this.output = output;
    }

}
