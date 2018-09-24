/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.dto.Block;
import ch.post.it.evoting.verifier.dto.Report;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

/**
 * Class ReportGeneratorTest.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class ReportGeneratorTest {

    private Report report;

    @Before
    public void init(){
        // provide some data
        Report report = new Report();
        report.setTitre("Resultat du controle");
        report.setCanton("Canton de Neuchatel");
        report.setDate(new Date());

        List<Block> blocks = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            Block block = new Block();
            block.setTitre("Block " + i);
            block.setDescription("Description du Block " + i);
            List tests = new ArrayList();
            for (int j = 1; j < 11; j++) {
                ch.post.it.evoting.verifier.dto.Test test = new ch.post.it.evoting.verifier.dto.Test();
                test.setBlockId(i);
                test.setId(i + "-" + j);
                test.setTestId(j);
                Map<Language,String> desc = new HashMap<>();
                desc.put(Language.FR, "description du test " + j);
                desc.put(Language.DE, "beschreibung des tests " + j);
                test.setDescription(desc);
                test.setStatus(Status.OK);
                tests.add(test);
            }
            block.setTests(tests);
            blocks.add(block);
        }
        report.setBlocksResults(blocks);
        this.report = report;
    }

    @Ignore
    @Test
    public void generatePDF() {
        ReportGenerator reportGenerator = new ReportGenerator();
        Map<String, Object> content = new HashMap<>();
        content.put("reportDataSet", this.report);
        reportGenerator.generate(content);
    }
}