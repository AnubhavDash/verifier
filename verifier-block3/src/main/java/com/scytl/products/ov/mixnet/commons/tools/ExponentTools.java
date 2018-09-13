/**
 * @author aescala
 * @date 16/09/2013 17:24:53
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.tools;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

import java.math.BigInteger;

/**
 * Defines a set of utility methods related to {@code Exponent}.
 */
public class ExponentTools {

    public static Exponent innerProduct(final Exponent[] e1, final Exponent[] e2, final BigInteger order,
            final Exponent challengeInnerProduct) {

        Exponent result = new Exponent(0, order);
        Exponent accumulator = challengeInnerProduct;
        for (int i = 0, length = e1.length; i < length; i++) {
            result = result.add(e1[i].multiply(e2[i]).multiply(accumulator));
            accumulator = accumulator.multiply(challengeInnerProduct);
        }

        return result;
    }

    public static Exponent[] multiplyByScalar(final Exponent[] expo, final Exponent factor) {

        final Exponent[] result = new Exponent[expo.length];
        for (int i = 0, length = result.length; i < length; i++) {
            result[i] = expo[i].multiply(factor);
        }
        return result;
    }

    public static Exponent[] negate(final Exponent[] expo) {

        final Exponent[] result = new Exponent[expo.length];
        for (int i = 0, length = result.length; i < length; i++) {
            result[i] = expo[i].negate();
        }
        return result;
    }

    public static Exponent[] getMinus1Vector(final int length, final BigInteger order) {

        final Exponent[] result = new Exponent[length];
        final Exponent minusOne = new Exponent(-1, order);
        for (int i = 0, size = result.length; i < size; i++) {
            result[i] = minusOne;
        }
        return result;
    }

}
