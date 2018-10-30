package ch.post.it.evoting.verifier.block.block3.loader;

import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;

import java.util.List;

public interface VoterWithProofLoader {

    ElGamalEncryptedBallot getEncyptedBallots();
    List<GjosteenElGamalPlaintext> getDecryptedBallots();
    DecryptionProof[]  getProofs();

}
