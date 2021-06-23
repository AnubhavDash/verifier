package ch.post.it.evoting.verifier.common.block.dto.revised.onlinemixing;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class Exponent {

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger q;
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger value;

	public Exponent(
			@JsonProperty("q")
					BigInteger q,
			@JsonProperty("value")
					BigInteger value) {
		this.q = q;
		this.value = value;
	}
}
