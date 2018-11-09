package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationDataExtractor;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationStruct;
import ch.post.it.evoting.verifier.block.block2.secureLog.RegularLogEntry;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Test05 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test05.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test05.description"));
        def.setId(5);
        def.setName("checkVoteBallotBox");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectory);

            //count in the logs
            Stream<SecureLogEntry> logEntry = Deserializer.fromLines(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), ".*\\.json",
                    line -> {
                        try {
                            return SecureLogEntry.from(line);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to deserialize SecureLogEntry", e);
                        }
                    });
            Pattern pattern = Pattern.compile(".*\\|000\\|(.*)\\|.*\\|.*\\|#encryptedOptions=\"(.*)\" #ccx_id=.*\n");
            Map<String, String> mapSecureLogs = Flux.fromStream(logEntry)
                    .filter(sl -> sl.getPreview() != null && !sl.getPreview())
                    .filter(s1 -> s1 instanceof RegularLogEntry)
                    .cast(RegularLogEntry.class)
                    .filter(s1 -> s1.getRaw().matches(".*\\|VOTVAL\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|.*\n"))
                    .map(s1 -> {
                        Matcher matcher = pattern.matcher(s1.getRaw());
                        matcher.matches();
                        return Tuples.of(matcher.group(1), matcher.group(2));
                    })
                    .collectMap(t -> t.getT1(), t -> t.getT2()).block();

            //for all ballotbox
            //get downloadedBallotBox.csv --> votingCardId, encryptedOptions
            //foreach mapDownloadedBallotBox
            //check that mapDownloadedBallotBox[votingCardId] == mapSecuredLogs[votingCardId]
            Map<String, String> mapDownloadedBallotBoxs = new HashMap<>();

            List<File> downloadedBallotBoxFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2TestSuite.PATH_BALLOTBOXES).toFile(),
                    "downloadedBallotBox.*\\.csv",
                    true);

            for (File downloadedBbFile : downloadedBallotBoxFiles) {
                try(Stream<String> lines = Files.lines(downloadedBbFile.toPath())) {
                    Map<String, String> map = lines
                            .map(Test05::extractFromLine)
                            .filter(entry -> entry.getKey() != null)
                            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
                    mapDownloadedBallotBoxs.putAll(map);
                }
            }

            mapDownloadedBallotBoxs.entrySet()
                    .stream()
                    .forEach(entry -> {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (mapSecureLogs.containsKey(key)) {
                            if (!value.equals(mapSecureLogs.get(key))) {
                                throw new TestFailureException("encryptedOptions is not the same in DownloadedBallotBox and SecureLogs !", key, value);
                            }
                        } else {
                            throw new TestFailureException("Unknown votingCardId !", key);
                        }
                    });

            result.setStatus(Status.OK);

        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof RuntimeException) {
                LOGGER.error("Test failed, cause : " + e.getMessage(), e);
                if (e.getCause() instanceof SecureLogBundleValidationException) {
                    //TODO
                }
            }
            if (e instanceof TestFailureException) {
                String[] args = ((TestFailureException) e).getArgs();
                if(args.length == 2){
                    LOGGER.debug("checkpoint entry : "+ args[1] +" the does not verify", e);
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test05.nok.message", args[1], "no data"));
                }
                if(args.length == 3){
                    LOGGER.debug("checkpoint entry and attributes of the entry : "+ args[0] + ", " + args[1] + " the does not verify", e);
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test05.nok.message", args[1], args[2]));
                }
            } else if (e instanceof NoSuchFileException) {
                LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test05.file.not.found.message", ((NoSuchFileException) e).getFile()));
            } else if (e instanceof FileNotFoundException) {
                LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test05.file.not.found.message", e.getMessage()));
            }
        }
        return result;
    }

    static AbstractMap.SimpleEntry<String, String> extractFromLine(String line) {
        String vcId = null;
        String encOptions = null;
        final String VOTING_CARD_ID_TAG = "\"votingCardId\":\"";
        final String ENCRYPTED_OPTIONS_TAG = "\"encryptedOptions\":\"";
        if (line != null && !line.isEmpty() && line.contains(VOTING_CARD_ID_TAG)) {
            int vcIdStartIndex = line.indexOf(VOTING_CARD_ID_TAG) + VOTING_CARD_ID_TAG.length();
            vcId = line.substring(vcIdStartIndex, line.indexOf(",", vcIdStartIndex + 1) - 1);

            int encOptionsStartIndex = line.indexOf(ENCRYPTED_OPTIONS_TAG) + ENCRYPTED_OPTIONS_TAG.length();
            encOptions = line.substring(encOptionsStartIndex, line.indexOf(",", encOptionsStartIndex + 1) - 1);
        }
        return new AbstractMap.SimpleEntry(vcId, encOptions);
    }

}
