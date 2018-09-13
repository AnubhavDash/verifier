/**
 * @author aescala
 * @date 20/09/2013 15:48:39
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public interface Cryptosystem {

    Randomness getFreshRandomness();

    Randomness[] getVectorFreshRandomness(int length);

    Ciphertext encrypt(final Plaintext p);

    Ciphertext encrypt(final Plaintext p, final Randomness r);

    Ciphertext encryptRaisingToRandom(Exponent[] b, Randomness tau);

    Ciphertext getEncryptionOf1();

    Ciphertext getEncryptionOf1(Randomness r);

    Randomness get0Randomness();

    int getNumberOfMessages();
}
