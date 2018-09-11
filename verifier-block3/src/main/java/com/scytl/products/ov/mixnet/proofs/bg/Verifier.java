/**
 * @author aescala
 * @date 18/09/2013 18:05:38
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import java.math.BigInteger;

import org.slf4j.Logger;

import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

abstract class Verifier {

    private final String errorType;

    Verifier(String errorType) {
        this.errorType = errorType;
    }

    protected abstract Logger getLogger();

    protected abstract CommitmentParams getParams();

    protected abstract BigInteger getOrder();

    boolean isGroupElement(PublicCommitment commitment, String commitmentName) {
        if (!commitment.getElement().isGroupElement()) {
            getLogger().error("ERROR(" + errorType + "): " + commitmentName + " is not a group element");
            return false;
        }
        return true;
    }

    boolean isGroupElement(PublicCommitment[] commitment, String commitmentName) {
        for (int k = 0; k < commitment.length; k++) {
            if (!isGroupElement(commitment[k], commitmentName + "[" + k + "]"))
                return false;
        }
        return true;
    }

    boolean hasValidLength(int length, int validLength, String name) {
        if (length != validLength) {
            getLogger().error("ERROR(" + errorType + "): " + name + " does not have the expected length");
            return false;
        }
        return true;
    }

    private boolean isExponent(Exponent exponent, String exponentName) {
        if (!exponent.isExponent()) {
            getLogger().error("ERROR(" + errorType + "): " + exponentName + " is not a valid exponent");
            return false;
        }
        return true;
    }

    boolean isCiphertext(Ciphertext[] ciphertext, String ciphertextName) {
        for (int k = 0; k < ciphertext.length; k++) {
            if (!ciphertext[k].isCiphertext()) {
                getLogger()
                    .error("ERROR(" + errorType + "): " + ciphertextName + "[" + k + "] is not a valid ciphertext");
                return false;
            }
        }
        return true;
    }

    boolean hasValidOrder(Exponent exponent, String exponentName) {
        if (!hasSameOrder(exponent)) {
            getLogger().error("ERROR(" + errorType + "): " + exponentName + " is not a group element");
            return false;
        }
        return true;
    }

    boolean isValidOpening(Exponent[] e1, Exponent e2, PublicCommitment commitment, String name) {
        if (!commitment.verifyOpening(e1, e2, getParams())) {
            getLogger().error("ERROR(" + errorType + "): the commitment to " + name + " is incorrect");
            return false;
        }
        return true;
    }

    boolean isCommitmentTo0(PublicCommitment commitment, String commitmentName, BigInteger order) {
        if (!commitment.verifyOpening(new Exponent[] {new Exponent(0, order) }, new Exponent(0, order), getParams())) {
            getLogger()
                .error("ERROR(" + errorType + "): " + commitmentName + " is not a commitment to 0 with randomness 0");
            return false;
        }
        return true;
    }

    boolean areEquals(Exponent e1, Exponent e2, String name) {
        if (!e1.equals(e2)) {
            getLogger().error("ERROR(" + errorType + "): " + name + " are not the same");
            return false;
        }
        return true;
    }

    /**
     * Validates Exponent if it's exponent, if it has valid order, if it has valid length
     * 
     * @param exponent
     *            Exponent[]
     * @param validLength
     *            valid length
     * @param exponentName
     *            the name of the exponent for log message
     * @return true if all the checkings are correct and false otherwise
     */
    boolean isValidExponent(Exponent[] exponent, int validLength, String exponentName) {
        return hasValidLength(exponent.length, validLength, exponentName) && isValidExponent(exponent, exponentName);
    }

    /**
     * Validates Exponent if it's exponent, if it has valid order
     * 
     * @param exponent
     *            Exponent
     * @param exponentName
     *            the name of the exponent for log message
     * @return true if all the checkings are correct and false otherwise
     */
    boolean isValidExponent(Exponent exponent, String exponentName) {
        return isExponent(exponent, exponentName) && hasValidOrder(exponent, exponentName);
    }

    /**
     * Validates Exponent if it's exponent, if it has valid order
     * 
     * @param exponent
     *            Exponent[]
     * @param exponentName
     *            the name of the exponent for log message
     * @return true if all the checkings are correct and false otherwise
     */
    private boolean isValidExponent(Exponent[] exponent, String exponentName) {
        for (int i = 0; (i < exponent.length); i++) {
            if (!isExponent(exponent[i], exponentName + "[" + i + "]"))
                return false;
            if (!hasValidOrder(exponent[i], exponentName + "[" + i + "]"))
                return false;
        }
        return true;
    }

    private boolean hasSameOrder(Exponent exponent) {
        return getOrder().equals(exponent.getOrder());
    }
}
