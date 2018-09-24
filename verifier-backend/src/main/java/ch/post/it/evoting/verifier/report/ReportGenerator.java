/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.dto.Report;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class ReportGenerator.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class ReportGenerator {

    public void generate(Report content) {

        try {
            //safe content
            Map<String, Object> parameters = new HashMap<String, Object>();

            List<Report> list = new ArrayList<Report>();
            list.add(content);
            JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(list);
            InputStream report = this.getClass().getClassLoader().getResourceAsStream("jasper/Vreport.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, jrDataSource);

            // Export to PDF.
            JasperExportManager.exportReportToPdfFile(jasperPrint, "verifier-backend/target/verifier-result.pdf");

            System.out.println("Done!");
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

}
