/**
 * @author aescala
 * @date 18/09/2013 15:31:50
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

public final class HadamardProductProofInitialMessage {

    private final PublicCommitment[] _cB;

    @JsonCreator
    public HadamardProductProofInitialMessage(@JsonProperty("commitmentPublicB") final PublicCommitment[] cB) {
        _cB = cB;
    }

    /**
     * @return Returns the cB.
     */
    public PublicCommitment[] getCommitmentPublicB() {
        return _cB;
    }

    @Override
    public String toString() {
        final StringBuilder strbldr = new StringBuilder();
        strbldr.append("HadamardProductProofInitialMessage [_cB=");
        strbldr.append(Arrays.toString(_cB));
        strbldr.append("]");
        return strbldr.toString();
    }
}
