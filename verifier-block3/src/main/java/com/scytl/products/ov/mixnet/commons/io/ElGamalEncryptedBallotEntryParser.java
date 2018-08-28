/**
 * @author afries
 * @date   Mar 6, 2015 11:20:06 AM
 *
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 *
 * All rights reserved.
 *
 */
package com.scytl.products.ov.mixnet.commons.io;

import com.googlecode.jcsv.reader.CSVEntryParser;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.exceptions.InvalidInputException;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

final class ElGamalEncryptedBallotEntryParser implements CSVEntryParser<ElGamalEncryptedBallot> {

    private final BigInteger _p;
    private final BigInteger _q;

    public ElGamalEncryptedBallotEntryParser(final BigInteger p, final BigInteger q) {
        _p = p;
        _q = q;
    }

    /**
     * @see com.googlecode.jcsv.reader.CSVEntryParser#parseEntry(String[])
     */
    @Override
    public ElGamalEncryptedBallot parseEntry(final String... args) {

        validateInputs(args);

        List<ZpElement> elements = new ArrayList<>();
        for (String s : args) {
            elements.add(new ZpElement(s, _p, _q));
        }

        return new ElGamalEncryptedBallot(elements);
    }

    private void validateInputs(final String... args) {

        String PATTERN = "^[0-9]*$";

        if ((args == null) || (args.length == 0)) {
            throw new InvalidInputException("The input must be an initialized and non-empty");
        }

        for (String num : args) {
            if (!num.matches(PATTERN)) {
                throw new InvalidInputException("The input contains non-numeric characters");
            }
        }
    }
}
