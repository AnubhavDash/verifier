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
    private String urnLabel;
    private String urn;
    private String reportDateLabel;
    private String reportDate;
    private String reportTimeLabel;
    private String reportTime;

}
