/**
 * @author aescala
 * @date 18/09/2013 18:05:38
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import java.math.BigInteger;

import ch.post.it.evoting.verifier.block.block3.BGResultNotifier;
import ch.post.it.evoting.verifier.block.block3.BGOfflineVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.TestType;
import ch.post.it.evoting.verifier.common.Status;
import org.apache.log4j.Logger;

import com.scytl.products.ov.mixnet.commons.beans.proofs.SingleValueProductProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.SingleValueProductProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import com.scytl.products.ov.mixnet.commons.tools.RandomOracleHash;

public class SingleValueProductProofVerifier extends Verifier {
    private final static Logger LOGGER = Logger.getLogger(SingleValueProductProofVerifier.class);

    private final CommitmentParams _compars;

    private final PublicCommitment _cA;

    private final Exponent _b;

    private final int _n;

    private final RandomOracleHash _RO;

    public SingleValueProductProofVerifier(final CommitmentParams compars, final PublicCommitment cA, final Exponent b,
            final BigInteger groupOrder) {
        super("Single Value Product Argument");
        _compars = compars;
        _cA = cA;
        _b = b;
        _n = _compars.getCommitmentLength();
        _RO = new RandomOracleHash(groupOrder);
    }

    public boolean verify(final SingleValueProductProofInitialMessage ini, final SingleValueProductProofAnswer ans, BGResultNotifier notifier) {

        if (!validateInitialData(ini, ans, notifier)) {
            notifier.notify(TestType.SingleValueProductProof, Status.NOK, "InitialData validation failed");
            return false;
        }

        _RO.addDataToRO(_cA);
        _RO.addDataToRO(_b);
        _RO.addDataToRO(ini);
        final Exponent challengeX = _RO.getHash();

        final PublicCommitment comCATilde = _cA.exponentiate(challengeX).multiply(ini.getCommitmentPublicD());
        final PublicCommitment comCDeltadelta =
            ini.getCommitmentPublicHighDelta().exponentiate(challengeX).multiply(ini.getCommitmentPublicLowDelta());

        final Exponent[] openingcdeltaDelta = new Exponent[_n - 1];
        for (int i = 0; i < openingcdeltaDelta.length; i++) {
            openingcdeltaDelta[i] = challengeX.multiply(ans.getExponentsTildeB()[i + 1])
                .add(ans.getExponentsTildeB()[i].multiply(ans.getExponentsTildeA()[i + 1]).negate());
        }
        if (!validate(ans, challengeX, comCATilde, comCDeltadelta, openingcdeltaDelta)) {
            notifier.notify(TestType.SingleValueProductProof, Status.NOK, "validation failed");
            return false;
        }

        LOGGER.info("The Single Value Product Argument was verified successfully!");
        notifier.notify(TestType.SingleValueProductProof, Status.OK, null);
        return true;

    }

    private boolean validate(SingleValueProductProofAnswer ans, Exponent challengeX, PublicCommitment comCATilde,
            PublicCommitment comCDeltadelta, Exponent[] openingcdeltaDelta) {
        return isValidOpening(openingcdeltaDelta, ans.getExponentTildeS(), comCDeltadelta, "Delta or delta")
            && isValidOpening(ans.getExponentsTildeA(), ans.getExponentTildeR(), comCATilde, "a or d")
            && areEquals(ans.getExponentsTildeB()[0], ans.getExponentsTildeA()[0], "tilde a and b")
            && areEquals(ans.getExponentsTildeB()[_n - 1], challengeX.multiply(_b), "tilde b and bchallenge");
    }

    private boolean validateInitialData(SingleValueProductProofInitialMessage ini, SingleValueProductProofAnswer ans, BGResultNotifier notifier) {
        return isGroupElement(ini.getCommitmentPublicD(), "cd")
            && isGroupElement(ini.getCommitmentPublicLowDelta(), "cdelta")
            && isGroupElement(ini.getCommitmentPublicHighDelta(), "cDelta")
            && isValidExponent(ans.getExponentsTildeA(), _n, "a tilde")
            && isValidExponent(ans.getExponentsTildeB(), _n, "b tilde") && isValidExponent(ans.getExponentTildeR(), "r")
            && isValidExponent(ans.getExponentTildeS(), "s");
    }

    public CommitmentParams getParams() {
        return _compars;
    }

    public BigInteger getOrder() {
        return _compars.getGroup().getOrder();
    }

    public Logger getLogger() {
        return LOGGER;
    }
}
