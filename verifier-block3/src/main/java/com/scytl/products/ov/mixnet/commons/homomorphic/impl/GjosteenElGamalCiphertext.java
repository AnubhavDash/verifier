/**
 * @author aescala
 * @date 04/11/2013 16:08:58
 * Copyright (C) 2013 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.homomorphic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;

public class GjosteenElGamalCiphertext implements Ciphertext {

    private final GroupElement _gamma;

    private final List<GroupElement> _phis;

    public GjosteenElGamalCiphertext(final GroupElement gamma,
                                     final List<GroupElement> phis) {

        validateInputs(gamma, phis);

        _gamma = gamma;
        _phis = phis;
    }

    @JsonCreator
    public GjosteenElGamalCiphertext(@JsonProperty("gamma") final GroupElement gamma,
            @JsonProperty("phis") final GroupElement phis) {

        validateInputs(gamma, Arrays.asList(phis));

        _gamma = gamma;
        _phis = Arrays.asList(phis);
    }

    public GjosteenElGamalCiphertext(final List<GroupElement> elements) {

        validateInputs(elements);

        _gamma = elements.get(0);
        _phis = elements.subList(1, elements.size());
    }

    public GjosteenElGamalCiphertext(final GroupElement[] elements) {

        validateInputs(elements);

        _gamma = elements[0];

        _phis = new ArrayList<>();
        for (int i = 1, numElements = elements.length; i < numElements; i++) {
            _phis.add(elements[i]);
        }
    }

    @JsonIgnore
    @Override
    public GroupElement[] getParts() {

        GroupElement[] gammaAndPhis = new GroupElement[_phis.size() + 1];

        gammaAndPhis[0] = _gamma;
        for (int i = 0, numPhis = _phis.size(); i < numPhis; i++) {
            gammaAndPhis[i + 1] = _phis.get(i);
        }

        return gammaAndPhis;
    }

    public GroupElement getGamma() {
        return _gamma;
    }

    public List<GroupElement> getPhis() {
        return _phis;
    }

    @Override
    public GjosteenElGamalCiphertext multiply(final Ciphertext otherCiphertext) {

        final GjosteenElGamalCiphertext otherGjosteenElGamalCiphertext = validateInputAndCastSafely(otherCiphertext);

        final GroupElement otherGamma = otherGjosteenElGamalCiphertext.getGamma();
        final GroupElement resultGamma = _gamma.multiply(otherGamma);

        final List<GroupElement> otherPhis = otherGjosteenElGamalCiphertext.getPhis();
        final List<GroupElement> resultPhis = new ArrayList<>();
        for (int i = 0, numPhis = _phis.size(); i < numPhis; i++) {
            resultPhis.add(_phis.get(i).multiply(otherPhis.get(i)));
        }

        return new GjosteenElGamalCiphertext(resultGamma, resultPhis);
    }

    @Override
    public GjosteenElGamalCiphertext exponentiate(final Exponent exponent) {

        final GroupElement gammaExponentiated = _gamma.exponentiate(exponent);

        final List<GroupElement> phisExponentiated =
            _phis.stream().map(phi -> phi.exponentiate(exponent)).collect(Collectors.toList());

        return new GjosteenElGamalCiphertext(gammaExponentiated, phisExponentiated);
    }

    @Override
    public int hashCode() {

        int result = 9;
        int aux = _gamma.hashCode();
        result = 13 * result + aux;
        for (GroupElement i : _phis) {
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
        for (GroupElement phi : _phis) {
            if (!phi.isGroupElement()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
    	//TODO please, modify this method once the toString() method in the CiphertextImpl class of crypotlib is changed.
        StringBuilder strbrd = new StringBuilder();
        strbrd.append("CiphertextImpl [gamma=");
        strbrd.append(_gamma.toString());
        strbrd.append(", phis=");
        GroupElement[] phisArray = new GroupElement[_phis.size()];
        strbrd.append(Arrays.toString(_phis.toArray(phisArray)));
        strbrd.append("]");
        return strbrd.toString();
    }

    private void validateInputs(final GroupElement gamma, final List<GroupElement> phis) {

        if (gamma == null) {
            throw new RuntimeException("The gamma parameter was null");
        }

        if ((phis == null) || (phis.isEmpty())) {
            throw new RuntimeException("The list of phis was null or empty");
        }
    }

    private void validateInputs(final List<GroupElement> elements) {

        if (elements == null) {
            throw new RuntimeException("The list of elements was null");
        }

        if (elements.size() < 2) {
            throw new RuntimeException("The list of elements was " + elements.size()
                + ", which is less than the minumum size which is two (gamma and one phi).");
        }
    }

    private void validateInputs(final GroupElement[] elements) {

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
