/**
 * @author afries
 * @date Mar 12, 2015 10:58:32 AM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import java.io.IOException;
import java.nio.file.Path;

import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;

interface ProofsReader {

    ShuffleProof read(Path path, String batch) throws IOException;
}
