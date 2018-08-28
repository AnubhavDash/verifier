/**
 * @author afries
 * @date Mar 12, 2015 10:58:32 AM
 * Copyright (C) 2015 Scytl Secure Electronic Voting SA
 * All rights reserved.
 */
package com.scytl.products.ov.mixnet.commons.io;

import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;

import java.io.IOException;
import java.nio.file.Path;

interface ProofsReader {

    ShuffleProof read(Path path, String batch) throws IOException;
}
