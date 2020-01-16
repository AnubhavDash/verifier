package ch.post.it.evoting.verifier.common.block.converter;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.databind.util.StdConverter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Converter from hexadecimal {@link String} array to {@link Vector} of {@link BigInteger}s.
 */
public class HexStringArrayToBigIntegerListConverter extends StdConverter<String[], List<BigInteger>> {

    @Override
    public List<BigInteger> convert(String[] hexArray) {
        List<BigInteger> vector = new ArrayList<>();
        for (String hex : hexArray) {
            vector.add(TypeConverter.hexaStringToBigInteger(hex));
        }
        return Collections.unmodifiableList(vector);
    }
}
