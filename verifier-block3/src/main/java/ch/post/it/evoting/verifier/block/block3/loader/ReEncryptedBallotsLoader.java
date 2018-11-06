package ch.post.it.evoting.verifier.block.block3.loader;

import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;

import java.io.IOException;

public interface ReEncryptedBallotsLoader {
    ElGamalEncryptedBallots getReEncryptedBallots() throws IOException;
}

