package ch.post.it.evoting.verifier.common.block.dto;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class RandomOracleHashParameters {

	private String id;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger q;

	private String x;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger output;
}
