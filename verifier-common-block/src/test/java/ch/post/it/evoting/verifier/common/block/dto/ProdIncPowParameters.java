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
