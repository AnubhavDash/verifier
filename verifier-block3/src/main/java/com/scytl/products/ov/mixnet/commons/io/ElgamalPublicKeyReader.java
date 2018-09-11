/**
 * @author jruiz
 * @date 23/06/15 18:10
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ElgamalPublicKeyReader extends SerializedDataReader {

	/**
	 * Reads an ElGamal public key from a file. Note: this methods assumes that
	 * the file will contain the following lines:
	 * <ul>
	 * <li>P parameter</li>
	 * <li>Q parameter</li>
	 * <li>G parameter</li>
	 * <li>Part 1 of the public key</li>
	 * <li>Part 2 of the public key</li>
	 * <li>...</li>
	 * <li>Part N of the public key</li>
	 * </ul>
	 *
	 * @param publicKeyFile
	 *            the path of the file containing the ElGamal public key.
	 * @return The reconstructed ElGamalPublicKey
	 * @throws IOException if the information can not be obtained from the files for some reason
	 */
	public static ElGamalPublicKey readPublicKeyFromFile(final Path publicKeyFile) throws IOException {

		validateInput(publicKeyFile);

		final File fileContainingPublicKey = publicKeyFile.toFile();

		validateFileIsAccessible(fileContainingPublicKey);

		final ZpGroup reconstructedZpGroup;
		final List<GroupElement> publicKeyGroupElements = new ArrayList<>();

		try (BufferedReader fileReader = new BufferedReader(new FileReader(fileContainingPublicKey))) {

			final String pAsString = readLineAndConfirmNotNull(fileReader, publicKeyFile.toString());
			final String qAsString = readLineAndConfirmNotNull(fileReader, publicKeyFile.toString());
			final String gAsString = readLineAndConfirmNotNull(fileReader, publicKeyFile.toString());

			final ZpGroupParams params = new ZpGroupParams(new BigInteger(pAsString), new BigInteger(qAsString));
			reconstructedZpGroup = ZpGroupReader.createZpGroupFromParameterStrings(pAsString, qAsString, gAsString);

			while (true) {
				final String line = fileReader.readLine();
				if (line == null) {
					break;
				}

				final BigInteger lineAsBigInteger = new BigInteger(line);
				publicKeyGroupElements.add(new ZpElement(lineAsBigInteger, params));
			}
		}

		return new ElGamalPublicKey(publicKeyGroupElements, reconstructedZpGroup);
	}

	private static void validateInput(final Path pathOfFileContainingPublicKey) {

		if (pathOfFileContainingPublicKey == null) {
			throw new IllegalArgumentException("The received path cannot be null");
		}
	}
}
