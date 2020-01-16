package ch.post.it.evoting.verifier.common.block.converter;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.math.BigInteger;
import java.util.Vector;

/**
 * Converter from hexadecimal {@link String} array to {@link Vector} of {@link BigInteger}s.
 */
public class HexStringArrayToBigIntegerVectorConverter extends StdConverter<String[], Vector<BigInteger>> {

    @Override
    public Vector<BigInteger> convert(String[] hexArray) {
        Vector<BigInteger> vector = new Vector<>();
        for (String hex : hexArray) {
            vector.add(TypeConverter.hexaStringToBigInteger(hex));
        }

        return vector;
    }
}
