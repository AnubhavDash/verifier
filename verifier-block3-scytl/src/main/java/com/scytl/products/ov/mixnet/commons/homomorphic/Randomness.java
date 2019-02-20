/**
 * @author aescala
 * @date 20/09/2013 15:06:20
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface Randomness {

    Randomness add(Randomness r);

    Randomness multiply(Exponent e);

    boolean isRandomness();

    Exponent getExponent();
}
