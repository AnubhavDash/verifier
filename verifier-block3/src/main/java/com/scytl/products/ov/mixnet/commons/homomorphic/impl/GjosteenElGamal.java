/**
 * @author aescala
 * @date 04/11/2013 16:08:45
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import com.scytl.products.ov.mixnet.commons.homomorphic.Cryptosystem;
import com.scytl.products.ov.mixnet.commons.homomorphic.Plaintext;
import com.scytl.products.ov.mixnet.commons.homomorphic.Randomness;
import com.scytl.products.ov.mixnet.commons.mathematical.Group;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Gjosteen ElGamal cryptosystem.
 */
public class GjosteenElGamal implements Cryptosystem {

    private final Exponent[] _privateKey;

    private final GroupElement[] _publicKey;

    private final GroupElement _generator;

    private final Group _group;

    /**
     * Create a GjosteenElGamal cryptosystem that will operate over the received mathematical group.
     * <P>
     * This constructor calculates the public key (which can be derived from the private key), and sets both the public
     * key and the private key within the created cryptosystem. Therefore, a cryptosystem that has been created using
     * this constructor is fully initialized and ready to be used for encrypting and decrypting.
     *
     * @param group
     *            the mathematical group over which this cryptosystem operates.
     * @param privKey
     *            the private key.
     */
    public GjosteenElGamal(final Group group, final Exponent[] privKey) {
        _group = group;
        _privateKey = privKey;
        _generator = group.getGenerator();
        int numKeyElements = privKey.length;
        _publicKey = new GroupElement[numKeyElements];
        for (int i = 0; i < numKeyElements; i++) {
            _publicKey[i] = _generator.exponentiate(_privateKey[i]);
        }
    }

    /**
     * Create a GjosteenElGamal cryptosystem that will operate over the received mathematical group.
     * <P>
     * This constructor ONLY sets both the public key within the created cryptosystem, it DOES NOT SET A PRIVATE KEY.
     * Therefore, a cryptosystem that has been created using this constructor can only be used for decrypting if a
     * private key is passed to the decrypt method at the time of performing the decryption.
     *
     * @param group
     *            the mathematical group over which this cryptosystem operates.
     * @param pubKey
     *            the public key.
     */
    public GjosteenElGamal(final Group group, final ElGamalPublicKey pubKey) {
        _group = group;
        _generator = group.getGenerator();
        _privateKey = null;
        List<GroupElement> publicKeyAsListGroupElements = pubKey.getPubKeys();
        _publicKey = publicKeyAsListGroupElements.toArray(new GroupElement[publicKeyAsListGroupElements.size()]);
    }

    /**
     * Create a GjosteenElGamal cryptosystem that will operate over the received mathematical group.
     * <P>
     * This constructor creates a random private key, containing {@code numOpts} elements, it then derives the
     * corresponding public key from the private key. It sets both the public key and the private key within the created
     * cryptosystem. Therefore, a cryptosystem that has been created using this constructor is fully initialized and
     * ready to be used for encrypting and decrypting.
     *
     * @param group
     *            the mathematical group over which this cryptosystem operates.
     * @param numOpts
     *            the number of elements that should exist in the created pair of keys.
     */
    public GjosteenElGamal(final Group group, final int numOpts) {
        this(group, Exponent.getVectorRandomExponent(numOpts, group.getOrder()));
    }

    public Randomness getFreshRandomness() {
        return new GjosteenElGamalRandomness(Exponent.getRandomExponent(_group.getOrder()));
    }

    public Randomness[] getVectorFreshRandomness(final int length) {
        Randomness[] result = new Randomness[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getFreshRandomness();
        }
        return result;
    }

    public GjosteenElGamalCiphertext encrypt(final Plaintext plaintext, final Randomness randomness) {
        final Exponent randomExponent = ((GjosteenElGamalRandomness) randomness).getRandomnessValue();
        final GroupElement gamma = _generator.exponentiate(randomExponent);
        final List<GroupElement> phis = new ArrayList<>();

        for (int i = 0, numPhis = _publicKey.length; i < numPhis; i++) {
            phis.add(_publicKey[i].exponentiate(randomExponent)
                .multiply(((GjosteenElGamalPlaintext) plaintext).getValue(i)));
        }
        return new GjosteenElGamalCiphertext(gamma, phis);
    }

    public GjosteenElGamalCiphertext encrypt(final Plaintext plaintext) {
        return encrypt(plaintext, new GjosteenElGamalRandomness(Exponent.getRandomExponent(_group.getOrder())));
    }

    public GjosteenElGamalCiphertext encrypt(final GroupElement[] plaintext, final Randomness randomness) {
        return encrypt(new GjosteenElGamalPlaintext(plaintext), randomness);
    }

    public GjosteenElGamalCiphertext encrypt(final GroupElement[] plaintext) {
        return encrypt(plaintext, getFreshRandomness());
    }

    /**
     * Decrypts the received ciphertext using the private key that is set within this cryptosystem.
     *
     * @param ciphertext
     *            the ciphertext to be decrypted.
     * @return the decrypted plaintext.
     */
    public GroupElement[] decrypt(final GroupElement[] ciphertext) {
        validateInputToSingleArgDecrypt(ciphertext);
        return decrypt(ciphertext, _privateKey);
    }

    /**
     * Decrypts the received ciphertext using the received private key.
     *
     * @param ciphertext
     *            the ciphertext to be decrypted.
     * @param privateKeyExponents
     *            the private key to be used to decrypt the ciphertext (as an array of Exponents).
     * @return the decrypted plaintext.
     */
    public GroupElement[] decrypt(final GroupElement[] ciphertext, final Exponent[] privateKeyExponents) {
        validateInputToDoubleArgDecrypt(ciphertext, privateKeyExponents);
        GroupElement gamma = ciphertext[0];

        int numPhis = ciphertext.length - 1;
        GroupElement[] phis = new GroupElement[numPhis];
        System.arraycopy(ciphertext, 1, phis, 0, numPhis);

        Exponent negatedExponent;
        GroupElement[] plaintext = new GroupElement[numPhis];

        for (int i = 0; i < numPhis; i++) {
            negatedExponent = privateKeyExponents[i].negate();
            plaintext[i] = gamma.exponentiate(negatedExponent).multiply(phis[i]);
        }
        return plaintext;
    }

    public GjosteenElGamalCiphertext encryptRaisingToRandom(final Exponent b, final Randomness tau) {
        GroupElement[] aux = new GroupElement[_publicKey.length];
        GroupElement aux2 = _group.getGenerator().exponentiate(b);
        for (int i = 0; i < aux.length; i++) {
            aux[i] = aux2;
        }
        Plaintext p = new GjosteenElGamalPlaintext(aux);
        return encrypt(p, tau);
    }

    public GjosteenElGamalCiphertext getEncryptionOf1() {
        GroupElement[] aux = new GroupElement[_publicKey.length];
        for (int i = 0; i < aux.length; i++) {
            aux[i] = _group.getIdentityElement();
        }
        return encrypt(aux);
    }

    public GjosteenElGamalCiphertext getEncryptionOf1(final Randomness r) {
        GroupElement[] aux = new GroupElement[_publicKey.length];
        for (int i = 0; i < aux.length; i++) {
            aux[i] = _group.getIdentityElement();
        }
        return encrypt(aux, r);
    }

    public Randomness get0Randomness() {
        return new GjosteenElGamalRandomness(new Exponent(0, _group.getOrder()));
    }

    public int getNumberOfMessages() {
        return _publicKey.length;
    }

    private void validateInputToSingleArgDecrypt(final GroupElement[] ciphertext) {
        if (ciphertext == null) {
            throw new RuntimeException("The received ciphertext was null");
        } else if (_privateKey == null) {
            throw new RuntimeException(
                "Cannot decrypt using this method because a private key is not set within this cryptosystem");
        } else if (ciphertext.length != _privateKey.length + 1) {
            throw new RuntimeException(
                "The ciphertext must be the same length as the private key that is set within this cryptosystem");
        }
    }

    private void validateInputToDoubleArgDecrypt(final GroupElement[] ciphertext,
            final Exponent[] privateKeyExponents) {
        if (ciphertext == null) {
            throw new RuntimeException("The received ciphertext was null");
        } else if (privateKeyExponents == null) {
            throw new RuntimeException("The received private key was null");
        } else if (ciphertext.length != privateKeyExponents.length + 1) {
            throw new RuntimeException("The ciphertext must be the same length as the private key");
        }
    }
}
