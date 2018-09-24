/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class ReportGenerator.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class ReportGenerator {

    public void generate(Map<String, Object> content){

        try {
            InputStream report = getClass().getResourceAsStream("/jasper/Vreport-test.jasper");

            // URL url = (ReportGenerator.class).newInstance().getClass().getResource("/jasper/Vreport-test.jrxml");
            //File jrxml = new File(getClass().getResource("/jasper/Vreport-test.jrxml").getFile());
            //JasperReport jasperReport = JasperCompileManager.compileReport(jrxml.getName());

            Map<String, Object> parameters = content == null ? new HashMap<String, Object>() : content;

            // JasperPrint jasperPrint = JasperFillManager.fillReport("resources/jasper/Vreport-test.jasper", parameters);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters);

            // Make sure the output directory exists.
            File outDir = new File("C:/jasperoutput");
            outDir.mkdirs();

            // Export to PDF.
            JasperExportManager.exportReportToPdfFile(jasperPrint,"C:/jasperoutput/StyledTextReport.pdf");

            System.out.println("Done!");
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

}
