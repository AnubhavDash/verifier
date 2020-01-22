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
import java.util.List;

public final class MathHelper {

    private static final BigInteger TWO = new BigInteger("2");

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
     * Utility functions - 1.5 Commitment computation
     * <p>
     * Commitment computation of a list of {@link BigInteger} elements
     *
     * @param ec    encryption group context
     * @param r     random exponent
     * @param a_vec list of elements to be committed
     * @param ck    commitment key
     * @return
     */
    public static BigInteger computeCommitment(EncryptionGroup ec, BigInteger r, List<BigInteger> a_vec, CommitmentKey ck) {
        BigInteger modeExpResult = modExp(ck.getH(), r, ec.getP());
        BigInteger modeExpProductResult = modeExpResult.multiply(modExpProduct(ck.getG(), a_vec, ec.getP()));
        return modeExpProductResult.mod(ec.getP());
    }


    public static boolean isEulerCriterionValid(BigInteger vo, BigInteger p) {
        BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(TWO);
        BigInteger ec = vo.modPow(exponent, p);
        return MathHelper.areEqual(ec, BigInteger.ONE);
    }


    /**
     * Utility to verify membership for Z_q
     *
     * @param x a number
     * @param eg EncryptionGroup
     * @return true if x &isin; Z_q, false otherwise
     */
    public boolean isInZ_q(BigInteger x, EncryptionGroup eg) {
        return isGTE(x, BigInteger.ZERO) && isLT(x, eg.getQ());
    }


    public static boolean isPrime(BigInteger value) {
        return value.isProbablePrime(Integer.MAX_VALUE);
    }


    /**
     * Utility functions - 1.4.1 Modular Exponentiation
     *
     * @param b the base
     * @param e the exponent
     * @param m the modulus
     * @return the modular exponentiation, b exp(e) mod m
     */
    public static BigInteger modExp(BigInteger b, BigInteger e, BigInteger m) {
        // Naive implementation before optimisation
        return b.modPow(e, m);
    }


    /**
     * Utility functions - 1.4.2 Modular exponentiation product
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
     * Check if x is greater than or equal to n
     *
     * @param x a number
     * @param n a number
     * @return true if x is greater than or equal to n
     */
    private boolean isGTE(BigInteger x, BigInteger n) {
        return x.compareTo(n) >= 0;
    }

    /**
     * Check if x is greater than n
     *
     * @param x a number
     * @param n a number
     * @return true if x is greater than n
     */
    private boolean isGT(BigInteger x, BigInteger n) {
        return x.compareTo(n) > 0;
    }

    /**
     * Check if x is less than or equal to n
     *
     * @param x a number
     * @param n a number
     * @return true if x is less than or equal to n
     */
    private boolean isLTE(BigInteger x, BigInteger n) {
        return x.compareTo(n) <= 0;
    }

    /**
     * Check if x is less than n
     *
     * @param x a number
     * @param n a number
     * @return true if x is less than n
     */
    private boolean isLT(BigInteger x, BigInteger n) {
        return x.compareTo(n) < 0;
    }
}
