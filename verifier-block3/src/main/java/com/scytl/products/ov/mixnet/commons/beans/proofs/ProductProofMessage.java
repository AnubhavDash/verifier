/**
 * @author aescala
 * @date 19/09/2013 16:43:37
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

public final class ProductProofMessage {

    private final SingleValueProductProofInitialMessage _iniSVA;

    private final SingleValueProductProofAnswer _ansSVA;

    private final HadamardProductProofInitialMessage _iniHPA;

    private final HadamardProductProofAnswer _ansHPA;

    private final PublicCommitment _cB;

    @JsonCreator
    public ProductProofMessage(@JsonProperty("commitmentPublicB") final PublicCommitment cB,
            @JsonProperty("iniSVA") final SingleValueProductProofInitialMessage iniSVA,
            @JsonProperty("ansSVA") final SingleValueProductProofAnswer ansSVA,
            @JsonProperty("iniHPA") final HadamardProductProofInitialMessage iniHPA,
            @JsonProperty("ansHPA") final HadamardProductProofAnswer ansHPA) {
        _cB = cB;
        _iniSVA = iniSVA;
        _ansSVA = ansSVA;
        _iniHPA = iniHPA;
        _ansHPA = ansHPA;
    }

    /**
     * @return Returns the iniSVA.
     */
    public SingleValueProductProofInitialMessage getIniSVA() {
        return _iniSVA;
    }

    /**
     * @return Returns the ansSVA.
     */
    public SingleValueProductProofAnswer getAnsSVA() {
        return _ansSVA;
    }

    /**
     * @return Returns the iniHPA.
     */
    public HadamardProductProofInitialMessage getIniHPA() {
        return _iniHPA;
    }

    /**
     * @return Returns the ansHPA.
     */
    public HadamardProductProofAnswer getAnsHPA() {
        return _ansHPA;
    }

    /**
     * @return Returns the cB.
     */
    public PublicCommitment getCommitmentPublicB() {
        return _cB;
    }

    @Override
    public String toString() {
        return _cB.toString() + _iniSVA.toString() + _ansSVA.toString() + _iniHPA.toString() + _ansHPA.toString();
    }
}
