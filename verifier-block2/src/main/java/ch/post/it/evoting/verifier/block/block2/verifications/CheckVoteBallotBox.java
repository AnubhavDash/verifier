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
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationDataExtractor;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationStruct;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.AbstractVerification;
import ch.post.it.evoting.verifier.common.block.VerificationFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;
import org.apache.log4j.Logger;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckVoteBallotBox extends AbstractVerification {

    private static final Logger LOGGER = Logger.getLogger(CheckVoteBallotBox.class);

    @Override
    public VerificationDefinition getVerificationDefinition() {
        VerificationDefinition def = new VerificationDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test05.description"));
        def.setId(5);
        def.setName("checkVoteBallotBox");
        def.addVerificationTrait(VerificationTrait.PreDecryption);
        return def;
    }

    @Override
    public VerificationResult executeVerification(File inputDirectory) {
        VerificationResult result = new VerificationResult(getVerificationDefinition());
        try {
            VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectory);

            //count in the logs
            final Pattern patternEncryptedOption = Pattern.compile(".*\\|000\\|(.*)\\|.*\\|.*\\|#encryptedOptions=\"(.*)\" #ccx_id=.*\n");
            final Pattern pattern = Pattern.compile("\\|VOTVAL\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|");
            Map<String, String> mapSecureLogs = SecureLogEntry.loadRegularLogs(inputDirectory, pattern)
                    .map(s1 -> {
                        Matcher matcher = patternEncryptedOption.matcher(s1.getRaw());
                        matcher.matches();
                        return Tuples.of(matcher.group(1), matcher.group(2));
                    })
                    .collectMap(t -> t.getT1(), t -> t.getT2()).block();

            //for all ballotbox
            //get downloadedBallotBox.csv --> votingCardId, encryptedOptions
            //foreach mapDownloadedBallotBox
            //check that mapDownloadedBallotBox[votingCardId] == mapSecuredLogs[votingCardId]
            Map<String, String> mapDownloadedBallotBoxs = new HashMap<>();

            List<File> downloadedBallotBoxFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2VerificationSuite.PATH_BALLOTBOXES).toFile(),
                    "downloadedBallotBox.*\\.csv",
                    true);

            for (File downloadedBbFile : downloadedBallotBoxFiles) {
                try (Stream<String> lines = Files.lines(downloadedBbFile.toPath())) {
                    Map<String, String> map = lines
                            .map(l -> {
                                try {
                                    return extractFromLine(l);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .filter(entry -> entry.getKey() != null)
                            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
                    if (!mergeMapWithoutDuplicates(mapDownloadedBallotBoxs, map)) {
                        throw new VerificationFailureException("Duplicate votingCardId in downloadedBallotBox files");
                    }
                }
            }

            //check sizes
            if (mapDownloadedBallotBoxs.size() != mapSecureLogs.size()) {
                throw new VerificationFailureException("the number of encrypted votes in the secure logs and downloadboxes are not equal");
            }

            mapDownloadedBallotBoxs.entrySet()
                    .stream()
                    .forEach(entry -> {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (mapSecureLogs.containsKey(key)) {
                            if (!value.equals(mapSecureLogs.get(key))) {
                                throw new VerificationFailureException("encryptedOptions is not the same in DownloadedBallotBox and SecureLogs !", key, value);
                            }
                        } else {
                            throw new VerificationFailureException("Unknown votingCardId !", key);
                        }
                    });

            result.setStatus(Status.OK);
        } catch (NoSuchFileException e) {
            LOGGER.error("a NoSuchFileException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test05.file.not.found.message", e.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error("a FileNotFoundException error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test05.file.not.found.message", e.getMessage()));
        } catch (VerificationFailureException e) {
            result.setStatus(Status.NOK);
            String[] args = e.getArgs();
            if (args.length == 1) {
                LOGGER.debug("the number of encrypted votes in the secure logs and downloadboxes are not equal", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test05.nok.numberVotes.mismatch.message"));
            }
            if (args.length == 2) {
                LOGGER.debug("checkpoint entry : " + args[1] + " the does not verify", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test05.nok.message", args[1], "no data"));
            }
            if (args.length == 3) {
                LOGGER.debug("checkpoint entry and attributes of the entry : " + args[0] + ", " + args[1] + " the does not verify", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "test05.nok.message", args[1], args[2]));
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2VerificationSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }

    static boolean mergeMapWithoutDuplicates(Map<String, String> map1, Map<String, String> map2) {
        try {
            map2.forEach((k, v) -> {
                if (map1.containsKey(k)) {
                    throw new IllegalArgumentException("map1 already contains key");
                } else {
                    map1.put(k, v);
                }
            });
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    static AbstractMap.SimpleEntry<String, String> extractFromLine(String line) throws IOException {
        if (!line.isEmpty() && line.indexOf("}}|") != -1) {
            line = line.substring(0, line.indexOf("}}|") + 2);
            DownloadedBallot db = Deserializer.fromJson(TypeConverter.stringToByte(line), DownloadedBallot.class);
            return new AbstractMap.SimpleEntry(db.getVote().getVotingCardId(), db.getVote().getEncryptedOptions());
        } else {
            return new AbstractMap.SimpleEntry(null, null);
        }
    }

}
