package ch.post.it.evoting.verifier.block.block3.scytl.loader;

import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;

import java.io.File;
import java.io.IOException;

public interface OnlineDataLoader extends EncryptedBallotsLoader, EncryptionParametersLoader, PublicKeyLoader, ReEncryptedBallotsLoader, ShuffleProofLoader, VoterWithProofLoader, CommitmentParametersLoader {
    ElGamalPublicKey getDecryptionPublicKey(File pkJsonFile, int nbKeys) throws IOException;

    CommitmentParams getCommitmentParams() throws IOException;
}
