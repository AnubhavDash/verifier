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
            metadata.getReportTime());
    }

    public Report(String title, String urnLabel, String urn, String reportDateLabel, String reportDate, String reportTimeLabel, String reportTime) {
        this.title = title;
        this.urnLabel = urnLabel;
        this.urn = urn;
        this.reportDateLabel = reportDateLabel;
        this.reportDate = reportDate;
        this.reportTimeLabel = reportTimeLabel;
        this.reportTime = reportTime;
    }

}
