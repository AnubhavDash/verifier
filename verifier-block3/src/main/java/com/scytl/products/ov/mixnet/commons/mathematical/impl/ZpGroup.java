/**
 * @author aescala
 * @date 16/09/2013 13:00:37
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.mathematical.impl;

import java.math.BigInteger;

import com.scytl.products.ov.mixnet.commons.mathematical.Group;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.tools.BigIntTools;

public class ZpGroup implements Group {

    private final ZpGroupParams _params;

    private ZpElement _gen;

    public ZpGroup(final ZpGroupParams params) {
        _params = params;
        _gen = getGenerator();
    }

    public ZpGroup(final BigInteger p, final BigInteger q) {
        this(new ZpGroupParams(p, q));
    }

    public ZpGroup(final BigInteger p, final BigInteger q, final ZpElement generator) {
        this(new ZpGroupParams(p, q), generator);
    }

    public ZpGroup(final ZpGroupParams params, final ZpElement generator) {
        _params = new ZpGroupParams(params.getP(), params.getOrder());
        _gen = generator;
    }

    @Override
    public BigInteger getOrder() {
        return _params.getOrder();
    }

    public BigInteger getP() {
        return _params.getP();
    }

    @Override
    public ZpElement getGenerator() {

        if (_gen != null) {
            return _gen;
        } else {
            BigInteger auxgen = BigIntTools.generateBigInteger(_params.getP());

            while ((auxgen.compareTo(BigInteger.ONE) == 0)
                || auxgen.modPow(_params.getOrder(), _params.getP()).compareTo(BigInteger.ONE) != 0) {
                auxgen = BigIntTools.generateBigInteger(_params.getP());
            }
            return new ZpElement(auxgen, _params);
        }
    }

    @Override
    public GroupElement getRandomElement() {
        Exponent expo = Exponent.getRandomExponent(getOrder());
        if (_gen == null) {
            _gen = getGenerator();
        }

        return _gen.exponentiate(expo);
    }

    @Override
    public GroupElement[] getVectorRandomElement(final int length) {
        GroupElement[] result = new GroupElement[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getRandomElement();
        }
        return result;
    }

    @Override
    public ZpElement getIdentityElement() {
        return new ZpElement(BigInteger.ONE, _params);
    }

    public ZpGroupParams getParams() {
        return _params;
    }
}
