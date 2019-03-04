/**
 * This file is part of Verifier Swiss Post.
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.  If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.loader.offline;

import ch.post.it.evoting.verifier.block.block3.scytl.loader.VoterWithProofLoader;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.OnlineDecryptionProof;
import ch.post.it.evoting.verifier.dto.onlinemixing.EncryptedBallot;
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
import java.math.BigInteger;
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

    public OfflineVoterWithProofLoader(Path path) throws IOException {
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
    }

    static DecryptionProof convertToProof(String proofString, GroupElement gamma) {
        DecryptionProof result = null;
        try {
            OnlineDecryptionProof odp = Deserializer.fromJson(TypeConverter.stringToByte(proofString.substring(1, proofString.length() - 1).replace("\"\"", "\"")), OnlineDecryptionProof.class);

            Exponent challenge = new Exponent(TypeConverter.base64ToBigInteger(odp.getZkProof().getHash()), TypeConverter.base64ToBigInteger(odp.getZkProof().getQ()));

            List<Exponent> responses = Arrays.stream(odp.getZkProof().getValues().toArray(new String[0]))
                    .map(val -> cleanWriteInsBoundaryQuotes(val))
                    .map(val -> new Exponent(TypeConverter.base64ToBigInteger(val), TypeConverter.base64ToBigInteger(odp.getZkProof().getQ())))
                    .collect(Collectors.toList());

            result = new DecryptionProof(challenge, responses.toArray(new Exponent[]{}));
            result.setGammaOfCiphertext(gamma.getValue());

        } catch (IOException e) {
            throw new RuntimeException("Unable to map to proof", e);
        }
        return result;
    }


    private static String cleanWriteInsBoundaryQuotes(String original) {
        final String QUOTE = "\"";
        if (original != null) {
            String result = original;
            boolean corrected = false;

            if (result.startsWith(QUOTE)) {
                result = result.substring(1);
                corrected = true;
            }
            if (result.endsWith(QUOTE)) {
                result = result.substring(0, result.length() - 1);
                corrected = true;
            }
            return corrected ? cleanWriteInsBoundaryQuotes(result) : result;
        } else {
            return original;
        }
    }

    @Override
    public ElGamalEncryptedBallots getEncryptedBallots() {
        return encryptedBallots;
    }

    static ElGamalEncryptedBallot convertToEncryptedBallot(String ebsString) {
        List<GroupElement> zpElements = new ArrayList<>();
        try {
            EncryptedBallot[] ebs = Deserializer.fromJson(TypeConverter.stringToByte(ebsString.substring(1, ebsString.length() - 1).replace("\"\"", "\"")), EncryptedBallot[].class);
            for (EncryptedBallot eb : ebs) {
                zpElements.add(new ZpElement(eb.getValue(), eb.getP(), eb.getQ()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to map to EncryptedBallot", e);
        }

        return new ElGamalEncryptedBallot(zpElements);
    }

    @Override
    public List<GjosteenElGamalPlaintext> getPlaintexts() {
        return plaintexts;
    }

    static GjosteenElGamalPlaintext convertToPlainText(String gjosteenElGamalsString, ZpGroupParams params) {
        ZpElement[] zpElements = null;
        try {
            BigInteger[] gjosteenElGamals = Deserializer.fromJson(TypeConverter.stringToByte(gjosteenElGamalsString), BigInteger[].class);
            zpElements = Arrays.stream(gjosteenElGamals)
                    .map(value -> new ZpElement(value, params))
                    .collect(Collectors.toList()).toArray(new ZpElement[]{});
        } catch (IOException e) {
            throw new RuntimeException("Unable to map to plaintext", e);
        }
        return new GjosteenElGamalPlaintext(zpElements);
    }

    @Override
    public DecryptionProof[] getProofs() {
        return decryptionProofs.toArray(new DecryptionProof[]{});
    }
}
