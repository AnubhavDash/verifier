/**
 * @author aescala
 * @date 04/11/2013 16:09:22
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import com.scytl.products.ov.mixnet.commons.homomorphic.Plaintext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;

public class GjosteenElGamalPlaintext implements Plaintext {

    private final GroupElement[] _m;

    public GjosteenElGamalPlaintext(final GroupElement[] m) {
        _m = m;
    }

    public GroupElement getValue(final int i) {
        return _m[i];
    }

    public GjosteenElGamalPlaintext multiply(final Plaintext p) {
        GroupElement[] aux = new GroupElement[_m.length];
        for (int i = 0; i < aux.length; i++) {
            aux[i] = _m[i].multiply(((GjosteenElGamalPlaintext) p).getValue(i));
        }
        return new GjosteenElGamalPlaintext(aux);
    }

    public String toString() {
        String output = "[";
        for (GroupElement element : _m) {
            output = output.concat(element.toString() + ",");
        }
        output = output.substring(0, output.length() - 1);
        return output.concat("]");
    }
}
