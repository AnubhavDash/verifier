package ch.post.it.evoting.verifier.common.block.converter;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.math.BigInteger;

/**
 * Converter from hexadecimal {@link String} to {@link BigInteger} value.
 */
public class HexStringToBigIntegerConverter extends StdConverter<String, BigInteger> {

    @Override
    public BigInteger convert(String hex) {
        return TypeConverter.hexaStringToBigInteger(hex);
    }
}
