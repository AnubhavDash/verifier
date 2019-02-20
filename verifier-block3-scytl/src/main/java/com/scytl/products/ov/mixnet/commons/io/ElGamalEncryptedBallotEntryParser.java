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

import java.util.ArrayList;
import java.util.List;

import com.googlecode.jcsv.reader.CSVEntryParser;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.exceptions.InvalidInputException;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

public final class ElGamalEncryptedBallotEntryParser implements CSVEntryParser<ElGamalEncryptedBallot> {

    private final ZpGroupParams _zpGroupParams;

    public ElGamalEncryptedBallotEntryParser(final ZpGroupParams zpGroupParams) {
        _zpGroupParams = zpGroupParams;
    }

    /**
     * @see CSVEntryParser#parseEntry(String[])
     */
    @Override
    public ElGamalEncryptedBallot parseEntry(final String... args) {

        validateInputs(args);

        List<GroupElement> elements = new ArrayList<>();
        for (String s : args) {
            elements.add(new ZpElement(s, _zpGroupParams));
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
