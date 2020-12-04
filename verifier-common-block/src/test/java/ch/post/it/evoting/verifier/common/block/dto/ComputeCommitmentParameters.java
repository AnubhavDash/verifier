package ch.post.it.evoting.verifier.common.block.dto;


import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.CommitmentKey;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
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

}
