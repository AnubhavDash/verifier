/**
 * @author afries  3/03/2015
 *
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.ballots;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates a list of ballots that have been encrypted using the ElGamal cryptosystem.
 * <p>
 * See {@link ElGamalEncryptedBallot} for the description of the contents of an encrypted ballot that has been encrypted
 * using the ElGamal cryptosystem.
 */
public final class ElGamalEncryptedBallots implements EncryptedBallots<ElGamalEncryptedBallot> {

    private final List<ElGamalEncryptedBallot> _ballots;

    /**
     * Create a ElGamalEncryptedBallots, setting the received list of ballots.
     * 
     * @param ballots
     *            a list of {@link ElGamalEncryptedBallot}.
     * @throws IllegalArgumentException
     *             if {@code ballots} is null
     */
    public ElGamalEncryptedBallots(final List<ElGamalEncryptedBallot> ballots) {

        List<ElGamalEncryptedBallot> defensiveListBallots = getDefensiveList(ballots);

        validateInputs(defensiveListBallots);

        _ballots = ballots;
    }

    @Override
    public List<ElGamalEncryptedBallot> getBallots() {
        return getDefensiveList(_ballots);
    }

    private List<ElGamalEncryptedBallot> getDefensiveList(final List<ElGamalEncryptedBallot> elements) {

        // List<ElGamalEncryptedBallot> defensiveList = ;

        return elements.stream().collect(Collectors.toList());
    }

    private void validateInputs(final List<ElGamalEncryptedBallot> ballots) {
        if (ballots == null) {
            throw new IllegalArgumentException("The list of ballots must be initialized");
        }
    }
}
