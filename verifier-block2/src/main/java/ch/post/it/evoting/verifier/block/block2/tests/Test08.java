package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.dto.HostMappingElement;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.common.block.tools.TypeConverter;
import ch.post.it.evoting.verifier.dto.DownloadedBallot;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test08 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test08.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test08.description"));
        def.setId(8);
        def.setName("checkConfirmationCodeBallotBox");
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            //for all ballotbox
            //get downloadedBallotBox.csv --> votingCardId, encryptedOptions
            //foreach mapDownloadedBallotBox
            //check that mapDownloadedBallotBox[votingCardId] == mapSecuredLogs[votingCardId]
            Map<String, String> mapDownloadedBallotBoxs = new HashMap<>();

            List<File> downloadedBallotBoxFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2TestSuite.PATH_BALLOTBOXES).toFile(),
                    "downloadedBallotBox.*\\.csv",
                    true);

            for (File downloadedBbFile : downloadedBallotBoxFiles) {

                File successVotes = downloadedBbFile.toPath().getParent().resolve("successfulVotes.csv").toFile();
                File failedVotes = downloadedBbFile.toPath().getParent().resolve("failedVotes.csv").toFile();

                try (Stream<String> lines = Files.lines(downloadedBbFile.toPath())) {
                    Map<String, String> mapDownloadedBb = lines
                            .map(line -> {
                                try {
                                    return extractDbInfosFromLine(line);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .filter(entry -> entry.getKey() != null)
                            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

                    List<String> votingCardSuccessList = new ArrayList<>();
                    Deserializer.fromCsv(successVotes.getParentFile(), successVotes.getName(), ";", array -> {
                        if (array == null || array.length <= 2) { return null; }
                        return array[0];
                    }).forEach(value -> { if(value != null){ votingCardSuccessList.add(value); }});

                    List<String> votingCardFailedList = new ArrayList<>();
                    Deserializer.fromCsv(failedVotes.getParentFile(), failedVotes.getName(), ";", array -> {
                        if (array == null || array.length <= 2) { return null; }
                        return array[0];
                    }).forEach(value -> { if(value != null){ votingCardFailedList.add(value); }});

                    List<String> combinedList = new ArrayList<>(votingCardSuccessList);
                    combinedList.addAll(votingCardFailedList);

                    //test size
                    if(combinedList.size() != mapDownloadedBb.size()){
                        throw new TestFailureException("there is a mismatch between the list of successful and failed votes and the download ballot box");
                    }

                }
            }

            result.setStatus(Status.OK);
        } catch (FileNotFoundException e) {
            LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message", e.getMessage()));
        } catch (NoSuchFileException e) {
            LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test08.file.not.found.message", e.getFile()));
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            String[] args = e.getArgs();
            if (args.length == 1) {
                LOGGER.debug("the number of encrypted votes in the secure logs and downloadboxes are not equal", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test08.nok.numberVotes.mismatch.message"));
            }
            if (args.length == 2) {
                LOGGER.debug("checkpoint entry : " + args[1] + " the does not verify", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test08.nok.message", args[1], "no data"));
            }
            if (args.length == 3) {
                LOGGER.debug("checkpoint entry and attributes of the entry : " + args[0] + ", " + args[1] + " the does not verify", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test08.nok.message", args[1], args[2]));
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected error", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }


    static AbstractMap.SimpleEntry<String, String> extractDbInfosFromLine(String line) throws IOException {
        if (!line.isEmpty() && line.indexOf("}}|") != -1) {
            line = line.substring(0, line.indexOf("}}|") + 2);
            DownloadedBallot db = Deserializer.fromJson(TypeConverter.stringToByte(line), DownloadedBallot.class);
            return new AbstractMap.SimpleEntry(db.getVote().getVotingCardId(), db.getVote().getEncryptedOptions());
        } else {
            return new AbstractMap.SimpleEntry(null, null);
        }
    }


}
