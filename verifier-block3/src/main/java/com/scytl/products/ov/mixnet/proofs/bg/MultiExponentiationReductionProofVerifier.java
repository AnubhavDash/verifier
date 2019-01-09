/**
 * @author aescala
 * @date 28/10/2013 11:49:22
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationReductionAnswer;
import com.scytl.products.ov.mixnet.commons.beans.proofs.MultiExponentiationReductionInitialMessage;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.Cryptosystem;
import com.scytl.products.ov.mixnet.commons.mathematical.Group;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import com.scytl.products.ov.mixnet.commons.tools.CiphertextTools;
import com.scytl.products.ov.mixnet.commons.tools.RandomOracleHash;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class MultiExponentiationReductionProofVerifier extends Verifier {
    private final static Logger LOGGER = Logger.getLogger(MultiExponentiationReductionProofVerifier.class);

    private final Cryptosystem _cryptosystem;

    private final Group _group;

    private final CommitmentParams _compars;

    private final Ciphertext[][] _vecC;

    private final Ciphertext _C;

    private final PublicCommitment[] _cA;

    private final int _mu;

    private final int _n;

    private final int _mprime;

    private final int _numiterationsleft;

    private final int _lengthB;

    private final RandomOracleHash _RO;

    public MultiExponentiationReductionProofVerifier(final Cryptosystem cryptosystem, final Group group,
            final CommitmentParams compars, final Ciphertext[][] vecC, final Ciphertext C, final PublicCommitment[] cA,
            final int mu, final int numiterations) {
        super("multiExpoReductionArg");
        _cryptosystem = cryptosystem;
        _lengthB = _cryptosystem.getNumberOfMessages();
        _group = group;
        _compars = compars;
        _vecC = vecC;
        _C = C;
        _cA = cA;
        _n = compars.getCommitmentLength();
        _mu = mu;
        _numiterationsleft = numiterations - 1;
        _mprime = _cA.length / _mu;
        _RO = new RandomOracleHash(_group.getOrder());
    }

    public boolean verify(final MultiExponentiationReductionInitialMessage initial,
            final MultiExponentiationReductionAnswer answer) throws NoSuchAlgorithmException {

        _RO.addDataToRO(initial);
        final Exponent challengeX = _RO.getHash();

        final PublicCommitment[] cb = initial.getCommitmentPublicB();
        final Ciphertext[] E = initial.getCiphertextsE();
        final Exponent[] b = answer.getExponentsB();
        final Exponent s = answer.getExponentS();

        if (!validate(cb, E, b, s))
            return false;

        if (!_C.equals(E[_mu - 1])) {
            LOGGER.error("ERROR(multiExpoReductionArg): C is not equal to E[_mu-1]");
            return false;
        }

        PublicCommitment comCb = cb[0];
        Exponent accumulator = challengeX;
        for (int i = 1; i < cb.length; i++) {
            comCb = comCb.multiply(cb[i].exponentiate(accumulator));
            accumulator = accumulator.multiply(challengeX);
        }
        if (!isValidOpening(b, s, comCb, "B"))
            return false;

        final Ciphertext[][] vecCprime = new Ciphertext[_mprime][_n];
        final PublicCommitment[] cAprime = new PublicCommitment[_mprime];
        Ciphertext Cprime = computePrime(challengeX, E, b, vecCprime, cAprime);

        if (_numiterationsleft == 0) {
            final MultiExponentiationBasicProofVerifier verifier = new MultiExponentiationBasicProofVerifier(
                _cryptosystem, _compars, vecCprime, Cprime, cAprime, _group.getOrder());
            if (!verifier.verify(answer.getIniBasic(), answer.getAnsBasic())) {
                LOGGER.error("ERROR(multiExpoReductionArg):  MultiExpoBasic argument didn't verify");
                return false;
            }
        } else {
            final MultiExponentiationReductionProofVerifier verifier = new MultiExponentiationReductionProofVerifier(
                _cryptosystem, _group, _compars, vecCprime, Cprime, cAprime, _mu, _numiterationsleft);
            if (!verifier.verify(answer.getIniReduct(), answer.getAnsReduct())) {
                LOGGER.error("ERROR(multiExpoReductionArg):  MultiExpoBasic argument didn't verify");
                return false;
            }
        }

        LOGGER.info("INFO: The Multi Expo Reduction Argument was verified successfully!");
        return true;
    }

    private Ciphertext computePrime(final Exponent challengeX, final Ciphertext[] E, final Exponent[] b,
            final Ciphertext[][] vecCprime, final PublicCommitment[] cAprime) {
        Exponent one = new Exponent(1, _group.getOrder());
        for (int l = 0; l < _mprime; l++) {
            Exponent xacum = one;
            for (int i = 0; i < _mu; i++) {
                for (int k = 0; k < _n; k++) {
                    final Ciphertext aux = _vecC[_mu * l + _mu - 1 - i][k].exponentiate(xacum);
                    vecCprime[l][k] = (i == 0 ? aux : vecCprime[l][k].multiply(aux));
                }
                final PublicCommitment aux = _cA[_mu * l + i].exponentiate(xacum);
                cAprime[l] = (i == 0 ? aux : cAprime[l].multiply(aux));
                xacum = xacum.multiply(challengeX);
            }
        }
        Exponent xacum = one;
        final Exponent[] vecX = new Exponent[2 * _mu - 1];
        for (int i = 0; i < vecX.length; i++) {
            vecX[i] = xacum;
            xacum = xacum.multiply(challengeX);
        }

        Ciphertext Cprime = CiphertextTools.compVecCiphVecExp(E, vecX);
        //final Exponent[] auxb = ExponentTools.negate(b);
        _cryptosystem.getEncryptionOf1();
        //final Ciphertext aux = _cryptosystem.encryptRaisingToRandom(auxb, _cryptosystem.get0Randomness());
        //Cprime = Cprime.multiply(aux);
        return Cprime;
    }

    private boolean validate(PublicCommitment[] cb, Ciphertext[] E, Exponent[] b, Exponent s) {
        return hasValidLength(cb.length, 2 * _mu - 1, "cb") && isGroupElement(cb, "cB")
            && hasValidLength(E.length, 2 * _mu - 1, "E") && isCiphertext(E, "E") && isValidExponent(b, _lengthB, "b")
            && isValidExponent(s, "s") && isCommitmentTo0(cb[_mu - 1], "cb[mu-1]", _group.getOrder());
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
