/**
 * @author vmateu  9/12/2016
 * <p>
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.decrypt.beans;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

import java.math.BigInteger;
import java.util.Base64;

public class DecryptionProof {

    private BigInteger _gammaOfCiphertext;

    private Exponent _challenge;

    private Exponent[] _response;

    public DecryptionProof(Exponent challenge, Exponent[] response) {
        this._challenge = challenge;
        this._response = response;
    }

    public DecryptionProof(String proof) {
        BigInteger exponentMod = BigInteger.ZERO;

        proof = proof.substring(12);
        String[] parts = proof.split(",");
        for (String part : parts) {
            String[] subParts = part.split("\"*\"");
            for (int i = 0; i < subParts.length; i++) {
                if (exponentMod.equals(BigInteger.ZERO) && subParts[i].equals("q")) {
                    i = i + 2;
                    exponentMod = new BigInteger(subParts[i]);
                }
                if (subParts[i].equals("hash")) {
                    i = i + 2;
                    _challenge = new Exponent(new BigInteger(Base64.getDecoder().decode(subParts[i])), exponentMod);
                }
                if (subParts[i].equals("values")) {
                    i = i + 2;
                    // [TODO]: Check if the separator is a coma for more than one value
                    String[] valuesResponse = subParts[i].split(",");
                    int numValuesResponse = valuesResponse.length;
                    _response = new Exponent[numValuesResponse];
                    for (int j = 0; j < numValuesResponse; j++) {
                        _response[j] = new Exponent(new BigInteger(valuesResponse[j]), exponentMod);
                    }
                }

            }
        }
    }

    public Exponent getChallenge() {
        return _challenge;
    }

    public Exponent[] getResponse() {
        return _response;
    }

    public BigInteger getGammaOfCiphertexts() {
        return _gammaOfCiphertext;
    }

    public void setGammaOfCiphertext(final BigInteger gamma) {
        this._gammaOfCiphertext = gamma;
    }

    public String toString() {
        String output = "Proof content: \n challenge = " + _challenge.toString() + "\n response = ";
        for (Exponent e : _response) {
            output = output.concat(e.getValue().toString() + ", ");
        }
        return output.substring(0, output.length() - 2);
    }
}
