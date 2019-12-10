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
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.*;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckCommitmentParameters extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckCommitmentParameters.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(3);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification08.description"));
        def.setId(8);
        def.setName("checkCommitmentParameters(cp)");
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            Path path = inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_ELECTION_SETUP);
            DataConfigEE dataConfigEE = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();
            List<File> commitmentParamFiles = new ArrayList<>();
            ballotBoxes.forEach(ballotBox -> {
                String ballotBoxId = ballotBox.getId();
                try {
                    File[] commitmentParamFolders = PathHelper.listDirectories(inputDirectory.toPath().resolve(Block3VerificationSuite.PATH_BALLOTBOXES).resolve(ballotBoxId));
                    for (File folder : commitmentParamFolders) {
                        commitmentParamFiles.add(PathHelper.getFile(folder, "commitmentParameters.*\\.json"));
                    }
                } catch (FileNotFoundException e) {
                    throw new VerificationFailureException("commitmentParameters.json not found");
                }
            });

            if (commitmentParamFiles.isEmpty()) {
                throw new VerificationFailureException("no commitmentParameters.json found");
            }

            final BigInteger p = TypeConverter.stringToBigInteger(commitmentParamFiles.stream().flatMap(f -> {
                try {
                    Optional<String> optionalfirst = Files.lines(f.toPath()).findFirst();
                    if (optionalfirst.isPresent()) {
                        return Stream.of(optionalfirst.get());
                    } else {
                        throw new RuntimeException("no first line in file");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).reduce(null, (s1, s2) -> {
                if (s1 != null && !s1.equals(s2)) {
                    throw new RuntimeException("P parameter not unique");
                }
                return s2;
            }));

            List<BigInteger> errors = commitmentParamFiles.stream()
                    .flatMap(f -> {
                        try {
                            return Files.lines(f.toPath()).skip(3);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(s -> TypeConverter.stringToBigInteger(s))
                    .filter(bi -> !MathHelper.isEulerCriterionValid(bi, p)).collect(Collectors.toList());

            if (errors.isEmpty()) {
                result.setStatus(Status.OK);
            } else {
                result.setStatus(Status.NOK);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification08.nok.message", errors.toString()));
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification08.file.not.found.message"));
        } catch (VerificationFailureException e) {
            LOGGER.error("no commitmentParameters.json found");
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "verification08.file.not.found.message"));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block3VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
