package ch.post.it.evoting.verifier.common.block.dto;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;

import lombok.Getter;

@Getter
public class ComputePhiExponentiationParameters {

	private String id;

	private EncryptionGroup eg;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> g_vec;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger x;

	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> output_vec;

}
