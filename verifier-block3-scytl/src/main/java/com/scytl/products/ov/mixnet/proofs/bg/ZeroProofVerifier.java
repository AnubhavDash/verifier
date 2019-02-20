/**
 * @author aescala
 * @date 18/09/2013 10:58:20
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import ch.post.it.evoting.verifier.block.block3.scytl.BGResultNotifier;
import ch.post.it.evoting.verifier.block.block3.scytl.Status;
import ch.post.it.evoting.verifier.block.block3.scytl.TestType;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ZeroProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ZeroProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import com.scytl.products.ov.mixnet.commons.tools.ExponentTools;
import com.scytl.products.ov.mixnet.commons.tools.RandomOracleHash;
import org.apache.log4j.Logger;

import java.math.BigInteger;

public class ZeroProofVerifier extends Verifier {
    private final static Logger LOGGER = Logger.getLogger(ZeroProofVerifier.class);

    private final CommitmentParams _params;

    private final PublicCommitment[] _cA;

    private final PublicCommitment[] _cB;

    private final int _n;

    private final int _m;

    private final RandomOracleHash _RO;

    private final BigInteger _groupOrder;

    private final Exponent _challengeInnerProduct;

    public ZeroProofVerifier(final CommitmentParams params, final PublicCommitment[] cA, final PublicCommitment[] cB,
                             final BigInteger groupOrder, final Exponent challengeInnerProduct) {
        super("Zero Argument");
        _params = params;
        _n = _params.getCommitmentLength();
        _m = cA.length;

        _groupOrder = groupOrder;

        _cA = new PublicCommitment[_m + 1];
        System.arraycopy(cA, 0, _cA, 1, _m);

        _cB = new PublicCommitment[_m + 1];
        System.arraycopy(cB, 0, _cB, 0, _m);
        _RO = new RandomOracleHash(groupOrder);
        _RO.addDataToRO(cA);
        _RO.addDataToRO(cB);
        _challengeInnerProduct = challengeInnerProduct;
    }

    public boolean verify(final ZeroProofInitialMessage initial, final ZeroProofAnswer answer, BGResultNotifier notifier) {

        final PublicCommitment[] cD = initial.getCommitmentPublicD();
        _cA[0] = initial.getCommitmentPublicA0();
        _cB[_m] = initial.getCommitmentPublicBM();

        if (!(isGroupElement(_cA[0], "cA0") && isGroupElement(_cB[_m], "cBm"))) {
            notifier.notify(TestType.ZeroProof, Status.NOK, "cA0 or cBm are not GroupElement");
            return false;
        }

        if ((cD.length != 2 * _m + 1) && (_m != 1)) {
            LOGGER.error("ERROR(Zero Argument): cD does not have the expected length");
            notifier.notify(TestType.ZeroProof, Status.NOK, "ERROR(Zero Argument): cD does not have the expected length");
            return false;
        }

        if (!isGroupElement(cD, "cD")) {
            notifier.notify(TestType.ZeroProof, Status.NOK, "cD is not GroupElement");
            return false;
        }
        if (!cD[_m + 1].verifyOpening(new Exponent[]{new Exponent(0, _groupOrder)}, new Exponent(0, _groupOrder),
                _params) && (_m != 1)) {
            LOGGER.error("ERROR(Zero Argument): cD[m+1] is not a commitment to 0 with randomness 0");
            notifier.notify(TestType.ZeroProof, Status.NOK, "ERROR(Zero Argument): cD[m+1] is not a commitment to 0 with randomness 0");
            return false;
        }

        if (!validate(answer)) {
            notifier.notify(TestType.ZeroProof, Status.NOK, "ZeroProof validation failed");
            return false;
        }

        PublicCommitment comCAR = _cA[0];
        PublicCommitment comCBS = _cB[_m];
        PublicCommitment comCABT = cD[0];

        // The statement is added to the RO in the constructor of this class
        _RO.addDataToRO(initial);
        final Exponent challengeX = _RO.getHash();

        Exponent accumulator = challengeX;

        for (int i = 1; i < _m + 1; i++) {
            comCAR = comCAR.multiply(_cA[i].exponentiate(accumulator));
            comCBS = comCBS.multiply(_cB[_m - i].exponentiate(accumulator));
            comCABT = comCABT.multiply(cD[i].exponentiate(accumulator));
            accumulator = accumulator.multiply(challengeX);
        }

        for (int i = _m + 1; i < 2 * _m + 1; i++) {
            comCABT = comCABT.multiply(cD[i].exponentiate(accumulator));
            accumulator = accumulator.multiply(challengeX);
        }
        if (!checkAllOpenings(comCAR, comCBS, comCABT, answer)) {
            notifier.notify(TestType.ZeroProof, Status.NOK, "checkAllOpenings failed");
            return false;
        }

        LOGGER.info("The Zero Argument was verified successfully!");
        return true;
    }

    private boolean validate(final ZeroProofAnswer answer) {
        return isValidExponent(answer.getExponentsA(), _n, "a") && isValidExponent(answer.getExponentsB(), _n, "b")
                && isValidExponent(answer.getExponentR(), "r") && isValidExponent(answer.getExponentS(), "s")
                && isValidExponent(answer.getExponentT(), "t");
    }

    private boolean checkAllOpenings(PublicCommitment comCAR, PublicCommitment comCBS, PublicCommitment comCABT,
                                     ZeroProofAnswer answer) {
        return isValidOpening(answer.getExponentsA(), answer.getExponentR(), comCAR, "a")
                && isValidOpening(answer.getExponentsB(), answer.getExponentS(), comCBS, "b")
                && isValidOpening(new Exponent[]{ExponentTools.innerProduct(answer.getExponentsA(), answer.getExponentsB(),
                _groupOrder, _challengeInnerProduct)}, answer.getExponentT(), comCABT, "inner b");
    }

    public CommitmentParams getParams() {
        return _params;
    }

    public BigInteger getOrder() {
        return _groupOrder;
    }

    public Logger getLogger() {
        return LOGGER;
    }
}
