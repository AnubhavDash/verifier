/**
 * @author aescala
 * @date 28/10/2013 16:25:46
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.proofs.bg.commitments;

import com.scytl.products.ov.mixnet.commons.mathematical.Group;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.tools.MultiExponentiationImpl;

import java.math.BigInteger;

public class PrivateCommitment {

    private final Exponent _r;

    private final Exponent[] _exponents;

    private final CommitmentParams _params;

    private ZpElement _commitment;

    private boolean _repeatedExponents;

    public PrivateCommitment(final Exponent[] m, final Exponent r, final CommitmentParams params) {

        _params = params;
        _exponents = new Exponent[m.length];
        System.arraycopy(m, 0, _exponents, 0, m.length);
        _r = r;
        _repeatedExponents = false;
    }

    public PrivateCommitment(final Exponent m, final Exponent r, final CommitmentParams params, int timesRepeated) {

        _params = params;
        _exponents = new Exponent[timesRepeated];
        for (int i = 0; i < timesRepeated; i++) {
            _exponents[i] = m;
        }
        _r = r;
        _repeatedExponents = true;
    }

    public PrivateCommitment(final Exponent[] exponents, final BigInteger order, final CommitmentParams params) {
        this(exponents, Exponent.getRandomExponent(order), params);
    }

    public PrivateCommitment(final Exponent exponent, final Exponent exponentR, final CommitmentParams params) {
        this(new Exponent[] {exponent }, exponentR, params);
    }

    public PrivateCommitment(final Exponent exponent, final CommitmentParams params) {
        this(new Exponent[] {exponent }, exponent.getOrder(), params);
    }

    private void commit() {
        final ZpElement aux = _params.getH().exponentiate(_r);
        ZpElement element;
        if (_repeatedExponents) {
            element = _params.getG()[0];
            for (int i = 1; i < _exponents.length; i++) {
                element = element.multiply(_params.getG()[i]);
            }
            element = element.exponentiate(_exponents[0]);
        } else {
            ZpElement[] bases = new ZpElement[_exponents.length];
            if (_exponents.length != _params.getG().length) {
                System.arraycopy(_params.getG(), 0, bases, 0, _exponents.length);
                element = MultiExponentiationImpl.computeMultiExpo(bases, _exponents);
            } else {
                element = MultiExponentiationImpl.computeMultiExpo(_params.getG(), _exponents);
            }
        }
        _commitment = aux.multiply(element);
    }

    private ZpGroup getZpGroup(final Group group) {

        if (group instanceof ZpGroup) {
            return (ZpGroup) group;
        } else {
            throw new RuntimeException("Currently, LimMultiExpo only supports Zp mathematical groups");
        }
    }

    public Exponent[] getM() {
        return _exponents;
    }

    public Exponent getR() {
        return _r;
    }

    public PrivateCommitment multiply(final PrivateCommitment com) {

        final Exponent[] mresult = new Exponent[_params.getG().length];
        for (int i = 0, length = mresult.length; i < length; i++) {
            mresult[i] = _exponents[i].add(com.getM()[i]);
        }
        final Exponent rresult = _r.add(com.getR());
        return new PrivateCommitment(mresult, rresult, _params);
    }

    public PrivateCommitment exponentiate(final Exponent e) {
        final Exponent[] mresult = new Exponent[_params.getG().length];

        for (int i = 0, length = mresult.length; i < length; i++) {
            mresult[i] = _exponents[i].multiply(e);
        }
        final Exponent rresult = _r.multiply(e);
        return new PrivateCommitment(mresult, rresult, _params);
    }

    public PublicCommitment makePublicCommitment() {
        if (_commitment == null) {
            commit();
        }
        return new PublicCommitment(_commitment);
    }
}
