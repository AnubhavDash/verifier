package ch.post.it.evoting.verifier.common.block.dto;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ModInvParameters {

    private String id;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger b;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger m;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger output;

    @JsonCreator
    public ModInvParameters(@JsonProperty("id") String id,
                            @JsonProperty("b") BigInteger b,
                            @JsonProperty("m") BigInteger m,
                            @JsonProperty("output") BigInteger output) {
        this.id = id;
        this.b = b;
        this.m = m;
        this.output = output;
    }

}
