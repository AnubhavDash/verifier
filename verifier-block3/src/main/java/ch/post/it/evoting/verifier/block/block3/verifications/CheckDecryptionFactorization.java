/**
 * This file is part of Verifier Swiss Post.
 * <p>
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineVoterWithProofLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import reactor.core.publisher.Flux;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CheckDecryptionFactorization extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition verificationDefinition = new VerificationDefinition();
        verificationDefinition.setBlockId(3);
        verificationDefinition.setCategory(Category.CONSISTENCY);
        verificationDefinition.setId(12);
        verificationDefinition.setName("checkDecryptionFactorization");
        verificationDefinition.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification12.description"));

        return verificationDefinition;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        File[] ballotBoxes = PathHelper.listDirectories(inputDirectoryPath.resolve(Block3VerificationSuite.PATH_BALLOTBOXES));
        for (File balloBox : ballotBoxes) {
            // decompressedVotes
            List<BigInteger> decompVotesbigIntList = Flux.fromIterable(Deserializer.fromCsv(balloBox, "decompressedVotes\\.csv", ";", tab -> {
                BigInteger bigInt = BigInteger.ONE;
                for (int i = 0; i < tab.length; i++) {
                    // ignore write ins
                    if (!tab[i].contains("#")) {
                        bigInt = bigInt.multiply(new BigInteger(tab[i]));
                    }
                }
                return bigInt;
            })).collectList().block();

            if (decompVotesbigIntList == null) {
                throw buildVerificationFailureException(
                        "error occurs while parsing data in decompressedVotes.csv",
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification12.nok.message"
                );
            }

            // votes with proof
            OfflineVoterWithProofLoader offlineVoterWithProofLoader = new OfflineVoterWithProofLoader(balloBox.toPath().resolve("0"));
            List<BigInteger> voterWithProofbigIntList = offlineVoterWithProofLoader.getPlaintexts()
                    .stream()
                    .map(plaintext -> plaintext.getValue(0).getValue())
                    .collect(Collectors.toList());

            if (voterWithProofbigIntList == null) {
                throw buildVerificationFailureException(
                        "error occurs while parsing data in voterWithProofbigIntList.csv",
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification12.nok.message",
                        balloBox.getName());
            }

            // finally to the check
            if (decompVotesbigIntList.size() != voterWithProofbigIntList.size()) {
                throw buildVerificationFailureException(
                        "factorization not correct !",
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification12.nok.message",
                        decompVotesbigIntList.toString());
            }

            Boolean allMatch = Flux.fromIterable(decompVotesbigIntList)
                    .zipWith(Flux.fromIterable(voterWithProofbigIntList))
                    .all(t -> MathHelper.areEqual(t.getT1(), t.getT2()))
                    .block();

            if (!allMatch) {
                throw buildVerificationFailureException(
                        "factorization not correct !",
                        Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification12.nok.message",
                        decompVotesbigIntList.toString());
            }
        }

        result.setStatus(Status.OK);
        return result;
    }
}
