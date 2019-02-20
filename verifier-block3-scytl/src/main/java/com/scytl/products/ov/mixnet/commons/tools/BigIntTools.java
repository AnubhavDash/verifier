/**
 * @author aescala
 * @date   13/09/2013 09:28:07
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.tools;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BigIntTools {
    public static BigInteger generateBigInteger(final BigInteger max) {
        BigInteger expo = new BigInteger(max.bitLength(), new SecureRandom());
        while (expo.compareTo(max) != -1) {
            expo = new BigInteger(max.bitLength(), new SecureRandom());
        }
        return expo;
    }

    public static BigInteger generateInvertibleBigInteger(final BigInteger max) {
        BigInteger expo = generateBigInteger(max);
        while (expo.compareTo(BigInteger.ZERO) == 0) {
            expo = generateBigInteger(max);
        }
        return expo;
    }
}
