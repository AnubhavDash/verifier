package ch.post.it.evoting.verifier.common.block.dto;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;

import lombok.Getter;

@Getter
public class ComputePhiSchnorrParameters {

	private String id;

	private EncryptionGroup eg;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger x;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger output;

}
