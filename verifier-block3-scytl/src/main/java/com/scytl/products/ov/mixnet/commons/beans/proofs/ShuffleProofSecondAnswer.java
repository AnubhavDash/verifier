/**
 * @author aescala
 * @date   30/10/2013 14:57:43
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ShuffleProofSecondAnswer {

    private final MultiExponentiationBasicProofInitialMessage _iniMEBasic;

    private final MultiExponentiationBasicProofAnswer _ansMEBasic;

    private final MultiExponentiationReductionInitialMessage _iniMEReduct;

    private final MultiExponentiationReductionAnswer _ansMEReduct;

    private final ProductProofMessage _msgPA;

    @JsonCreator
    public ShuffleProofSecondAnswer(
            @JsonProperty("iniMEReduct") final MultiExponentiationReductionInitialMessage iniMEReduct,
            @JsonProperty("ansMEReduct") final MultiExponentiationReductionAnswer ansMEReduct,
            @JsonProperty("msgPA") final ProductProofMessage msgPA,
            @JsonProperty("iniMEBasic") final MultiExponentiationBasicProofInitialMessage iniMEBasic,
            @JsonProperty("ansMEBasic") final MultiExponentiationBasicProofAnswer ansMEBasic) {
        _iniMEBasic = iniMEBasic;
        _ansMEBasic = ansMEBasic;
        _iniMEReduct = iniMEReduct;
        _ansMEReduct = ansMEReduct;
        _msgPA = msgPA;
    }

    public ShuffleProofSecondAnswer(final MultiExponentiationReductionInitialMessage iniMEReduct,
            final MultiExponentiationReductionAnswer ansMEReduct, final ProductProofMessage msgPA) {
        _iniMEBasic = null;
        _ansMEBasic = null;
        _iniMEReduct = iniMEReduct;
        _ansMEReduct = ansMEReduct;
        _msgPA = msgPA;
    }

    public ShuffleProofSecondAnswer(final MultiExponentiationBasicProofInitialMessage iniMEBasic,
            final MultiExponentiationBasicProofAnswer ansMEBasic, final ProductProofMessage msgPA) {
        _iniMEBasic = iniMEBasic;
        _ansMEBasic = ansMEBasic;
        _iniMEReduct = null;
        _ansMEReduct = null;
        _msgPA = msgPA;
    }

    /**
     * @return Returns the iniMEBasic.
     */
    public MultiExponentiationBasicProofInitialMessage getIniMEBasic() {
        return _iniMEBasic;
    }

    /**
     * @return Returns the ansMEBasic.
     */
    public MultiExponentiationBasicProofAnswer getAnsMEBasic() {
        return _ansMEBasic;
    }

    /**
     * @return Returns the iniMEReduct.
     */
    public MultiExponentiationReductionInitialMessage getIniMEReduct() {
        return _iniMEReduct;
    }

    /**
     * @return Returns the ansMEReduct.
     */
    public MultiExponentiationReductionAnswer getAnsMEReduct() {
        return _ansMEReduct;
    }

    /**
     * @return Returns the msgPA.
     */
    public ProductProofMessage getMsgPA() {
        return _msgPA;
    }

    @Override
    public String toString() {
        final StringBuilder strbldr = new StringBuilder();
        if ((_iniMEBasic != null) && (_ansMEBasic != null)) {
            strbldr.append(_iniMEBasic.toString());
            strbldr.append(_ansMEBasic.toString());
        } else {
            strbldr.append(_iniMEReduct != null ? _iniMEReduct.toString() : null);
            strbldr.append(_ansMEReduct != null ? _ansMEReduct.toString() : null);
        }
        strbldr.append(_msgPA.toString());
        return strbldr.toString();
    }
}
