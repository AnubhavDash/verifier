package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationDataExtractor;
import ch.post.it.evoting.verifier.block.block2.loader.VoterInformationStruct;
import ch.post.it.evoting.verifier.block.block2.securelog.RegularLogEntry;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Test06 extends Test {

    private static final Logger LOGGER = Logger.getLogger(Test06.class);

    @Override
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.EVIDENCE);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test06.description"));
        def.setId(6);
        def.setName("checkVoteUnity");
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    @Override
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            VoterInformationStruct voterInformation = VoterInformationDataExtractor.getInfo(inputDirectory);

            // create host/CC mapping
            File mapping = PathHelper.getFile(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), "mapping_cc_hosts.csv");
            Iterable<HostMappingElement> iterable = Deserializer.fromCsv(mapping.getParentFile(), mapping.getName(), ";", Deserializer.toHostMappingElement);
            Map<String, String> hostCcMapping = StreamSupport.stream(iterable.spliterator(), false)
                    .skip(1)
                    .collect(Collectors.toMap(HostMappingElement::getHostname, HostMappingElement::getCc));

            Stream<SecureLogEntry> logEntry = Deserializer.fromLines(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), ".*\\.json",
                    line -> {
                        try {
                            return SecureLogEntry.from(line);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to deserialize SecureLogEntry", e);
                        }
                    });
            Pattern pattern = Pattern.compile(".*\\|000\\|(.*)\\|.*\\|.*\\|#encryptedOptions=\".*\" #ccx_id=.*\n");
            Map<String, Map<String, Long>> nbVotingCardPerCC = Flux.fromStream(logEntry)
                    .filter(sl -> sl.getPreview() != null && !sl.getPreview())
                    .filter(s1 -> s1 instanceof RegularLogEntry)
                    .cast(RegularLogEntry.class)
                    .filter(s1 -> s1.getRaw().matches(".*\\|VOTVAL\\|-\\|.*\\|" + voterInformation.getEeid() + "\\|.*\n"))
                    .map(s1 -> {
                        Matcher matcher = pattern.matcher(s1.getRaw());
                        matcher.matches();
                        String votingCardId = matcher.group(1);
                        return Tuples.of(s1.getHost(), votingCardId);
                    })
                    .groupBy(s1 -> hostCcMapping.containsKey(s1.getT1()) ? hostCcMapping.get(s1.getT1()) : s1.getT1())
                    .flatMap(ccGroup -> {
                        return ccGroup.map(Tuple2::getT2).reduce(Collections.synchronizedMap(new HashMap<String, Long>()), (m, votingCardId) -> {
                            m.put(votingCardId, m.getOrDefault(votingCardId, 0L) + 1L);
                            return m;
                        }).map(m -> Tuples.of(ccGroup.key(), m));
                    })
                    .collectMap(Tuple2::getT1, Tuple2::getT2).block();

            List<String> problematicVotingCardIds = nbVotingCardPerCC.values().stream()
                    .flatMap(m -> m.entrySet().stream())
                    .filter(e -> e.getValue() > 1)
                    .map(e -> e.getKey()).collect(Collectors.toList());
            if (!problematicVotingCardIds.isEmpty()) {
                throw new TestFailureException(problematicVotingCardIds.toArray(new String[]{}));
            }
            result.setStatus(Status.OK);
        } catch (Exception e) {
            result.setStatus(Status.NOK);
            if (e instanceof TestFailureException) {
                String[] args = ((TestFailureException) e).getArgs();
                LOGGER.debug("Test failed, problematic votingcard ids : " + Arrays.toString(args));
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test06.nok.message", args));
            } else if (e instanceof NoSuchFileException) {
                result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test06.file.not.found.message", ((NoSuchFileException) e).getFile()));
            } else if (e instanceof FileNotFoundException) {
                LOGGER.error("Test in error, cause : " + e.getMessage() + " is missing", e);
                if( e.getLocalizedMessage().equals(".*\\.json")){
                    result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test06.file.not.found.message", "logs JSON file"));
                }
                 else{
                     result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test06.file.not.found.message", e.getMessage()));
                }
            }
        }
        return result;
    }

}
