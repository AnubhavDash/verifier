/**
 * @author aescala
 * @date 15/10/2013 15:46:33
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.tools;

import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalCiphertext;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;

public class CiphertextTools {

    public static Ciphertext compVecCiphVecExp(final Ciphertext[] C, final Exponent[] e) {

        ZpElement[][] bases = new ZpElement[C[0].getParts().length][C.length];

        for (int i = 0, length = C.length; i < length; i++) {
            ZpElement[] a = C[i].getParts();
            for (int j = 0; j < a.length; j++) {
                bases[j][i] = a[j];
            }

        }

        ZpElement[] b = new ZpElement[C[0].getParts().length];
        for (int j = 0; j < b.length; j++) {
            b[j] = MultiExponentiationImpl.computeMultiExpo(bases[j], e);
        }
        return new GjosteenElGamalCiphertext(b);
    }
}
