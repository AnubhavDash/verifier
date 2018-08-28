/**
 * @author afries
 * @date Apr 8, 2015 2:01:26 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.tools;

import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.EncryptedBallots;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;

import java.util.List;

public final class MatrixArranger {

    /**
     * Arranges the received {@link ElGamalEncryptedBallot} into a matrix of {@link Ciphertext}.
     *
     * @param reencryptedBallots
     *            the EncryptedBallots to be arranged.
     * @param m
     *            the m parameter.
     * @param n
     *            the n parameter.
     * @return a matrix of Ciphertexts (arranged as m * n).
     */
    public static Ciphertext[][] arrangeInCiphertextMatrix(
            final EncryptedBallots<ElGamalEncryptedBallot> reencryptedBallots, final int m, final int n) {

        if (reencryptedBallots.getBallots().size() != (m * n)) {
            throw new IllegalArgumentException("Incorrect combination of m, n and ballot size: " + m + ", " + n + ", "
                + reencryptedBallots.getBallots().size());
        }

        final List<ElGamalEncryptedBallot> reencryptedBallotsList = reencryptedBallots.getBallots();
        final Ciphertext[][] ret = new Ciphertext[m][n];
        int j = -1;
        for (int i = 0, size = reencryptedBallotsList.size(); i < size; i++) {
            final int k = i % n;
            if (k == 0) {
                j++;
            }
            ret[j][k] = reencryptedBallotsList.get(i);
        }
        return ret;
    }

}
