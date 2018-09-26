package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.report.pojo.Report;
import com.lowagie.text.pdf.BaseFont;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
