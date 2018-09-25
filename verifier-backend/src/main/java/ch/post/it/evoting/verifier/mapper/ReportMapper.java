/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.dto.*;
import ch.post.it.evoting.verifier.report.pojo.Block;
import ch.post.it.evoting.verifier.report.pojo.Report;
import ch.post.it.evoting.verifier.report.pojo.ReportMetadata;
import ch.post.it.evoting.verifier.report.pojo.TestReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ReportMapper {

    private ReportMapper(){}

    private static ReportMapper INSTANCE = new ReportMapper();

    public static ReportMapper getInstance()
    {   return INSTANCE;
    }

    public Report map(List<Test> testsList, ReportMetadata metadata, Language lang) {
        Report result = new Report(metadata);
        result.setBlocksResults(getBlocksResults(testsList, lang));
        return result;
    }

    public List<Block> getBlocksResults(List<Test> testsList, Language lang) {
        HashMap<Integer, Block> blocksMap = new HashMap<Integer, Block>();
        testsList.forEach(t -> {
            int blockId = t.getBlockId();
            blocksMap.putIfAbsent(blockId, new Block());
            Block block = blocksMap.get(blockId);
            // TODO label should be provided by propertiees files
            block.setTitre("Block " + blockId);
            block.setDescription("Description du block " + blockId);
            block.setTestIdLabel("N°");
            block.setTestNameLabel("Name");
            block.setTestCategoryLabel("Kategorie");
            block.setTestDescriptionLabel("Description");
            block.setTestStatusLabel("Status");
            block.setTestMessageLabel("");
            List<TestReport> tests = block.getTests() == null ? new ArrayList<>() : block.getTests();
            tests.add(TestReportMapper.INSTANCE.map(t, lang));
            block.setTests(tests);
        });
        return new ArrayList(blocksMap.values());
    }

}
