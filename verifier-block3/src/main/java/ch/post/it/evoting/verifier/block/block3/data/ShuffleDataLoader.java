package ch.post.it.evoting.verifier.block.block3.data;

import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.configuration.locations.DefaultLocationNames;
import com.scytl.products.ov.mixnet.commons.constants.Constants;
import com.scytl.products.ov.mixnet.commons.homomorphic.Ciphertext;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamal;
import com.scytl.products.ov.mixnet.commons.io.BGReader;
import com.scytl.products.ov.mixnet.commons.io.ElGamalEncryptedBallotsLoader;
import com.scytl.products.ov.mixnet.commons.io.JSONProofsReader;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.tools.MatrixArranger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ShuffleDataLoader {

    final ZpGroup zpGroup;
    final ElGamalPublicKey publicKey;
    final GjosteenElGamal cryptoSystem;
    final ElGamalEncryptedBallots encryptedBallots;
    final ElGamalEncryptedBallots reencryptedBallots;
    final ShuffleProof shuffleProof;

    final CommitmentParams commitmentParams;
    final Ciphertext[][] encryptedBallotsCiphertext;
    final Ciphertext[][] reencryptedBallotsCiphertext;


    public ShuffleDataLoader(File file) throws IOException {
        Path bbPath = file.toPath().getParent();
        final String batchName = file.getName();

        zpGroup = BGReader.createZpGroup(bbPath, batchName);
        publicKey = BGReader.createElGamalPublicKey(batchName, bbPath);
        cryptoSystem = new GjosteenElGamal(zpGroup, publicKey);

        encryptedBallots =
                ElGamalEncryptedBallotsLoader.loadCSV(zpGroup.getP(), zpGroup.getOrder(), bbPath, batchName,
                        DefaultLocationNames.ENCRYPTED_BALLOTS_OUTPUT_FILE_NAME + Constants.CSV_FILE_EXTENSION);

        reencryptedBallots = ElGamalEncryptedBallotsLoader.loadCSV(
                zpGroup.getP(), zpGroup.getOrder(), bbPath, batchName,
                DefaultLocationNames.REENCRYPTED_BALLOTS_OUTPUT_FILE_NAME + Constants.CSV_FILE_EXTENSION);

        JSONProofsReader proofsReader = new JSONProofsReader();
        shuffleProof = proofsReader.read(bbPath, batchName);

        final int N = encryptedBallots.getBallots().size();
        final int m = shuffleProof.getInitialMessage().length;
        final int n = N / m;

        commitmentParams = BGReader.createCommitmentParams(zpGroup, n, batchName, bbPath);
        encryptedBallotsCiphertext = MatrixArranger.arrangeInCiphertextMatrix(encryptedBallots, m, n);
        reencryptedBallotsCiphertext = MatrixArranger.arrangeInCiphertextMatrix(reencryptedBallots, m, n);
    }

    public ZpGroup getZpGroup() {
        return zpGroup;
    }

    public ElGamalPublicKey getPublicKey() {
        return publicKey;
    }

    public GjosteenElGamal getCryptoSystem() {
        return cryptoSystem;
    }

    public ElGamalEncryptedBallots getEncryptedBallots() {
        return encryptedBallots;
    }

    public ElGamalEncryptedBallots getReencryptedBallots() {
        return reencryptedBallots;
    }

    public ShuffleProof getShuffleProof() {
        return shuffleProof;
    }

    public CommitmentParams getCommitmentParams() {
        return commitmentParams;
    }

    public Ciphertext[][] getEncryptedBallotsCiphertext() {
        return encryptedBallotsCiphertext;
    }

    public Ciphertext[][] getReencryptedBallotsCiphertext() {
        return reencryptedBallotsCiphertext;
    }
}
