package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.loader.VoterWithProofLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.scytl.decrypt.beans.DecryptionProof;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallot;
import com.scytl.products.ov.mixnet.commons.ballots.ElGamalEncryptedBallots;
import com.scytl.products.ov.mixnet.commons.homomorphic.impl.GjosteenElGamalPlaintext;
import com.scytl.products.ov.mixnet.commons.mathematical.GroupElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.Exponent;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpElement;
import com.scytl.products.ov.mixnet.commons.mathematical.impl.ZpGroupParams;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OfflineVoterWithProofLoader implements VoterWithProofLoader {

    @Getter
    @Setter
    private class VotesWithProofLine {
        private String encryptedBallot;
        private String plainText;
        private String proof;
    }

    private ElGamalEncryptedBallots encryptedBallots;
    private List<GjosteenElGamalPlaintext> plaintexts;
    private List<DecryptionProof> decryptionProofs;

    public OfflineVoterWithProofLoader(Path path) {
        try {
            List<ElGamalEncryptedBallot> encryptedBallotList = new ArrayList<>();
            List<GjosteenElGamalPlaintext> plaintextList = new ArrayList<>();
            List<DecryptionProof> decryptionProofList = new ArrayList<>();

            Flux.fromIterable(Deserializer.fromCsv(path.toFile(), "votesWithProof\\.csv", ";", tab -> {
                VotesWithProofLine result = new VotesWithProofLine();
                result.setEncryptedBallot(tab[0]);
                result.setPlainText(tab[1]);
                result.setProof(tab[2]);
                return result;
            })).subscribe(votesWithProofLine -> {
                ElGamalEncryptedBallot encryptedBallot = convertToEncryptedBallot(votesWithProofLine.getEncryptedBallot());
                encryptedBallotList.add(encryptedBallot);
                GroupElement gamma = encryptedBallot.getGamma();

                plaintextList.add(convertToPlainText(votesWithProofLine.getPlainText(), gamma.getParams()));

                decryptionProofList.add(convertToProof(votesWithProofLine.getProof(), gamma));
            });

            this.encryptedBallots = new ElGamalEncryptedBallots(encryptedBallotList);
            this.plaintexts = plaintextList;
            this.decryptionProofs = decryptionProofList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static DecryptionProof convertToProof(String string, GroupElement gamma) {
        final String HASH_TAG = "\"\"hash\"\":\"\"";
        final String Q_TAG = "\"\"q\"\":\"\"";
        final String VALUES_TAG = "\"\"values\"\":[\"\"";
        int hashStartIndex = string.indexOf(HASH_TAG) + HASH_TAG.length();
        String hash = string.substring(hashStartIndex, string.indexOf("\"", hashStartIndex + 1));

        int qStartIndex = string.indexOf(Q_TAG) + Q_TAG.length();
        String q = string.substring(qStartIndex, string.indexOf("\"", qStartIndex + 1));

        Exponent challenge = new Exponent(TypeConverter.base64ToBigInteger(hash), TypeConverter.base64ToBigInteger(q));

        int valuesIndex = string.indexOf(VALUES_TAG) + VALUES_TAG.length();
        String[] values = string.substring(valuesIndex, string.indexOf("\"\"]", valuesIndex + 1)).split(",");

        List<Exponent> responses = Arrays.stream(values).map(val -> new Exponent(TypeConverter.base64ToBigInteger(val), TypeConverter.base64ToBigInteger(q)))
                .collect(Collectors.toList());

        DecryptionProof result = new DecryptionProof(challenge, responses.toArray(new Exponent[]{}));
        result.setGammaOfCiphertext(gamma.getValue());
        return result;
    }

    @Override
    public ElGamalEncryptedBallots getEncyptedBallots() {
        return encryptedBallots;
    }

    static ElGamalEncryptedBallot convertToEncryptedBallot(String ebs) {
        List<GroupElement> zpElements = new ArrayList<>();

        final String VALUE_TAG = "\"\"value\"\":";
        final String P_TAG = "\"\"p\"\":";
        final String Q_TAG = "\"\"q\"\":";

        int valueIndex = 0;
        while ((valueIndex = ebs.indexOf(VALUE_TAG, valueIndex + 1)) != -1) {
            valueIndex = valueIndex + VALUE_TAG.length();
            int endValueIndex = ebs.indexOf(',', valueIndex);
            String value = ebs.substring(valueIndex, endValueIndex);

            int pIndex = ebs.indexOf(P_TAG, endValueIndex) + P_TAG.length();
            int endPIndex = ebs.indexOf(',', pIndex + 1);
            String p = ebs.substring(pIndex, endPIndex);

            int qIndex = ebs.indexOf(Q_TAG, endPIndex) + Q_TAG.length();
            int endQIndex = ebs.indexOf('}', qIndex + 1);
            String q = ebs.substring(qIndex, endQIndex);
            zpElements.add(new ZpElement(TypeConverter.stringToBigInteger(value),
                    TypeConverter.stringToBigInteger(p),
                    TypeConverter.stringToBigInteger(q)));
        }
        return new ElGamalEncryptedBallot(zpElements);
    }

    @Override
    public List<GjosteenElGamalPlaintext> getPlaintexts() {
        return plaintexts;
    }

    static GjosteenElGamalPlaintext convertToPlainText(String string, ZpGroupParams params) {
        int startIndex = string.indexOf('[');
        int endIndex = string.indexOf(']');
        String content = string.substring(startIndex + 1, endIndex);

        ZpElement[] zpElements = Arrays.stream(content.split(","))
                .map(value -> new ZpElement(TypeConverter.stringToBigInteger(value), params))
                .collect(Collectors.toList()).toArray(new ZpElement[]{});
        return new GjosteenElGamalPlaintext(zpElements);
    }

    @Override
    public DecryptionProof[] getProofs() {
        return decryptionProofs.toArray(new DecryptionProof[]{});
    }
}
