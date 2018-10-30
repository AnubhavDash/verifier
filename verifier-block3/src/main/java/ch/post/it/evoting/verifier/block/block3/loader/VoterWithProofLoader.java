package ch.post.it.evoting.verifier.block.block3.loader;

import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;

import java.util.List;

public interface VoterWithProofLoader {

    ElGamalEncryptedBallots getEncyptedBallots();
    List<GjosteenElGamalPlaintext> getPlaintexts();
    DecryptionProof[]  getProofs();

}
