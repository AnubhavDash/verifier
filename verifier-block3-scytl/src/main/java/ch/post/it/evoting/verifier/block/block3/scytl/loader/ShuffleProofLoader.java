package ch.post.it.evoting.verifier.block.block3.scytl.loader;


import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;

import java.io.IOException;

public interface ShuffleProofLoader {

    ShuffleProof getShuffleProof() throws IOException;
}
