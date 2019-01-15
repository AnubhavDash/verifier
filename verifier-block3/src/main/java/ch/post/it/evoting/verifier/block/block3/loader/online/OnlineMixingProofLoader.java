package ch.post.it.evoting.verifier.block.block3.loader.online;

import ch.post.it.evoting.verifier.block.block3.loader.*;
import ch.post.it.evoting.verifier.block.block3.loader.online.mapper.SecondAnswerMapper;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.OnlineDecryptionProof;
import ch.post.it.evoting.verifier.dto.ZkProof;
import ch.post.it.evoting.verifier.dto.onlinemixing.OnlineMixing;
import ch.post.it.evoting.verifier.dto.onlinemixing.OnlineShuffleProof;
import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProof;
import com.scytl.products.ov.mixnet.commons.beans.proofs.ShuffleProofSecondAnswer;
import com.scytl.products.ov.mixnet.commons.exceptions.VerifierException;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.ElGamalPublicKey;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
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
    public ElGamalEncryptedBallots getReEncryptedBallots() {
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
        ZpGroup zpGroup = this.getZpGroup();
        List<GjosteenElGamalPlaintext> gjosteenElGamalPlaintexts = onlineMixing.getVotes()
                .stream()
                .map(vote -> {
                    return new GjosteenElGamalPlaintext(
                            vote.getPhis()
                                    .stream()
                                    .map(phis -> new ZpElement(phis, zpGroup.getParams()))
                                    .collect(Collectors.toList()).toArray(new GroupElement[]{}));
                })
                .collect(Collectors.toList());

        return gjosteenElGamalPlaintexts;
    }

    @Override
    public DecryptionProof[] getProofs() {
        return onlineMixing.getDecryptionProofs()
                    .stream()
                    .map(dpStr -> createDecryptionProofFromString(dpStr))
                    .collect(Collectors.toList())
                    .toArray(new DecryptionProof[]{});
    }

    private DecryptionProof createDecryptionProofFromString(String str){
        Exponent challenge = null;
        Exponent[] response = new Exponent[]{};
        try {
            OnlineDecryptionProof onlineDecryptionProof = Deserializer.fromJson(str.getBytes(StandardCharsets.UTF_8), OnlineDecryptionProof.class);
            ZkProof zkProof = onlineDecryptionProof.getZkProof();
            String q = zkProof.getQ();
            String hash = zkProof.getHash();
            List<String> values = zkProof.getValues();

            BigInteger exponentMod = TypeConverter.base64ToBigInteger(q);
            challenge = new Exponent(TypeConverter.base64ToBigInteger(hash), exponentMod);
            response = values.stream()
                    .map(value -> new Exponent(TypeConverter.base64ToBigInteger(value), exponentMod))
                    .collect(Collectors.toList())
                    .toArray(new Exponent[]{});
        } catch (IOException e) {
            //TODO handle exception throw a new VerifierException?
            e.printStackTrace();
            throw new VerifierException("Todo");
        }

        return new DecryptionProof(challenge, response);
    }

    @Override
    public CommitmentParams getCommitmentParams(ZpGroup zpGroup, int numberOfVoters) throws IOException {
        return new CommitmentParams(zpGroup, numberOfVoters);
    }
}
