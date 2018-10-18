package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
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
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Test03 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test03.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test03.description"));
        def.setId(3);
        def.setName("checkNumberChoiceReturnCodes");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            //Get the voterInformation.csv Files and count
            Path path = inputDirectory.toPath().resolve(Block2TestSuite.PATH_ELECTION_SETUP);
            DataConfigEE dataConfigEE = Deserializer.fromJson(path.toFile(), "dataConfig_updated_.*\\.json", DataConfigEE.class);
            List<String> votingCardSets = dataConfigEE
                                                    .getElectionEvent()
                                                    .getBallotBoxes()
                                                    .stream()
                                                    .map(ballotBox -> ballotBox.getVcsId())
                                                    .collect(Collectors.toList());
            List<File> voterInformationFiles = new ArrayList<>();
            votingCardSets.forEach( votingCardSet -> {
                    try {
                        File folder = inputDirectory.toPath().resolve(Block2TestSuite.PATH_ELECTION_SETUP).resolve(Block2TestSuite.PATH_VOTING_CARD_SETS).resolve(votingCardSet).toFile();
                        voterInformationFiles.add(PathHelper.getFile(folder, "voterInformation.*\\.csv"));
                    } catch (FileNotFoundException e) {
                        throw new TestFailureException("voterInformation.csv not found", inputDirectory.getName());
                    }
                });


            //count in the logs
            Stream<SecureLogEntry> logEntryStream = Deserializer.fromLines(inputDirectory, "secure_logs_2018_10_16.json",
                    line -> {
                        try {
                            return SecureLogEntry.from(line);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to deserialize SecureLogEntry", e);
                        }
                    }).filter(sl -> sl.getIndex() != null && sl.getIndex().equals("it_evoting_cc"));



            result.setStatus(Status.OK);

        } catch (RuntimeException e) {
            result.setStatus(Status.NOK);
            if (e.getCause() instanceof SecureLogBundleValidationException) {
                //TODO
            }
        } catch (Exception e) {
            result.setStatus(Status.NOK);
        }

        return result;
    }
}
