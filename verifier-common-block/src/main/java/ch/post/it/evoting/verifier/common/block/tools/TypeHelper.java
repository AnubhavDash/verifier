package ch.post.it.evoting.verifier.common.block.tools;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Class TypeConverter.
 * This class is the supplier for :
 *  - convert needs,
 *  - decode needs,
 *  - verify needs,
 * @author lalandret
 * @version $$Revision$$
 */
public class TypeHelper {

    public static BigInteger base64ToBigInteger(String value) {
        return new BigInteger(Base64.getDecoder().decode(value));
    }

    public static boolean isPrime(BigInteger value) {
        //TODO check this or find another method
        return value.isProbablePrime(Integer.MAX_VALUE);
    }

    public static byte[] StringToByte(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] BigIntegerToByte(BigInteger bi) {
        return bi.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] Base64ToByte(String b64) {
        return Base64.getDecoder().decode(b64);
    }

    public static BigInteger ByteToBigInteger(byte[] b) {
        return new BigInteger(b);
    }

    public static String ByteToString(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    public static String BigIntegerToB64String(BigInteger bigInt) { return Base64.getEncoder().encodeToString(bigInt.toByteArray()); }

    public static byte[] ByteToB64ByteArray(byte[] b) {
        return Base64.getEncoder().encode(b);
    }

}
