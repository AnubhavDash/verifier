package ch.post.it.evoting.verifier.common.block.dto.converter;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.util.StdConverter;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;

/**
 * Converter from {@link String} to {@link BigInteger} value.
 */
public class StringToBigIntegerConverter extends StdConverter<String, BigInteger> {

	@Override
	public BigInteger convert(String s) {
		return TypeConverter.stringToBigInteger(s);
	}
}
