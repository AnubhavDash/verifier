/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report.pojo;

import ch.post.it.evoting.verifier.common.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ReportMetadata {
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

}
