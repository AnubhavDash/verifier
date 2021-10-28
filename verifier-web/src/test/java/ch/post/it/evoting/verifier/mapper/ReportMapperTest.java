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
package ch.post.it.evoting.verifier.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.report.model.Block;
import ch.post.it.evoting.verifier.report.model.Report;

class ReportMapperTest {

	private Report metadata;
	private List<Verification> testsList;

	@BeforeEach
	void init() {
		//generate some info
		Report infos = new Report();
		infos.setTitle("Verifikationsbericht");
		infos.setHeaderTitleLabel("Urnengang");
		infos.setHeaderTitle("Nationalratshahl 23.10.2019");
		infos.setReportDateLabel("Datum Bericht");
		infos.setReportTimeLabel("Zeit Bericht");
		Date now = new Date();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.y");
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:s");
		infos.setReportDate(dateFormatter.format(now));
		infos.setReportTime(timeFormatter.format(now));
		this.metadata = infos;

		List<Verification> list = new ArrayList<>();

		for (int i = 1; i < 4; i++) {
			for (int j = 1; j < 11; j++) {
				Verification verification = new Verification();
				verification.setBlockId(i);
				verification.setId("" + j);
				verification.setName("le nom du test " + j);
				verification.setCategory(Category.AUTHENTICITY);
				HashMap<Language, String> desc = new HashMap<>();
				desc.put(Language.FR, "description du test " + j + " in french");
				desc.put(Language.DE, "description du test " + j + " in deutch");
				verification.setDescription(desc);
				verification.setStatus(Status.OK);
				HashMap<Language, String> mess = new HashMap<>();
				verification.setMessage(mess);
				if (j == 3) {
					verification.setStatus(Status.NOK);
					mess.put(Language.FR, "test " + j + " failed in french");
					mess.put(Language.DE, "test " + j + " failed in deutch");
					verification.setMessage(mess);
				}
				list.add(verification);
			}
			this.testsList = list;
		}

	}

	@Test
	void map() {
		//map in French
		Report report = ReportMapper.INSTANCE.map(this.metadata, this.testsList, Language.FR);
		List<Block> blocksResults = report.getBlocksResults();
		Assertions.assertEquals(3, blocksResults.size(), "problem with number of blocks in the report");
	}

}
