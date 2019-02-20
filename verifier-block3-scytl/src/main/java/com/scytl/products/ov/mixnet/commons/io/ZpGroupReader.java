/**
 * @author jruiz
 * @date 23/06/15 18:13
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

import java.math.BigInteger;

public class ZpGroupReader {

    public static ZpGroup createZpGroupFromParameterStrings(final String pAsString, final String qAsString,
                                                            final String gAsString) {

        final BigInteger pAsBigInteger = new BigInteger(pAsString);
        final BigInteger qAsBigInteger = new BigInteger(qAsString);

        final ZpGroupParams zpGroupParams = new ZpGroupParams(pAsBigInteger, qAsBigInteger);

        //final ZpGroup reconstructedZpGroup = new ZpGroup(pAsBigInteger, qAsBigInteger, new ZpElement(gAsString, zpGroupParams));
        return new ZpGroup(pAsBigInteger, qAsBigInteger, new ZpElement(gAsString, zpGroupParams));
    }
}
