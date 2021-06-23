package ch.post.it.evoting.verifier.common.block.dto;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;

import lombok.Getter;

@Getter
public class BilinearMappingParameters {

	private EncryptionGroup eg;

	@JsonProperty("a")
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> a_vec;

	@JsonProperty("b")
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> b_vec;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger y;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger output;

}
