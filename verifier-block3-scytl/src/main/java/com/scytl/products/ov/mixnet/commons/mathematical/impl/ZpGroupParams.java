/**
 * @author dsilva
 * @date Mar 23, 2015 3:40:24 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.mathematical.impl;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Immutable bean for aggregating the values that characterize a Zp mathematical group
 */
public class ZpGroupParams {

    private final BigInteger _p;

    private final BigInteger _q;

    /**
     * @param p
     *            the size of the field, Z_{p}
     * @param q
     *            the order of a proper generator of a cyclic multiplicative subgroup
     */
    @JsonCreator
    public ZpGroupParams(@JsonProperty("p") final BigInteger p, @JsonProperty("order") final BigInteger q) {
        super();
        _p = p;
        _q = q;
    }

    /**
     * @return Returns the p.
     */
    public BigInteger getP() {
        return _p;
    }

    /**
     * @return Returns the q.
     */
    public BigInteger getOrder() {
        return _q;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_p == null) ? 0 : _p.hashCode());
        result = prime * result + ((_q == null) ? 0 : _q.hashCode());
        return result;
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ZpGroupParams other = (ZpGroupParams) obj;
        if (_p == null) {
            if (other._p != null) {
                return false;
            }
        } else if (!_p.equals(other._p)) {
            return false;
        }
        if (_q == null) {
            if (other._q != null) {
                return false;
            }
        } else if (!_q.equals(other._q)) {
            return false;
        }
        return true;
    }

}
