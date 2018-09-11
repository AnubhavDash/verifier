/**
 * @author afries
 * @date Mar 12, 2015 11:00:28 AM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.configuration.locations.DefaultLocationNames;
import com.scytl.products.ov.mixnet.commons.constants.Constants;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalCiphertext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONProofsReader implements ProofsReader {
    private final static Logger LOGGER = LoggerFactory.getLogger(JSONProofsReader.class);

    @Override
    public ShuffleProof read(Path outputParentPath, String batchName) throws IOException {
        Path proofsPath = Paths.get(outputParentPath.toString(), batchName,
                DefaultLocationNames.PROOFS_OUTPUT_FILE_NAME + Constants.JSON_FILE_EXTENSION);
        LOGGER.debug("ShuffleProof path = " + proofsPath.toString());

        final Path fullPath = proofsPath.toAbsolutePath();

        final ObjectMapper mapper = new ObjectMapper();

        //NAE
        SimpleModule module = new SimpleModule("CustomModel", Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(GroupElement.class, ZpElement.class);
        resolver.addMapping(Ciphertext.class, GjosteenElGamalCiphertext.class);
        module.setAbstractTypes(resolver);
        mapper.registerModule(module);
        //NAE

        ShuffleProof shuffleProof;
        shuffleProof = mapper.readValue(fullPath.toFile(), ShuffleProof.class);
        return shuffleProof;
    }
}
