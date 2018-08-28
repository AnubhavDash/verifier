/**
 * @author afries
 * @date Apr 8, 2015 2:01:26 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.tools;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

import java.math.BigInteger;

public final class MultiExponentiationImpl {

    public static ZpElement computeMultiExpo(final ZpElement[] base, final Exponent[] expo) {
        if (base.length == 0) {
            System.out.println("ERROR: There are no bases to exponentiate");
        }
        if (expo.length == 0) {
            System.out.println("ERROR: No exponents received");
        }
        if (expo.length != base.length) {
            System.out.println(
                "ERROR: Not the same amount of bases (" + base.length + ") and exponents (" + expo.length + ")");
            return null;
        } else {
            if (base.length == 1) {
                return base[0].exponentiate(expo[0]);
            }
            int window = chooseWindow(base.length);
            int windowCount = getWindowCount(expo[0].getOrder().bitLength(), window);
            // System.out.println("bases = "+base.length+" window size = "+window);
            BigInteger[][] powers = computePowers(base, window);
            int[][] windowedExponents = computeWindowedExponents(expo, windowCount, window);

            return new ZpElement(
                computeMultiExponent(windowedExponents, powers, base[0].getP(), window, windowCount),
                base[0].getP(), base[0].getQ());
        }

    }

    private static int chooseWindow(int baseCount) {
        int window;
        // the constants are borrowed from the article mentioned above.
        if (baseCount <= 11) {
            window = 2;
        } else if (baseCount <= 25) {
            window = 3;
        } else if (baseCount <= 61) {
            window = 4;
        } else if (baseCount <= 148) {
            window = 5;
        } else if (baseCount <= 324) {
            window = 6;
        } else if (baseCount <= 776) {
            window = 7;
        } else if (baseCount <= 1892) {
            window = 8;
        } else if (baseCount <= 3826) {
            window = 9;
        } else if (baseCount <= 12269) {
            window = 6;
        } else if (baseCount <= 23513) {
            window = 11;
        } else {
            window = 12;
        }
        return window;
    }

    private static BigInteger[][] computePowers(ZpElement[] bases, int window) {
        BigInteger[][] powers = new BigInteger[bases.length][1 << window];
        BigInteger modulo = bases[0].getP();
        for (int i = 0; i < powers.length; i++) {
            powers[i][0] = BigInteger.ONE;
            for (int j = 1; j < powers[i].length; j++) {
                powers[i][j] = powers[i][j - 1].multiply(bases[i].getValue()).mod(modulo);
            }
        }
        return powers;
    }

    private static BigInteger computeMultiExponent(int[][] exponents, BigInteger[][] powers, BigInteger modulo,
            int window, int windowCount) {
        BigInteger value = BigInteger.ONE;
        for (int j = windowCount - 1; j >= 0; j--) {
            if (!value.equals(BigInteger.ONE)) {
                for (int k = 0; k < window; k++) {
                    value = value.multiply(value).mod(modulo);
                }
            }
            for (int i = 0; i < powers.length; i++) {
                BigInteger factor = powers[i][exponents[i][j]];
                if (!factor.equals(BigInteger.ONE)) {
                    value = value.multiply(factor).mod(modulo);
                }
            }
        }
        return value;
    }

    private static int[][] computeWindowedExponents(Exponent[] exponents, int windowCount, int window) {
        int exponentsLength = exponents.length;
        int[][] windowedExponents = new int[exponentsLength][windowCount];
        int mask = (1 << window) - 1;
        for (int i = 0; i < windowedExponents.length; i++) {
            BigInteger exponent = exponents[i].getValue();
            for (int j = 0; j < windowedExponents[i].length; j++) {
                windowedExponents[i][j] = exponent.intValue() & mask;
                exponent = exponent.shiftRight(window);
            }
        }
        return windowedExponents;
    }

    private static int getWindowCount(int maximumBitLengthOfExponent, int window) {
        int count = maximumBitLengthOfExponent / window;
        if (maximumBitLengthOfExponent % window > 0) {
            count++;
        }
        return count;
    }
}
