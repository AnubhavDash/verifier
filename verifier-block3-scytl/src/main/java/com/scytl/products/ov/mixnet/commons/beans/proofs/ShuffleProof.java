/**
 * @author dsilva
 * @date   Mar 11, 2015 3:55:23 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 *
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

public class ShuffleProof {

    private final PublicCommitment[] _initialMessage;

    private final PublicCommitment[] _firstAnswer;

    private final ShuffleProofSecondAnswer _secondAnswer;

    @JsonCreator
    public ShuffleProof(@JsonProperty("initialMessage") final PublicCommitment[] initialMessage,
            @JsonProperty("firstAnswer") final PublicCommitment[] firstAnswer,
            @JsonProperty("secondAnswer") final ShuffleProofSecondAnswer secondAnswer) {

        _initialMessage = initialMessage;
        _firstAnswer = firstAnswer;
        _secondAnswer = secondAnswer;
    }

    private ShuffleProof(final Builder builder) {
        _initialMessage = builder._initialMessage;
        _firstAnswer = builder._firstAnswer;
        _secondAnswer = builder._secondAnswer;
    }

    public PublicCommitment[] getInitialMessage() {
        return _initialMessage;
    }

    public PublicCommitment[] getFirstAnswer() {
        return _firstAnswer;
    }

    public ShuffleProofSecondAnswer getSecondAnswer() {
        return _secondAnswer;
    }

    public static class Builder {

        private PublicCommitment[] _initialMessage;

        private PublicCommitment[] _firstAnswer;

        private ShuffleProofSecondAnswer _secondAnswer;

        public Builder withInitialMessage(final PublicCommitment[] initialMessage) {
            _initialMessage = initialMessage;
            return this;
        }

        public Builder withFirstAnswer(final PublicCommitment[] firstAnswer) {
            _firstAnswer = firstAnswer;
            return this;
        }

        public Builder withSecondAnswer(final ShuffleProofSecondAnswer secondAnswer) {
            _secondAnswer = secondAnswer;
            return this;
        }

        public ShuffleProof build() {
            checkValues();
            return new ShuffleProof(this);
        }

        private void checkValues() {
            if (_initialMessage == null) {
                throw new IllegalStateException("Initial message cannot be null");
            }
            if (_firstAnswer == null) {
                throw new IllegalStateException("First answer cannot be null");
            }
            if (_secondAnswer == null) {
                throw new IllegalStateException("Second answer cannot be null");
            }
        }
    }
}
