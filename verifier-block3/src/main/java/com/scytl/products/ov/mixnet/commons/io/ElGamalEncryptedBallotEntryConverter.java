/**
 * @author mmanzano
 * @date   Mar 6, 2015 3:14:47 PM
 *
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 *
 * All rights reserved.
 *
 */
package com.scytl.products.ov.mixnet.commons.io;

import java.util.List;

import com.googlecode.jcsv.writer.CSVEntryConverter;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.exceptions.InvalidInputException;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;

final class ElGamalEncryptedBallotEntryConverter implements CSVEntryConverter<ElGamalEncryptedBallot> {

    /**
     * @see CSVEntryConverter#convertEntry(Object)
     */
    @Override
    public String[] convertEntry(final ElGamalEncryptedBallot encryptedBallot) {

        validateInputs(encryptedBallot);

        final List<GroupElement> phis = encryptedBallot.getPhis();
        final int columnsSize = encryptedBallot.getPhis().size() + 1;

        String[] columns = new String[columnsSize];

        columns[0] = encryptedBallot.getGamma().toString();

        for (int i = 1; i < columnsSize; i++) {
            columns[i] = phis.get(i - 1).toString();
        }

        return columns;
    }

    private void validateInputs(final ElGamalEncryptedBallot encryptedBallot) {

        if (encryptedBallot == null) {
            throw new InvalidInputException("The ballot must be initialized");
        }
    }
}
