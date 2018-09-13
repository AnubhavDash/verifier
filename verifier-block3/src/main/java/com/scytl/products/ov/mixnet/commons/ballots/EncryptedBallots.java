/**
 * @author afries  6/03/2015
 *
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.ballots;

import java.util.List;

/**
 * Represents a collection of encrypted ballots.
 * 
 * @param <T>
 *            the type of encrypted ballots contained in this EncryptedBallots.
 */
public interface EncryptedBallots<T extends EncryptedBallot> {

    /**
     * Get the contents of this EncryptedBallots as a list of type {@code T}.
     * 
     * @return the list of encrypted ballots contained in this EncryptedBallots.
     */
    List<T> getBallots();
}
