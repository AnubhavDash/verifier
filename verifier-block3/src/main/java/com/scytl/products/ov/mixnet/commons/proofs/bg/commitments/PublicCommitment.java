/**
 * @author aescala
 * @date 16/09/2013 16:15:58
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.proofs.bg.commitments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

/**
 * Encapsulates a public commitment.
 */
public class PublicCommitment {

    private final GroupElement _commitment;

    @JsonCreator
    public PublicCommitment(@JsonProperty("element") final GroupElement com) {
        _commitment = com;
    }

    public PublicCommitment multiply(final PublicCommitment commitment) {
        return new PublicCommitment(_commitment.multiply(commitment.getElement()));
    }

    public PublicCommitment exponentiate(final Exponent expo) {
        return new PublicCommitment(_commitment.exponentiate(expo));
    }

    public GroupElement getElement() {
        return _commitment;
    }


    public boolean verifyOpening(final Exponent[] exponents, final Exponent exponentR, final CommitmentParams params) {
        final PublicCommitment newCommitment =
            new PrivateCommitment(exponents, exponentR, params).makePublicCommitment();
        return _commitment.equals(newCommitment.getElement());
    }

    public boolean isEqual(final PublicCommitment commitment) {
        return (_commitment.equals(commitment.getElement()));
    }

    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	builder.append("PublicCommitment [_commitment=");
    	builder.append(_commitment);
    	builder.append("]");
    	return builder.toString();
    }
}
