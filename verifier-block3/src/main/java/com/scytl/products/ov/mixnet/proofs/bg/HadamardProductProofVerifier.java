/**
 * @author aescala
 * @date 18/09/2013 16:25:46
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import ch.post.it.evoting.verifier.block.block3.BGResultNotifier;
import ch.post.it.evoting.verifier.block.block3.BGOfflineVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.TestType;
import ch.post.it.evoting.verifier.common.Status;
import org.apache.log4j.Logger;

import com.scytl.products.ov.mixnet.commons.beans.proofs.HadamardProductProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.HadamardProductProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PrivateCommitment;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import com.scytl.products.ov.mixnet.commons.tools.RandomOracleHash;

public class HadamardProductProofVerifier {
    private final static Logger LOGGER = Logger.getLogger(HadamardProductProofVerifier.class);

    private final CommitmentParams _params;

    private final PublicCommitment[] _cA;

    private final PublicCommitment _cB;

    private final int _n;

    private final int _m;

    private final RandomOracleHash _RO;

    private final BigInteger _groupOrder;

    public HadamardProductProofVerifier(final CommitmentParams params, final PublicCommitment[] cA,
            final PublicCommitment cB, final BigInteger groupOrder) {
        _params = params;
        _n = _params.getCommitmentLength();
        _m = cA.length;
        _cA = cA;
        _cB = cB;
        _groupOrder = groupOrder;
        _RO = new RandomOracleHash(_groupOrder);
    }

    public boolean verify(final HadamardProductProofInitialMessage initial, final HadamardProductProofAnswer answer, BGResultNotifier notifier)
            throws NoSuchAlgorithmException {

        final PublicCommitment[] cGivenB = initial.getCommitmentPublicB();
        if (isValidCommitment(cGivenB, notifier)) {
            notifier.notify(TestType.HadamardProductProof, Status.OK, null);
        } else {
            return false;
        }

        final PublicCommitment[] cD = new PublicCommitment[_m];
        _RO.addDataToRO(_cA);
        _RO.addDataToRO(_cB);
        _RO.addDataToRO(initial);
        final Exponent challengeX = _RO.getHash();
        _RO.addDataToRO("1");
        final Exponent challengeInnerProduct = _RO.getHash();

        Exponent acumulator = challengeX;
        cD[0] = cGivenB[0].exponentiate(acumulator);
        PublicCommitment aux = cGivenB[1].exponentiate(acumulator);
        for (int i = 1; i < cD.length - 1; i++) {
            acumulator = acumulator.multiply(challengeX);
            cD[i] = cGivenB[i].exponentiate(acumulator);
            aux = aux.multiply(cGivenB[i + 1].exponentiate(acumulator));

        }
        cD[_m - 1] = aux;

        final PublicCommitment[] cZeroArgumentA = new PublicCommitment[_m];
        System.arraycopy(_cA, 1, cZeroArgumentA, 0, _m - 1);
        /*
         * cZeroArgumentA[_m - 1] = (new PrivateCommitment(ExponentTools.getMinus1Vector(_n, _groupOrder), new
         * Exponent(0, _groupOrder), _params)).makePublicCommitment();
         */

        cZeroArgumentA[_m - 1] =
            new PrivateCommitment(new Exponent(-1, _groupOrder), new Exponent(0, _groupOrder), _params, _n)
                .makePublicCommitment();

        final ZeroProofVerifier verifZero =
            new ZeroProofVerifier(_params, cZeroArgumentA, cD, _groupOrder, challengeInnerProduct);
        if (verifZero.verify(answer.getInitial(), answer.getAnswer(), notifier)) {
            notifier.notify(TestType.ZeroProof, Status.OK, null);
        } else {
            return false;
        }

        LOGGER.info("The Hadamard Argument was verified successfully!");
        return true;
    }

    private boolean isValidCommitment(PublicCommitment[] cGivenB, BGResultNotifier notifier) {
        if ((cGivenB.length != _m) && (cGivenB.length != 1)) {
            LOGGER.error("ERROR(Hadamard Argument): commitment to B does not have the expected length");
            notifier.notify(TestType.HadamardProductProof, Status.NOK, "ERROR(Hadamard Argument): commitment to B does not have the expected length");
            return false;
        }

        for (int i = 0; i < cGivenB.length; i++) {
            if (!cGivenB[i].getElement().isGroupElement()) {
                LOGGER.error("ERROR(Zero Argument): cB[" + i + "] is not a group element");
                notifier.notify(TestType.HadamardProductProof, Status.NOK, "ERROR(Zero Argument): cB[" + i + "] is not a group element");
                return false;
            }
        }

        if (!cGivenB[0].isEqual(_cA[0])) {
            LOGGER.error("ERROR(Hadamard Argument): commitment to B[0] does not correspond to commitment to A[0]");
            notifier.notify(TestType.HadamardProductProof, Status.NOK, "ERROR(Hadamard Argument): commitment to B[0] does not correspond to commitment to A[0]");
            return false;
        }

        if (!cGivenB[_m - 1].isEqual(_cB)) {
            if (!cGivenB[1].isEqual(_cB)) {
                LOGGER.error("ERROR(Hadamard Argument): commitment to B[m-1] does not correspond to commitment to b");
                notifier.notify(TestType.HadamardProductProof, Status.NOK, "ERROR(Hadamard Argument): commitment to B[m-1] does not correspond to commitment to b");
                return false;
            }
        }
        return true;
    }
}
