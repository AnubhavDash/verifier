/*
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
package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.common.block.tools.path.PathNode;
import ch.post.it.evoting.verifier.common.block.tools.path.StructureKey;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CheckConfirmedVotesBallotBox extends AbstractVerification {

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                "verification08.description"));
        def.setId(8);
        def.setName("checkConfirmedVotesBallotBox");
        def.addVerificationTrait(VerificationTrait.PRE_DECRYPTION);
        return def;
    }

    @Override
    public VerificationResult verify(Path inputDirectoryPath) throws Exception {
        VerificationResult result = new VerificationResult();

        final PathNode downloadedBallotDirPathNode = pathService.buildFromRootPath(StructureKey.BALLOT_BOX_ID_DIR, inputDirectoryPath);

        // Iterate over all ballotBox id directories.
        for (Path regexPathDirs : downloadedBallotDirPathNode.getRegexPaths()) {

            // Retrieve the downloaded ballot box file.
            final PathNode downloadedFilePathNode = pathService.buildFromDynamicAncestorPath(StructureKey.DOWNLOADED_BALLOT_BOX, regexPathDirs);

            // Stream over all lines of current ballot file.
            try (Stream<String> lines = Files.lines(downloadedFilePathNode.getPath())) {

                // Create a map with two entries:
                // true => list of confirmed votes
                // false => list of unconfirmed votes
                Map<Boolean, List<String>> partitionedBallotBox = partitionDownloadedBallotBox(lines);

                // Retrieve success and failed votes files.
                final PathNode successVotesPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.SUCCESSFUL_VOTES, regexPathDirs);
                final PathNode failedVotesPathNode = pathService.buildFromDynamicAncestorPath(StructureKey.FAILED_VOTES, regexPathDirs);

                // Extract the votingCardId's.
                List<String> votingCardSuccessList = extractVotingCardIds(successVotesPathNode.getPath());
                List<String> votingCardFailedList = extractVotingCardIds(failedVotesPathNode.getPath());

                // Check that the lists are equals (containing exactly the same elements).
                if (!partitionedBallotBox.get(true).equals(votingCardSuccessList)) {
                    throw buildVerificationFailureException(
                            "The list of confirmed votes and the list of successful votes are not equal.",
                            Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                            "verification08.confirmed.nok.message"
                    );
                }
                if (!partitionedBallotBox.get(false).equals(votingCardFailedList)) {
                    throw buildVerificationFailureException(
                            "The list of unconfirmed votes and the list of failed votes are not equal.",
                            Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                            "verification08.unconfirmed.nok.message"
                    );
                }
            }
        }

        result.setStatus(Status.OK);
        return result;
    }

    private Map<Boolean, List<String>> partitionDownloadedBallotBox(Stream<String> lines) {
        return lines
                .parallel()
                // Convert a line to an object containing votingCardId and confirmed boolean.
                .map(this::extractDbInfosFromLine)
                // Remove potential nulls coming from extraction.
                .filter(Objects::nonNull)
                // Check for duplicates. Method distinctByKey throws in case of duplicates.
                .filter(distinctByKey(VotingEntry::getVotingCardId))
                // Sort according to votingCardId's.
                .sorted(Comparator.comparing(VotingEntry::getVotingCardId))
                // Main collect.
                .collect(
                        // Partition by confirmed or not.
                        Collectors.partitioningBy(
                                VotingEntry::isConfirmed,
                                // Mapping to extract only the votingCardId.
                                Collectors.mapping(
                                        VotingEntry::getVotingCardId,
                                        Collectors.toCollection(ArrayList::new)
                                )
                        )
                );
    }

    // Adapted from: https://stackoverflow.com/questions/23699371/java-8-distinct-by-property/27872852#27872852
    private Predicate<VotingEntry> distinctByKey(Function<VotingEntry, String> keyExtractor) {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        return t -> {
            if (!seen.add(keyExtractor.apply(t))) {
                throw buildVerificationFailureException(
                        "A voting card ID appears multiple times in the downloaded ballot box.",
                        Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                        "verification08.duplicate.nok.message",
                        keyExtractor.apply(t)
                );
            }
            return true;
        };
    }

    private List<String> extractVotingCardIds(Path votes) throws IOException {
        return StreamSupport.stream(
                Deserializer.fromCsv(votes, ";", array -> {
                    if (array == null || array.length <= 2) {
                        return null;
                    }
                    return array[0];
                }).spliterator(), true)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private VotingEntry extractDbInfosFromLine(String line) {
        if (!line.isEmpty() && line.contains("}}|")) {
            int endJsonObjectIndex = line.indexOf("}}|") + 2;
            String json = line.substring(0, endJsonObjectIndex);
            String voteCastCode = line.substring(endJsonObjectIndex + 1).split("\\|")[0];
            try {
                DownloadedBallot db = Deserializer.fromJson(TypeConverter.stringToByte(json), DownloadedBallot.class);
                return new VotingEntry(db.getVote().getVotingCardId(), !voteCastCode.isEmpty());
            } catch (IOException e) {
                throw new RuntimeException();
            }
        } else {
            return null;
        }
    }

    @Getter
    private static class VotingEntry {
        private String votingCardId;
        private boolean confirmed;

        VotingEntry(String votingCardId, boolean confirmed) {
            this.votingCardId = votingCardId;
            this.confirmed = confirmed;
        }
    }
}
