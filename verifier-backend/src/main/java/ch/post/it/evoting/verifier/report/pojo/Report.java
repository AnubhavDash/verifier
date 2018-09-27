/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Report {

    private String title;
    private String headerTitleLabel;
    private String headerTitle;
    private String reportDateLabel;
    private String reportDate;
    private String reportTimeLabel;
    private String reportTime;
    private String commentLabel;
    private String signaturetLabel;
    private String placeDatetLabel;
    private String lastNameLabel;
    private String firstNameLabel;
    private String footerTitleLabel;
    private String footerTitle;
    private String footerDateLabel;
    private String footerDate;

    private List<Block> blocksResults;

    public Report() {
    }

    public Report(ReportMetadata metadata) {
        this(metadata.getTitle(),
            metadata.getHeaderTitleLabel(),
            metadata.getHeaderTitle(),
            metadata.getReportDateLabel(),
            metadata.getReportDate(),
            metadata.getReportTimeLabel(),
            metadata.getReportTime(),
            metadata.getCommentLabel(),
            metadata.getSignaturetLabel(),
            metadata.getPlaceDatetLabel(),
            metadata.getLastNameLabel(),
            metadata.getFirstNameLabel(),
            metadata.getFooterTitleLabel(),
            metadata.getFooterTitle(),
            metadata.getFooterDateLabel(),
            metadata.getFooterDate());
    }

    public Report(String title, String headerTitleLabel, String headerTitle, String reportDateLabel, String reportDate, String reportTimeLabel, String reportTime,
                  String commentLabel, String signaturetLabel, String placeDatetLabel, String lastNameLabel, String firstNameLabel, String footerTitleLabel, String footerTitle,
                  String footerDateLabel, String footerDate) {
        this.title = title;
        this.headerTitleLabel = headerTitleLabel;
        this.headerTitle = headerTitle;
        this.reportDateLabel = reportDateLabel;
        this.reportDate = reportDate;
        this.reportTimeLabel = reportTimeLabel;
        this.reportTime = reportTime;
        this.commentLabel = commentLabel;
        this.signaturetLabel = signaturetLabel;
        this.placeDatetLabel = placeDatetLabel;
        this.lastNameLabel = lastNameLabel;
        this.firstNameLabel = firstNameLabel;
        this.footerTitleLabel = footerTitleLabel;
        this.footerTitle = footerTitle;
        this.footerDateLabel = footerDateLabel ;
        this.footerDate = footerDate;
    }

}
