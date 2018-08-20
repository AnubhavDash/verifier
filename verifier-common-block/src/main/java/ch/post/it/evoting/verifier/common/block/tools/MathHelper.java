package ch.post.it.evoting.verifier.common.block.tools;

import java.math.BigInteger;

public class MathHelper {
    private MathHelper() {
        //private constructor, use static
    }

    public static boolean isPrime(BigInteger value) {
        return value.isProbablePrime(Integer.MAX_VALUE);
    }
}
