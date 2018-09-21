/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report.dto;

import java.util.Date;
import java.util.List;

/**
 * Class ReportDto.
 * This represents TODO.
 *
 * @author Lalandret
 * @version $$Revision$$
 */
public class ReportDto {
    private String titre;
    private String canton;
    private Date date;
    private List<BlockDto> blocksResults;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<BlockDto> getBlocksResults() {
        return blocksResults;
    }

    public void setBlocksResults(List<BlockDto> blocksResults) {
        this.blocksResults = blocksResults;
    }
}
