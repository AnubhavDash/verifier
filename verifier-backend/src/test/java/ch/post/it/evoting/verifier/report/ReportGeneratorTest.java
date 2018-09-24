/*
 * ------------------------------------------------------------------------------------------------
 * Copyright 2014 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id$
 * ------------------------------------------------------------------------------------------------
 */

package ch.post.it.evoting.verifier.report;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Class ReportGeneratorTest.
 * This represents TODO.
 *
 * @author lalandret
 * @version $$Revision$$
 */
public class ReportGeneratorTest {

    @Ignore
    @Test
    public void generatePDFTestEntityTest() {
        Map<String, Object> content = new HashMap<>();
        content.put("testId", "1");
        content.put("name", "isprime(p)");
        content.put("category", "Complitness");
        content.put("descDE", "description en allemand");
        content.put("descFR", "description en français");
        content.put("message", "OK");
        content.put("status", "OK");

        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generate(content);
    }
}