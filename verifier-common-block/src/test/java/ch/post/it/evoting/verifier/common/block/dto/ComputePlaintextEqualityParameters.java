package ch.post.it.evoting.verifier.common.block.dto;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;

import lombok.Getter;

@Getter
public class ComputePlaintextEqualityParameters {

	private String id;

	private EncryptionGroup eg;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> h_vec;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> h_vec_bar;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger r;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger r_bar;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> output_vec;

}
