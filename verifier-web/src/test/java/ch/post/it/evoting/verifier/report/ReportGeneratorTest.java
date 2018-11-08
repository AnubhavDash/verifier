package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.report.pojo.Block;
import ch.post.it.evoting.verifier.report.pojo.Report;
import ch.post.it.evoting.verifier.report.pojo.Test;
import org.junit.Before;
import org.junit.Ignore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportGeneratorTest {

    private Report report;

    @Before
    public void init() {
        // provide some data
        Report report = new Report();
        report.setTitle("Verifikationsbericht");
        report.setHeaderTitleLabel("Urnenang :");
        report.setHeaderTitle("Nationalratswahl 23.10.2019");
        report.setReportDateLabel("Datum Bericht :");
        report.setReportTimeLabel("Zeit Bericht :");
        Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.y");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        report.setReportDate(dateFormatter.format(now));
        report.setReportTime(timeFormatter.format(now));

        List<Block> blocks = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            Block block = new Block();
            block.setTitre("Block " + i);
            block.setDescription("Description du Block " + i);
            List tests = new ArrayList();
            for (int j = 1; j < 16; j++) {
                Test test = new Test();
                test.setTestIdLabel("N°");
                test.setTestNameLabel("Name");
                test.setTestCategoryLabel("Kategorie");
                test.setTestDescriptionLabel("Description");
                test.setTestStatusLabel("Status");
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
}