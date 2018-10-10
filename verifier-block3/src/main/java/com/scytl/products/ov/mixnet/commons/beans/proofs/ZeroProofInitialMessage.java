/**
 * @author aescala
 * @date 18/09/2013 10:47:18
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

public class ZeroProofInitialMessage {

    private final PublicCommitment _cA0;

    private final PublicCommitment _cBM;

    private final PublicCommitment[] _cD;

    @JsonCreator
    public ZeroProofInitialMessage(@JsonProperty("commitmentPublicA0") final PublicCommitment cA0,
            @JsonProperty("commitmentPublicBM") final PublicCommitment cBM,
            @JsonProperty("commitmentPublicD") final PublicCommitment[] cD) {

        _cA0 = cA0;
        _cBM = cBM;
        _cD = cD;
    }

    /**
     * @return Returns the cA0.
     */
    public PublicCommitment getCommitmentPublicA0() {
        return _cA0;
    }

    /**
     * @return Returns the cBM.
     */
    public PublicCommitment getCommitmentPublicBM() {
        return _cBM;
    }

    /**
     * @return Returns the cD.
     */
    public PublicCommitment[] getCommitmentPublicD() {
        return _cD;
    }

    @Override
    public String toString() {    	
        final StringBuilder strbldr = new StringBuilder();
        strbldr.append("ZeroProofInitialMessage [_cA0=");
        strbldr.append(_cA0);
        strbldr.append(", _cBM=");
        strbldr.append(_cBM);
        strbldr.append(", _cD=");
        strbldr.append(Arrays.toString(_cD));
        strbldr.append("]");
        return strbldr.toString();
    }

}
