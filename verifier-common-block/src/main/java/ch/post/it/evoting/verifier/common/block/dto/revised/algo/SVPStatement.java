package ch.post.it.evoting.verifier.common.block.dto.revised.algo;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.it.evoting.verifier.common.block.dto.converter.StringToBigIntegerConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SVPStatement implements Statement {

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger c_a;

	@JsonDeserialize(converter = StringToBigIntegerConverter.class)
	private BigInteger b;

}
