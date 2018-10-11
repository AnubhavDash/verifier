/**
 * @author aescala
 * @date 28/10/2013 15:47:48
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

public class MultiExponentiationReductionInitialMessage {

    private final PublicCommitment[] _cb;

    private final Ciphertext[] _E;

    @JsonCreator
    public MultiExponentiationReductionInitialMessage(@JsonProperty("commitmentPublicB") final PublicCommitment[] cb,
            @JsonProperty("ciphertextsE") final Ciphertext[] E) {
        _cb = cb;
        _E = E;
    }

    /**
     * @return Returns the cb.
     */
    public PublicCommitment[] getCommitmentPublicB() {
        return _cb;
    }

    /**
     * @return Returns the e.
     */
    public Ciphertext[] getCiphertextsE() {
        return _E;
    }

    @Override
    public String toString() {
        final StringBuilder strbldr = new StringBuilder();
        strbldr.append("MultiExponentiationReductionInitialMessage [_cb=");
        strbldr.append(Arrays.toString(_cb));
        strbldr.append(", _E=");
        strbldr.append(Arrays.toString(_E));
        strbldr.append("]");
        return strbldr.toString();
    }
}
