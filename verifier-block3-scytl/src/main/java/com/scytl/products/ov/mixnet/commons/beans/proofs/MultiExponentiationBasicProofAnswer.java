/**
 * @author aescala
 * @date   10/10/2013 16:59:56
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.homomorphic.Randomness;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public class MultiExponentiationBasicProofAnswer {

    private final Exponent[] _a;

    private final Exponent _r;

    private final Exponent _b;

    private final Exponent _s;

    private final Randomness _tau;



    @JsonCreator
    public MultiExponentiationBasicProofAnswer(@JsonProperty("exponentsA") final Exponent[] a,
            @JsonProperty("exponentR") final Exponent r, @JsonProperty("exponentsB") final Exponent b,
            @JsonProperty("exponentS") final Exponent s, @JsonProperty("randomnessTau") final Randomness tau) {
        _a = a;
        _r = r;
        _b = b;
        _s = s;
        _tau = tau;
    }

    /**
     * @return Returns the a.
     */
    public Exponent[] getExponentsA() {
        return _a;
    }

    /**
     * @return Returns the r.
     */
    public Exponent getExponentR() {
        return _r;
    }

    /**
     * @return Returns the b.
     */
    public Exponent getExponentB() {
        return _b;
    }

    /**
     * @return Returns the s.
     */
    public Exponent getExponentS() {
        return _s;
    }

    /**
     * @return Returns the tau.
     */
    public Randomness getRandomnessTau() {
        return _tau;
    }



    @Override
    public String toString() {
        final StringBuilder strbldr = new StringBuilder();
        for (final Exponent a_a : _a) {
            strbldr.append(a_a.toString());
        }
        strbldr.append(_r.toString());
        strbldr.append(_b.toString());

        strbldr.append(_s.toString());

        return strbldr.toString();
    }
}
