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
package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.report.model.Block;
import ch.post.it.evoting.verifier.report.model.Report;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReportMapperTest {

    private Report metadata;
    private List<Verification> testsList;

    @Before
    public void init() {
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
    public void map() {
        //map in French
        Report report = ReportMapper.INSTANCE.map(this.metadata, this.testsList, Language.FR);
        List<Block> blocksResults = report.getBlocksResults();
        Assert.assertEquals("problem with number of blocks in the report", 3, blocksResults.size());
    }


}
