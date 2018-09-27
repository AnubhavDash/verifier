package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.Test;
import ch.post.it.evoting.verifier.mapper.ReportMapper;
import ch.post.it.evoting.verifier.report.pojo.Report;
import ch.post.it.evoting.verifier.report.pojo.ReportMetadata;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ReportGenerator {

    private static final Logger LOGGER = Logger.getLogger(ReportGenerator.class);
    public static final String MESSAGE_BUNDLE_NAME = "message";

    public byte[] generate(List<Test> tests) {
       // return generate(tests, Locale.GERMAN);
        return generate(tests, Locale.FRENCH);
    }

    public byte[] generate(List<Test> tests, Locale locale) {

        try {
            ReportMetadata infos = new ReportMetadata();
            infos.setTitle((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.head.title", locale)));
            infos.setHeaderTitleLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.title", locale)));
            // TODO how to get the data to set ?
            infos.setHeaderTitle("Nationalratswahl 23.10.2019");
            infos.setReportDateLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.date", locale)));
            infos.setReportTimeLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.time", locale)));
            Date now = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.y");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
            infos.setReportDate(dateFormatter.format(now));
            infos.setReportTime(timeFormatter.format(now));
            infos.setCommentLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.comment", locale)));
            infos.setSignaturetLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.sign", locale)));
            infos.setPlaceDatetLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.place.date", locale)));
            infos.setLastNameLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.lastname", locale)));
            infos.setFirstNameLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.firstname", locale)));
            infos.setFooterTitleLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.footer.title", locale)));
            infos.setFooterTitle("Nationalratswahl 23.10.2019");
            infos.setFooterDateLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.footer.date", locale)));
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
