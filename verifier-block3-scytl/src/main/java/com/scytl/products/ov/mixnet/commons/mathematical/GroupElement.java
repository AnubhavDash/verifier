/**
 * @author aescala
 * @date 16/09/2013 11:40:06
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.mathematical;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface GroupElement {

    GroupElement multiply(GroupElement element);

    GroupElement exponentiate(Exponent expo);

    GroupElement inverse();

    boolean isGroupElement();

    BigInteger getValue();

    ZpGroupParams getParams();

}
