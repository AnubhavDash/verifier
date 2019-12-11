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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import ch.post.it.evoting.verifier.dto.EncryptionParameters;
import ch.post.it.evoting.verifier.dto.Option;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class IsQuadraticResidueVO extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(IsQuadraticResidueVO.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification06.description"));
        def.setId(6);
        def.setName("isQuadraticResidue([vo])");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_CRYPTO_SETUP);
            EncryptionParameters encryptionParameters = Deserializer.fromJson(path.toFile(), "encryptionParameters\\.json", EncryptionParameters.class);
            String pString = encryptionParameters.getP();
            BigInteger p = TypeConverter.stringToBigInteger(pString);

            path = inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_ELECTION_SETUP);
            DataConfigEE dataConfigEE = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();

            //votations
            Collection<Integer> errors = ballotBoxes.parallelStream()
                    .flatMap(bb -> bb.getCountingCircles().stream())
                    .flatMap(cc -> cc.getDomainOfInfluence().stream())
                    .flatMap(doi -> doi.getVotes().stream())
                    .flatMap(v -> v.getQuestions().stream())
                    .flatMap(q -> q.getOptions().stream())
                    .filter(o -> !MathHelper.isEulerCriterionValid(BigInteger.valueOf(o.getPrimeNumber()), p))
                    .map(Option::getPrimeNumber)
                    .collect(Collectors.toList());

            //lists
            errors.addAll(
                    ballotBoxes.parallelStream()
                            .flatMap(bb -> bb.getCountingCircles().stream())
                            .flatMap(cc -> cc.getDomainOfInfluence().stream())
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getLists().stream())
                            .filter(l -> !MathHelper.isEulerCriterionValid(BigInteger.valueOf(l.getPrimeNumber()), p))
                            .map(ch.post.it.evoting.verifier.dto.List::getPrimeNumber)
                            .collect(Collectors.toList()));

            //candidates
            errors.addAll(
                    ballotBoxes.parallelStream()
                            .flatMap(bb -> bb.getCountingCircles().stream())
                            .flatMap(cc -> cc.getDomainOfInfluence().stream())
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getLists().stream())
                            .flatMap(l -> l.getCandidatePositions().stream())
                            .flatMap(cp -> cp.getPrimeNumber().stream())
                            .filter(v -> !MathHelper.isEulerCriterionValid(BigInteger.valueOf(v), p))
                            .collect(Collectors.toList()));

            //candidates without lists
            errors.addAll(
                    ballotBoxes.stream()
                            .flatMap(bb -> bb.getCountingCircles().stream())
                            .flatMap(cc -> cc.getDomainOfInfluence().stream())
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getCandidates().stream())
                            .flatMap(c -> c.getPrimeNumber().stream())
                            .filter(v -> !MathHelper.isEulerCriterionValid(BigInteger.valueOf(v), p))
                            .collect(Collectors.toList()));


            if (errors.isEmpty()) {
                result.setStatus(Status.OK);
            } else {
                result.setStatus(Status.NOK);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification06.nok.message", errors.toString()));
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification06.file.not.found.message"));
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
