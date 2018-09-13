/**
 * @author vmateu  15/12/2016
 *
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.tools;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class RandomOracleHashDecrypt {

    private final MessageDigest _md;

    private final BigInteger _groupOrder;

    public RandomOracleHashDecrypt(final BigInteger groupOrder) throws NoSuchAlgorithmException {
        _md = MessageDigest.getInstance("SHA-512/224");
        _groupOrder = groupOrder;
    }

    public void addDataToRO(final List<ZpElement> elements) {
        String uniqueIdsConcat = concatValues(elements);
        _md.update(uniqueIdsConcat.getBytes(StandardCharsets.UTF_8));
    }

    public void addDataToRO(final String o) {
        _md.update(o.getBytes(StandardCharsets.UTF_8));
    }

    public Exponent getHash() {
        final BigInteger aux = new BigInteger(1, _md.digest());
        return new Exponent(aux, _groupOrder);
    }

    public void reset() {
        _md.reset();
    }

    private String concatValues(final List<ZpElement> elements) {
        StringBuilder result = new StringBuilder();
        for (ZpElement element : elements) {
            result.append(element.getValue());
        }
        return result.toString();
    }

}
