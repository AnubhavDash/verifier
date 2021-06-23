package ch.post.it.evoting.verifier.common.block.dto.revised.algo;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringArrayToBigIntegerListConverter;
import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SVPArgument implements Argument {

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger c_d;

	@JsonProperty("c_lower_delta")
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger c_lowerDelta;

	@JsonProperty("c_upper_delta")
	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger c_upperDelta;

	@JsonProperty("a_tilde")
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> a_tilde_vec;

	@JsonProperty("b_tilde")
	@JsonDeserialize(converter = StringArrayToBigIntegerListConverter.class)
	private List<BigInteger> b_tilde_vec;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger r_tilde;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger s_tilde;

}
