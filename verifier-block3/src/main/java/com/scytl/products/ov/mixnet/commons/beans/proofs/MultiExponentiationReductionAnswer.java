/**
 * @author aescala
 * @date 28/10/2013 16:25:46
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public class MultiExponentiationReductionAnswer {
    private final static Logger LOGGER = LoggerFactory.getLogger(MultiExponentiationReductionAnswer.class);

    private final Exponent[] _b;

    private final Exponent _s;

    private final MultiExponentiationBasicProofInitialMessage _iniBasic;

    private final MultiExponentiationBasicProofAnswer _ansBasic;

    private final MultiExponentiationReductionInitialMessage _iniReduct;

    private final MultiExponentiationReductionAnswer _ansReduct;

    @JsonCreator
    public MultiExponentiationReductionAnswer(@JsonProperty("exponentsB") final Exponent[] b,
            @JsonProperty("exponentS") final Exponent s,
            @JsonProperty("iniBasic") final MultiExponentiationBasicProofInitialMessage iniBasic,
            @JsonProperty("ansBasic") final MultiExponentiationBasicProofAnswer ansBasic,
            @JsonProperty("iniReduct") final MultiExponentiationReductionInitialMessage iniReduct,
            @JsonProperty("ansReduct") final MultiExponentiationReductionAnswer ansReduct) {
        _b = b;
        _s = s;
        _iniBasic = iniBasic;
        _ansBasic = ansBasic;
        _iniReduct = iniReduct;
        _ansReduct = ansReduct;
    }

    public MultiExponentiationReductionAnswer(final Exponent[] b, final Exponent s,
            final MultiExponentiationBasicProofInitialMessage iniBasic,
            final MultiExponentiationBasicProofAnswer ansBasic) {
        _b = b;
        _s = s;
        _iniBasic = iniBasic;
        _ansBasic = ansBasic;
        _iniReduct = null;
        _ansReduct = null;
    }

    public MultiExponentiationReductionAnswer(final Exponent[] b, final Exponent s,
            final MultiExponentiationReductionInitialMessage iniReduct,
            final MultiExponentiationReductionAnswer ansReduct) {
        _b = b;
        _s = s;
        _iniBasic = null;
        _ansBasic = null;
        _iniReduct = iniReduct;
        _ansReduct = ansReduct;
    }

    /**
     * @return Returns the b.
     */
    public Exponent[] getExponentsB() {
        return _b;
    }

    /**
     * @return Returns the s.
     */
    public Exponent getExponentS() {
        return _s;
    }

    /**
     * @return Returns the iniBasic.
     */
    public MultiExponentiationBasicProofInitialMessage getIniBasic() {
        return _iniBasic;
    }

    /**
     * @return Returns the ansBasic.
     */
    public MultiExponentiationBasicProofAnswer getAnsBasic() {
        return _ansBasic;
    }

    /**
     * @return Returns the iniReduct.
     */
    public MultiExponentiationReductionInitialMessage getIniReduct() {
        return _iniReduct;
    }

    /**
     * @return Returns the ansReduct.
     */
    public MultiExponentiationReductionAnswer getAnsReduct() {
        return _ansReduct;
    }

    @Override
    public String toString() {
        final StringBuilder strbldr = new StringBuilder();
        for (final Exponent a_b : _b) {
            strbldr.append(a_b.toString());
        }
        strbldr.append(_s.toString());
        if ((_iniBasic != null) && (_ansBasic != null)) {
            strbldr.append(_iniBasic.toString());
            strbldr.append(_ansBasic.toString());
        } else if ((_iniReduct != null) && (_ansReduct != null)) {
            strbldr.append(_iniReduct.toString());
            strbldr.append(_ansReduct.toString());
        } else {
            LOGGER.error("Error(ToString): answer msg ME reduct not initialized");
        }

        return strbldr.toString();
    }
}
