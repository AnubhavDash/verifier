/**
 * @author aescala
 * @date 16/09/2013 12:54:07
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.mathematical;

import java.math.BigInteger;

/**
 *
 */
public interface Group {
    GroupElement getGenerator();

    BigInteger getOrder();

    GroupElement getRandomElement();

    GroupElement[] getVectorRandomElement(int length);

    GroupElement getIdentityElement();

}
