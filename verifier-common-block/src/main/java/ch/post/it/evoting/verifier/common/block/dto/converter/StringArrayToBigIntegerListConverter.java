package ch.post.it.evoting.verifier.common.block.dto.converter;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.util.StdConverter;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;

/**
 * Converter from {@link String} array to {@link List} of {@link BigInteger}s.
 */
public class StringArrayToBigIntegerListConverter extends StdConverter<String[], List<BigInteger>> {

	@Override
	public List<BigInteger> convert(String[] sArray) {
		return Arrays.stream(sArray)
				.map(TypeConverter::stringToBigInteger)
				.collect(Collectors.toUnmodifiableList());
	}
}
