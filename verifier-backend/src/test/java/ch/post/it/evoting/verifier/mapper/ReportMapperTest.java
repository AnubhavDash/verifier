/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.mapper;

import ch.post.it.evoting.verifier.common.Category;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.Status;
import ch.post.it.evoting.verifier.report.pojo.Block;
import ch.post.it.evoting.verifier.report.pojo.Report;
import ch.post.it.evoting.verifier.report.pojo.ReportMetadata;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Class ReportMapperTest.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class ReportMapperTest {

    private ReportMetadata metadata;
    private List<ch.post.it.evoting.verifier.dto.Test> testsList;

    @Before
    public void init(){
        //generate some info
        ReportMetadata infos = new ReportMetadata();
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

        List<ch.post.it.evoting.verifier.dto.Test> list = new ArrayList<>();

        for(int i = 1; i < 4; i++ ){
            for (int j = 1; j < 11; j++) {
                ch.post.it.evoting.verifier.dto.Test test = new ch.post.it.evoting.verifier.dto.Test();
                test.setBlockId(i);
                test.setId("" + j);
                test.setName("le nom du test " + j);
                test.setCategory(Category.AUTHENTICITY);
                HashMap<Language, String> desc = new HashMap();
                desc.put(Language.FR, "description du test " + j + " in french");
                desc.put(Language.DE, "description du test " + j + " in deutch");
                test.setDescription(desc);
                test.setStatus(Status.OK);
                HashMap<Language, String> mess = new HashMap();
                test.setMessage(mess);
                if(j == 3 ){
                    test.setStatus(Status.NOK);
                    mess.put(Language.FR, "test " + j + " failed in french");
                    mess.put(Language.DE, "test " + j + " failed in deutch");
                    test.setMessage(mess);
                }
                list.add(test);
            }
            this.testsList = list;
        }

    }

    @Test
    public void map() {
        //map in French
        Report report = ReportMapper.INSTANCE.map(this.testsList, this.metadata, Language.FR);
        List<Block> blocksResults = report.getBlocksResults();
        Assert.assertEquals("problem with number of blocks in the report", 3, blocksResults.size());
        int z = blocksResults.size();
    }


}