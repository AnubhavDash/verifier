/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.report.pojo.Report;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Collections;
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

    private static final Logger LOGGER = Logger.getLogger(ReportGenerator.class);

    public byte[] generate(Report content) {

        try {
            Map<String, Object> parameters = new HashMap<>();

            JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(Collections.singletonList(content));

            InputStream report = this.getClass().getClassLoader().getResourceAsStream("jasper/Vreport.jasper");

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, jrDataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            LOGGER.error("unable to generate the PDF report", e);
            throw new RuntimeException(e);
        }
    }

}
