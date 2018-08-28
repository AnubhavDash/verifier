package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalCipherText {

    private final String _gamma;
    private final String _phis;

    @JsonCreator
    public InternalCipherText(@JsonProperty("gamma") final String gamma,
                              @JsonProperty("phis") final String phis) {
        _gamma = gamma;
        _phis = phis;
    }

    public String getGamma() {
        return _gamma;
    }

    public String getPhis() {
        return _phis;
    }
}
