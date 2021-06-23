package ch.post.it.evoting.verifier.common.block.dto;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class ModInvParameters {

	private String id;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger b;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger m;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger output;

}
