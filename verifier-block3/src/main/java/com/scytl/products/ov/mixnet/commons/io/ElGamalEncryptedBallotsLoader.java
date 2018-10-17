/**
 * @author mmanzano
 * @date Mar 5, 2015 2:31:13 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

/**
 * Loads an {@code ElGamalEncryptedBallots} from a source.
 */
public final class ElGamalEncryptedBallotsLoader {
    private final static Logger LOGGER = Logger.getLogger(ElGamalEncryptedBallotsLoader.class);

    public static ElGamalEncryptedBallots loadCSV(final ZpGroupParams groupParams, Path outputParentPath,
            String batchName, String fileName) throws IOException {

        ElGamalEncryptedBallotEntryParser entryParser = new ElGamalEncryptedBallotEntryParser(groupParams);

        final ElGamalEncryptedBallots elGamalEncryptedBallots;

        Path fullPath = Paths.get(outputParentPath.toString(), batchName, fileName);
        LOGGER.debug("Output path = " + fullPath.toString());

        try (Reader reader = new FileReader(fullPath.toString());
                CSVReader<ElGamalEncryptedBallot> elGamalEncryptedBallotReader =
                    new CSVReaderBuilder<ElGamalEncryptedBallot>(reader).entryParser(entryParser).build()) {

            // TODO[Javi] check csv size limit to load on memory here
            elGamalEncryptedBallots = new ElGamalEncryptedBallots(elGamalEncryptedBallotReader.readAll());
        }

        return elGamalEncryptedBallots;
    }
}
