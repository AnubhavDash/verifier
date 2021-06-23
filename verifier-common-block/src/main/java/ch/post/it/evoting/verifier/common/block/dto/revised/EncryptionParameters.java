package ch.post.it.evoting.verifier.common.block.dto.revised;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.Getter;

@Getter
public class EncryptionParameters {

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger p;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger q;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private final BigInteger g;

	private final String seed;
	private final int pCounter;
	private final int qCounter;

	public EncryptionParameters(
			@JsonProperty("p")
					BigInteger p,
			@JsonProperty("q")
					BigInteger q,
			@JsonProperty("g")
					BigInteger g,
			@JsonProperty("seed")
					String seed,
			@JsonProperty("pCounter")
					int pCounter,
			@JsonProperty("qCounter")
					int qCounter) {
		this.p = p;
		this.q = q;
		this.g = g;
		this.seed = seed;
		this.pCounter = pCounter;
		this.qCounter = qCounter;
	}
}
