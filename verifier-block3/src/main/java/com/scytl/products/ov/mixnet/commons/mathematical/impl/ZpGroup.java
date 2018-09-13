/**
 * @author aescala
 * @date 16/09/2013 13:00:37
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.mathematical.impl;

import com.scytl.products.ov.mixnet.commons.mathematical.Group;
//import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.tools.BigIntTools;

import java.math.BigInteger;

public class ZpGroup implements Group {

    private final BigInteger _p;
    private final BigInteger _q;

    private ZpElement _gen;

    public ZpGroup(final BigInteger p, BigInteger q) {
        _p = p;
        _q = q;
        _gen = getGenerator();
    }

    public ZpGroup(final BigInteger p, final BigInteger q, final ZpElement generator) {
        _p = p;
        _q = q;
        _gen = generator;
    }

    @Override
    public BigInteger getOrder() {
        return _q;
    }

    public BigInteger getP() {
        return _p;
    }

    @Override
    public ZpElement getGenerator() {

        if (_gen != null) {
            return _gen;
        } else {
            BigInteger auxgen = BigIntTools.generateBigInteger(_p);

            while ((auxgen.compareTo(BigInteger.ONE) == 0)
                || auxgen.modPow(_q, _p).compareTo(BigInteger.ONE) != 0) {
                auxgen = BigIntTools.generateBigInteger(_p);
            }
            return new ZpElement(auxgen, _p, _q);
        }
    }

    @Override
    public ZpElement getRandomElement() {
        Exponent expo = Exponent.getRandomExponent(getOrder());
        if (_gen == null) {
            _gen = getGenerator();
        }

        return _gen.exponentiate(expo);
    }

    @Override
    public ZpElement[] getVectorRandomElement(final int length) {
        ZpElement[] result = new ZpElement[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getRandomElement();
        }
        return result;
    }

    @Override
    public ZpElement getIdentityElement() {
        return new ZpElement(BigInteger.ONE, _p, _q);
    }

}
