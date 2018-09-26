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
    private String urnLabel;
    private String urn;
    private String reportDateLabel;
    private String reportDate;
    private String reportTimeLabel;
    private String reportTime;
    private String commentLabel;
    private String signaturetLabel;
    private String placeDatetLabel;
    private String lastNameLabel;
    private String firstNameLabel;
    private String footerUrnLabel;
    private String footerUrn;
    private String footerDateLabel;
    private String footerDate;

    private List<Block> blocksResults;

    public Report() {
    }

    public Report(ReportMetadata metadata) {
        this(metadata.getTitle(),
            metadata.getUrnLabel(),
            metadata.getUrn(),
            metadata.getReportDateLabel(),
            metadata.getReportDate(),
            metadata.getReportTimeLabel(),
            metadata.getReportTime(),
            metadata.getCommentLabel(),
            metadata.getSignaturetLabel(),
            metadata.getPlaceDatetLabel(),
            metadata.getLastNameLabel(),
            metadata.getFirstNameLabel(),
            metadata.getFooterUrnLabel(),
            metadata.getFooterUrn(),
            metadata.getFooterDateLabel(),
            metadata.getFooterDate());
    }

    public Report(String title, String urnLabel, String urn, String reportDateLabel, String reportDate, String reportTimeLabel, String reportTime,
            String commentLabel, String signaturetLabel, String placeDatetLabel, String lastNameLabel, String firstNameLabel, String footerUrnLabel, String footerUrn,
            String footerDateLabel, String footerDate) {
        this.title = title;
        this.urnLabel = urnLabel;
        this.urn = urn;
        this.reportDateLabel = reportDateLabel;
        this.reportDate = reportDate;
        this.reportTimeLabel = reportTimeLabel;
        this.reportTime = reportTime;
        this.commentLabel = commentLabel;
        this.signaturetLabel = signaturetLabel;
        this.placeDatetLabel = placeDatetLabel;
        this.lastNameLabel = lastNameLabel;
        this.firstNameLabel = firstNameLabel;
        this.footerUrnLabel = footerUrnLabel;
        this.footerUrn = footerUrn;
        this.footerDateLabel = footerDateLabel ;
        this.footerDate = footerDate;
    }

}
