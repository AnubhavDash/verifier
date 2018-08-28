/**
 * @author aescala
 * @date 16/09/2013 11:43:41
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.mathematical.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class ZpElement /*implements GroupElement*/ {

    private final BigInteger _p;

    private final BigInteger _q;

    private final BigInteger _value;

    public ZpElement(final BigInteger value, final BigInteger p, final BigInteger q) {
        _value = value;
        _p = p;
        _q = q;
    }

    public ZpElement(final long value, final BigInteger p, final BigInteger q) {
        _value = BigInteger.valueOf(value);
        _p = p;
        _q = q;
    }

    @JsonCreator
    public ZpElement(@JsonProperty("value") final String value, @JsonProperty("p") final BigInteger p, @JsonProperty("q") final BigInteger q) {
        _value = new BigInteger(value);
        _p = p;
        _q = q;
    }

    public BigInteger getValue() {
        return _value;
    }

    public ZpElement multiply(final ZpElement element) {
        final ZpElement zpinput = (ZpElement) element;
        final BigInteger outputvalue = _value.multiply(zpinput.getValue()).mod(_p);
        return new ZpElement(outputvalue, _p, _q);
    }

    public ZpElement exponentiate(final Exponent expo) {
        final BigInteger outputvalue = _value.modPow(expo.getValue(), _p);
        return new ZpElement(outputvalue, _p, _q);
    }

    public ZpElement inverse() {
        return new ZpElement(_value.modInverse(_p), _p, _q);
    }

    @JsonIgnore
    public boolean isGroupElement() {
        boolean correct = true;

        if (_value.compareTo(BigInteger.ZERO) != 1) {
            correct = false;
        }

        if (_value.compareTo(_p) != -1) {
            correct = false;
        }

        if (_value.modPow(_q, _p).compareTo(BigInteger.ONE) != 0) {
            correct = false;
        }
        return correct;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 29;
        int result = 1;
        result = prime * result + ((_p == null) ? 0 : _p.hashCode());
        result = prime * result + ((_q == null) ? 0 : _q.hashCode());
        result = prime * result + ((_value == null) ? 0 : _value.hashCode());
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
        ZpElement other = (ZpElement) obj;
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
        if (_value == null) {
            if (other._value != null) {
                return false;
            }
        } else if (!_value.equals(other._value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return _value.toString();
    }

    public BigInteger getP() {
        return _p;
    }

    public BigInteger getQ() {
        return _q;
    }

}
