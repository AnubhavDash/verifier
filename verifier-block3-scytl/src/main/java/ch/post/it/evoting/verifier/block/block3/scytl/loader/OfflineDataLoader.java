package ch.post.it.evoting.verifier.block.block3.scytl.loader;

public class OfflineDataLoader {
    private CommitmentParametersLoader commitmentParametersLoader;
    private EncryptedBallotsLoader encryptedBallotsLoader;
    private EncryptionParametersLoader encryptionParametersLoader;
    private PublicKeyLoader publicKeyLoader;
    private ReEncryptedBallotsLoader reEncryptedBallotsLoader;
    private ShuffleProofLoader shuffleProofLoader;
    private VoterWithProofLoader voterWithProofLoader;

    public CommitmentParametersLoader getCommitmentParametersLoader() {
        return commitmentParametersLoader;
    }

    public void setCommitmentParametersLoader(CommitmentParametersLoader commitmentParametersLoader) {
        this.commitmentParametersLoader = commitmentParametersLoader;
    }

    public EncryptedBallotsLoader getEncryptedBallotsLoader() {
        return encryptedBallotsLoader;
    }

    public void setEncryptedBallotsLoader(EncryptedBallotsLoader encryptedBallotsLoader) {
        this.encryptedBallotsLoader = encryptedBallotsLoader;
    }

    public EncryptionParametersLoader getEncryptionParametersLoader() {
        return encryptionParametersLoader;
    }

    public void setEncryptionParametersLoader(EncryptionParametersLoader encryptionParametersLoader) {
        this.encryptionParametersLoader = encryptionParametersLoader;
    }

    public PublicKeyLoader getPublicKeyLoader() {
        return publicKeyLoader;
    }

    public void setPublicKeyLoader(PublicKeyLoader publicKeyLoader) {
        this.publicKeyLoader = publicKeyLoader;
    }

    public ReEncryptedBallotsLoader getReEncryptedBallotsLoader() {
        return reEncryptedBallotsLoader;
    }

    public void setReEncryptedBallotsLoader(ReEncryptedBallotsLoader reEncryptedBallotsLoader) {
        this.reEncryptedBallotsLoader = reEncryptedBallotsLoader;
    }

    public ShuffleProofLoader getShuffleProofLoader() {
        return shuffleProofLoader;
    }

    public void setShuffleProofLoader(ShuffleProofLoader shuffleProofLoader) {
        this.shuffleProofLoader = shuffleProofLoader;
    }

    public VoterWithProofLoader getVoterWithProofLoader() {
        return voterWithProofLoader;
    }

    public void setVoterWithProofLoader(VoterWithProofLoader voterWithProofLoader) {
        this.voterWithProofLoader = voterWithProofLoader;
    }
}