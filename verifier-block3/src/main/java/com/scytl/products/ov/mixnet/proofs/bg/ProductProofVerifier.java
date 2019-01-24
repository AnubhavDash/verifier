/**
 * @author aescala
 * @date 18/09/2013 19:15:45
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.proofs.bg;

import ch.post.it.evoting.verifier.block.block3.BGOfflineVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.BGResultNotifier;
import ch.post.it.evoting.verifier.block.block3.TestType;
import ch.post.it.evoting.verifier.common.Status;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ProductProofMessage;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class ProductProofVerifier {
    private final static Logger LOGGER = Logger.getLogger(ProductProofVerifier.class);

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

    public boolean verify(final ProductProofMessage ans, BGResultNotifier notifier) throws NoSuchAlgorithmException {

        boolean productProof = ans.getCommitmentPublicB().getElement().isGroupElement();
        if (productProof) {
            notifier.notify(TestType.ProductProof, Status.OK, null);
        }
        else {
            LOGGER.error("ERROR(Product Argument): cd is not a group element");
            notifier.notify(TestType.ProductProof, Status.NOK, "ERROR(Product Argument): cd is not a group element");
            return false;
        }
        final HadamardProductProofVerifier verifHA =
                new HadamardProductProofVerifier(_pars, _cA, ans.getCommitmentPublicB(), _groupOrder);
        final boolean answer1 = verifHA.verify(ans.getIniHPA(), ans.getAnsHPA(), notifier);

        final SingleValueProductProofVerifier verifSVA =
                new SingleValueProductProofVerifier(_pars, ans.getCommitmentPublicB(), _b, _groupOrder);
        final boolean answer2 = verifSVA.verify(ans.getIniSVA(), ans.getAnsSVA(), notifier);
        notifier.notify(TestType.SingleValueProductProof, answer1 ? Status.OK : Status.NOK, "SingleValueProductProof failed");

        return answer1 && answer2;
    }

}
