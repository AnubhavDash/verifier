/**
 * @author aescala
 * @date 18/09/2013 19:15:45
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scytl.products.ov.mixnet.commons.beans.proofs.ProductProofMessage;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

public class ProductProofVerifier {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProductProofVerifier.class);

    private final CommitmentParams _pars;

    private final PublicCommitment[] _cA;

    private final Exponent _b;

    private final BigInteger _groupOrder;

    public ProductProofVerifier(final CommitmentParams pars, final PublicCommitment[] cA, final Exponent b,
            final BigInteger groupOrder) {
        _pars = pars;
        _cA = cA;
        _b = b;
        _groupOrder = groupOrder;

    }

    public boolean verify(final ProductProofMessage ans) throws NoSuchAlgorithmException {

        if (!ans.getCommitmentPublicB().getElement().isGroupElement()) {
            LOGGER.error("ERROR(Product Argument): cd is not a group element");
            return false;
        }
        final HadamardProductProofVerifier verifHA =
            new HadamardProductProofVerifier(_pars, _cA, ans.getCommitmentPublicB(), _groupOrder);
        final boolean answer1 = verifHA.verify(ans.getIniHPA(), ans.getAnsHPA());

        final SingleValueProductProofVerifier verifSVA =
            new SingleValueProductProofVerifier(_pars, ans.getCommitmentPublicB(), _b, _groupOrder);
        final boolean answer2 = verifSVA.verify(ans.getIniSVA(), ans.getAnsSVA());

        return answer1 && answer2;
    }

}
