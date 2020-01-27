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
import lombok.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
        // Pre requirements
        requireIsInZ_q(r, encryptionGroup);
        requireVectorIsInZ_q(a_vec, encryptionGroup);
        requireIsMember(ck.getH(), encryptionGroup);
        requireVectorIsMember(ck.getG_vec(), encryptionGroup);

        // Commitment computation
        BigInteger modeExpResult = modExp(ck.getH(), r, encryptionGroup.getP());
        BigInteger modeExpProductResult = modeExpResult.multiply(modExpProduct(ck.getG_vec(), a_vec, encryptionGroup.getP()));
        BigInteger result = modeExpProductResult.mod(encryptionGroup.getP());

        // Post requirement
        requireIsMember(result, encryptionGroup);

        return result;
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
        return isGTE(x, BigInteger.TWO) && isLT(x, encryptionGroup.getP())
                && JacobiSymbol.computeJacobiSymbol(x, encryptionGroup.getP()) == 1;
    }


    public static boolean isPrime(BigInteger value) {
        return value.isProbablePrime(Integer.MAX_VALUE);
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
        int dimension = b_vec.size();
        if (dimension != e_vec.size()) {
            throw new IllegalArgumentException("Bases and exponents vectors must have the same dimension.");
        }

        // Naive implementation before optimisation
        BigInteger acc = BigInteger.ONE;
        for (int i = 0; i < dimension; i++) {
            acc = acc.multiply(modExp(b_vec.get(i), e_vec.get(i), m));
        }
        acc = acc.mod(m);

        return acc;
    }


    /**
     * Utility functions - 1.4.3 Modular inverse
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
}
