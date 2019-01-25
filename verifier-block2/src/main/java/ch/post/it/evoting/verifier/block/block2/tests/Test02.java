package ch.post.it.evoting.verifier.block.block2.tests;

import ch.post.it.evoting.verifier.block.block2.Block2TestSuite;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleCreator;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogBundleValidationException;
import ch.post.it.evoting.verifier.block.block2.securelog.SecureLogEntry;
import ch.post.it.evoting.verifier.common.*;
import ch.post.it.evoting.verifier.common.block.TestFailureException;
import ch.post.it.evoting.verifier.common.block.dto.HostMappingElement;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.common.block.tools.PathHelper;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import org.apache.log4j.Logger;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

//TODO uncommment the test once signature PROBLEM is resolved
public class Test02 /*extends Test*/ {

    private static final Logger LOGGER = Logger.getLogger(Test02.class);

    /*@Override*/
    public TestDefinition getTestDefinition() {
        TestDefinition def = new TestDefinition();
        def.setBlockId(2);
        def.setCategory(Category.AUTHENTICITY);
        def.setDescription(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test02.description"));
        def.setId(2);
        def.setName("checkSecureLogSignature");
        def.addTestTrait(TestTrait.PreDecryption);
        return def;
    }

    /*@Override*/
    public TestResult executeTest(File inputDirectory) {
        TestResult result = new TestResult(getTestDefinition());
        try {
            // create host/CC mapping
            File mapping = PathHelper.getFile(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), "mapping_cc_hosts.csv");
            Iterable<HostMappingElement> iterable = Deserializer.fromCsv(mapping.getParentFile(), mapping.getName(), ";", Deserializer.toHostMappingElement);
            Map<String, String> hostCcMapping = StreamSupport.stream(iterable.spliterator(), false)
                    .skip(1)
                    .collect(Collectors.toMap(HostMappingElement::getHostname, HostMappingElement::getCc));

            // create CC/Pem mapping
            File[] pemFiles = PathHelper.getFiles(inputDirectory.toPath().resolve(Block2TestSuite.PATH_CC_CERTIFICATES).toFile(), ".*cc.*_log_sign.pem");
            Map<String, byte[]> ccPemMapping = Arrays.stream(pemFiles).map(f -> {
                try {
                    return new AbstractMap.SimpleEntry<>(f.getName().substring(0, 3).toUpperCase(), Files.readAllBytes(f.toPath()));
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

            Map<String, byte[]> mapPem = hostCcMapping.entrySet().stream().map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), ccPemMapping.get(entry.getValue()))).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)); //TODO

            Stream<SecureLogEntry> logEntryStream = Deserializer.fromLines(inputDirectory.toPath().resolve(Block2TestSuite.PATH_SECURE_LOGS).toFile(), ".*\\.json",
                    line -> {
                        try {
                            return SecureLogEntry.from(line);
                        } catch (IOException e) {
                            throw new RuntimeException("Unable to deserialize SecureLogEntry", e);
                        }
                    });

            Flux.fromIterable(logEntryStream::iterator)
                    .filter(s -> s.getPreview() != null && !s.getPreview())
                    .groupBy(s -> String.format("%s|%s", s.getHost(), s.getSource()))
                    .flatMap(s -> SecureLogBundleCreator.from(s, mapPem))
                    .subscribe(b -> {
                        try {
                            b.validateSignature();
                        } catch (SecureLogBundleValidationException e) {
                            LOGGER.error("Validation failed because on host {" + e.getHost() + "} " + e.getMessage());
                            throw new TestFailureException(b.getBeginCheckPoint().getRaw());
                        }
                    }, e -> {
                        if (e instanceof TestFailureException) {
                            throw (TestFailureException) e;
                        } else {
                            throw new RuntimeException(e);
                        }
                    });

            result.setStatus(Status.OK);

        } catch (IOException e) {
            LOGGER.error("a IOException error occurred", e);
            result.setStatus(Status.NOK);
        } catch (TestFailureException e) {
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "test02.nok.message", e.getArgs()));
        } catch (Exception e) {
            LOGGER.error("an unexpected error occurred", e);
            result.setStatus(Status.NOK);
            result.setMessage(TranslationHelper.getFromResourceBundle(Block2TestSuite.RESOURCE_BUNDLE_NAME, "error.generic.message"));
        }
        return result;
    }
}
