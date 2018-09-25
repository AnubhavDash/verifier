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

}
