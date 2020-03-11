package ch.post.it.evoting.verifier.common.block.dto;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class RandomOracleHashParameters {

    private String id;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger q;

    private String x;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger output;
}
