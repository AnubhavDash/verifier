/**
 * @author vmateu 20/02/2017
 * <p>
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import com.scytl.products.ov.mixnet.commons.configuration.locations.DefaultLocationNames;
import com.scytl.products.ov.mixnet.commons.constants.Constants;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BGReader {
    private final static Logger LOGGER = Logger.getLogger(BGReader.class);

    /**
     * Create commitment parameters
     *
     * @param numberOfVoters number of votes
     * @param batchName      batch name
     * @param rootPath       root path
     * @return Commitment parameters found in the file
     * @throws IOException if the information is not found in the path
     */
    public static CommitmentParams createCommitmentParams(final ZpGroup zpGroup, final int numberOfVoters,
                                                          final String batchName, final Path rootPath) throws IOException {

        String file = DefaultLocationNames.COMMITMENT_PARAMETERS_OUTPUT_FILE_NAME + Constants.JSON_FILE_EXTENSION;
        Path path = Paths.get(rootPath.toString(), batchName, file);
        LOGGER.debug("Commitment Params path = " + path.toString());

        return CommitmentParamsReader.readCommitmentParamsFromFile(zpGroup, path, numberOfVoters);
    }

    /**
     * Gets Encryption Parameters File and build ZpGroup
     *
     * @param rootPath  root path
     * @param batchName batch name
     * @return ZpGroup
     * @throws IOException if the information is not found in the path
     */
    public static ZpGroup createZpGroup(Path rootPath, final String batchName) throws IOException {
        String file = DefaultLocationNames.ENCRYPTION_PARAMETERS_OUTPUT_FILE_NAME + Constants.JSON_FILE_EXTENSION;
        final Path path = Paths.get(rootPath.toString(), batchName, file);
        LOGGER.debug("Encryption Parameters File = " + path.toString());

        return ZpGroupReader.build(path);
    }

    /**
     * Gets public key file and creates ElGamal public key
     *
     * @param batchName  batch name
     * @param outputPath output path
     * @return ElGamal public key
     * @throws IOException if the information is not found in the path
     */
    public static ElGamalPublicKey createElGamalPublicKey(final String batchName, final Path outputPath)
            throws IOException {
        String file = DefaultLocationNames.PUBLIC_KEY_OUTPUT_FILE_NAME + Constants.JSON_FILE_EXTENSION;
        Path path = Paths.get(outputPath.toString(), batchName, file);
        LOGGER.debug("ElGamal public key path = " + path.toString());

        return ElgamalPublicKeyReader.readPublicKeyFromFile(path);
    }

}
