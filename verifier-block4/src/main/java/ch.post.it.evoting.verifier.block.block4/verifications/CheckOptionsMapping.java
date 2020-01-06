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
package ch.post.it.evoting.verifier.block.block4.verifications;

import ch.post.it.evoting.verifier.block.block4.Block4VerificationSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.VerificationDefinition;
import ch.post.it.evoting.verifier.common.VerificationResult;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.dto.revised.BallotBox;
import ch.post.it.evoting.verifier.common.block.dto.revised.CandidateList;
import ch.post.it.evoting.verifier.common.block.dto.revised.CountingCircle;
import ch.post.it.evoting.verifier.common.block.dto.revised.ElectionEvent;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import com.scytl.xmlns.decrypt._1.Results;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CheckOptionsMapping extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(4);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4VerificationSuite.RESOURCE_BUNDLE_NAME, "verification01.description"));
        def.setId(1);
        def.setName("checkOptionsMapping");
        return def;
    }

    @Override
    public VerificationResult verify(File inputDirectory) throws Exception {
        VerificationResult result = new VerificationResult();

        Path path = inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_ELECTION_SETUP);
        ElectionEvent electionEvent = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", ElectionEvent.class);
        List<BallotBox> ballotBoxes = electionEvent.getBallotBoxes();

        for (BallotBox ballotBox : ballotBoxes) {
            // Prepare ballot box ids
            String ballotBoxId = TypeConverter.UUIDToStringWithoutDash(ballotBox.getId());
            String ballotBoxAuthId = TypeConverter.UUIDToStringWithoutDash(ballotBox.getAuthId());

            for (CountingCircle countingCircle : ballotBox.getCountingCircles()) {

                String countingCircleId = countingCircle.getId();
                // 1 Generate map<prime, alias>
                // Votations
                Map<BigInteger, String> primeAliasMap = countingCircle.getDomainsOfInfluence().stream()
                        .flatMap(doi -> doi.getVotes().stream())
                        .flatMap(v -> v.getQuestions().stream())
                        .flatMap(q -> q.getOptions().stream().map(option -> new AbstractMap.SimpleEntry<>(option.getPrimeNumber(), option.getAlias().toString())))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
                // Lists
                primeAliasMap.putAll(countingCircle.getDomainsOfInfluence().stream()
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getLists().stream())
                        .collect(Collectors.toMap(CandidateList::getPrimeNumber, CandidateList::getAlias))
                );
                // Candidates
                primeAliasMap.putAll(countingCircle.getDomainsOfInfluence().stream()
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getLists().stream())
                        .flatMap(l -> l.getCandidatePositions().stream())
                        .flatMap(cp -> cp.getPrimeNumbers().stream().map(prime -> new AbstractMap.SimpleEntry<>(prime, cp.getCandidateListId().toString())))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
                );
                primeAliasMap.putAll(countingCircle.getDomainsOfInfluence().stream()
                        .flatMap(doi -> doi.getElections().stream())
                        .flatMap(e -> e.getCandidates().stream())
                        .flatMap(c -> c.getPrimeNumbers().stream().map(prime -> new AbstractMap.SimpleEntry<>(prime, c.getAlias().toString())))
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
                );

                // 2 Generate map<prime, count>, but before retrieve the ballotbox file
                Map<String, Long> primesCountMap = getCorrectFileAndExtractPrimesCount(inputDirectory, ballotBoxId);

                // 3 Generate map<alias, count>
                final Path resultsPath = inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_RESULTS);
                Results decryptResult = Deserializer.fromXml(resultsPath.toFile(), "evoting-decrypt_.*\\.xml", Results.class);
                Map<String, Long> aliasCountMap = decryptResult.getBallotsBox().stream()
                        .filter(bb -> ballotBoxAuthId.equals(bb.getBallotBoxIdentification()))
                        .flatMap(theBb -> theBb.getCountingCircle().stream())
                        .filter(cc -> countingCircleId.equals(cc.getCountingCircleIdentification()))
                        .flatMap(theCc -> theCc.getDomainOfInfluence().stream())
                        .flatMap(doi -> doi.getVote().stream())
                        .flatMap(v -> v.getBallot().stream())
                        .flatMap(b -> b.getChosenAnswerIdentification().stream())
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                aliasCountMap.putAll(decryptResult.getBallotsBox().stream()
                        .filter(bb -> {
                            return ballotBoxAuthId.equals(bb.getBallotBoxIdentification());
                        })
                        .flatMap(theBb -> theBb.getCountingCircle().stream())
                        .filter(cc -> {
                            return countingCircleId.equals(cc.getCountingCircleIdentification());
                        })
                        .flatMap(theCc -> {
                            return theCc.getDomainOfInfluence().stream();
                        })
                        .flatMap(doi -> doi.getElection().stream())
                        .flatMap(e -> e.getBallot().stream())
                        .flatMap(b -> {
                            List<Stream<String>> coll = new LinkedList<>();
                            if (b.getChosenCandidateListIdentification() != null) {
                                coll.add(b.getChosenCandidateListIdentification().stream());
                            }
                            if (b.getChosenCandidateIdentification() != null) {
                                coll.add(b.getChosenCandidateIdentification().stream());
                            }
                            if (b.getChosenWriteInsCandidateValue() != null) {
                                coll.add(b.getChosenWriteInsCandidateValue().stream().map(s -> "#" + s));
                            }
                            if (b.getChosenListIdentification() != null) {
                                coll.add(Stream.of(b.getChosenListIdentification()));
                            }
                            return coll.stream().flatMap(Function.identity());
                        })
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));

                // Finally do the check
                // TODO If no aliasCountMap, throw a business error ?
                aliasCountMap.forEach((alias, aliasCount) -> {
                    if (alias.startsWith("#")) {
                        Long nb = primesCountMap.entrySet().stream()
                                .filter(e -> e.getKey().endsWith(alias))
                                .mapToLong(e -> {
                                    Long aLong = e.getValue();
                                    return aLong != null ? aLong : 0L;
                                })
                                .sum();
                        if (!nb.equals(aliasCount)) {
                            throw buildVerificationFailureException(
                                    "The occurrences for an option are different in decryptedBallots.csv and evoting-decrypt.csv",
                                    Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification01.nok.message",
                                    alias
                            );
                        }
                    } else {
                        Long nb = primeAliasMap.entrySet().stream()
                                .filter(e -> e.getValue().equals(alias))
                                .map(Map.Entry::getKey)
                                .mapToLong(p -> {
                                    Long aLong = primesCountMap.get(p.toString());
                                    return aLong != null ? aLong : 0L;
                                })
                                .sum();
                        if (!nb.equals(aliasCount)) {
                            throw buildVerificationFailureException(
                                    "The occurrences for an option are different in decryptedBallots.csv and evoting-decrypt.csv",
                                    Block4VerificationSuite.RESOURCE_BUNDLE_NAME,
                                    "verification01.nok.message",
                                    alias
                            );
                        }
                    }
                });
            }

        }
        result.setStatus(Status.OK);
        return result;
    }

    private Map<String, Long> getCorrectFileAndExtractPrimesCount(File inputDirectory, String ballotBoxId) throws IOException {
        Path path = inputDirectory.toPath().resolve(Block4VerificationSuite.PATH_BALLOTBOXES).resolve(ballotBoxId);
        Iterable<List<String>> iterable = Deserializer.fromCsv(path.toFile(),
                "decompressedVotes\\.csv", ";", Arrays::asList);

        return StreamSupport.stream(iterable.spliterator(), false)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

}
