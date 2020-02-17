/*
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
import ch.post.it.evoting.verifier.common.block.tools.*;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

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
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        // Get commitment parameters files
        List<Path> commitmentParametersPaths = new ArrayList<>();
        PathNode ballotBoxIdDirectoriesPathNode = pathService.buildPathNode(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);
        for (Path ballotBoxIdDirectoryPath : ballotBoxIdDirectoriesPathNode.getRegexPaths()) {
            PathNode ballotBoxOfflineDirectoriesPathNode = pathService.buildFromDynamicPathNode(StructureKey.BALLOT_BOX_OFFLINE_DIR, ballotBoxIdDirectoryPath);
            for (Path ballotBoxOfflineDirectoryPath : ballotBoxOfflineDirectoriesPathNode.getRegexPaths()) {
                PathNode commitmentParametersPathNode = pathService.buildFromDynamicPathNode(StructureKey.COMMITMENT_PARAMETERS, ballotBoxOfflineDirectoryPath);
                commitmentParametersPaths.add(commitmentParametersPathNode.getPath());
            }
        }

        // No commitment parameters files found
        if (commitmentParametersPaths.isEmpty()) {
            throw new FileNotFoundException("no commitmentParameters.json found");
        }

        // Extract p
        final BigInteger p = TypeConverter.stringToBigInteger(commitmentParametersPaths.stream().flatMap(path -> {
            try {
                Optional<String> optionalFirst = Files.lines(path).findFirst();
                if (optionalFirst.isPresent()) {
                    return Stream.of(optionalFirst.get());
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

        // Check errors
        final List<BigInteger> errors = commitmentParametersPaths.stream()
                .flatMap(path -> {
                    try {
                        return Files.lines(path).skip(3);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(TypeConverter::stringToBigInteger)
                .filter(bi -> !MathHelper.isEulerCriterionValid(bi, p)).collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw buildVerificationFailureException(
                    "Commitment parameters verification failed",
                    Block3VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification08.nok.message",
                    errors.toString()
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
