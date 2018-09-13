/**
 * @author aescala
 * @date 20/09/2013 15:06:20
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public interface RandomnessCollection {

    RandomnessCollection add(RandomnessCollection r);

    RandomnessCollection multiply(Exponent e);

}
