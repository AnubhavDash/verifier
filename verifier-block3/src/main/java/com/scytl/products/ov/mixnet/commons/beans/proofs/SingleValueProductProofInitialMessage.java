/**
 * @author aescala
 * @date 18/09/2013 18:06:17
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PrivateCommitment;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

public final class SingleValueProductProofInitialMessage {

    private final PublicCommitment _cd;

    // TODO improve fields names!
    private final PublicCommitment _cdelta;

    private final PublicCommitment _cDelta;

    @JsonCreator
    public SingleValueProductProofInitialMessage(@JsonProperty("commitmentPublicD") final PublicCommitment cd,
            @JsonProperty("commitmentPublicLowDelta") final PublicCommitment cdelta,
            @JsonProperty("commitmentPublicHighDelta") final PublicCommitment cDelta) {
        _cd = cd;
        _cdelta = cdelta;
        _cDelta = cDelta;
    }

    public SingleValueProductProofInitialMessage(final PrivateCommitment cd, final PrivateCommitment cdelta,
            final PrivateCommitment cDelta) {

        _cd = cd.makePublicCommitment();
        _cdelta = cdelta.makePublicCommitment();
        _cDelta = cDelta.makePublicCommitment();

    }

    /**
     * @return Returns the cd.
     */
    public PublicCommitment getCommitmentPublicD() {
        return _cd;
    }

    /**
     * @return Returns the cdelta.
     */
    public PublicCommitment getCommitmentPublicLowDelta() {
        return _cdelta;
    }

    /**
     * @return Returns the cDelta.
     */
    public PublicCommitment getCommitmentPublicHighDelta() {
        return _cDelta;
    }

    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	builder.append("SingleValueProductProofInitialMessage [cd=");
    	builder.append(_cd);
    	builder.append(", commitmentPublicLowDelta=");
    	builder.append(_cdelta);
    	builder.append(", commitmentPublicHighDelta=");
    	builder.append(_cDelta);
    	builder.append("]");
    	return builder.toString();
    }
    


}
