/**
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.loader.online;

import ch.post.it.evoting.verifier.block.block3.loader.online.mapper.SecondAnswerMapper;
import ch.post.it.evoting.verifier.block.block3.scytl.loader.OnlineDataLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.CcMixingPublicKey;
import ch.post.it.evoting.verifier.dto.OnlineDecryptionProof;
import ch.post.it.evoting.verifier.dto.PublicKey;
import ch.post.it.evoting.verifier.dto.ZkProof;
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
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroup;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.CommitmentParams;
import com.scytl.products.ov.mixnet.commons.proofs.bg.commitments.PublicCommitment;
import org.apache.commons.codec.DecoderException;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class OnlineMixingProofLoader implements OnlineDataLoader {

    private final OnlineMixing onlineMixing;

    public OnlineMixingProofLoader(Path path) throws IOException {
        this.onlineMixing = load(path);
    }

    protected OnlineMixing load(Path path) throws IOException {
        return Deserializer.fromJson(path.toFile().getParentFile(), path.toFile().getName(), OnlineMixing.class);
    }

    @Override
    public ElGamalEncryptedBallots getEncryptedBallots() {
        ZpGroup zpGroup = this.getZpGroup();
        return new ElGamalEncryptedBallots(onlineMixing.getPreviousVotes()
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
    public ElGamalPublicKey getPublicKey() {
        ZpGroupParams params = new ZpGroupParams(onlineMixing.getPreviousVoteEncryptionKey().getZpSubgroup().getP(), onlineMixing.getPreviousVoteEncryptionKey().getZpSubgroup().getQ());
        ZpGroup zpGroup = new ZpGroup(params, new ZpElement(onlineMixing.getPreviousVoteEncryptionKey().getZpSubgroup().getG(), params));
        List<GroupElement> pubKeys = onlineMixing.getPreviousVoteEncryptionKey().getElements().stream().map(bigInt -> new ZpElement(bigInt, params)).collect(Collectors.toList());
        return new ElGamalPublicKey(pubKeys, zpGroup);
    }

    public ElGamalPublicKey getDecryptionPublicKey(File pkJsonFile, int nbKeys) throws IOException {
        try {
            ZpGroupParams params = new ZpGroupParams(onlineMixing.getVoteEncryptionKey().getZpSubgroup().getP(), onlineMixing.getVoteEncryptionKey().getZpSubgroup().getQ());
            ZpGroup zpGroup = new ZpGroup(params, new ZpElement(onlineMixing.getVoteEncryptionKey().getZpSubgroup().getG(), params));
            List<GroupElement> pubKeys = new ArrayList<>();

            //retrieve in the file the pkey regarding the eeid
            String electionEventId = onlineMixing.getVoteSetId().getBallotBoxId().getElectionEventId();

            CcMixingPublicKey[] ccMixingPublicKey = Deserializer.fromJson(pkJsonFile.getParentFile(), pkJsonFile.getName(), CcMixingPublicKey[].class);

            String pKeyStr = Arrays.stream(ccMixingPublicKey)
                    .filter(e -> electionEventId.equals(e.getElectionEventId()))
                    .map(e -> e.getPublicKey())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Unable to retrieve the publicKey"));

            byte[] decodedPkey = TypeConverter.hexaStringToByte(pKeyStr);
            PublicKey publicKey = Deserializer.fromJson(decodedPkey, PublicKey.class);


            List<BigInteger> elements = publicKey.getPublicKey().getElements().stream().map(TypeConverter::base64ToBigInteger).collect(Collectors.toList());
            if (elements.isEmpty()) {
                throw new IllegalArgumentException("No elements found in publicKey");
            }
            // In case the final key has only 1 element: Multiply all “elements” from CCN mixing public key modulo p
            // In case that the key has more than 1 element (n elements), the first n-1 elements of the CCN mixing public key can be used directly. For the last mixing public key elements, multiply the remaining elements together

            for (int i = 0; i < nbKeys - 1; i++) {
                pubKeys.add(new ZpElement(elements.get(i), params));
            }
            pubKeys.add(new ZpElement(multiplyElements(elements.subList(nbKeys - 1, elements.size()), params.getP()), params));

            return new ElGamalPublicKey(pubKeys, zpGroup);
        }
        catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    private BigInteger multiplyElements(List<BigInteger> elements, BigInteger p) {
        if (elements != null && !elements.isEmpty() && p != null) {
            AtomicReference<BigInteger> result = new AtomicReference<>(BigInteger.ONE);
            elements.forEach(elem -> result.set(result.get().multiply(elem).mod(p)));
            return result.get();
        } else {
            throw new IllegalArgumentException("Invalid input parameters");
        }

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

    public ElGamalEncryptedBallots getVotes() {
        ZpGroup zpGroup = this.getZpGroup();
        return new ElGamalEncryptedBallots(onlineMixing.getVotes()
                .stream()
                .map(pv -> new ElGamalEncryptedBallot(
                        new ZpElement(pv.getGamma(), zpGroup.getParams()),
                        pv.getPhis().stream().map(p -> new ZpElement(p, zpGroup.getParams())).collect(Collectors.toList())))
                .collect(Collectors.toList()));
    }

    @Override
    public DecryptionProof[] getProofs() {
        Flux<BigInteger> gammas = Flux.fromStream(this.onlineMixing.getShuffledVotes().stream().map(sv -> sv.getGamma()));

        DecryptionProof[] decryptionProofs =
                Flux.fromStream(onlineMixing.getDecryptionProofs().stream())
                        .zipWith(gammas)
                        .map(tuple -> createDecryptionProofFromString(tuple.getT1(), tuple.getT2()))
                        .collectList()
                        .block()
                        .toArray(new DecryptionProof[]{});

        return decryptionProofs;
    }

    /*private void updateDecryptionProofsSetGamma(DecryptionProof[] decryptionProofs, List<BigInteger> gammas) {
        for(int i = 0; i < decryptionProofs.length; i++ ){
            decryptionProofs[i].setGammaOfCiphertext(gammas.get(i));
        }
    }*/

    private DecryptionProof createDecryptionProofFromString(String str, BigInteger gammaOfCiphertext) {
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
            throw new RuntimeException(e);
        }
        DecryptionProof decryptionProof = new DecryptionProof(challenge, response);
        decryptionProof.setGammaOfCiphertext(gammaOfCiphertext);

        return decryptionProof;
    }

    @Override
    public CommitmentParams getCommitmentParams(ZpGroup zpGroup, int numberOfVoters) throws IOException {
        CommitmentParams result = null;
        List<String> commitmentParameters = onlineMixing.getCommitmentParameters();
        //[0] p, [1] q, [2] g, [3] h, [4] n G
        if (commitmentParameters.size() >= 5) {
            GroupElement h = new ZpElement(commitmentParameters.get(3), zpGroup.getParams());
            GroupElement[] g = commitmentParameters.stream()
                    .skip(4)
                    .map(str -> new ZpElement(TypeConverter.stringToBigInteger(str), zpGroup.getParams()))
                    .collect(Collectors.toList()).toArray(new GroupElement[]{});
            result = new CommitmentParams(zpGroup, h, g);
        }
        return result;
    }

    public CommitmentParams getCommitmentParams() throws IOException {
        final int N = getEncryptedBallots().getBallots().size();
        int n = N;
        if (N != 0 && N != 1) {
            final int m = getShuffleProof().getInitialMessage().length;
            n = N / m;
        }
        return getCommitmentParams(getZpGroup(), n);
    }
}
