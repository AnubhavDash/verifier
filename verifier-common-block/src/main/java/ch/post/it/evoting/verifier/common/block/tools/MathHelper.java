package ch.post.it.evoting.verifier.common.block.tools;

import java.math.BigInteger;

public class MathHelper {

    private static final BigInteger TWO = new BigInteger("2");

    private MathHelper() {
        //private constructor, use static
    }

    public static boolean isPrime(BigInteger value) {
        return value.isProbablePrime(Integer.MAX_VALUE);
    }

    public static boolean isEulerCriterionValid(BigInteger vo, BigInteger p) {
        BigInteger exponent = (p.subtract(BigInteger.ONE)).divide(TWO);
        BigInteger ec = vo.modPow(exponent, p);
        return ec.equals(BigInteger.ONE);
    }

    public static boolean isEulerCriterionInvalid(BigInteger vo, BigInteger p) {
        return !isEulerCriterionValid(vo, p);
    }

}
