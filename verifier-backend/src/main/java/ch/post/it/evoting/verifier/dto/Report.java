/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.dto;

import ch.post.it.evoting.verifier.processor.VerifierProcessor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Report {
    private String title;
    private String urn;
    private String reportDate;
    private String reportTime;
    private List<Block> blocksResults;

}
