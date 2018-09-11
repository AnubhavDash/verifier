/**
 * @author aescala  30/10/2013
 *
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProofSecondAnswer;
import com.scytl.products.ov.mixnet.commons.constants.Constants;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.Cryptosystem;
import com.scytl.products.ov.mixnet.commons.mathematical.Group;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PrivateCommitment;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import com.scytl.products.ov.mixnet.commons.tools.CiphertextTools;
import com.scytl.products.ov.mixnet.commons.tools.RandomOracleHash;

public class ShuffleProofVerifier {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShuffleProofVerifier.class);

    private final Group _group;

    private final Cryptosystem _cryptosystem;

    private final CommitmentParams _compars;

    private final Ciphertext[][] _C;

    private final Ciphertext[][] _Cprime;

    private final int _m;

    private final int _n;

    private final RandomOracleHash _RO;

    private final int _numiterations;

    private final int _mu;

    private ShuffleProofVerifier(final Group group, final Cryptosystem cryptosystem, final CommitmentParams compars,
            final Ciphertext[][] C, final Ciphertext[][] cPrime, final int mu, final int numiterations) {
        _group = group;
        _cryptosystem = cryptosystem;
        _compars = compars;
        _C = C;
        _Cprime = cPrime;
        _m = _Cprime.length;// m;
        _n = _Cprime[0].length;// n;
        _RO = new RandomOracleHash(group.getOrder());
        _mu = mu;
        _numiterations = numiterations;

    }

    public ShuffleProofVerifier(final Cryptosystem cryptosystem, final CommitmentParams compars, final Ciphertext[][] C,
            final Ciphertext[][] cPrime) {
        this(compars.getGroup(), cryptosystem, compars, C, cPrime, 0, 0);
    }

    public boolean verifyProof(final PublicCommitment[] cA, final PublicCommitment[] cB,
            final ShuffleProofSecondAnswer ans) throws NoSuchAlgorithmException {
        LOGGER.debug("\n-------------- Start Verification -------------------");
        long initTime = System.currentTimeMillis();

        if (!(isValidCommitment(cA, "cA") && isValidCommitment(cB, "cB")))
            return false;

        _RO.addDataToRO(_C);
        _RO.addDataToRO(_Cprime);
        _RO.addDataToRO(cA);
        final Exponent challengeX = _RO.getHash();
        _RO.addDataToRO(cB);
        final Exponent challengeY = _RO.getHash();
        _RO.addDataToRO("1");
        final Exponent challengeZ = _RO.getHash();
        _RO.reset();

        LOGGER.debug("Computing first commitments");
        PublicCommitment[] cPA = computeCommitment(cA, cB, challengeY, challengeZ);
        final Exponent[][] vecX = computeVecX(challengeX);
        final Exponent rhsPA = computeRightHandProductArg(challengeY, challengeZ, vecX);
        final Ciphertext lhsME = computeLeftHandMEArg(vecX);

        final ProductProofVerifier verifPA = new ProductProofVerifier(_compars, cPA, rhsPA, _group.getOrder());

        LOGGER.debug("Verifying Produc Argument...");
        if (!verifPA.verify(ans.getMsgPA())) {
            LOGGER.error("ERROR(Shuffle Argument):  Product Argument didn't verify");
            return false;
        }

        // if (_numiterations == 0) {
        LOGGER.debug("Verifying Multi-Exponentiation Argument...");
        final MultiExponentiationBasicProofVerifier verifME =
            new MultiExponentiationBasicProofVerifier(_cryptosystem, _compars, _Cprime, lhsME, cB, _group.getOrder());
        if (!verifME.verify(ans.getIniMEBasic(), ans.getAnsMEBasic())) {
            LOGGER.error("ERROR(Shuffle Argument):  MultiExpo argument didn't verify");
            return false;
        }
        /*
         * } else { final MultiExponentiationReductionProofVerifier verifME = new
         * MultiExponentiationReductionProofVerifier( _cryptosystem, _group, _compars, _Cprime, lhsME, cB, _mu,
         * _numiterations); if (!verifME.verify(ans.getIniMEReduct(), ans.getAnsMEReduct())) {
         * LOGGER.error("ERROR(Shuffle Argument):  MultiExpo argument didn't verify"); return false; } }
         */
        LOGGER.debug(" Verification done in " + (System.currentTimeMillis() - initTime) + "ms");
        return true;
    }

    private Exponent[][] computeVecX(final Exponent challengeX) {
        Exponent accum = challengeX;
        final Exponent[][] result = new Exponent[_m][_n];
        for (int i = 0; i < _m; i++) {
            for (int j = 0; j < _n; j++) {
                result[i][j] = accum;
                accum = accum.multiply(challengeX);
            }
        }
        return result;
    }

    private Exponent computeRightHandProductArg(final Exponent challengeY, final Exponent challengeZ,
            final Exponent[][] vecX) {
        Exponent result = new Exponent(1, _group.getOrder());
        Exponent minusZ = challengeZ.negate();

        for (int i = 0; i < _m * _n; i++) {
            final Exponent factor =
                challengeY.multiply(new Exponent(i + 1, _group.getOrder())).add(vecX[i / _n][i % _n]).add(minusZ);
            result = result.multiply(factor);
        }
        return result;
    }

    private Ciphertext computeLeftHandMEArg(final Exponent[][] vecX) {
        Ciphertext result = null;
        for (int i = 0; i < _m; i++) {
            final Ciphertext aux = CiphertextTools.compVecCiphVecExp(_C[i], vecX[i]);
            result = (result == null ? aux : result.multiply(aux));
        }
        return result;
    }

    private boolean isValidCommitment(PublicCommitment[] commitment, String commitmentName) {
        if (commitment.length != _m) {
            LOGGER.error("ERROR(Shuffle Argument): " + commitmentName + " doesn't have the expected length");
            return false;
        }

        for (int i = 0; i < commitment.length; i++) {
            if (!commitment[i].getElement().isGroupElement()) {
                LOGGER.error("ERROR(Shuffle Argument): " + commitmentName + "[" + i + "] is not a group element");
                return false;
            }
        }
        return true;
    }

    private PublicCommitment[] computeCommitment(PublicCommitment[] cA, PublicCommitment[] cB, Exponent challengeY,
            Exponent challengeZ) {
        final Exponent zNegated = challengeZ.negate();

        final PublicCommitment[] cMinusZ = new PublicCommitment[_m];
        Exponent zero = new Exponent(0, _group.getOrder());
        final PublicCommitment minusZCommitment =
            new PrivateCommitment(zNegated, zero, _compars, _n).makePublicCommitment();
        for (int i = 0; i < _m; i++) {
            cMinusZ[i] = minusZCommitment;
        }

        final PublicCommitment[] cD = new PublicCommitment[_m];
        for (int i = 0; i < cD.length; i++) {
            cD[i] = cA[i].exponentiate(challengeY).multiply(cB[i]);
        }

        PublicCommitment[] cPA;
        if (_m == 1) {
            cPA = new PublicCommitment[_m + 1];
        } else {
            cPA = new PublicCommitment[_m];
        }
        for (int i = 0; i < _m; i++) {
            cPA[i] = cD[i].multiply(cMinusZ[i]);
        }

        if (_m == 1) {
            Exponent identity = new Exponent(Constants.MULTIPLICATIVE_IDENTITY, _compars.getGroup().getOrder());
            cPA[_m] = new PrivateCommitment(identity, zero, _compars, _n).makePublicCommitment();
        }
        return cPA;
    }
}
