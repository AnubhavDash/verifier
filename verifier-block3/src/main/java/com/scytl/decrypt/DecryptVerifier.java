/**
 * @author vmateu  9/12/2016
 *
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.decrypt;

import ch.post.it.evoting.verifier.block.block3.loader.EncryptionParametersLoader;
import ch.post.it.evoting.verifier.block.block3.loader.PublicKeyLoader;
import ch.post.it.evoting.verifier.block.block3.loader.VoterWithProofLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineEncryptionParametersLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflinePublicKeyLoader;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineVoterWithProofLoader;
import ch.post.it.evoting.verifier.block.block3.loader.online.OnlineMixingProofLoader;
import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.decrypt.proofs.decrypt.DecryptionProofVerifier;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.constants.Constants;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;
import com.scytl.products.ov.mixnet.commons.io.ZpGroupReader;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import org.apache.commons.codec.DecoderException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DecryptVerifier {

	private final static Logger LOGGER = Logger.getLogger(DecryptVerifier.class);

	public static int verifyOnline(Path rootPath, File pkJsonFile) {
		try{
				OnlineMixingProofLoader onlineMixingProofLoader = new OnlineMixingProofLoader(rootPath);
				ZpGroup zPGroup = onlineMixingProofLoader.getZpGroup();
				ElGamalPublicKey publicKey = onlineMixingProofLoader.getDecryptionPublicKey(pkJsonFile);
				ElGamalEncryptedBallots ballots = onlineMixingProofLoader.getReEncryptedBallots();
				List<GjosteenElGamalPlaintext> plaintexts = onlineMixingProofLoader.getPlaintexts();
				DecryptionProof[] proofs = onlineMixingProofLoader.getProofs();

			if (ballots.getBallots().isEmpty()) {
				LOGGER.info("There are no ballots to be decrypted.");
				return -1;
			}
			if (plaintexts.isEmpty()) {
				LOGGER.info("There are no decrypted ballots.");
				return -1;
			}
			if (proofs.length == 0) {
				LOGGER.info("There are no decryption proofs.");
				return -1;
			}
			return validate(ballots.getBallots(), plaintexts, proofs, publicKey, zPGroup);
		} catch (IOException | DecoderException e) {
			LOGGER.error("Problems loading files: " + e.getMessage(), e);
			return -2;
		}
	}


	public static int verify(Path rootPath) {
        try {
			EncryptionParametersLoader encryptionParametersLoader = new OfflineEncryptionParametersLoader(rootPath.resolve("0"));
			PublicKeyLoader publicKeyLoader = new OfflinePublicKeyLoader(rootPath);
			VoterWithProofLoader voterWithProofLoader = new OfflineVoterWithProofLoader(rootPath.resolve("0"));
            ZpGroup zPGroup = encryptionParametersLoader.getZpGroup();
            ElGamalPublicKey publicKey = publicKeyLoader.getPublicKey();
            ElGamalEncryptedBallots ballots = voterWithProofLoader.getEncyptedBallots();
            List<GjosteenElGamalPlaintext> plaintexts = voterWithProofLoader.getPlaintexts();
            DecryptionProof[] proofs = voterWithProofLoader.getProofs();

            /*
            zPGroup = createZpGroup(rootPath);
            publicKey = ElgamalPublicKeyReader.readPublicKeyFromFile(Paths.get(rootPath.toString(),
                DefaultLocationNames.PUBLIC_KEY_OUTPUT_FILE_NAME + Constants.JSON_FILE_EXTENSION));

            ballots = ElGamalEncryptedBallotsLoader.loadCSV(zPGroup.getParams(), Paths.get(rootPath.toString()), "",
                "encryptedBallots.csv");
            */
            if (ballots.getBallots().isEmpty()) {
                LOGGER.info("There are no ballots to be decrypted.");
                return -1;
            }
            /*
            plaintexts = getPlaintextsFromFile(rootPath.toString(), zPGroup);
            */
            if (plaintexts.isEmpty()) {
                LOGGER.info("There are no decrypted ballots.");
                return -1;
            }
            /*
            proofs = getProofsFromFile(ballots.getBallots().size(), rootPath.toString());
            */
            if (proofs.length == 0) {
                LOGGER.info("There are no decryption proofs.");
                return -1;
            }

            return validate(ballots.getBallots(), plaintexts, proofs, publicKey, zPGroup);
        } catch (IOException e) {
            LOGGER.error("Problems loading files: " + e.getMessage());
            return -2;
        }
    }

	private static ZpGroup createZpGroup(Path rootPath) throws IOException {

		final Path encryptionParametersFile;
		encryptionParametersFile = Paths.get(rootPath.toString(),
				"encryptedParams" + Constants.JSON_FILE_EXTENSION);
		return ZpGroupReader.build(encryptionParametersFile);
	}

	private static int validate(List<ElGamalEncryptedBallot> ballotsList,
			List<GjosteenElGamalPlaintext> plaintexts,
			DecryptionProof[] proofs, ElGamalPublicKey key, ZpGroup zPGroup) {
		if (!validateSizes(ballotsList, plaintexts, proofs))
			return 0;
		for (int i = 0; i < proofs.length; i++) {
			Boolean proofVerified = false;
			for (int j = 0; j < ballotsList.size() && !proofVerified; j++) {
				if (proofs[i].getGammaOfCiphertexts().equals(
						ballotsList.get(j).getGamma().getValue())) {
					if (!DecryptionProofVerifier.verify(ballotsList.get(j),
							plaintexts.get(i), proofs[i], key, zPGroup)) {
						LOGGER.error("failed at ballot number " + i);
						return 0;
					}
					ballotsList.remove(j);
					proofVerified = true;
				}
			}
			if (!proofVerified)
				return 0;
		}
		return 1;
	}

	private static boolean validateSizes(
			List<ElGamalEncryptedBallot> ballotsList,
			List<GjosteenElGamalPlaintext> plaintexts, DecryptionProof[] proofs) {
		if (proofs.length != ballotsList.size()) {
			LOGGER.error("There are not the same amount of ballots and proofs.");
			return false;
		}
		if (proofs.length != plaintexts.size()) {
			LOGGER.error("There are not the same amount of plaintexts and proofs.");
			return false;
		}
		if (plaintexts.size() != ballotsList.size()) {
			LOGGER.error("There are not the same amount of ballots and plaintexts.");
			return false;
		}
		return true;
	}

	private static DecryptionProof[] getProofsFromFile(int amountOfBallots,
			String path) throws IOException {
		String csvFile = path.concat("/" + "proofs.csv");
		String line;// = "";
		String cvsSplitBy = ";";
		DecryptionProof[] proofsOutput = new DecryptionProof[amountOfBallots];

		try(BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			for (int i = 0; (line = br.readLine()) != null; i++) {
				String[] fields = line.split(cvsSplitBy);

				byte[] proof = Base64.getDecoder().decode(fields[3]);
				String JSonProof = new String(proof);
				proofsOutput[i] = new DecryptionProof(JSonProof);
				proofsOutput[i].setGammaOfCiphertext(new BigInteger(fields[0]));
			}
			return proofsOutput;
		}
	}

	private static List<GjosteenElGamalPlaintext> getPlaintextsFromFile(
			String path, ZpGroup group) throws IOException {
		String filePath = path.concat("/" + "decryptedBallots.csv");
		String line;
		String cvsSplitByBallot = ";";
		// Assuming we use , to separate decryption from different phis.
		String cvsSplitPhis = ",";
		List<GjosteenElGamalPlaintext> plaintextsOutput = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			while ((line = br.readLine()) != null) {
				String[] plaintextsPerBallot = line.split(cvsSplitPhis);
				GroupElement[] plaintextsAsGroupElements = new GroupElement[plaintextsPerBallot.length];
				for (int i = 0; i < plaintextsPerBallot.length; i++) {
					String[] multipliedElementsPerPlaintext = plaintextsPerBallot[i]
							.split(cvsSplitByBallot);
					ZpElement resultingPlaintext = new ZpElement(BigInteger.ONE,
							group.getParams());
					for (String multipliedElement : multipliedElementsPerPlaintext) {
						ZpElement elementAux = new ZpElement(new BigInteger(
								multipliedElement), group.getParams());
						resultingPlaintext = (ZpElement) resultingPlaintext
								.multiply(elementAux);
					}
					plaintextsAsGroupElements[i] = resultingPlaintext;
				}
				plaintextsOutput.add(new GjosteenElGamalPlaintext(
						plaintextsAsGroupElements));
			}
			return plaintextsOutput;
		}
	}

}
