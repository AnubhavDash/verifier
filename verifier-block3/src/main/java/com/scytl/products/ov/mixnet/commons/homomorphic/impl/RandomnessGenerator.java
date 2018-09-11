/**
 * @author afries
 * @date Mar 3, 2015 5:49:46 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import java.math.BigInteger;
import java.util.List;

import com.scytl.products.ov.mixnet.commons.homomorphic.Randomness;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public class RandomnessGenerator {

    private final BigInteger _groupOrder;

    public RandomnessGenerator(final BigInteger groupOrder) {
        _groupOrder = groupOrder;
    }

    public Randomness[] generate(final int numberExponents) {
        List<Exponent> randomListExponent = Exponent.getRandomListExponent(numberExponents, _groupOrder);

        final Randomness[] rho = new Randomness[numberExponents];
        for (int i = 0; i < randomListExponent.size(); i++) {
            Exponent exponent = randomListExponent.get(i);
            rho[i] = new GjosteenElGamalRandomness(exponent);
        }
        return rho;
    }
}
