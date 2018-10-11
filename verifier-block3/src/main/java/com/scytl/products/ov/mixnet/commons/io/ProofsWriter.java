/**
 * @author mmanzano
 * @date Mar 9, 2015 12:06:36 PM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import java.nio.file.Path;

import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;

public interface ProofsWriter {

    void write(Path path, ShuffleProof shuffleProof);
}
