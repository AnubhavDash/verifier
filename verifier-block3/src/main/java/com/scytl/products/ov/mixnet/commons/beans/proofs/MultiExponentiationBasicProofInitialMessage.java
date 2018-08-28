/**
 * @author aescala
 * @date 10/10/2013 16:54:44
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.beans.proofs;

import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalCiphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.InternalCipherText;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MultiExponentiationBasicProofInitialMessage {

    private final PublicCommitment _cA0;

    private final PublicCommitment[] _cB;

    private final Ciphertext[] _E;

    @JsonCreator
    public MultiExponentiationBasicProofInitialMessage(@JsonProperty("commitmentPublicA0") final PublicCommitment cA0,
            @JsonProperty("commitmentPublicB") final PublicCommitment[] cB,
            @JsonProperty("ciphertextsE") final InternalCipherText[] e) {
        _cA0 = cA0;
        _cB = cB;

        _E = Stream.of(e).map(cipher -> {
            String[] gamma = cipher.getGamma().split(";");
            String[] phis = cipher.getPhis().split(";");

            return new GjosteenElGamalCiphertext(
                new ZpElement(new BigInteger(gamma[0]), new BigInteger(gamma[1]), new BigInteger(gamma[2])),
                Arrays.asList(new ZpElement(new BigInteger(phis[0]), new BigInteger(phis[1]), new BigInteger(phis[2]))));})
                .collect(Collectors.toList()).toArray(new GjosteenElGamalCiphertext[]{});
    }

    /**
     * @return Returns the cA0.
     */
    public PublicCommitment getCommitmentPublicA0() {
        return _cA0;
    }

    /**
     * @return Returns the cB.
     */
    public PublicCommitment[] getCommitmentPublicB() {
        return _cB;
    }

    /**
     * @return Returns the e.
     */
    public Ciphertext[] getCiphertextsE() {
        return _E;
    }

    @Override
    public String toString() {
        final StringBuilder strbldr = new StringBuilder();
        strbldr.append(_cA0.toString());
        for (final PublicCommitment a_cB : _cB) {
            strbldr.append(a_cB.toString());
        }
        for (final Ciphertext a_E : _E) {
            strbldr.append(a_E.toString());
        }
        return strbldr.toString();
    }
}
