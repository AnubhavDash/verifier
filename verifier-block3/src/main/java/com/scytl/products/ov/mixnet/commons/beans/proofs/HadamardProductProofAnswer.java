/**
 * @author aescala
 * @date 18/09/2013 16:31:22
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class HadamardProductProofAnswer {

    private final ZeroProofInitialMessage _initial;

    private final ZeroProofAnswer _answer;

    @JsonCreator
    public HadamardProductProofAnswer(@JsonProperty("initial") final ZeroProofInitialMessage initial,
            @JsonProperty("answer") final ZeroProofAnswer answer) {
        _initial = initial;
        _answer = answer;
    }

    /**
     * @return Returns the initial.
     */
    public ZeroProofInitialMessage getInitial() {
        return _initial;
    }

    /**
     * @return Returns the answer.
     */
    public ZeroProofAnswer getAnswer() {
        return _answer;
    }

    @Override
    public String toString() {
        return _initial.toString() + _answer.toString();
    }

}
