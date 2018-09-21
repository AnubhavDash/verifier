package ch.post.it.evoting.verifier.processor;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.TestResult;
import ch.post.it.evoting.verifier.common.VerifierBlock;
import ch.post.it.evoting.verifier.common.block.tools.Deserializer;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.mapper.TestExecutionStatusMapper;
import ch.post.it.evoting.verifier.report.ReportGenerator;
import ch.post.it.evoting.verifier.util.TestDefinitionTools;
import ch.post.it.evoting.verifier.report.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        //TODO move the code below in test
        testsStatus.forEach(t -> {
            log.info("test status => " + t.toString());
        });
        ResultDTO result = new ResultDTO();
        ReportDTO report = new ReportDTO();
        report.setTitre("Resultat du controle");
        report.setCanton("Canton de Neuchatel");
        report.setDate(new Date());

        HashMap<Integer, BlockDTO> blocksMap = new HashMap<Integer, BlockDTO>();
        testsStatus.forEach(t -> {
            int blockId = t.getBlockId();
            blocksMap.putIfAbsent(blockId, new BlockDTO());
            BlockDTO blockDTO = blocksMap.get(blockId);
            blockDTO.setTitre("Block " + blockId);
            blockDTO.setDewscription("Description du block " +  blockId);
            List<Test> tests = blockDTO.getTests();
            tests.add(t);
        });
        List<BlockDTO> blockDTOList = blocksMap.values().stream().collect(Collectors.toList());
        report.setBlocksResults(blockDTOList);
        result.setReport(report);

        BlockDTO block = blockDTOList.get(0);
        Test test = block.getTests().get(0);
        Map<String, Object> content = new HashMap<>();
        content.put("testId", test.getId());
        content.put("name", test.getName());
        content.put("category", test.getCategory().toString());
        content.put("descDE", test.getDescription().get(Language.DE));
        content.put("descFR", test.getDescription().get(Language.FR));
        content.put("message", "OK");
        content.put("status", test.getStatus().toString());

        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generate(content);

        Deserializer.toJson(result, new File(configurationOutputDirectory + File.separator + jsonReportName));
    }

    public class ResultDTO{
        private ReportDTO report;

        public ReportDTO getReport() {
            return report;
        }

        public void setReport(ReportDTO report) {
            this.report = report;

        }
    }

    public class ReportDTO{
        private String titre;
        private String canton;
        private Date date;
        private List<BlockDTO> blocksResults;

        public String getTitre() {
            return titre;
        }

        public void setTitre(String titre) {
            this.titre = titre;
        }

        public String getCanton() {
            return canton;
        }

        public void setCanton(String canton) {
            this.canton = canton;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public List<BlockDTO> getBlocksResults() {
            return blocksResults;
        }

        public void setBlocksResults(List<BlockDTO> blocksResults) {
            this.blocksResults = blocksResults;
        }
    }

    public class BlockDTO{
        private String titre;
        private String dewscription;
        private List<Test> tests;

        public BlockDTO() {
            setTests(new ArrayList<>());
        }

        public String getTitre() {
            return titre;
        }

        public void setTitre(String titre) {
            this.titre = titre;
        }

        public String getDewscription() {
            return dewscription;
        }

        public void setDewscription(String dewscription) {
            this.dewscription = dewscription;
        }

        public List<Test> getTests() {
            return tests;
        }

        public void setTests(List<Test> tests) {
            this.tests = tests;
        }
    }

}