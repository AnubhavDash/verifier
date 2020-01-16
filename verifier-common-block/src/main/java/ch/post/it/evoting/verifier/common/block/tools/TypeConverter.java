/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.common.block.tools;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

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
        return i == null ? null : BigInteger.valueOf(i.longValue());
    }

    public static String UUIDToStringWithoutDash(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static BigInteger hexaStringToBigInteger(String hex) {
        int index = 0;
        if (hex.startsWith("0x", index) || hex.startsWith("0X", index)) {
            index += 2;
        }
        else if (hex.startsWith("#", index)) {
            index ++;
        }
        return new BigInteger(hex.substring(index), 16);
    }
}
