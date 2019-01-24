package ch.post.it.evoting.verifier.common.block.tools;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TypeConverter {

    private TypeConverter() {
        //private constructor, use static
    }

    public static BigInteger base64ToBigInteger(String value) {
        return new BigInteger(Base64.getDecoder().decode(value));
    }

    public static byte[] stringToByte(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] bigIntegerToByte(BigInteger bi) {
        return bi.toString().getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] base64ToByte(String b64) {
        return Base64.getDecoder().decode(b64);
    }

    public static BigInteger byteToBigInteger(byte[] b) {
        return new BigInteger(b);
    }

    public static String byteToBase64String(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    public static String byteToString(byte[] b) {
        return new String(b, StandardCharsets.UTF_8);
    }

    public static BigInteger stringToBigInteger(String s) {
        return new BigInteger(s);
    }

    public static String bigIntegerToB64String(BigInteger bigInt) {
        return Base64.getEncoder().encodeToString(bigInt.toByteArray());
    }

    public static byte[] hexaStringToByte(String hex) throws DecoderException {
        return hex == null ? null : Hex.decodeHex(hex.toCharArray());
    }

    public static byte[] byteToB64ByteArray(byte[] b) {
        return Base64.getEncoder().encode(b);
    }

    public static BigInteger integerToBigInteger(Integer i) {
        if (i == null) {
            return null;
        } else {
            return BigInteger.valueOf(i.longValue());
        }
    }
}