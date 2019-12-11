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
package ch.post.it.evoting.verifier.block.block3.verifications;

import ch.post.it.evoting.verifier.block.block3.Block3VerificationSuite;
import ch.post.it.evoting.verifier.block.block3.loader.offline.OfflineVoterWithProofLoader;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class CheckDecryptionFactorization extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckDecryptionFactorization.class);

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
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            File[] ballotBoxes = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_BALLOTBOXES));
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
                    throw new VerificationFailureException("error occurs while parsing data in decompressedVotes.csv");
                }

                // votes with proof
                OfflineVoterWithProofLoader offlineVoterWithProofLoader = new OfflineVoterWithProofLoader(balloBox.toPath().resolve("0"));
                List<BigInteger> voterWithProofbigIntList = offlineVoterWithProofLoader.getPlaintexts()
                        .stream()
                        .map(plaintext -> plaintext.getValue(0).getValue())
                        .collect(Collectors.toList());

                if (voterWithProofbigIntList == null) {
                    throw new VerificationFailureException("error occurs while parsing data in voterWithProofbigIntList.csv", balloBox.getName());
                }

                // finally to the check
                if (decompVotesbigIntList.size() != voterWithProofbigIntList.size()) {
                    throw new VerificationFailureException("factorization not correct !", decompVotesbigIntList.toString());
                }

                Boolean allMatch = Flux.fromIterable(decompVotesbigIntList)
                        .zipWith(Flux.fromIterable(voterWithProofbigIntList))
                        .all(t -> t.getT1().equals(t.getT2()))
                        .block();

                if (!allMatch) {
                    throw new VerificationFailureException("factorization not correct !", decompVotesbigIntList.toString());
                }
            }
            result.setStatus(Status.OK);
        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification12.nok.message", e.getArgs()[1]));
        } catch (IOException e) {
            result.setStatus(Status.NOK);
            LOGGER.error("a IOException error occurred", e);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification12.file.not.found.message", e.getCause().getLocalizedMessage()));
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            LOGGER.error("an unexpected error occurred", e);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
