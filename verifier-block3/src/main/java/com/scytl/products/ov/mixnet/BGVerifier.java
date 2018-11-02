/**
 * @author jruiz  23/06/15
 * <p>
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet;

import ch.post.it.evoting.verifier.block.block3.BGResultNotifier;
import ch.post.it.evoting.verifier.block.block3.BGVerificationProcessor;
import ch.post.it.evoting.verifier.block.block3.loader.*;
import ch.post.it.evoting.verifier.block.block3.loader.offline.*;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import ch.post.it.evoting.verifier.common.Status;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.exceptions.VerifierException;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamal;
import com.scytl.products.ov.mixnet.commons.io.BGReader;
import com.scytl.products.ov.mixnet.commons.io.JSONProofsReader;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.tools.MatrixArranger;
import com.scytl.products.ov.mixnet.proofs.bg.ShuffleProofVerifier;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BGVerifier {
    private final static Logger LOGGER = Logger.getLogger(BGVerifier.class);

    public static boolean verify(final Path outputParentPath) throws VerifierException {
        return verify(outputParentPath, (testType, status, message) -> {
        });
    }

    public static boolean verify(final Path outputParentPath, BGResultNotifier notifier) throws VerifierException {

        try {

            JSONProofsReader proofsReader = new JSONProofsReader();
            final Map<String, Boolean> result = new HashMap<>();
            Boolean verified;

            final File[] ballotBoxes = outputParentPath.toFile().listFiles(File::isDirectory);
            for (File ballotBox : ballotBoxes) {
                final File[] files = ballotBox.listFiles(File::isDirectory);
                if (files != null) {
                    // offline
                    for (File file : files) {
                        EncryptionParametersLoader encryptionParametersLoader = new OfflineEncryptionParametersLoader(file.toPath());
                        PublicKeyLoader publicKeyLoader = new OfflinePublicKeyLoader(file.toPath());
                        EncryptedBallotsLoader offlineEncryptedBallotsLoader = new OfflineEncryptedBallotsLoader(file.toPath());
                        ReEncryptedBallotsLoader offlineReEncryptedBallotsLoader = new OfflineReEncryptedBallotsLoader(file.toPath());
                        ShuffleProofLoader offlineShuffleProofLoader = new OfflineShuffleProofLoader(file.toPath());
                        CommitmentParametersLoader commitmentParametersLoader = new OfflineCommitmentParametersLoader(file.toPath());

                        final String batchName = file.getName();
                        if (!batchName.equals("tally")) {

                            try {
                                // ZpGroup zpGroup = BGReader.createZpGroup(ballotBox.toPath(), batchName);
                                // ElGamalPublicKey publicKey = BGReader.createElGamalPublicKey(batchName, ballotBox.toPath());
                                ZpGroup zpGroup = encryptionParametersLoader.getZpGroup();
                                ElGamalPublicKey publicKey = publicKeyLoader.getPublicKey();
                                GjosteenElGamal cryptosystem = new GjosteenElGamal(zpGroup, publicKey);
                                // System.out.println("path = "+outputParentPath.toString()+"/"+batchName);
                                LOGGER.debug("Ballots before mixing");
                                /*
                                final ElGamalEncryptedBallots encryptedBallots =
                                        ElGamalEncryptedBallotsLoader.loadCSV(zpGroup.getParams(), ballotBox.toPath(), batchName,
                                                DefaultLocationNames.ENCRYPTED_BALLOTS_OUTPUT_FILE_NAME + Constants.CSV_FILE_EXTENSION);
                                 */
                                final ElGamalEncryptedBallots encryptedBallots = offlineEncryptedBallotsLoader.getEncryptedBallots();
                                if (encryptedBallots.getBallots().isEmpty()) {
                                    LOGGER.info("0 ballots, nothing to mix!");
                                    /*return true;*/
                                } else {

                                    LOGGER.debug("Re-encrypted ballots");
                                /*
                                final ElGamalEncryptedBallots reencryptedBallots = ElGamalEncryptedBallotsLoader.loadCSV(
                                        zpGroup.getParams(), ballotBox.toPath(), batchName,
                                        DefaultLocationNames.REENCRYPTED_BALLOTS_OUTPUT_FILE_NAME + Constants.CSV_FILE_EXTENSION);
                                */
                                    final ElGamalEncryptedBallots reencryptedBallots = offlineReEncryptedBallotsLoader.getReEncryptedBallots();
                                    if (reencryptedBallots.getBallots().isEmpty()) {
                                        LOGGER.info("0 ballots reencrypted, no mixing performed!");
                                        /*return true;*/
                                    } else {

                                        //final ShuffleProof shuffleProof = proofsReader.read(ballotBox.toPath(), batchName);
                                        final ShuffleProof shuffleProof = offlineShuffleProofLoader.getShuffleProof();

                                        /*final ShuffleProofVerifier shuffleProofVerifier = getVerifier(zpGroup, cryptosystem,
                                                shuffleProof, ballotBox.toPath(), batchName, encryptedBallots, reencryptedBallots);*/
                                        final ShuffleProofVerifier shuffleProofVerifier = getVerifier(zpGroup, cryptosystem,
                                                shuffleProof, commitmentParametersLoader, encryptedBallots, reencryptedBallots);

                                        verified = shuffleProofVerifier.verifyProof(shuffleProof.getInitialMessage(),
                                                shuffleProof.getFirstAnswer(), shuffleProof.getSecondAnswer(), notifier);

                                        result.put(batchName, verified);
                                    }
                                }

                            } catch (final Exception e) {
                                LOGGER.error("An error occurred while verifying batch " + batchName, e);
                                notifier.notify(BGVerificationProcessor.TestType.ShuffleProof, Status.NOK, "An error occurred while verifying batch " + batchName);
                            }
                        }
                    }
                }
                // online
                final File[] onlineMixing = ballotBox.listFiles(((dir, name) -> name.matches(".*ccn_m.?\\.json")));
/*                for (File file : onlineMixing) {
                    OnlineMixingProofLoader onlineMixingProofLoader = new OnlineMixingProofLoader(file.toPath());
                    ZpGroup zpGroup = onlineMixingProofLoader.getZpGroup();
                    ElGamalPublicKey publicKey = onlineMixingProofLoader.getPublicKey();
                    GjosteenElGamal cryptosystem = new GjosteenElGamal(zpGroup, publicKey);
                    final ElGamalEncryptedBallots encryptedBallots = onlineMixingProofLoader.getEncryptedBallots();
                    if (encryptedBallots.getBallots().isEmpty()) {
                        LOGGER.info("0 ballots, nothing to mix!");
                        *//*return true;*//*
                    } else {

                        LOGGER.debug("Re-encrypted ballots");
                        final ElGamalEncryptedBallots reencryptedBallots = onlineMixingProofLoader.getReEncryptedBallots();
                        if (reencryptedBallots.getBallots().isEmpty()) {
                            LOGGER.info("0 ballots reencrypted, no mixing performed!");
                        } else {
                            final ShuffleProof shuffleProof = onlineMixingProofLoader.getShuffleProof();
                            final ShuffleProofVerifier shuffleProofVerifier = getVerifier(zpGroup, cryptosystem,
                                    shuffleProof, onlineMixingProofLoader, encryptedBallots, reencryptedBallots);
                            verified = shuffleProofVerifier.verifyProof(shuffleProof.getInitialMessage(),
                                    shuffleProof.getFirstAnswer(), shuffleProof.getSecondAnswer(), notifier);
                            result.put(file.getName(), verified);
                        }
                    }
                }*/
            }
            return getResult(result);
        } catch (Exception e) {
            throw new VerifierException("unable to instantiate the loader", e);
        }
    }

    /**
     * Gets ShuffleProofVerifier
     *
     * @param shuffleProof       The bean containing the output of a shuffle proof
     * @param encryptedBallots   Ballots before being mixed
     * @param reencryptedBallots Ballots mixed and reencrypted
     * @return Shuffle proof verifier configured with the given parameters
     * @throws IOException If files were not accessible or have problems when reading it
     */
    private static ShuffleProofVerifier getVerifier(ZpGroup zpGroup, GjosteenElGamal cryptosystem,
                                                    ShuffleProof shuffleProof, CommitmentParametersLoader commitmentParametersLoader,
                                                    ElGamalEncryptedBallots encryptedBallots, ElGamalEncryptedBallots reencryptedBallots) throws IOException {

        final int N = encryptedBallots.getBallots().size();
        final int m = shuffleProof.getInitialMessage().length;
        final int n = N / m;

        LOGGER.debug("Configured n = " + n + " and m = " + m);

        final CommitmentParams commitmentParams = commitmentParametersLoader.getCommitmentParams(zpGroup, n);
        /*BGReader.createCommitmentParams(zpGroup, n, batchName, outputParentPath);*/

        final Ciphertext[][] encryptedBallotsCiphertext =
                MatrixArranger.arrangeInCiphertextMatrix(encryptedBallots, m, n);

        final Ciphertext[][] reencryptedBallotsCiphertext =
                MatrixArranger.arrangeInCiphertextMatrix(reencryptedBallots, m, n);

        return new ShuffleProofVerifier(cryptosystem, commitmentParams, encryptedBallotsCiphertext,
                reencryptedBallotsCiphertext);
    }

    /**
     * Gets final result. If all batches have been verified correctly return true
     *
     * @param result result map
     * @return a boolean value which is true if everything went ok, and 0 if some mixing proofs are incorrect
     */
    private static boolean getResult(Map<String, Boolean> result) {
        boolean finalResult = true;
        if (result.values().isEmpty()) {
            finalResult = false;
            LOGGER.info("Ballots not mixed in this ballot box");
        }
        for (final Boolean b : result.values()) {
            finalResult &= b;
        }
        return finalResult;
    }
}
