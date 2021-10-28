/*
 * Copyright 2021 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import ch.post.it.evoting.verifier.report.model.Block;
import ch.post.it.evoting.verifier.report.model.Report;
import ch.post.it.evoting.verifier.report.model.Test;

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
