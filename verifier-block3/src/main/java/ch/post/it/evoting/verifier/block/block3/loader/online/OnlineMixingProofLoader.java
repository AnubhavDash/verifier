package ch.post.it.evoting.verifier.block.block3.loader.online;

import ch.post.it.evoting.verifier.block.block3.loader.*;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.OnlineDecryptionProof;
import ch.post.it.evoting.verifier.dto.PublicKey;
import ch.post.it.evoting.verifier.dto.PublicKey__1;
import ch.post.it.evoting.verifier.dto.onlinemixing.OnlineMixing;
import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class OnlineMixingProofLoader implements EncryptedBallotsLoader, EncryptionParametersLoader, PublicKeyLoader, ReEncryptedBallotsLoader, ShuffleProofLoader, VoterWithProofLoader, CommitmentParametersLoader {

    //private final Path path;
    private final OnlineMixing onlineMixing;

    public OnlineMixingProofLoader(Path path) throws IOException {
        //this.path=path;
        this.onlineMixing = load(path);
        //this.other = convert(this.onlineMixing.monStringQuiFaitchier);
    }

    protected OnlineMixing load(Path path) throws IOException {
        return Deserializer.fromJson(path.toFile().getParentFile(), path.toFile().getName(), OnlineMixing.class);
    }

    @Override
    public ElGamalEncryptedBallots getEncryptedBallots() throws IOException {
        return null;
    }

    @Override
    public ZpGroup getZpGroup() {
        BigInteger p = onlineMixing.getEncryptionParameters().getP();
        BigInteger q =  onlineMixing.getEncryptionParameters().getQ();
        ZpGroupParams zpGroupParams = new ZpGroupParams(p, q);
        return new ZpGroup(zpGroupParams, new ZpElement(onlineMixing.getEncryptionParameters().getG(), zpGroupParams));
    }

    @Override
    public ElGamalPublicKey getPublicKey() throws IOException {
        ZpGroupParams params = new ZpGroupParams(onlineMixing.getVoteEncryptionKey().getZpSubgroup().getP(), onlineMixing.getVoteEncryptionKey().getZpSubgroup().getQ());
        ZpGroup zpGroup = new ZpGroup(params, new ZpElement(onlineMixing.getVoteEncryptionKey().getZpSubgroup().getG(), params));
        List<GroupElement> pubKeys = onlineMixing.getVoteEncryptionKey().getElements().stream().map(bigInt -> new ZpElement(bigInt, params)).collect(Collectors.toList());
        return new ElGamalPublicKey(pubKeys, zpGroup);
    }

    @Override
    public ElGamalEncryptedBallots getReEncryptedBallots() throws IOException {
        OnlineDecryptionProof onlineDecryptionProof = new OnlineDecryptionProof();

        return null;
    }

    @Override
    public ShuffleProof getShuffleProof() throws IOException {
        return null;
    }

    @Override
    public ElGamalEncryptedBallots getEncyptedBallots() {
        return null;
    }

    @Override
    public List<GjosteenElGamalPlaintext> getPlaintexts() {
        return null;
    }

    @Override
    public DecryptionProof[] getProofs() {
        return new DecryptionProof[0];
    }

    @Override
    public CommitmentParams getCommitmentParams(ZpGroup zpGroup, int numberOfVoters) throws IOException {
        return null;
    }
}
