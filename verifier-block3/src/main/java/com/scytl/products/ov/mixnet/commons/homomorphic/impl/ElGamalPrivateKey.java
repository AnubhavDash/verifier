/**
 * @author afries
 * @date Mar 4, 2015 5:53:40 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import com.scytl.products.ov.mixnet.commons.mathematical.Group;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

import java.util.List;

class ElGamalPrivateKey {

    private final List<Exponent> _privKeys;

    private final Group _group;

    public ElGamalPrivateKey(final List<Exponent> privKey, final Group group) {

        validateInputs(privKey, group);

        _privKeys = privKey;
        _group = group;
    }

    private void validateInputs(final List<Exponent> privKey, final Group group) throws IllegalArgumentException {

        if ((privKey == null) || (privKey.isEmpty())) {
            throw new IllegalArgumentException("The private key must be an initialised non-empty list");
        }

        if (group == null) {
            throw new IllegalArgumentException("The group must be an initialised instance of ZpSubgroup");
        }
    }

    public List<Exponent> getPrivKeys() {
        return _privKeys;
    }

    public Group getGroup() {
        return _group;
    }
}
