package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.loader.VoterWithProofLoader;
import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;

import java.nio.file.Path;
import java.util.List;

public class OfflineVoterWithProofLoader implements VoterWithProofLoader {

    private final Path path;

    public OfflineVoterWithProofLoader(Path path){ this.path = path; }

    @Override
    public ElGamalEncryptedBallot getEncyptedBallots() {
        return null;
    }

    @Override
    public List<GjosteenElGamalPlaintext> getDecryptedBallots() {
        return null;
    }

    @Override
    public DecryptionProof[] getProofs() {
        return new DecryptionProof[0];
    }
}
