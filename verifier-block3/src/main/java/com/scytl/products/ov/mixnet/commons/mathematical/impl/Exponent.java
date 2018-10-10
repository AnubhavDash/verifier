/**
 * @author aescala
 * @date 16/09/2013 15:16:11
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.mathematical.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.tools.BigIntTools;

public class Exponent {

    private final BigInteger _order;

    private final BigInteger _value;

    public Exponent(final BigInteger value, final BigInteger order) {
        this(value, order, false);
    }

    // TODO [Marc] Re-think this approach. Use a Factory to create
    // 'smallExponents'?
    public Exponent(final long value, final BigInteger order) {
        this(BigInteger.valueOf(value), order, value >= 0);
    }

    private Exponent(final BigInteger value, final BigInteger order, final boolean safe) {

        _order = order;
        if (safe) {
            _value = value;
        } else {
            _value = value.mod(_order);
        }
    }

    @JsonCreator
    public Exponent(@JsonProperty("value") final String value, @JsonProperty("q") final BigInteger order) {
        this(new BigInteger(value), order, false);
    }

    public static Exponent getRandomExponent(final BigInteger order) {
        return new Exponent(BigIntTools.generateBigInteger(order), order, true);
    }

    public static Exponent[] getVectorRandomExponent(final int length, final BigInteger order) {
        final Exponent[] result = new Exponent[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getRandomExponent(order);
        }
        return result;
    }

    public static List<Exponent> getRandomListExponent(final int length, final BigInteger order) {

        final List<Exponent> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            result.add(getRandomExponent(order));
        }
        return result;
    }

    public static Exponent getRandomInvertibleExponent(final BigInteger order) {
        return new Exponent(BigIntTools.generateInvertibleBigInteger(order), order, true);
    }

    public BigInteger getValue() {
        return _value;
    }

    public Exponent add(final Exponent e) {
        final BigInteger result = _value.add(e.getValue()).mod(_order);
        return new Exponent(result, _order, true);
    }

    public Exponent subtract(final Exponent e) {
        final BigInteger result = _value.subtract(e.getValue()).mod(_order);
        return new Exponent(result, _order, true);
    }

    public Exponent multiply(final Exponent e) {
        final BigInteger result = _value.multiply(e.getValue()).mod(_order);
        return new Exponent(result, _order, true);
    }

    @JsonIgnore
    public Exponent getInverse() {
        return new Exponent(_value.modInverse(_order), _order, true);
    }

    public Exponent divide(final Exponent e) {
        return multiply(e.getInverse());
    }

    public Exponent[] getVectorRandomInvertibleExponent(final int length, final BigInteger order) {
        final Exponent[] result = new Exponent[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getRandomInvertibleExponent(order);
        }
        return result;
    }

    @JsonIgnore
    public boolean isExponent() {
        boolean correct = true;

        if (_value.compareTo(BigInteger.ZERO) == -1) {
            correct = false;
        }

        if (_value.compareTo(_order) != -1) {
            correct = false;
        }
        return correct;
    }

    @JsonIgnore
    public boolean isInvertibleExponent() {
        boolean correct = true;

        if (_value.compareTo(BigInteger.ZERO) != 1) {
            correct = false;
        }

        if (_value.compareTo(_order) != -1) {
            correct = false;
        }
        return correct;
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + ((_order == null) ? 0 : _order.hashCode());
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
        Exponent other = (Exponent) obj;
        if (_order == null) {
            if (other._order != null) {
                return false;
            }
        } else if (!_order.equals(other._order)) {
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
    	StringBuilder builder = new StringBuilder();
    	builder.append("Exponent [_q=");
    	builder.append(_order);
    	builder.append(", _value=");
    	builder.append(_value);
    	builder.append("]");
        return builder.toString();
    }

    // XXX added
    public Exponent negate() {
        return new Exponent(_value.negate().mod(_order), _order, true);
    }

    /**
     * @return Returns the order.
     */
    public BigInteger getOrder() {
        return _order;
    }

}
