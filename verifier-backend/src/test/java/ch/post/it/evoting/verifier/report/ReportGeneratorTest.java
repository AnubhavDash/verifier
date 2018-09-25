package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.report.pojo.Block;
import ch.post.it.evoting.verifier.report.pojo.Report;
import ch.post.it.evoting.verifier.report.pojo.TestReport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void init() {
        // provide some data
        Report report = new Report();
        report.setTitle("Verifikationsbericht");
        report.setUrnLabel("Urnenang :");
        report.setUrn("Nationalratswahl 23.10.2019");
        report.setReportDateLabel("Datum Bericht :");
        report.setReportDate("23.10.2019");
        report.setReportTimeLabel("Zeit Bericht :");
        report.setReportTime("11:12:30");

        List<Block> blocks = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            Block block = new Block();
            block.setTitre("Block " + i);
            block.setDescription("Description du Block " + i);
            block.setTestIdLabel("N°");
            block.setTestNameLabel("Name");
            block.setTestCategoryLabel("Kategorie");
            block.setTestDescriptionLabel("Description");
            block.setTestStatusLabel("Status");
            List tests = new ArrayList();
            for (int j = 1; j < 11; j++) {
                TestReport test = new TestReport();
                test.setId("" + j);
                test.setName("le nom du test " + j);
                test.setCategory("Integrity");
                test.setDescription("description du test " + j);
                test.setStatus("OK");
                test.setMessage("");
                if(j == 3 ){
                    test.setStatus("NOK");
                    test.setMessage("The signature verification of the file eCH-0045.xml failed");
                }
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
        reportGenerator.generate(this.report);
    }
}