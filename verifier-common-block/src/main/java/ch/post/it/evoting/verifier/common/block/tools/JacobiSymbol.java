package ch.post.it.evoting.verifier.common.block.tools;

import java.math.BigInteger;

public final class JacobiSymbol {

    private static final BigInteger THREE = BigInteger.valueOf(3L);
    private static final BigInteger FOUR = BigInteger.valueOf(4L);
    private static final BigInteger SEVEN = BigInteger.valueOf(7L);
    private static final BigInteger EIGHT = BigInteger.valueOf(8L);

    /**
     * Compute the jacobi symbol <code>(a/n)</code>, as described in:
     * <a href="http://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.186-4.pdf">Digital signature standard (DSS). FIPS PUB
     * 186-4, National Institute of Standards and Technology (NIST), 2013.</a>, pp. 76-77
     *
     * @param initial_a the starting value of a
     * @param n the value of n
     *
     * @return the computed jacobi symbol
     */
    public static int computeJacobiSymbol(BigInteger initial_a, BigInteger n) {
        // Step 1: a = a mod n
        BigInteger a = initial_a.mod(n);
        // Step 2: if a = 1 or n = 1 return 1
        if (MathHelper.areEqual(a, BigInteger.ONE) || MathHelper.areEqual(n, BigInteger.ONE)) {
            return 1;
        }
        // Step 3: if a = 0 return 0
        if (MathHelper.areEqual(a, BigInteger.ZERO)) {
            return 0;
        }
        // Step 4: define e and a_1 such that a = 2^e * a_1 where a_1 is odd
        int e = 0;
        BigInteger a_1 = a;
        while (MathHelper.areEqual(a_1.remainder(BigInteger.TWO), BigInteger.ZERO)) {
            e++;
            a_1 = a_1.divide(BigInteger.TWO);
        }
        // Step 5 : if e is even, then s = 1
        //          else if n mod 8 = 1 or n mod 8 = 7, then s = 1
        //          else if n mod 8 = 3 or n mod 8 = 5, then s = -1
        int s;
        if (e % 2 == 0) {
            s = 1;
        } else {
            BigInteger n_mod_eight = n.mod(EIGHT);
            if (MathHelper.areEqual(n_mod_eight, BigInteger.ONE) || MathHelper.areEqual(n_mod_eight, SEVEN)) {
                s = 1;
            } else { // n_mod_eight.equals(THREE) || n_mod_eight.equals(FIVE)
                s = -1;
            }
        }
        // Step 6: if n mod 4 = 3 and a_1 mod 4 = 3, then s = -s
        if (MathHelper.areEqual(n.mod(FOUR), THREE) && MathHelper.areEqual(a_1.mod(FOUR), THREE)) {
            s = -s;
        }
        // Step 7: n_1 = n mod a_1
        BigInteger n_1 = n.mod(a_1);
        // Step 8: return s * JacobiSymbol(n_1, a_1)
        return s * computeJacobiSymbol(n_1, a_1);
    }

}
