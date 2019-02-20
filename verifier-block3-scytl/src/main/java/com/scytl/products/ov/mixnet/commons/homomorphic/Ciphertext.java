/**
 * @author aescala
 * @date 20/09/2013 16:05:30
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface Ciphertext {

    GroupElement[] getParts();

    Ciphertext multiply(Ciphertext c);

    Ciphertext exponentiate(Exponent e);

    boolean isCiphertext();
}
