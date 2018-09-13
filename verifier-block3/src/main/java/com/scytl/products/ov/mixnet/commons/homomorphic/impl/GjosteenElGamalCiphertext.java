/**
 * @author aescala
 * @date 04/11/2013 16:08:58
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GjosteenElGamalCiphertext implements Ciphertext {

    private final ZpElement _gamma;

    private final List<ZpElement> _phis;

    @JsonCreator
    public GjosteenElGamalCiphertext(@JsonProperty("gamma") final ZpElement gamma,
            @JsonProperty("phis") final List<ZpElement> phis) {

        validateInputs(gamma, phis);

        _gamma = gamma;
        _phis = phis;
    }

    public GjosteenElGamalCiphertext(final List<ZpElement> elements) {

        validateInputs(elements);

        _gamma = elements.get(0);
        _phis = elements.subList(1, elements.size());
    }

    public GjosteenElGamalCiphertext(final ZpElement[] elements) {

        validateInputs(elements);

        _gamma = elements[0];

        _phis = new ArrayList<>();
        for (int i = 1, numElements = elements.length; i < numElements; i++) {
            _phis.add(elements[i]);
        }
    }

    @JsonIgnore
    @Override
    public ZpElement[] getParts() {

        ZpElement[] gammaAndPhis = new ZpElement[_phis.size() + 1];

        gammaAndPhis[0] = _gamma;
        for (int i = 0, numPhis = _phis.size(); i < numPhis; i++) {
            gammaAndPhis[i + 1] = _phis.get(i);
        }

        return gammaAndPhis;
    }

    public ZpElement getGamma() {
        return _gamma;
    }

    public List<ZpElement> getPhis() {
        return _phis;
    }

    @Override
    public GjosteenElGamalCiphertext multiply(final Ciphertext otherCiphertext) {

        final GjosteenElGamalCiphertext otherGjosteenElGamalCiphertext = validateInputAndCastSafely(otherCiphertext);

        final ZpElement otherGamma = otherGjosteenElGamalCiphertext.getGamma();
        final ZpElement resultGamma = _gamma.multiply(otherGamma);

        final List<ZpElement> otherPhis = otherGjosteenElGamalCiphertext.getPhis();
        final List<ZpElement> resultPhis = new ArrayList<>();
        for (int i = 0, numPhis = _phis.size(); i < numPhis; i++) {
            resultPhis.add(_phis.get(i).multiply(otherPhis.get(i)));
        }

        return new GjosteenElGamalCiphertext(resultGamma, resultPhis);
    }

    @Override
    public GjosteenElGamalCiphertext exponentiate(final Exponent exponent) {

        final ZpElement gammaExponentiated = _gamma.exponentiate(exponent);

        final List<ZpElement> phisExponentiated =
            _phis.stream().map(phi -> phi.exponentiate(exponent)).collect(Collectors.toList());

        return new GjosteenElGamalCiphertext(gammaExponentiated, phisExponentiated);
    }

    @Override
    public int hashCode() {

        int result = 9;
        int aux = _gamma.hashCode();
        result = 13 * result + aux;
        for (ZpElement i : _phis) {
            aux = i.hashCode();
            result = 11 * result + aux;
        }
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof GjosteenElGamalCiphertext) {
            final GjosteenElGamalCiphertext other = (GjosteenElGamalCiphertext) obj;

            if (_gamma.equals(other.getGamma()) && _phis.equals(other.getPhis())) {
                return true;
            }
        }
        return false;
    }

    @Override
    @JsonIgnore
    public boolean isCiphertext() {

        if (!_gamma.isGroupElement()) {
            return false;
        }
        for (ZpElement phi : _phis) {
            if (!phi.isGroupElement()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder strbrd = new StringBuilder();
        strbrd.append(_gamma.toString());
        for (ZpElement phi : _phis) {
            strbrd.append(" ");
            strbrd.append(phi.toString());
        }

        return strbrd.toString();
    }

    private void validateInputs(final ZpElement gamma, final List<ZpElement> phis) {

        if (gamma == null) {
            throw new RuntimeException("The gamma parameter was null");
        }

        if ((phis == null) || (phis.isEmpty())) {
            throw new RuntimeException("The list of phis was null or empty");
        }
    }

    private void validateInputs(final List<ZpElement> elements) {

        if (elements == null) {
            throw new RuntimeException("The list of elements was null");
        }

        if (elements.size() < 2) {
            throw new RuntimeException("The list of elements was " + elements.size()
                + ", which is less than the minumum size which is two (gamma and one phi).");
        }
    }

    private void validateInputs(final ZpElement[] elements) {

        if (elements == null) {
            throw new RuntimeException("The array of elements was null");
        }

        if (elements.length < 2) {
            throw new RuntimeException("The list of elements was " + elements.length
                + ", which is less than the minumum size which is two (gamma and one phi).");
        }
    }

    private GjosteenElGamalCiphertext validateInputAndCastSafely(final Ciphertext otherCiphertext) {

        if (otherCiphertext == null) {
            throw new RuntimeException("The received ciphertext was null");
        }

        if (otherCiphertext instanceof GjosteenElGamalCiphertext) {
            return (GjosteenElGamalCiphertext) otherCiphertext;
        } else {
            throw new RuntimeException("The received ciphertext was not a GjosteenElGamalCiphertext");
        }
    }
}
