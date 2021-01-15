package ch.post.it.evoting.verifier.common.block.dto;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class ModExpProductParameters {

	private String id;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> b_vec;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> e_vec;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger m;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger output;

}
