/**
 * @author aescala 29/10/2013
 *
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.tools;

import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RandomOracleHash {

    private MessageDigest _md;

    private final BigInteger _groupOrder;

    private StringBuilder _strbdr;

    public RandomOracleHash(final BigInteger groupOrder) {
        try {
            _md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            _md = null;
            e.printStackTrace();
        }
        _groupOrder = groupOrder;
        _strbdr = new StringBuilder();
    }

    public void addDataToRO(final Object o) {
        _strbdr.append(o.toString());
    }

    public void addDataToRO(final String o) {
        _strbdr.append(o);
    }

    public void addDataToRO(final Object[] o) {
        for (Object anO : o) {
            addDataToRO(anO);
        }
    }

    public void addDataToRO(final Ciphertext[][] o) {
        for (Ciphertext[] anO : o) {
            addDataToRO(anO);
        }
    }

    public Exponent getHash() {

        _md.reset();
        _md.update(_strbdr.toString().getBytes());
        final BigInteger aux = new BigInteger(1, _md.digest());
        return new Exponent(aux, _groupOrder);
    }

    public void reset() {
        _strbdr = new StringBuilder();
    }

}
