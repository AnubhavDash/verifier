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
import ch.post.it.evoting.verifier.common.block.dto.HostMappingElement;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


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
            List<File> voterInformationFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2TestSuite.PATH_ELECTION_SETUP).resolve(Block2TestSuite.PATH_VOTING_CARD_SETS).toFile(),
                    "voterInformation.*\\.csv",
                    true);
            Long voterInformationCount = voterInformationFiles.stream()
                    .map(f -> {
                        try {
                            Stream<String> lines = Files.lines(f.toPath());
                            long count1 = lines.count();
                            return count1;
                        } catch (IOException e) {
                            return null;
                        }
                    }).mapToLong(Long::longValue).sum();

            // create host/CC mapping
            File mapping = PathHelper.getFile(inputDirectory, "mapping_cc_hosts.csv");
            Iterable<HostMappingElement> iterable = Deserializer.fromCsv(mapping.getParentFile(), mapping.getName(), ";", Deserializer.toHostMappingElement);
            Map<String, String> hostCcMapping = StreamSupport.stream(iterable.spliterator(), false)
                    .filter(hme -> !hme.getHostname().equalsIgnoreCase(Block2TestSuite.HOSTNAME_LABEL_MAPPING_FILE))
                    .map(hme -> {
                        return new AbstractMap.SimpleEntry<>(hme.getHostname(), hme.getCc());
                    }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            //count in the logs
            Stream<SecureLogEntry> logEntryStream = Deserializer.fromLines(inputDirectory, "secure_logs_90_mo.json",
                    line -> {
                        try {
                            return SecureLogEntry.from(line);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to deserialize SecureLogEntry", e);
                        }
                    }).filter(sl -> sl.getIndex() != null && sl.getIndex().equals("it_evoting_cc"));

            Map<String, Long> ccCountMap = new HashMap<>();
            logEntryStream.forEach( secureLogEntry -> {
                if(secureLogEntry instanceof RegularLogEntry){
                    String ccId = hostCcMapping.get(secureLogEntry.getHost());
                    String raw = secureLogEntry.getRaw();
                    if(raw.contains("GENPCC")){
                        incrementCountByCC(ccCountMap, ccId);
                    }
                }
            });

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

    private void incrementCountByCC(Map<String, Long> map, String ccId) {
        map.putIfAbsent(ccId, 0L);
        map.compute(ccId, (key, oldValue) -> oldValue + 1);
    }
}
