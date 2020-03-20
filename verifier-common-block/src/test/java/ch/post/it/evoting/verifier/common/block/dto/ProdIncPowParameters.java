package ch.post.it.evoting.verifier.common.block.dto;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;
import java.util.List;

@Getter
public class ProdIncPowParameters {

    @JsonProperty("eg")
    private EncryptionGroup encryptionGroup;

    @JsonProperty("a")
    @JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
    private List<BigInteger> a_vec;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger x;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger output;

}
