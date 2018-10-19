package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.secureLog.RegularLogEntry;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.secureLog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.common.TestDefinition;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.block.Test;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.dto.HostMappingElement;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Test04 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test04.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.CONSISTENCY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test04.description"));
        def.setId(4);
        def.setName("checkNumberVoteCastCodes");
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            //Get the voterInformation.csv Files and count
            List<File> voterInformationFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2TestSuite.PATH_ELECTION_SETUP).resolve(Block2TestSuite.PATH_VOTING_CARD_SETS).toFile(),
                    "voterInformation.*\\.csv",
                    true);
            Long voterInformationCount = voterInformationFiles.stream()
                    .map(f -> {
                        try {
                            return Files.lines(f.toPath()).count();
                        } catch (IOException e) {
                            throw  new RuntimeException("An error occurs while parsing the voterInformation.csv files", e.getCause());
                        }
                    }).mapToLong(Long::longValue).sum();

            // create host/CC mapping
            File mapping = PathHelper.getFile(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), "mapping_cc_hosts.csv");
            Iterable<HostMappingElement> iterable = Deserializer.fromCsv(mapping.getParentFile(), mapping.getName(), ";", Deserializer.toHostMappingElement);
            Map<String, String> hostCcMapping = StreamSupport.stream(iterable.spliterator(), false)
                    .skip(1)
                    .map(hme -> new AbstractMap.SimpleEntry<>(hme.getHostname(), hme.getCc()))
                    .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            //count in the logs
            Stream<SecureLogEntry> logEntry = Deserializer.fromLines(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), "secure_logs_90_mo.json",
                    line -> {
                        try {
                            return SecureLogEntry.from(line);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to deserialize SecureLogEntry", e);
                        }
                    });
            Map<String, Long> countByCC = Flux.fromStream(logEntry)
                    .filter(sl -> sl.getIndex() != null && sl.getIndex().equals("it_evoting_cc"))
                    .filter(s1 -> s1 instanceof RegularLogEntry)
                    .cast(RegularLogEntry.class)
                    .filter(s1 -> s1.getRaw().contains("GENPVCC"))
                    .groupBy(s1 -> hostCcMapping.get(s1.getHost()))
                    .flatMap(group -> {
                        String ccName = group.key();
                        return group.count().flux().map(count -> new AbstractMap.SimpleEntry<>(ccName, count));
                    }).collectMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue).block();

            if(countByCC == null){
                throw new RuntimeException("no values found while counting log foreach control component");
            }
            long nbDistinctValues = countByCC.values().stream().distinct().count();
            if (nbDistinctValues != 1) {
                //at this point with have 4 distincts values
                throw new TestFailureException("count of log for partial choice code generation is not the same for each control component", countByCC.values().toString());

            } else {
                //finally check the count with csv files count
                Long logCount = countByCC.values().stream().findFirst().get();
                result.setStatus( (voterInformationCount.equals(logCount)) ? Status.OK : Status.NOK );
            }

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
                LOGGER.debug("Test failed, cause : " + args[0]  +". Count for the CCs : " + args[1].toString());
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test04.nok.message"));
            } else if (e instanceof NoSuchFileException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test04.file.not.found.message", ((NoSuchFileException) e).getFile()));
            } else if (e instanceof FileNotFoundException) {
                LOGGER.error("Test in error, cause : "  + e.getMessage() + " is missing", e);
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test04.file.not.found.message", e.getMessage()));
            }
        }
        return result;
    }

}
