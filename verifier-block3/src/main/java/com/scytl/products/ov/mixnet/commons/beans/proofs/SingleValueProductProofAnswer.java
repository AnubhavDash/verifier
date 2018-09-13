/**
 * @author aescala
 * @date   18/09/2013 18:06:27
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public final class SingleValueProductProofAnswer {

    private final Exponent[] _tildeA;

    private final Exponent[] _tildeB;

    private final Exponent _tildeR;

    private final Exponent _tildeS;

    @JsonCreator
    public SingleValueProductProofAnswer(@JsonProperty("exponentsTildeA") final Exponent[] tildeA,
            @JsonProperty("exponentsTildeB") final Exponent[] tildeB,
            @JsonProperty("exponentTildeR") final Exponent tildeR,
            @JsonProperty("exponentTildeS") final Exponent tildeS) {
        _tildeA = tildeA;
        _tildeB = tildeB;
        _tildeR = tildeR;
        _tildeS = tildeS;
    }

    /**
     * @return Returns the tildeA.
     */
    public Exponent[] getExponentsTildeA() {
        return _tildeA;
    }

    /**
     * @return Returns the tildeB.
     */
    public Exponent[] getExponentsTildeB() {
        return _tildeB;
    }

    /**
     * @return Returns the tildeR.
     */
    public Exponent getExponentTildeR() {
        return _tildeR;
    }

    /**
     * @return Returns the tildeS.
     */
    public Exponent getExponentTildeS() {
        return _tildeS;
    }

    @Override
    public String toString() {
        final StringBuilder strbldr = new StringBuilder();
        for (final Exponent a_tildeA : _tildeA) {
            strbldr.append(a_tildeA.toString());
        }
        for (final Exponent a_tildeB : _tildeB) {
            strbldr.append(a_tildeB.toString());
        }
        strbldr.append(_tildeR.toString());
        strbldr.append(_tildeS.toString());
        return strbldr.toString();
    }

}
