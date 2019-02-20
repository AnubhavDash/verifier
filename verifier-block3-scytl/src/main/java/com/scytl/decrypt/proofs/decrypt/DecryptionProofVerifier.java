/**
 * @author vmateu  9/12/2016
 *
 * Copyright (C) 2017 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.decrypt.proofs.decrypt;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.tools.RandomOracleHashDecrypt;
import org.apache.log4j.Logger;

public class DecryptionProofVerifier {

    private static final Logger LOGGER = Logger.getLogger(DecryptionProofVerifier.class);

    public static boolean verify(ElGamalEncryptedBallot ballot, GjosteenElGamalPlaintext plaintext,
                                 DecryptionProof proof, ElGamalPublicKey key, ZpGroup zPGroup) {
        List<GroupElement> publicValues = new ArrayList<>();
        List<GroupElement> computedValues = new ArrayList<>();

        for (int i = 0; i < key.getPubKeys().size(); i++) {
            publicValues.add(key.getPubKeys().get(i));
            publicValues.add(ballot.getPhis().get(i).multiply(plaintext.getValue(i).inverse()));
        }

        for (int i = 0, j = 0; i < proof.getResponse().length; i++) {
            computedValues.add(zPGroup.getGenerator().exponentiate(proof.getResponse()[i])
                .multiply(publicValues.get(j).exponentiate(proof.getChallenge()).inverse()));

            computedValues.add(ballot.getGamma().exponentiate(proof.getResponse()[i])
                .multiply(publicValues.get(j + 1).exponentiate(proof.getChallenge()).inverse()));
            j = j + 2;
        }

        Exponent calculatedHash = calculateHash(publicValues, computedValues, proof.getChallenge().getOrder());
        // boolean toReturn = calculatedHash.equals(proof.getChallenge());
        /*
         * if(!toReturn){ System.out.println("Ballot = "+ ballot.toString()); System.out.println("Plaintext = "+
         * plaintext.toString()); System.out.println(proof.toString());
         * System.out.println("hash calc = "+calculatedHash.getValue().toString()); }
         */
        return calculatedHash != null && calculatedHash.equals(proof.getChallenge());
    }

    private static Exponent calculateHash(final List<GroupElement> publicValues,
            final List<GroupElement> computedValues, BigInteger q) {

        String stringForHash = "DecryptionProof";
        RandomOracleHashDecrypt RO;
        try {
            RO = new RandomOracleHashDecrypt(q);
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            LOGGER.error("error", e);
            return null;
        }
        RO.addDataToRO(publicValues);
        RO.addDataToRO(computedValues);
        RO.addDataToRO(stringForHash);
        return RO.getHash();
    }
}
