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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.*;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class IsQuadraticResidueVO extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification06.description"));
        def.setId(6);
        def.setName("isQuadraticResidue([vo])");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        final PathNode encryptParams = pathService.buildFromRootPath(StructureKey.ENCRYPTION_PARAMETERS, inputDirectoryPath);
        EncryptionParameters encryptionParameters = Deserializer.fromJson(encryptParams.getPath(), EncryptionParameters.class);
        EncryptionGroup encryptionGroup = encryptionParameters.getEncryptionGroup();
        BigInteger p = encryptionGroup.getP();

        final PathNode dataConfigPathNode = pathService.buildFromRootPath(StructureKey.DATA_CONFIG_UPDATED, inputDirectoryPath);
        ElectionEvent electionEvent = Deserializer.fromJson(dataConfigPathNode.getPath(), ElectionEvent.class);

        //votations
        Collection<BigInteger> errors = electionEvent.getBallotBoxes().parallelStream()
                .flatMap(bb -> bb.getCountingCircles().stream())
                .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                .flatMap(doi -> doi.getVotes().stream())
                .flatMap(v -> v.getQuestions().stream())
                .flatMap(q -> q.getOptions().stream())
                .filter(o -> !MathHelper.isEulerCriterionValid(o.getPrimeNumber(), p))
                .map(VoteOption::getPrimeNumber)
                .collect(Collectors.toList());

        //lists
        errors.addAll(
                electionEvent.getBallotBoxes().parallelStream()
                        .flatMap(bb -> bb.getCountingCircles().stream())
                        .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getLists().stream())
                        .filter(l -> !MathHelper.isEulerCriterionValid(l.getPrimeNumber(), p))
                        .map(CandidateList::getPrimeNumber)
                        .collect(Collectors.toList()));

        //candidates
        errors.addAll(
                electionEvent.getBallotBoxes().parallelStream()
                        .flatMap(bb -> bb.getCountingCircles().stream())
                        .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getLists().stream())
                        .flatMap(l -> l.getCandidatePositions().stream())
                        .flatMap(cp -> cp.getPrimeNumbers().stream())
                        .filter(v -> !MathHelper.isEulerCriterionValid(v, p))
                        .collect(Collectors.toList()));

        //candidates without lists
        errors.addAll(
                electionEvent.getBallotBoxes().stream()
                        .flatMap(bb -> bb.getCountingCircles().stream())
                        .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getCandidates().stream())
                        .flatMap(c -> c.getPrimeNumbers().stream())
                        .filter(v -> !MathHelper.isEulerCriterionValid(v, p))
                        .collect(Collectors.toList()));


        if (!errors.isEmpty()) {
            throw buildVerificationFailureException(
                    "Euler criterion does not equal to 1",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification06.nok.message",
                    errors.toString()
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
