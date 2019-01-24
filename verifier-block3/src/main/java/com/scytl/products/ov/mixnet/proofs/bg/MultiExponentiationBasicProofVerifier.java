/**
 * @author aescala
 * @date 14/10/2013 10:28:45
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import ch.post.it.evoting.verifier.block.block3.BGResultNotifier;
import ch.post.it.evoting.verifier.block.block3.BGOfflineVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.TestType;
import ch.post.it.evoting.verifier.common.Status;
import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationBasicProofAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationBasicProofInitialMessage;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.Cryptosystem;
import com.scytl.products.ov.mixnet.commons.homomorphic.Randomness;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import com.scytl.products.ov.mixnet.commons.tools.CiphertextTools;
import com.scytl.products.ov.mixnet.commons.tools.ExponentTools;
import com.scytl.products.ov.mixnet.commons.tools.RandomOracleHash;
import org.apache.log4j.Logger;

import java.math.BigInteger;

public class MultiExponentiationBasicProofVerifier extends Verifier {
    private final static Logger LOGGER = Logger.getLogger(MultiExponentiationBasicProofVerifier.class);

    private final Cryptosystem _cryptosystem;

    private final Ciphertext[][] _vecC;

    private final Ciphertext _C;

    private final PublicCommitment[] _cA;

    private final int _m;

    private final int _n;

    private final RandomOracleHash _RO;

    private final BigInteger _groupOrder;

    private final CommitmentParams _comParams;

    public MultiExponentiationBasicProofVerifier(final Cryptosystem cryptosystem, final CommitmentParams compars,
                                                 final Ciphertext[][] vecC, final Ciphertext C, final PublicCommitment[] cA, final BigInteger groupOrder) {
        super("multiExpoBasicArg");
        _cryptosystem = cryptosystem;
        _comParams = compars;
        _vecC = vecC;
        _C = C;
        _cA = cA;
        _m = _cA.length;
        _n = _comParams.getCommitmentLength();
        _groupOrder = groupOrder;
        _RO = new RandomOracleHash(_groupOrder);
    }

    public boolean verify(final MultiExponentiationBasicProofInitialMessage initial,
                          final MultiExponentiationBasicProofAnswer answer) {
        return verify(initial, answer, (test, status, message) -> {
        });
    }


    public boolean verify(final MultiExponentiationBasicProofInitialMessage initial,
                          final MultiExponentiationBasicProofAnswer answer, BGResultNotifier notifier) {

        final PublicCommitment cA0 = initial.getCommitmentPublicA0();
        final PublicCommitment[] cB = initial.getCommitmentPublicB();
        final Ciphertext[] E = initial.getCiphertextsE();
        final Exponent[] a = answer.getExponentsA();
        final Exponent b = answer.getExponentB();
        final Exponent r = answer.getExponentR();
        final Exponent s = answer.getExponentS();
        final Randomness tau = answer.getRandomnessTau();

        if (!(checkGroupElements(cA0, cB, E) && checkExponents(a, b, r, s) && checkTau(tau)
                && isCommitmentTo0(cB[_m], "cB[m]", _groupOrder))) {
            notifier.notify(TestType.MultiExponentiationProof, Status.NOK, "checks failed");
            return false;
        }

        if (!_C.equals(E[_m])) {
            LOGGER.error("ERROR(multiExpoBasicArg): C is not equal to E[_m]");
            notifier.notify(TestType.MultiExponentiationProof, Status.NOK, "ERROR(multiExpoBasicArg): C is not equal to E[_m]");
            return false;
        }

        PublicCommitment comCA = cA0;
        PublicCommitment comCB = cB[0];
        Ciphertext acumE = E[0];
        Ciphertext acumC = _cryptosystem.encryptRaisingToRandom(b, tau);

        Exponent oldaccumulator = new Exponent(1, _groupOrder);

        _RO.addDataToRO(_vecC);
        _RO.addDataToRO(_C);
        _RO.addDataToRO(_cA);
        _RO.addDataToRO(initial);
        final Exponent challengeX = _RO.getHash();

        Exponent accumulator = challengeX;

        for (int i = 1; i <= _m; i++) {
            comCA = comCA.multiply(_cA[i - 1].exponentiate(accumulator));
            comCB = comCB.multiply(cB[i].exponentiate(accumulator));
            acumE = acumE.multiply(E[i].exponentiate(accumulator));
            final Exponent[] aux = ExponentTools.multiplyByScalar(a, oldaccumulator);

            acumC = acumC.multiply(CiphertextTools.compVecCiphVecExp(_vecC[_m - i], aux));

            oldaccumulator = accumulator;
            accumulator = accumulator.multiply(challengeX);
        }

        for (int i = _m + 1; i < 2 * _m; i++) {
            comCB = comCB.multiply(cB[i].exponentiate(accumulator));
            acumE = acumE.multiply(E[i].exponentiate(accumulator));
            accumulator = accumulator.multiply(challengeX);
        }

        if (!checkOpenings(a, b, r, s, comCA, comCB)) {
            notifier.notify(TestType.MultiExponentiationProof, Status.NOK, "checkOpenings failed");
            return false;
        }

        if (!acumE.equals(acumC)) {
            LOGGER.error("ERROR(multiExpoBasicArg): the encryptions don't match");
            notifier.notify(TestType.MultiExponentiationProof, Status.NOK, "ERROR(multiExpoBasicArg): the encryptions don't match");
            return false;
        }

        LOGGER.info("The Basic Multi Expo Argument was verified successfully!");
        return true;
    }

    private boolean checkOpenings(Exponent[] a, Exponent b, Exponent r, Exponent s, PublicCommitment comCA,
                                  PublicCommitment comCB) {
        return isValidOpening(a, r, comCA, "a") && isValidOpening(b, s, comCB, "b");
    }

    private boolean checkTau(final Randomness tau) {
        return isRandomness(tau, "tau") && hasValidOrder(tau.getExponent(), "tau exponent");
    }

    private boolean checkExponents(final Exponent[] a, final Exponent b, final Exponent r, final Exponent s) {
        return isValidExponent(a, _n, "a") && isValidExponent(r, "r")
                && isValidExponent(b, "b") && isValidExponent(s, "s");
    }

    private boolean checkGroupElements(PublicCommitment cA0, PublicCommitment[] cB, Ciphertext[] E) {
        return isGroupElement(cA0, "cA0") && hasValidLength(cB.length, 2 * _m, "cB") && isGroupElement(cB, "cB")
                && hasValidLength(E.length, 2 * _m, "E") && isCiphertext(E, "E");
    }

    private boolean isRandomness(Randomness tau, String tauName) {
        if (!tau.isRandomness()) {
            LOGGER.error("ERROR(multiExpoBasicArg): " + tauName + " is not a valid randomness");
            return false;
        }
        return true;
    }

    public CommitmentParams getParams() {
        return _comParams;
    }

    public BigInteger getOrder() {
        return _groupOrder;
    }

    public Logger getLogger() {
        return LOGGER;
    }
}
