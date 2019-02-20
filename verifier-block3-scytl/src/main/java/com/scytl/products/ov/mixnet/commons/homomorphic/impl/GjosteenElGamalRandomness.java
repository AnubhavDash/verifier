/**
 * @author aescala
 * @date 23/09/2013 11:01:29
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.homomorphic.Randomness;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public class GjosteenElGamalRandomness implements Randomness {

    private final Exponent _r;

    public GjosteenElGamalRandomness(final long value, final BigInteger order) {
        this(new Exponent(BigInteger.valueOf(value), order));
    }

    public GjosteenElGamalRandomness(final BigInteger value, final BigInteger order) {
        this(new Exponent(value, order));
    }

    @JsonCreator
    public GjosteenElGamalRandomness(@JsonProperty("randomnessValue") final Exponent r) {
        _r = r;
    }

    public Exponent getRandomnessValue() {
        return _r;
    }

    @Override
    public Randomness add(final Randomness r) {
        return new GjosteenElGamalRandomness(_r.add(((GjosteenElGamalRandomness) r).getRandomnessValue()));
    }

    @Override
    public Randomness multiply(final Exponent e) {
        return new GjosteenElGamalRandomness(_r.multiply(e));
    }

    @Override
    @JsonIgnore
    public boolean isRandomness() {
        return _r.isExponent();
    }

    @Override
    @JsonIgnore
    public Exponent getExponent() {
        return _r;
    }

    @Override
    public String toString() {
        return _r.toString();
    }
}
