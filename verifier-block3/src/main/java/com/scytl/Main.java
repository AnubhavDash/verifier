/**
 * @author vmateu  9/12/2016
 *
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl;

import java.io.File;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scytl.decrypt.DecryptVerifier;
import com.scytl.products.ov.mixnet.BGVerifier;

public class Main {
    private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            LOGGER.error("Need the path to the election results folder");
            System.out.println(printHelp());
        } else if (args[0].equals("-fullElection") || args[0].equals("-f")) {
            verifyFullElection(args[1]);
        } else if (args[0].equals("-ballot") || args[0].equals("-b")) {
            verifyBallot(args[1]);
        } else if (args[0].equals("-h") || args[0].equals("-help")) {
            System.out.println(printHelp());
        } else {
            verifyBallotBox(args);
        }
    }

    private static void verifyFullElection(String path) {
        if (path == null) {
            System.out.println(printHelp());
        } else {
            final File[] files =
                Paths.get(path, "ONLINE", "electionInformation", "ballots").toFile().listFiles(File::isDirectory);
            if (files != null)
                for (File file : files)
                    verifyBallotByFile(file);
        }
    }

    private static void verifyBallot(String path) {
        if (path == null) {
            System.out.println(printHelp());
        } else {
            final File ballotIdFile = Paths.get(path).toFile();
            verifyBallotByFile(ballotIdFile);
        }
    }

    private static void verifyBallotByFile(File ballotIdFile) {
        if (ballotIdFile != null) {
            LOGGER.info(" ------ Validating ballot " + ballotIdFile.getName() + " ------");
            // File[] ballotBoxes = ballotIdFile.listFiles(File::isDirectory)[0].listFiles(File::isDirectory);
            File[] ballotBoxes =
                Paths.get(ballotIdFile.toString(), "ballotBoxes").toFile().listFiles(File::isDirectory);

            if (ballotBoxes != null) {
                for (File ballotBox : ballotBoxes) {
                    LOGGER.info("  -- Validating BB " + ballotBox.getName() + " -- \n");

                    boolean resultMixing = BGVerifier.verify(Paths.get(ballotBox.toString()));
                    if (resultMixing) {
                        LOGGER.info("    Mixing proofs are correct.");

                        int resultDecryption = DecryptVerifier.verify(Paths.get(ballotBox.toString()));
                        if (resultDecryption == 1) {
                            LOGGER.info("    Decryption proofs are correct \n");
                            LOGGER.info("  -- BB " + ballotBox.getName() + " validated --");
                        } else if (resultDecryption == 0) {
                            LOGGER.error("    INCORRECT decryption in BB " + ballotBox.getName());
                        } else if (resultDecryption == -1) {
                            LOGGER.info("  -- BB " + ballotBox.getName()
                                + " one of the decryption files is empty. Decryption proofs cannot be verified --");
                        } else {
                            LOGGER.info("  -- BB " + ballotBox.getName()
                                + " files not found. Decryption proofs cannot be verified --");
                        }
                    } else {
                        LOGGER.error("    INCORRECT mixing proofs or the ballot box was not mixed");
                    }
                }
            } else {
                LOGGER.info("    Ballot without ballot boxes \n");
            }
            LOGGER.info(" ------  Ballot " + ballotIdFile.getName() + " validated ------\n\n");
        }
    }

    private static void verifyBallotBox(String[] args) {
        boolean resultMixing = BGVerifier.verify(Paths.get(args[0]));
        /*
         * if(resultMixing){ int resultDecryption = DecryptVerifier.verify(Paths.get(args[0])); if(resultDecryption ==
         * 1) System.out.println("Everything is properly validated."); else if (resultDecryption == 0)
         * System.out.println("Decryption proofs are incorrect."); else if (resultDecryption < 0)
         * System.out.println("Some files were missing or empty."); }
         */
        if (resultMixing) {
            LOGGER.info("    Mixing proofs are correct.");

            int resultDecryption = DecryptVerifier.verify(Paths.get(args[0]));
            if (resultDecryption == 1) {
                LOGGER.info("    Decryption proofs are correct \n");
            } else if (resultDecryption == 0) {
                LOGGER.error("    INCORRECT decryption");
            } else {
                LOGGER.info("  One of the decryption files is empty. Decryption proofs cannot be verified --");
            }
        } else {
            LOGGER.error("    INCORRECT mixing proofs or the ballot box was not mixed");
        }
    }

    private static String printHelp() {
        return "The proper use of this tool is: \n"
            + "     java -jar mixnet-verifier-1.0.0.jar -fullElection $HOME/sdm/config/${election_event_id} \n"
            + "In case you want to run the verification tool for checking a ballot with all its ballot boxes: \n"
            + "     java -jar mixnet-verifier-1.0.0.jar -b $HOME/sdm/config/${election_event_id}/ONLINE/electionInformation/ballots/${ballot_id} \n"
            + "In case you only want to run the verification tool in a mixed and decrypted ballot box: \n"
            + "     java -jar mixnet-verifier-1.0.0.jar $HOME/sdm/config/${election_event_id}/ONLINE/electionInformation/ballots/${ballot_id}/ballotBoxes/${ballot_box_id}";
    }

}
