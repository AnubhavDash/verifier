/**
 * @author vmateu 20/02/2017
 * <p>
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import com.scytl.products.ov.mixnet.BGVerifier;
import com.scytl.products.ov.mixnet.commons.configuration.locations.DefaultLocationNames;
import com.scytl.products.ov.mixnet.commons.constants.Constants;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BGReader {
    private final static Logger LOGGER = Logger.getLogger(BGVerifier.class);

    /**
     * Create commitment parameters
     *
     * @param numberOfVoters
     *            number of votes
     * @param batchName
     *            batch name
     * @param rootPath
     *            root path
     * @return Commitment parameters found in the file
     * @throws IOException
     *             if the information is not found in the path
     */
    public static CommitmentParams createCommitmentParams(final ZpGroup zpGroup, final int numberOfVoters,
                                                          final String batchName, final Path rootPath) throws IOException {

        String file = DefaultLocationNames.COMMITMENT_PARAMETERS_OUTPUT_FILE_NAME + Constants.JSON_FILE_EXTENSION;
        Path path = Paths.get(rootPath.toString(), batchName, file);
        LOGGER.debug("Commitment Params path = " + path.toString());

        return CommitmentParamsReader.readCommitmentParamsFromFile(zpGroup, path, numberOfVoters);
    }

    public static CommitmentParams createCommitmentParams(final ZpGroup zpGroup, final int numberOfVoters,
                                                          final Path fullPath) throws IOException {

        String file = DefaultLocationNames.COMMITMENT_PARAMETERS_OUTPUT_FILE_NAME + Constants.JSON_FILE_EXTENSION;
        Path path = Paths.get(fullPath.toString(), file);
        LOGGER.debug("Commitment Params path = " + path.toString());

        return CommitmentParamsReader.readCommitmentParamsFromFile(zpGroup, path, numberOfVoters);
    }
}
