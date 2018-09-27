package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.block.block1.Block1TestSuite;
import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.mapper.ReportMapper;
import ch.post.it.evoting.verifier.report.pojo.Report;
import ch.post.it.evoting.verifier.report.pojo.ReportMetadata;
import com.lowagie.text.pdf.BaseFont;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ReportGenerator {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerator.class);
    public static final String MESSAGE_BUNDLE_NAME = "message";

    public byte[] generate(List<Test> tests) {
        return generate(tests, Locale.FRENCH);
    }

    public byte[] generate(List<Test> tests, Locale locale) {

        try {
            ReportMetadata infos = new ReportMetadata();
            infos.setTitle((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.head.title", locale)));
            infos.setUrnLabel("Urnengang:");
            infos.setUrn("Nationalratswahl 23.10.2019");
            infos.setReportDateLabel("Datum Bericht:");
            infos.setReportTimeLabel("Zeit Bericht:");
            Date now = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.y");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
            infos.setReportDate(dateFormatter.format(now));
            infos.setReportTime(timeFormatter.format(now));
            infos.setCommentLabel("Bemerkungen");
            infos.setSignaturetLabel("Unterschriften");
            infos.setPlaceDatetLabel("Ort / Datum");
            infos.setLastNameLabel("Name");
            infos.setFirstNameLabel("Vorname");
            infos.setFooterUrnLabel("Urnengang:");
            infos.setFooterUrn("Nationalratswahl 23.10.2019");
            infos.setFooterDateLabel("Datum / Zeit:");
            infos.setFooterDate(dateFormatter.format(now) + " / " + timeFormatter.format(now));

            //map to a Report Object
            Report content = ReportMapper.INSTANCE.map(tests, infos, Language.FR);

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
