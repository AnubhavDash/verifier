package ch.post.it.evoting.verifier.common.block.dto;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.CommitmentKey;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import ch.post.it.evoting.verifier.common.block.dto.revised.algo.SVPArgument;
import ch.post.it.evoting.verifier.common.block.dto.revised.algo.SVPStatement;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class VerifySVPArgumentParameters {

    private String id;

    private EncryptionGroup eg;

    private CommitmentKey ck;

    @JsonDeserialize(converter = StringToBigIntegerConverter.class)
    private BigInteger pk_mix;

    private SVPStatement statement;

    private SVPArgument argument;
}
