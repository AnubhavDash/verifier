/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.block.block4.tests;

import ch.evoting.xmlns.config._3.Configuration;
import ch.evoting.xmlns.decrypt._1.BallotsBoxType;
import ch.evoting.xmlns.decrypt._1.Results;
import ch.post.it.evoting.verifier.block.block4.Block4TestSuite;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.BallotBox;
import ch.post.it.evoting.verifier.dto.CandidatePosition;
import ch.post.it.evoting.verifier.dto.DataConfigEE;
import ch.post.it.evoting.verifier.dto.Option;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * /**
 * Test1 of Block4, Step checkOptionsMapping
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class Test01 extends Test {

    private static final Logger log = Logger.getLogger(Test01.class);
    private File inputDirectory;

    public File getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(4);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test01.description"));
        def.setId(1);
        def.setName("checkOptionsMapping");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        setInputDirectory(inputDirectory);
        TestResult result = new TestResult(getTestDefinition());
        try {
            DataConfigEE dataConfigEE = Deserializer.fromJson(inputDirectory, "dataConfig_.*\\.json", DataConfigEE.class);
            List<BallotBox> ballotBoxes = dataConfigEE.getElectionEvent().getBallotBoxes();

            ballotBoxes.stream().forEach( ballotBox -> {
                String ballotBoxId = ballotBox.getId();
                ballotBox.getCountingCircles().stream().forEach(countingCircle -> {
                    String countingCircleId = countingCircle.getId();
                    //1 Generate map<prime, alias>
                    Map<Integer, String> dataConfigPrimeAliasMap = new HashMap<>();
                    //votations
                    dataConfigPrimeAliasMap = countingCircle.getDomainOfInfluence().stream()
                            .flatMap(doi -> doi.getVotes().stream())
                            .flatMap(v -> v.getQuestions().stream())
                            .flatMap(q -> q.getOptions().stream())
                            .collect(Collectors.toMap(Option::getPrimeNumber, Option::getAlias));
                    //lists
                    dataConfigPrimeAliasMap.putAll( countingCircle.getDomainOfInfluence().stream()
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getLists().stream())
                            .collect(Collectors.toMap(ch.post.it.evoting.verifier.dto.List::getPrimeNumber, ch.post.it.evoting.verifier.dto.List::getAlias))
                    );
                    //candidates
                    dataConfigPrimeAliasMap.putAll( countingCircle.getDomainOfInfluence().stream()
                            .flatMap(doi -> doi.getElections().stream())
                            .flatMap(e -> e.getLists().stream())
                            .flatMap(l -> l.getCandidatePositions().stream())
                            .flatMap(cp -> cp.getPrimeNumber().stream().map(prime -> new AbstractMap.SimpleEntry<>(prime, cp.getAlias())))
                            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
                    );
                    //2 Generate map<prime, count>, but before retrieve the correct ecrypted file
                    Map<String,Long> decryptedPrimesCountMap = getCorrectFileAndExtractPrimesCount(countingCircleId);
                    //3 Generate map<alias, count>
                    // Results decryptResult = decryptResult = Deserializer.fromXml(inputDirectory, "evoting-decrypt.xml", Results.class);
                    // decryptResult.getBallotsBox();

                });

            });
            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof FileNotFoundException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "test01.file.not.found.message"));
            } else {
                log.error("Unexpected error", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block4TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
            }
        }
        return result;
    }

    private Map<String,Long> getCorrectFileAndExtractPrimesCount(String countingCircleId) {
        Iterable<List<String>> iterable = null;
        Map<String,Long> result = null;
        try {
            //TODO get the correct file regarding the countingCircleId
            iterable = Deserializer.fromCsv(inputDirectory, "decryptedBallots\\.csv", ";", array -> Arrays.asList(array));
            List<String> listGlobale = new ArrayList<>();
            for (List<String> list : iterable) {
                listGlobale.addAll(list);
            }
            result = listGlobale.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
