/*
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

import ch.post.it.evoting.verifier.common.block.dto.revised.CommitmentKey;
import ch.post.it.evoting.verifier.common.block.dto.revised.EncryptionGroup;
import ch.post.it.evoting.verifier.common.block.dto.revised.algo.Argument;
import ch.post.it.evoting.verifier.common.block.dto.revised.algo.SVPArgument;
import ch.post.it.evoting.verifier.common.block.dto.revised.algo.SVPStatement;
import ch.post.it.evoting.verifier.common.block.dto.revised.algo.Statement;
import com.google.common.primitives.Bytes;
import lombok.NonNull;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.*;

import static ch.post.it.evoting.verifier.common.block.tools.Requirements.*;

public final class MathHelper {

    private MathHelper() {
        //private constructor, use static
    }

    /**
     * Tests for mathematical equality the two provided {@link BigInteger}s parameters. For testing, the two
     * provided parameters must be non-null, otherwise a {@link NullPointerException} will be thrown indicating the
     * first parameter found to be null.
     *
     * @param valueA {@link NonNull} value to be tested
     * @param valueB {@link NonNull} value to be tested
     * @return true if parameter "valueA" is mathematically equal to parameter "valueB", false otherwise
     */
    public static boolean areEqual(@NonNull BigInteger valueA, @NonNull BigInteger valueB) {
        return valueA.compareTo(valueB) == 0;
    }


    public static boolean isEulerCriterionValid(BigInteger vo, BigInteger p) {
        BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(BigInteger.TWO);
        BigInteger ec = vo.modPow(exponent, p);
        return MathHelper.areEqual(ec, BigInteger.ONE);
    }


    /**
     * Utility to verify membership for Z<sub>q</sub>.
     *
     * @param x  a number
     * @param eg EncryptionGroup
     * @return true if x &isin; Z<sub>q</sub>, false otherwise
     */
    public static boolean isInZ_q(BigInteger x, EncryptionGroup eg) {
        return isGTE(x, BigInteger.ZERO) && isLT(x, eg.getQ());
    }


    /**
     * Utility to verify membership for encryptionGroup.
     *
     * @param x               A number
     * @param encryptionGroup the associated encryption group
     * @return true if <code>x &isin; encryptionGroup</code>, false otherwise
     */
    public static boolean isMember(BigInteger x, EncryptionGroup encryptionGroup) {
        return isGTE(x, BigInteger.ONE) && isLT(x, encryptionGroup.getP())
                && JacobiSymbol.computeJacobiSymbol(x, encryptionGroup.getP()) == 1;
    }


    public static boolean isPrime(BigInteger value) {
        return value.isProbablePrime(Integer.MAX_VALUE);
    }

    /**
     * Utility functions - 1.2.2 Random Oracle Hash.
     * <p>
     * This primitive is used to map an input value <code>∈ [0, 1]<sup>𝑛</sup></code> to a random-looking value <code>∈ ℤ𝑞</code>. This
     * is used by the proofs relying on the Fiat-Shamir heuristic to turn an interactive protocol into a non-interactive proof system.
     *
     * @param messageDigest A hash function.
     * @param x             An input value in bytes.
     * @param lowerBound    A lower bound <code>&isin; ℤ</code>, typically 0.
     * @param upperBound    An upper bound <code>&isin; ℤ</code>, typically group order (q).
     * @return A hash of the input value, {@code lowerBound <= output < upperBound}.
     */
    public static BigInteger randomOracleHash(MessageDigest messageDigest, byte[] x, BigInteger lowerBound, BigInteger upperBound) {
        // Check bounds.
        if (isGTE(lowerBound, upperBound)) {
            throw new IllegalArgumentException("The lower bound must be lower than the upper bound!");
        }

        // Hash input. Output size is upperBound - lowerBound - 1.
        byte[] digest = hashInput(messageDigest, x, lowerBound, upperBound);

        // Convert digest to a BigInteger.
        final BigInteger e = new BigInteger(1, digest);

        // Set the k most significant bits of e to 0's.
        int k = e.clearBit(0).getLowestSetBit();
        final BigInteger ePrime = setKLastMSBToZeros(e, k);

        // Reverse the bits.
        final BigInteger yPrime = reverseBits(ePrime);

        return (yPrime.add(lowerBound).add(BigInteger.ONE)).mod(upperBound);
    }


    /**
     * Utility functions - 1.2.3 Inverted string order concatenation.
     * <p>
     * Reverse and concatenate a list of strings. Only required temporarily to ensure compatibility with the non-compliant implementation.
     *
     * @param strings The list of strings to concatenate together.
     * @return The concatenation in reverse order of {@code strings}.
     */
    public static String reverseAndJoin(List<String> strings) {
        StringJoiner joiner = new StringJoiner("");
        for (ListIterator<String> iter = strings.listIterator(strings.size()); iter.hasPrevious(); )
            joiner.add(iter.previous());

        return joiner.toString();
    }


    /**
     * Utility functions - 1.4.1 Modular Exponentiation.
     *
     * @param b the base
     * @param e the exponent
     * @param m the modulus
     * @return the modular exponentiation, <code>b<sup>e</sup> mod m</code>
     */
    public static BigInteger modExp(BigInteger b, BigInteger e, BigInteger m) {
        // Naive implementation before optimisation
        return b.modPow(e, m);
    }


    /**
     * Utility functions - 1.4.2 Modular exponentiation product.
     *
     * @param b_vec a list of bases
     * @param e_vec a list of exponents
     * @param m     the modulus
     * @return the product of the modular exponentiation
     */
    public static BigInteger modExpProduct(List<BigInteger> b_vec, List<BigInteger> e_vec, BigInteger m) {
        requireVectorDimensionEqual(b_vec, e_vec);

        // Naive implementation before optimisation
        int dimension = b_vec.size();
        BigInteger acc = BigInteger.ONE;
        for (int i = 0; i < dimension; i++) {
            acc = acc.multiply(modExp(b_vec.get(i), e_vec.get(i), m));
        }
        acc = acc.mod(m);

        return acc;
    }


    /**
     * Utility functions - 1.4.3 Modular inverse.
     *
     * @param b the base
     * @param m the modulus
     * @return the modular inverse, <code>b<sup>-1</sup> mod m</code>
     */
    public static BigInteger modInv(BigInteger b, BigInteger m) {
        // Naive implementation before optimisation
        return b.modInverse(m);
    }


    /**
     * Utility functions - 1.4.4 Exponentiations product
     *
     * @param encryptionGroup The encryption group.
     * @param a_vec           The base vector.
     * @param x               The intermediate exponent.
     * @return The product of the elements of {@code a_vec} raised to increasing powers of {@code x}.
     */
    public static BigInteger prodIncPow(EncryptionGroup encryptionGroup, List<BigInteger> a_vec, BigInteger x) {
        // Pre requirements.
        requireVectorIsMember(a_vec, encryptionGroup);
        requireIsInZ_q(x, encryptionGroup);

        final BigInteger p = encryptionGroup.getP();
        final BigInteger q = encryptionGroup.getQ();

        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < a_vec.size(); i++) {
            result = result.multiply(modExp(a_vec.get(i), modExp(x, BigInteger.valueOf(i), q), p)).mod(p);
        }

        return result;
    }


    /**
     * Utility functions - 1.5 Commitment computation.
     * <p>
     * Commitment computation of a list of {@link BigInteger} elements.
     *
     * @param encryptionGroup encryption group context
     * @param r               random exponent
     * @param a_vec           list of elements to be committed
     * @param ck              commitment key
     * @return
     */
    public static BigInteger computeCommitment(EncryptionGroup encryptionGroup, BigInteger r, List<BigInteger> a_vec, CommitmentKey ck) {
        // Pre requirements.
        requireIsInZ_q(r, encryptionGroup);
        requireVectorIsInZ_q(a_vec, encryptionGroup);
        requireIsMember(ck.getH(), encryptionGroup);
        requireVectorIsMember(ck.getG_vec(), encryptionGroup);
        requireVectorDimensionLTE(a_vec, ck.getG_vec());

        // If the list of elements to be committed is shorter than the commitment key, pad it with zeros.
        List<BigInteger> padded = new ArrayList<>(a_vec);
        if (a_vec.size() < ck.getG_vec().size()) {
            for (int i = 0; i < ck.getG_vec().size() - a_vec.size(); i++) {
                padded.add(BigInteger.ZERO);
            }
        }

        // Commitment computation.
        BigInteger modeExpResult = modExp(ck.getH(), r, encryptionGroup.getP());
        BigInteger modeExpProductResult = modeExpResult.multiply(modExpProduct(ck.getG_vec(), padded, encryptionGroup.getP()));
        BigInteger result = modeExpProductResult.mod(encryptionGroup.getP());

        // Post requirement.
        requireIsMember(result, encryptionGroup);

        return result;
    }


    /**
     * Utility functions - 1.6.1 Schnorr proof’s phi function.
     * <p>
     * Proof of knowledge of a {@link BigInteger} element <code>x</code>.
     *
     * @param encryptionGroup encryption group context <code>(p, q, g)</code>
     * @param x               the value for which we want a proof of knowledge
     * @return <code>g<sup>x</sup> mod p</code>
     */
    public static BigInteger computePhiSchnorr(EncryptionGroup encryptionGroup, BigInteger x) {
        // Pre requirements
        Requirements.requireIsInZ_q(x, encryptionGroup);

        // Schnorr proof
        BigInteger result = modExp(encryptionGroup.getG(), x, encryptionGroup.getP());

        // Post requirements
        Requirements.requireIsMember(result, encryptionGroup);

        return result;
    }


    /**
     * Utility functions - 1.6.2 Exponentiation proof's phi function.
     * <p>
     * Proof of knowledge of a {@link BigInteger} element {@code x}.
     *
     * @param encryptionGroup The encryption group context <code>(p, q, g)</code>.
     * @param g_vec           A vector of generators.
     * @param x               The value for which we want a proof of knowledge.
     * @return The phi exponentiation of {@code g_vec}
     */
    public static List<BigInteger> computePhiExponentiation(EncryptionGroup encryptionGroup, List<BigInteger> g_vec, BigInteger x) {
        // Pre requirements.
        requireIsInZ_q(x, encryptionGroup);
        requireVectorIsMember(g_vec, encryptionGroup);

        List<BigInteger> output_vec = new ArrayList<>(g_vec.size());
        for (BigInteger g : g_vec) {
            final BigInteger output = MathHelper.modExp(g, x, encryptionGroup.getP());
            // Post requirements. Output also needs to be part of {G^n}_q.
            requireIsMember(output, encryptionGroup);
            output_vec.add(output);
        }

        return output_vec;
    }


    /**
     * Utility functions - 1.6.3 Plain-Text Equality proof's phi function.
     *
     * @param encryptionGroup The encryption group context <code>(p, q, g)</code>.
     * @param h_vec           The primary key vector.
     * @param h_vec_bar       The secondary key vector.
     * @param r               The first random number.
     * @param r_bar           The second random number.
     * @return The plain text equality proof.
     */
    public static List<BigInteger> computePhiPlaintextEquality(EncryptionGroup encryptionGroup, List<BigInteger> h_vec,
                                                               List<BigInteger> h_vec_bar, BigInteger r, BigInteger r_bar) {

        final BigInteger p = encryptionGroup.getP();
        final BigInteger g = encryptionGroup.getG();

        // Pre requirements.
        requireVectorDimensionEqual(h_vec, h_vec_bar);
        requireIsMember(g, encryptionGroup);
        requireIsInZ_q(r, encryptionGroup);
        requireIsInZ_q(r_bar, encryptionGroup);
        requireVectorIsMember(h_vec, encryptionGroup);
        requireVectorIsMember(h_vec_bar, encryptionGroup);

        List<BigInteger> output_vec = new ArrayList<>(2 + h_vec.size());
        final BigInteger r_exp = modExp(g, r, p);
        requireIsMember(r_exp, encryptionGroup); // Post requirement.
        output_vec.add(r_exp);

        final BigInteger r_bar_exp = modExp(g, r_bar, p);
        requireIsMember(r_bar_exp, encryptionGroup); // Post requirement.
        output_vec.add(r_bar_exp);

        for (int i = 0; i < h_vec.size(); i++) {
            final BigInteger numerator = modExp(h_vec.get(i), r, p);
            final BigInteger denominator = modExp(modInv(h_vec_bar.get(i), p), r_bar, p);

            final BigInteger output_i = numerator.multiply(denominator).mod(p);
            requireIsMember(output_i, encryptionGroup); // Post requirement.
            output_vec.add(output_i);
        }

        return output_vec;
    }

    /**
     * Utility functions - 1.7 Bilinear Map
     *
     * @param encryptionGroup The encryption group context <code>(p, q, g)</code>.
     * @param a_vec           A vector of values in <code>Z<sub>q</sub></code>.
     * @param b_vec           A second vector of values in <code>Z<sub>q</sub></code>.
     * @param y               The value in <code>Z<sub>q</sub></code> that defines the bilinear map.
     * @return A bilinear mapping. See Verifier - Block 3 document for more details.
     */
    public static BigInteger bilinearMapping(EncryptionGroup encryptionGroup, List<BigInteger> a_vec, List<BigInteger> b_vec,
                                             BigInteger y) {

        // Pre requirements.
        requireVectorDimensionEqual(a_vec, b_vec);
        requireVectorIsInZ_q(a_vec, encryptionGroup);
        requireVectorIsInZ_q(b_vec, encryptionGroup);
        requireIsInZ_q(y, encryptionGroup);

        final BigInteger q = encryptionGroup.getQ();

        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < a_vec.size(); i++) {
            final BigInteger modExp = modExp(y, BigInteger.valueOf(i + 1), q);
            final BigInteger multiplication = a_vec.get(i).multiply(b_vec.get(i)).multiply(modExp);
            result = result.add(multiplication).mod(q);
        }

        return result;
    }

    /**
     * Arguments - 2.1 Single Value Product Argument Verification
     *
     * @return {@code true} if the statement verification was successful.
     */
    public static boolean verifySVPArgument(EncryptionGroup encryptionGroup, CommitmentKey ck, BigInteger pk, Statement statement,
                                            Argument argument) {

        // TODO that or change parameters type?
        SVPStatement svpStatement = (SVPStatement) statement;
        SVPArgument svpArgument = (SVPArgument) argument;

        // Params extraction.
        final BigInteger c_a = svpStatement.getC_a();
        final BigInteger b = svpStatement.getB();
        final BigInteger c_d = svpArgument.getC_d();
        final BigInteger c_lowerDelta = svpArgument.getC_lowerDelta();
        final BigInteger c_upperDelta = svpArgument.getC_upperDelta();
        final List<BigInteger> a_tilde_vec = svpArgument.getA_tilde_vec();
        final List<BigInteger> b_tilde_vec = svpArgument.getB_tilde_vec();
        final BigInteger r_tilde = svpArgument.getR_tilde();
        final BigInteger s_tilde = svpArgument.getS_tilde();
        final BigInteger p = encryptionGroup.getP();
        final BigInteger q = encryptionGroup.getQ();

        // Pre requirements.
        requireIsInZ_q(b, encryptionGroup);
        requireIsMember(c_a, encryptionGroup);
        requireIsMember(c_d, encryptionGroup);
        requireIsMember(c_lowerDelta, encryptionGroup);
        requireIsMember(c_upperDelta, encryptionGroup);
        requireVectorDimensionEqual(a_tilde_vec, b_tilde_vec);
        requireVectorIsInZ_q(a_tilde_vec, encryptionGroup);
        requireVectorIsInZ_q(b_tilde_vec, encryptionGroup);
        requireIsInZ_q(r_tilde, encryptionGroup);
        requireIsInZ_q(s_tilde, encryptionGroup);

        // Build the string to be hashed by random oracle hash.
        List<String> elements = new ArrayList<>();
        elements.add(ToStringHelper.publicCommitmentToString(c_a, p, q));
        elements.add(ToStringHelper.exponentToString(b, q));
        elements.add(ToStringHelper.singleValueProductProofInitialMessageToString(c_d, c_lowerDelta, c_upperDelta, p, q));
        elements.add(ToStringHelper.commitmentParamsToString(ck.getH(), ck.getG_vec(), p, q));
        elements.add(ToStringHelper.zpGroupElementToString(pk, p, q));

        // Compute random oracle hash.
        MessageDigest messageDigest = getSHA256MessageDigest();
        byte[] input = TypeConverter.stringToByte(reverseAndJoin(elements));
        BigInteger x = randomOracleHash(messageDigest, input, BigInteger.ZERO, q);

        if (!areEqual(modExp(c_a, x, p).multiply(c_d).mod(p), computeCommitment(encryptionGroup, r_tilde, a_tilde_vec, ck))) {
            return false;
        }

        final int n = ck.getG_vec().size() - 1; // Index of last element.
        final List<BigInteger> args_vec = new ArrayList<>();
        for (int i = 0; i <= n - 1; i++) {
            final BigInteger leftTerm = x.multiply(b_tilde_vec.get(i + 1)).mod(q);
            final BigInteger rightTerm = b_tilde_vec.get(i).multiply(a_tilde_vec.get(i + 1)).mod(q);
            args_vec.add(leftTerm.subtract(rightTerm).mod(q));
        }

        if (!areEqual(modExp(c_upperDelta, x, p).multiply(c_lowerDelta).mod(p),
                computeCommitment(encryptionGroup, s_tilde, args_vec, ck))) {
            return false;
        }

        if (!areEqual(b_tilde_vec.get(0), a_tilde_vec.get(0))) {
            return false;
        }

        return areEqual(b_tilde_vec.get(n), x.multiply(b).mod(q));
    }


    // =====================================================================================================================================
    // Utility methods.
    // =====================================================================================================================================

    /**
     * Check if x is greater than or equal to n.
     *
     * @param x a number
     * @param n a number
     * @return true if {@code x} is greater than or equal to {@code n}
     */
    private static boolean isGTE(BigInteger x, BigInteger n) {
        return x.compareTo(n) >= 0;
    }

    /**
     * Check if x is greater than n.
     *
     * @param x a number
     * @param n a number
     * @return true if {@code x} is greater than {@code n}
     */
    private static boolean isGT(BigInteger x, BigInteger n) {
        return x.compareTo(n) > 0;
    }

    /**
     * Check if x is less than or equal to n.
     *
     * @param x a number
     * @param n a number
     * @return true if {@code x} is less than or equal to {@code n}
     */
    private static boolean isLTE(BigInteger x, BigInteger n) {
        return x.compareTo(n) <= 0;
    }

    /**
     * Check if x is less than n.
     *
     * @param x a number
     * @param n a number
     * @return true if {@code x} is less than {@code n}
     */
    private static boolean isLT(BigInteger x, BigInteger n) {
        return x.compareTo(n) < 0;
    }

    private static byte[] hashInput(MessageDigest messageDigest, byte[] x, BigInteger lowerBound, BigInteger upperBound) {
        // Output size in bytes.
        final int s = messageDigest.getDigestLength();

        final BigInteger c = upperBound.subtract(lowerBound).subtract(BigInteger.ONE);
        final int t = c.bitLength();
        // FIXME Implementation error 1.
        // Warning: Error required for compatibility. On the line below, a number of bits is divided by a number of bytes.
        final int n = StrictMath.floorDiv(t, s);
        final int r = t % s;

        // Hash input.
        byte[] input = x;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(n * s);
        for (int i = 0; i < n; i++) {
            // FIXME Implementation error 2.
            // Warning: as per the verifier specification, this diverges from the e-voting protocol, to ensure compatibility with the
            // implementation. Only the last element is fed to the hash, instead of the concatenation of the hashes computed so far.
            final byte[] digest = messageDigest.digest(input);
            byteBuffer.put(digest);
            input = digest;
        }
        byte[] e = byteBuffer.array();

        // Completion if needed.
        if (r > 0) {
            // FIXME Implementation error 3.
            // Warning: as per the verifier specification, this diverges from the e-voting protocol, to ensure compatibility with the
            // implementation. Only the hash of the empty string is taken as input in the following step, instead of the concatenation of
            // the hashes computed so far.
            final byte[] hashOfEmpty = messageDigest.digest("".getBytes(StandardCharsets.UTF_8));
            final byte[] digest = messageDigest.digest(hashOfEmpty);
            final byte[] e_n = Arrays.copyOfRange(digest, 0, r);
            e = Bytes.concat(e, e_n);
        }

        return e;
    }

    private static BigInteger setKLastMSBToZeros(BigInteger input, int k) {
        final int bitLength = input.bitLength();
        BigInteger output = input;

        // The most significant bit in a BigInteger is the bit at the biggest index.
        for (int i = 0; i < k; i++) {
            output = output.clearBit(bitLength - 1 - i);
        }

        return output;
    }

    private static BigInteger reverseBits(BigInteger ePrime) {
        // Store the length before because yPrime.bitLength would change in the for loop.
        final int bitLength = ePrime.bitLength();
        BigInteger yPrime = ePrime;

        for (int i = 0; i < bitLength; i++) {
            if (ePrime.testBit(i)) {
                yPrime = yPrime.setBit(bitLength - 1 - i);
            } else {
                yPrime = yPrime.clearBit(bitLength - 1 - i);
            }
        }

        return yPrime;
    }

    private static MessageDigest getSHA256MessageDigest() {
        // TODO use bouncyCastle?
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new IllegalArgumentException("Failed to instantiate hashing algorithm for SHA-256 with BouncyCastle: " + e.getMessage());
        }
        return messageDigest;
    }
}
