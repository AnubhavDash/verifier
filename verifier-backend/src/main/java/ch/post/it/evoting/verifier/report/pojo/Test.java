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

@Getter
@Setter
public class Test {
    private String testIdLabel;
    private String testNameLabel;
    private String testCategoryLabel;
    private String testDescriptionLabel;
    private String testStatusLabel;
    private String testMessageLabel;

    private String id;
    private String name;
    private String category;
    private String description;
    private String status;
    private String message;

}