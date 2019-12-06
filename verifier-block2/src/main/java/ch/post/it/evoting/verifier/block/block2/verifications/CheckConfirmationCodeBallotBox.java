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
package ch.post.it.evoting.verifier.block.block2.verifications;

import ch.post.it.evoting.verifier.block.block2.Block2VerificationSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Verification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CheckConfirmationCodeBallotBox extends Verification {

    private static final Logger LOGGER = Logger.getLogger(CheckConfirmationCodeBallotBox.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test08.description"));
        def.setId(8);
        def.setName("checkConfirmationCodeBallotBox");
        def.addVerificationTrait(VerificationTrait.PreDecryption);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            List<File> downloadedBallotBoxFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2VerificationSuite.PATH_BALLOTBOXES).toFile(),
                    "downloadedBallotBox.*\\.csv",
                    true);

            for (File downloadedBbFile : downloadedBallotBoxFiles) {

                File successVotes = downloadedBbFile.toPath().getParent().resolve("successfulVotes.csv").toFile();
                File failedVotes = downloadedBbFile.toPath().getParent().resolve("failedVotes.csv").toFile();

                try (Stream<String> lines = Files.lines(downloadedBbFile.toPath())) {
                    Map<String, Boolean> mapDownloadedBb = lines
                            .map(line -> {
                                try {
                                    return extractDbInfosFromLine(line);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .filter(entry -> entry.getKey() != null)
                            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

                    List<String> votingCardSuccessList = StreamSupport.stream(
                            Deserializer.fromCsv(successVotes.getParentFile(), successVotes.getName(), ";", array -> {
                                if (array == null || array.length <= 2) {
                                    return null;
                                }
                                return array[0];
                            }).spliterator(), false)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    List<String> votingCardFailedList = StreamSupport.stream(
                            Deserializer.fromCsv(failedVotes.getParentFile(), failedVotes.getName(), ";", array -> {
                                if (array == null || array.length <= 2) {
                                    return null;
                                }
                                return array[0];
                            }).spliterator(), false)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    //test size
                    if (mapDownloadedBb.size() != (votingCardSuccessList.size() + votingCardFailedList.size())) {
                        throw new VerificationFailureException("there is a mismatch count between the list of successful or failed votes and the download ballot box");
                    }

                    //test existence in success or failed list
                    if (mapDownloadedBb.entrySet()
                            .stream()
                            .map(e -> {
                                if (e.getValue()) {
                                    return votingCardSuccessList.contains(e.getKey());
                                } else {
                                    return votingCardFailedList.contains(e.getKey());
                                }
                            })
                            .filter(val -> val == false)
                            .count() > 0) {
                        throw new VerificationFailureException("there is a mismatch between the list of successful or failed votes and the download ballot box");
                    }
                }
            }

            result.setStatus(Status.OK);

        } catch (VerificationFailureException e) {
            LOGGER.debug(e.getArgs()[0], e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test08.nok.message"));
        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message", e.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

    private static AbstractMap.SimpleEntry<String, Boolean> extractDbInfosFromLine(String line) throws IOException {
        if (!line.isEmpty() && line.contains("}}|")) {
            int endJsonObjectIndex = line.indexOf("}}|") + 2;
            String json = line.substring(0, endJsonObjectIndex);
            String voteCastCode = line.substring(endJsonObjectIndex + 1).split("\\|")[0];
            DownloadedBallot db = Deserializer.fromJson(TypeConverter.stringToByte(json), DownloadedBallot.class);
            return new AbstractMap.SimpleEntry<>(db.getVote().getVotingCardId(), !voteCastCode.isEmpty());
        } else {
            return new AbstractMap.SimpleEntry<>(null, null);
        }
    }
}
