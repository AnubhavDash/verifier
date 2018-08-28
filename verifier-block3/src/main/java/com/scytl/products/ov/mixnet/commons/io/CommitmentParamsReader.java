/**
 * @author jruiz
 * @date 23/06/15 18:11
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;

class CommitmentParamsReader extends SerializedDataReader {
    private final static Logger LOGGER = Logger.getLogger(CommitmentParamsReader.class);

    /**
     * Reads a set of commitment parameters from a file. Note: a set of
     * commitment parameters contains a "h" value and an array of "g" values.
     * Additionally, a set of encryption parameters are stored with the
     * commitment parameters. Note: this methods assumes that the file will
     * contain the following lines:
     * <ul>
     * <li>P parameter</li>
     * <li>Q parameter</li>
     * <li>G parameter</li>
     * <li>h</li>
     * <li>g[0]</li>
     * <li>g[1]</li>
     * <li>g[2]</li>
     * <li>...</li>
     * <li>g[N]</li>
     * </ul>
     * For example, if the commitment parameters contains a "g" array with 64
     * elements, then the total number of lines in the file should be 68 (3
     * "encryption parameters" lines + 1 "h" line + 64 "g" lines).
     *
     * @param commitmentFile the path of the file containing a set of commitment
     *                       parameters.
     * @return the reconstructed commitment parameters.
     * @throws IOException If the date can not be read for some reason
     */
    public static CommitmentParams readCommitmentParamsFromFile(final ZpGroup zpGroup, final Path commitmentFile,
                                                                final int n) throws IOException {

        final File fileContainingCommitmentParams = Paths.get(commitmentFile.toString()).toFile();

        validateFileIsAccessible(fileContainingCommitmentParams);

        final ZpElement[] g = new ZpElement[n];
        final ZpElement h;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(fileContainingCommitmentParams))) {

            // read the group information
            final String pAsString = readLineAndConfirmNotNull(fileReader, commitmentFile.toString());
            final String qAsString = readLineAndConfirmNotNull(fileReader, commitmentFile.toString());
            final String gAsString = readLineAndConfirmNotNull(fileReader, commitmentFile.toString());

            final ZpGroup reconstructedZpGroup = ZpGroupReader
                    .createZpGroupFromParameterStrings(pAsString, qAsString, gAsString);

            // read the commitment values
            final String hAsString = readLineAndConfirmNotNull(fileReader, commitmentFile.toString());

            h = new ZpElement(new BigInteger(hAsString), reconstructedZpGroup.getP(), reconstructedZpGroup.getOrder());

            // read each of the remaining lines in the file, and assign them to
            // the commitments array
            int gArrayIndex = -1;
            while (true) {
                final String line = fileReader.readLine();
                if (line == null) {
                    break;
                }

                gArrayIndex++;
                final ZpElement groupElementCreatedFromLine = new ZpElement(new BigInteger(line), reconstructedZpGroup.getP(), reconstructedZpGroup.getOrder());
                g[gArrayIndex] = groupElementCreatedFromLine;
            }
        }
        LOGGER.debug("Created commitment parameters");
        return new CommitmentParams(zpGroup, h, g);
    }
}
