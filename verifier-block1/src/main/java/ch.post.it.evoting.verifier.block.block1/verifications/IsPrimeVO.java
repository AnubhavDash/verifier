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
package ch.post.it.evoting.verifier.block.block1.verifications;

import ch.post.it.evoting.verifier.block.block1.Block1VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.CandidateList;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.dto.revised.VoteOption;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.MathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class IsPrimeVO extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(1);
        def.setCategory(Category.INTEGRITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block1VerificationSuite.RESOURCE_BUNDLE_NAME, "verification05.description"));
        def.setId(5);
        def.setName("isPrime([vo])");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        def.addVerificationTrait(VerificationTrait.BLOCK_1);
        return def;
    }

    @Override
    public VerificationResult verify(File inputDirectory) throws Exception {
        VerificationResult result = new VerificationResult();

        Path path = inputDirectory.toPath().resolve(Block1VerificationSuite.PATH_ELECTION_SETUP);
        ElectionEvent electionEvent = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", ElectionEvent.class);

        // Votations
        Collection<BigInteger> errors = electionEvent.getBallotBoxes().stream()
                .flatMap(bb -> bb.getCountingCircles().stream())
                .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                .flatMap(doi -> doi.getVotes().stream())
                .flatMap(v -> v.getQuestions().stream())
                .flatMap(q -> q.getOptions().stream())
                .filter(o -> !MathHelper.isPrime(o.getPrimeNumber()))
                .map(VoteOption::getPrimeNumber)
                .collect(Collectors.toList());

        // Candidate lists
        errors.addAll(
                electionEvent.getBallotBoxes().stream()
                        .flatMap(bb -> bb.getCountingCircles().stream())
                        .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getLists().stream())
                        .filter(l -> !MathHelper.isPrime(l.getPrimeNumber()))
                        .map(CandidateList::getPrimeNumber)
                        .collect(Collectors.toList()));

        // Candidates
        errors.addAll(
                electionEvent.getBallotBoxes().stream()
                        .flatMap(bb -> bb.getCountingCircles().stream())
                        .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getLists().stream())
                        .flatMap(l -> l.getCandidatePositions().stream())
                        .flatMap(cp -> cp.getPrimeNumbers().stream())
                        .filter(v -> !MathHelper.isPrime(v))
                        .collect(Collectors.toList()));

        // Candidates without lists
        errors.addAll(
                electionEvent.getBallotBoxes().stream()
                        .flatMap(bb -> bb.getCountingCircles().stream())
                        .flatMap(cc -> cc.getDomainsOfInfluence().stream())
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getCandidates().stream())
                        .flatMap(c -> c.getPrimeNumbers().stream())
                        .filter(p -> !MathHelper.isPrime(p))
                        .collect(Collectors.toList()));

        if (!errors.isEmpty()) {
            throw buildVerificationFailureException(
                    "vo is not prime",
                    Block1VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification05.nok.message",
                    errors.toString()
            );
        }

        result.setStatus(Status.OK);
        return result;
    }
}
