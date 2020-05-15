/*
 * This file is part of Verifier Swiss Post.
 *
 * Verifier Swiss Post is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Verifier Swiss Post is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Verifier Swiss Post.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.report.model.Block;
import ch.post.it.evoting.verifier.report.model.Report;
import ch.post.it.evoting.verifier.report.model.Test;
import org.junit.jupiter.api.BeforeEach;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportGeneratorTest {

    @BeforeEach
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
            block.setTitle("Block " + i);
            block.setDescription("Description du Block " + i);
            List<Test> tests = new ArrayList<>();
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
                if (j == 3) {
                    test.setStatus("NOK");
                    test.setMessage("The signature verification of the file eCH-0045.xml failed");
                }
                tests.add(test);
            }
            block.setTests(tests);
            blocks.add(block);
        }
        report.setBlocksResults(blocks);
    }
}
