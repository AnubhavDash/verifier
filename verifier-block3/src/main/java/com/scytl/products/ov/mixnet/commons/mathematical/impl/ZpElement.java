/**
 * @author aescala
 * @date 16/09/2013 11:43:41
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.mathematical.impl;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;

public class ZpElement implements GroupElement {

    private final ZpGroupParams _params;

    private final BigInteger _value;

    public ZpElement(final BigInteger value, final ZpGroupParams zpGroupParams) {
        _value = value;
        _params = zpGroupParams;
    }

    public ZpElement(final long value, final ZpGroupParams zpGroupParams) {
        _value = BigInteger.valueOf(value);
        _params = zpGroupParams;
    }

    @JsonCreator
    public ZpElement(@JsonProperty("value") final String value, @JsonProperty("params") final ZpGroupParams params) {
        _value = new BigInteger(value);
        _params = params;
    }

    public BigInteger getValue() {
        return _value;
    }

    @Override
    public GroupElement multiply(final GroupElement element) {
        final ZpElement zpinput = (ZpElement) element;
        final BigInteger outputvalue = _value.multiply(zpinput.getValue()).mod(_params.getP());
        return new ZpElement(outputvalue, _params);
    }

    @Override
    public GroupElement exponentiate(final Exponent expo) {
        final BigInteger outputvalue = _value.modPow(expo.getValue(), _params.getP());
        return new ZpElement(outputvalue, _params);
    }

    @Override
    public GroupElement inverse() {
        return new ZpElement(_value.modInverse(_params.getP()), _params);
    }

    @Override
    @JsonIgnore
    public boolean isGroupElement() {
        boolean correct = true;

        if (_value.compareTo(BigInteger.ZERO) != 1) {
            correct = false;
        }

        if (_value.compareTo(_params.getP()) != -1) {
            correct = false;
        }

        if (_value.modPow(_params.getOrder(), _params.getP()).compareTo(BigInteger.ONE) != 0) {
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
        result = prime * result + ((_params == null) ? 0 : _params.hashCode());
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
        if (_params == null) {
            if (other._params != null) {
                return false;
            }
        } else if (!_params.equals(other._params)) {
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

    /**
     * @return Returns the zpGroupParams.
     */
    public ZpGroupParams getParams() {
        return _params;
    }

}
