package ch.post.it.evoting.verifier.processor;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.VerifierBlock;
import ch.post.it.evoting.verifier.report.pojo.Report;
import ch.post.it.evoting.verifier.report.pojo.ReportMetadata;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.mapper.ReportMapper;
import ch.post.it.evoting.verifier.mapper.TestExecutionStatusMapper;
import ch.post.it.evoting.verifier.report.ReportGenerator;
import ch.post.it.evoting.verifier.util.TestDefinitionTools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class VerifierProcessor {
    private static final Logger log = Logger.getLogger(VerifierProcessor.class);

    @Value("${inputDirectory}")
    private String configurationInputDirectory;

    @Value("${outputDirectory}")
    private String configurationOutputDirectory;
    @Value("${jsonReportName}")
    private String jsonReportName;

    @Value("#{'${verifier.blocks}'.split(';')}")
    private String[] configurationVerifierBlocks;

    private Map<String, Test> executionStatus;
    private List<VerifierBlock> blocks;

    private List<ProcessListener> listeners;
    private boolean processed;

    @PostConstruct
    private void init() {
        listeners = new LinkedList<>();
        processed = false;

        blocks = Arrays.stream(configurationVerifierBlocks).parallel().map(s -> {
            try {
                Class clazz = Class.forName(s);
                return (VerifierBlock) clazz.newInstance();
            } catch (Exception e) {
                log.fatal(e.getMessage(), e);
                throw new RuntimeException("Unable to load verifierBlocks", e);
            }
        }).collect(Collectors.toList());

        executionStatus = blocks.parallelStream()
                .flatMap(VerifierBlock::getTests)
                .collect(Collectors.toMap(TestDefinitionTools::computeUniqueKey, TestExecutionStatusMapper.INSTANCE::map));
    }

    public List<Test> getTestStatus() {
        List<Test> result = new LinkedList<>(executionStatus.values());
        result.sort(Comparator.comparingInt(Test::getBlockId).thenComparingInt(Test::getTestId));
        return result;
    }

    public void processTests() throws AlreadyStartedException {
        if (!processed) {
            processed = true;

            final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
            executor.execute(() -> {
                ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
                forkJoinPool.submit(() ->
                {
                    final File inputDirectory = new File(configurationInputDirectory);
                    blocks.stream()
                            .map(b -> b.process(inputDirectory).parallel())
                            .reduce(Stream.empty(), Stream::concat)
                            .parallel()
                            .forEach(t ->
                            {
                                log.debug(String.format("Test '%02d-%02d' performed on Thread '%s'", t.getTestDefinition().getBlockId(), t.getTestDefinition().getId(), Thread.currentThread().getName()));
                                testProcessed(t);
                            });
                    // FLATMAP kill the parallelism. Use concat instead --> TODO view this with LBO
                    // flatMap(b -> b.process(inputDirectory).parallel());
                });
            });
        } else {
            throw new AlreadyStartedException();
        }
    }

    protected synchronized void testProcessed(TestResult testResult) {
        Test testExecutionStatus = executionStatus.get(TestDefinitionTools.computeUniqueKey(testResult.getTestDefinition()));
        TestExecutionStatusMapper.INSTANCE.update(testExecutionStatus, testResult);

        listeners.forEach(l -> l.testProcessed(testExecutionStatus));
    }

    public void registerProcessListener(ProcessListener listener) {
        listeners.add(listener);
    }

    public void unregisterProcessListener(ProcessListener listener) {
        listeners.remove(listener);
    }

    public void resetExecution() {
        LinkedList<ProcessListener> copy = new LinkedList<>(this.listeners);
        init();
        this.listeners = copy;
    }

    public void generatePdf(List<Test> testsStatus) throws IOException {
        //generate met info
        ReportMetadata infos = new ReportMetadata();
        infos.setTitle("Verifikationsbericht");
        infos.setUrnLabel("Urnengang");
        infos.setUrn("Nationalratshahl 23.10.2019");
        infos.setReportDateLabel("Datum Bericht");
        infos.setReportTimeLabel("Zeit Bericht");
        Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.y");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:s");
        infos.setReportDate(dateFormatter.format(now));
        infos.setReportTime(timeFormatter.format(now));

        //map to a Report Object
        Report content = ReportMapper.getInstance().map(testsStatus, infos, Language.FR);

        //Generate PDF with the Report Object
        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generate(content);

    }


}