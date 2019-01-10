package ch.post.it.evoting.verifier.block.block3.loader.online;

import ch.post.it.evoting.verifier.block.block3.loader.*;
import ch.post.it.evoting.verifier.block.block3.loader.online.mapper.SecondAnswerMapper;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.dto.onlinemixing.OnlineMixing;
import ch.post.it.evoting.verifier.dto.onlinemixing.OnlineShuffleProof;
import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProofSecondAnswer;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

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
        //this.other = mapper(this.onlineMixing.monStringQuiFaitchier);
    }

    protected OnlineMixing load(Path path) throws IOException {
        return Deserializer.fromJson(path.toFile().getParentFile(), path.toFile().getName(), OnlineMixing.class);
    }

    @Override
    public ElGamalEncryptedBallots getEncryptedBallots() throws IOException {
        ZpGroup zpGroup = this.getZpGroup();
        return new ElGamalEncryptedBallots(onlineMixing.getVotes()
                .stream()
                .map(vote -> new ElGamalEncryptedBallot(
                        new ZpElement(vote.getGamma(), zpGroup.getParams()),
                        vote.getPhis().stream().map(p -> new ZpElement(p, zpGroup.getParams())).collect(Collectors.toList())))
                .collect(Collectors.toList()));
    }

    @Override
    public ZpGroup getZpGroup() {
        BigInteger p = onlineMixing.getEncryptionParameters().getP();
        BigInteger q = onlineMixing.getEncryptionParameters().getQ();
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
        ZpGroup zpGroup = this.getZpGroup();
        return new ElGamalEncryptedBallots(onlineMixing.getShuffledVotes()
                .stream()
                .map(pv -> new ElGamalEncryptedBallot(
                        new ZpElement(pv.getGamma(), zpGroup.getParams()),
                        pv.getPhis().stream().map(p -> new ZpElement(p, zpGroup.getParams())).collect(Collectors.toList())))
                .collect(Collectors.toList()));
    }

    @Override
    public ShuffleProof getShuffleProof() throws IOException {
        OnlineShuffleProof onlineShuffleProof = Deserializer.fromJson(onlineMixing.getShuffleProof().getBytes(), OnlineShuffleProof.class);
        List<PublicCommitment> initialMessages = onlineShuffleProof.getInitialMessage().stream().map(im -> new PublicCommitment(new ZpElement(im.getElement().getValue(), im.getElement().getP(), im.getElement().getQ()))).collect(Collectors.toList());
        List<PublicCommitment> firstAnswers = onlineShuffleProof.getFirstAnswer().stream().map(fa -> new PublicCommitment(new ZpElement(fa.getElement().getValue(), fa.getElement().getP(), fa.getElement().getQ()))).collect(Collectors.toList());

        ShuffleProofSecondAnswer secondAnswer = SecondAnswerMapper.INSTANCE.map(onlineShuffleProof.getSecondAnswer());
        ShuffleProof result = new ShuffleProof(initialMessages.toArray(new PublicCommitment[]{}), firstAnswers.toArray(new PublicCommitment[]{}), secondAnswer);
        return result;
    }

    @Override
    public ElGamalEncryptedBallots getEncyptedBallots() {
        ZpGroup zpGroup = this.getZpGroup();
        return new ElGamalEncryptedBallots(onlineMixing.getPreviousVotes()
                .stream()
                .map(pv -> new ElGamalEncryptedBallot(
                        new ZpElement(pv.getGamma(), zpGroup.getParams()),
                        pv.getPhis().stream().map(p -> new ZpElement(p, zpGroup.getParams())).collect(Collectors.toList())))
                .collect(Collectors.toList()));
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
        return new CommitmentParams(zpGroup, numberOfVoters);
    }
}
