/**
 * @author aescala
 * @date 18/09/2013 10:49:52
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public class ZeroProofAnswer {

    private final Exponent[] _a;

    private final Exponent[] _b;

    private final Exponent _r;

    private final Exponent _s;

    private final Exponent _t;

    @JsonCreator
    public ZeroProofAnswer(@JsonProperty("exponentsA") final Exponent[] a,
                           @JsonProperty("exponentsB") final Exponent[] b, @JsonProperty("exponentR") final Exponent r,
                           @JsonProperty("exponentS") final Exponent s, @JsonProperty("exponentT") final Exponent t) {
        _a = a;
        _b = b;
        _r = r;
        _s = s;
        _t = t;
    }

    /**
     * @return Returns the a.
     */
    public Exponent[] getExponentsA() {
        return _a;
    }

    /**
     * @return Returns the b.
     */
    public Exponent[] getExponentsB() {
        return _b;
    }

    /**
     * @return Returns the r.
     */
    public Exponent getExponentR() {
        return _r;
    }

    /**
     * @return Returns the s.
     */
    public Exponent getExponentS() {
        return _s;
    }

    /**
     * @return Returns the t.
     */
    public Exponent getExponentT() {
        return _t;
    }

    @Override
    public String toString() {
        final StringBuilder strbldr = new StringBuilder();
        for (Exponent a_a : _a) {
            strbldr.append(a_a.toString());
        }
        for (Exponent a_b : _b) {
            strbldr.append(a_b.toString());
        }
        strbldr.append(_r.toString());
        strbldr.append(_s.toString());
        strbldr.append(_t.toString());
        return strbldr.toString();
    }
}
