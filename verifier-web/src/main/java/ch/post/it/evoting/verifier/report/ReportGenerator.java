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
package ch.post.it.evoting.verifier.report;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.verifier.common.Language;
import ch.post.it.evoting.verifier.common.block.tools.TranslationHelper;
import ch.post.it.evoting.verifier.dto.Verification;
import ch.post.it.evoting.verifier.mapper.ReportMapper;
import ch.post.it.evoting.verifier.report.model.Report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportGenerator {

	public static final String MESSAGE_BUNDLE_NAME = "message";

	public byte[] generate(final String contestName, final Date contestDate, final List<Verification> verifications, final Language language) {
		try {
			final var locale = language.getLocale();

			final var dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
			final var timeFormatter = new SimpleDateFormat("HH:mm:ss");

			final var report = new Report();
			report.setTitle((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.head.title", locale)));
			report.setHeaderTitleLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.title", locale)));

			report.setHeaderTitle(String.format("%s %s", contestName, contestDate != null ? dateFormatter.format(contestDate) : ""));
			report.setReportDateLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.date", locale)));
			report.setReportTimeLabel((TranslationHelper.getFromResourceBundle(MESSAGE_BUNDLE_NAME, "report.header.time", locale)));
			final var now = new Date();
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
			final Report content = ReportMapper.INSTANCE.map(report, verifications, language);

			final Map<String, Object> parameters = new HashMap<>();

			final var jrDataSource = new JRBeanCollectionDataSource(Collections.singletonList(content));

			final InputStream jasper = this.getClass().getClassLoader().getResourceAsStream("jasper/Vreport.jasper");

			final var jasperPrint = JasperFillManager.fillReport(jasper, parameters, jrDataSource);

			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (JRException e) {
			throw new IllegalArgumentException("unable to generate the PDF report", e);
		}
	}

}
