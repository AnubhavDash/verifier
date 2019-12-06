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
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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

        List<File> downloadedBallotBoxFiles =
                PathHelper.getFiles(inputDirectoryPath.resolve(Block2VerificationSuite.PATH_BALLOTBOXES).toFile(),
                        "downloadedBallotBox.*\\.csv",
                        true);

        // Iterate over all ballotBox files.
        for (File downloadedBbFile : downloadedBallotBoxFiles) {

            File successVotes = downloadedBbFile.toPath().getParent().resolve("successfulVotes.csv").toFile();
            File failedVotes = downloadedBbFile.toPath().getParent().resolve("failedVotes.csv").toFile();

            // Stream over all lines of current ballot file.
            try (Stream<String> lines = Files.lines(downloadedBbFile.toPath())) {

                // Create a map with two lists: one is the successful votes (confirmed == true) and the other the failed ones
                // (confirmed == false).
                Map<Boolean, List<String>> partitionedBallotBox = lines
                        // Convert a line to an object containing votingCardId and confirmed boolean.
                        .map(this::extractDbInfosFromLine)
                        // Remove potential nulls coming from extraction.
                        .filter(Objects::nonNull)
                        // Collect into a temporary map which is used to find duplicates.
                        .collect(Collectors.groupingBy(VotingEntry::getVotingCardId, Collectors.mapping(Function.identity(),
                                Collectors.toList())))
                        // Iterate the temporary map.
                        .entrySet()
                        .stream()
                        // Mapping throws an exception if a duplicated votingCardId is found.
                        .map(this::checkDuplicateVotingEntry)
                        // Main collect.
                        .collect(
                                // Partition by confirmed or not.
                                Collectors.partitioningBy(
                                        VotingEntry::isConfirmed,
                                        // Mapping to extract only the votingCardId.
                                        Collectors.mapping(
                                                VotingEntry::getVotingCardId,
                                                // Perform a final sort. Can be replaced before main collect by
                                                // .sorted((v1, v2) -> v1.getVotingCardId().compareToIgnoreCase(v2.getVotingCardId())).
                                                Collectors.collectingAndThen(
                                                        Collectors.toList(),
                                                        l -> l.stream().sorted().collect(Collectors.toList())
                                                )
                                        )
                                )
                        );

                // Extract the votingCardId's.
                List<String> votingCardSuccessList = extractVotingCardIds(successVotes);
                List<String> votingCardFailedList = extractVotingCardIds(failedVotes);

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

    private VotingEntry checkDuplicateVotingEntry(Map.Entry<String, List<VotingEntry>> entry) {
        if (entry.getValue().size() > 1) {
            throw buildVerificationFailureException(
                    "A voting card ID appears multiple times in the downloaded ballot box.",
                    Block2VerificationSuite.RESOURCE_BUNDLE_NAME,
                    "verification08.duplicate.nok.message",
                    entry.getValue().get(0).getVotingCardId()
            );
        } else {
            return entry.getValue().get(0);
        }
    }

    private List<String> extractVotingCardIds(File votes) throws IOException {

        return StreamSupport.stream(
                Deserializer.fromCsv(votes.getParentFile(), votes.getName(), ";", array -> {
                    if (array == null || array.length <= 2) {
                        return null;
                    }
                    return array[0];
                }).spliterator(), false)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
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
    static class VotingEntry {
        private String votingCardId;
        private boolean confirmed;

        VotingEntry(String votingCardId, boolean confirmed) {
            this.votingCardId = votingCardId;
            this.confirmed = confirmed;
        }
    }
}
