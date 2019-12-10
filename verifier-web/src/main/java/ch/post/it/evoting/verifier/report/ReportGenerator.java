/**
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
package ch.post.it.evoting.verifier.report;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.mapper.ReportMapper;
import ch.post.it.evoting.verifier.report.model.Report;
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

    public byte[] generate(String contestName, Date contestDate, List<Verification> verifications, Language language) {
        try {
            Locale locale = language.getLocale();

            final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
            final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

            Report report = new Report();
            report.setTitle((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.head.title", locale)));
            report.setHeaderTitleLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.title", locale)));

            report.setHeaderTitle(String.format("%s %s", contestName, contestDate != null ? dateFormatter.format(contestDate) : ""));
            report.setReportDateLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.date", locale)));
            report.setReportTimeLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.time", locale)));
            Date now = new Date();
            report.setReportDate(dateFormatter.format(now));
            report.setReportTime(timeFormatter.format(now));
            report.setCommentLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.comment", locale)));
            report.setSignaturetLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.sign", locale)));
            report.setPlaceDatetLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.place.date", locale)));
            report.setLastNameLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.lastname", locale)));
            report.setFirstNameLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.lastpage.firstname", locale)));
            report.setFooterTitleLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.footer.title", locale)));
            report.setFooterTitle(report.getHeaderTitle());
            report.setFooterDateLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.footer.date", locale)));
            report.setFooterDate(dateFormatter.format(now) + " / " + timeFormatter.format(now));

            //map to a Report Object
            Report content = ReportMapper.INSTANCE.map(report, verifications, language);

            Map<String, Object> parameters = new HashMap<>();

            JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(Collections.singletonList(content));

            InputStream jasper = this.getClass().getClassLoader().getResourceAsStream("jasper/Vreport.jasper");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasper, parameters, jrDataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            LOGGER.error("unable to generate the PDF report", e);
            throw new RuntimeException(e);
        }
    }

}
