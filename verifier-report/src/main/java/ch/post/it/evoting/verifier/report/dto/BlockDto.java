/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Class BlockDto.
 * This represents TODO.
 *
 * @author Lalandret
 * @version $$Revision$$
 */
public class BlockDto {
    private String titre;
    private String dewscription;
    private List<TestDto> tests;

    public BlockDto() {
        setTests(new ArrayList<>());
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDewscription() {
        return dewscription;
    }

    public void setDewscription(String dewscription) {
        this.dewscription = dewscription;
    }

    public List<TestDto> getTests() {
        return tests;
    }

    public void setTests(List<TestDto> tests) {
        this.tests = tests;
    }
}
