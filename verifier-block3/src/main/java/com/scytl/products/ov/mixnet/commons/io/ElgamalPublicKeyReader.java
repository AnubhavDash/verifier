/**
 * @author jruiz
 * @date 23/06/15 18:10
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.PublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param publicKeyFile the path of the file containing the ElGamal public key.
     * @return The reconstructed ElGamalPublicKey
     * @throws IOException if the information can not be obtained from the files for some reason
     */
    public static ElGamalPublicKey readPublicKeyFromFile(final Path publicKeyFile) throws IOException {

        PublicKey publicKey = Deserializer.fromJson(publicKeyFile.getParent().toFile(), publicKeyFile.getFileName().toString(), PublicKey.class);

        final ZpGroup reconstructedZpGroup;
        final List<ZpElement> publicKeyGroupElements = new ArrayList<>();

        final BigInteger p = TypeConverter.base64ToBigInteger(publicKey.getPublicKey().getZpSubgroup().getP());
        final BigInteger q = TypeConverter.base64ToBigInteger(publicKey.getPublicKey().getZpSubgroup().getQ());
        final BigInteger g = TypeConverter.base64ToBigInteger(publicKey.getPublicKey().getZpSubgroup().getG());

        reconstructedZpGroup = ZpGroupReader.createZpGroupFromParameterStrings(p.toString(), q.toString(), g.toString());

        publicKeyGroupElements.addAll(publicKey.getPublicKey()
                .getElements().stream().map(e -> new ZpElement(TypeConverter.base64ToBigInteger(e), p, q)).collect(Collectors.toList()));

        return new ElGamalPublicKey(publicKeyGroupElements, reconstructedZpGroup);
    }

    private static void validateInput(final Path pathOfFileContainingPublicKey) {

        if (pathOfFileContainingPublicKey == null) {
            throw new IllegalArgumentException("The received path cannot be null");
        }
    }
}
